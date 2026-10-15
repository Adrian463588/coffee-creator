#include "gguf_guard.h"
#include "prompt_guard.h"
#include "llama.h"
#include <jni.h>
#include <memory>
#include <mutex>
#include <unordered_map>

namespace {

struct Runtime {
    std::mutex operation;
    std::atomic_bool cancelled{false};
    llama_model * model = nullptr;
    llama_context * context = nullptr;
    llama_sampler * sampler = nullptr;
    int context_size = 1024;
    int threads = 1;
    int remaining = 0;
    std::string architecture;
    std::vector<std::string> reserved_pieces;

    void end() {
        if (sampler) llama_sampler_free(sampler);
        sampler = nullptr;
        if (context) llama_free(context);
        context = nullptr;
        remaining = 0;
    }

    ~Runtime() {
        end();
        if (model) llama_model_free(model);
    }
};

std::once_flag backend_once;
std::mutex registry_mutex;
std::unordered_map<jlong, std::shared_ptr<Runtime>> runtimes;
jlong next_handle = 1;

std::shared_ptr<Runtime> runtime(jlong handle) {
    std::lock_guard<std::mutex> lock(registry_mutex);
    const auto found = runtimes.find(handle);
    if (found == runtimes.end()) throw std::runtime_error("Native model handle is no longer available.");
    return found->second;
}

void throw_java(JNIEnv * env, const std::exception & failure) {
    if (env->ExceptionCheck()) return;
    const auto type = env->FindClass("java/lang/IllegalStateException");
    if (type) {
        const auto allocation = dynamic_cast<const std::bad_alloc *>(&failure);
        env->ThrowNew(type, allocation ? "Native allocation failed. Close other apps or choose a smaller model." : failure.what());
    }
}

void discard_log(ggml_log_level, const char *, void *) {
    // Model paths, imported prompts and tokens are private; no native logging sink.
}

std::string bytes(JNIEnv * env, jbyteArray array, size_t limit) {
    if (!array) throw std::runtime_error("Native input is missing.");
    const jsize size = env->GetArrayLength(array);
    if (size < 0 || static_cast<size_t>(size) > limit) throw std::runtime_error("Native input exceeds its size limit.");
    std::string result(static_cast<size_t>(size), '\0');
    env->GetByteArrayRegion(array, 0, size, reinterpret_cast<jbyte *>(result.data()));
    if (env->ExceptionCheck()) throw std::runtime_error("Native input could not be read.");
    if (result.find('\0') != std::string::npos) throw std::runtime_error("Native text contains a null character.");
    return result;
}

bool abort_decode(void * data) {
    return static_cast<Runtime *>(data)->cancelled.load();
}

bool load_progress(float, void * data) {
    return !static_cast<Runtime *>(data)->cancelled.load();
}

void check_decode(Runtime & state, llama_batch batch) {
    if (state.cancelled.load()) return;
    const int result = llama_decode(state.context, batch);
    if (result != 0 && !state.cancelled.load()) {
        throw std::runtime_error("Native decode failed. Retry with a smaller compatible model/context.");
    }
}


std::vector<llama_token> tokenize_messages(JNIEnv * env, Runtime & state, jobjectArray roles, jobjectArray content) {
    if (!roles || !content || !state.model) throw std::runtime_error("Model/prompt unavailable.");
    const jsize count = env->GetArrayLength(roles);
    if (count < 2 || count > 14 || env->GetArrayLength(content) != count) {
        throw std::runtime_error("Prompt role/content count is invalid.");
    }
    std::vector<std::string> role_strings;
    std::vector<std::string> content_strings;
    role_strings.reserve(static_cast<size_t>(count));
    content_strings.reserve(static_cast<size_t>(count));
    for (jsize i = 0; i < count; ++i) {
        const auto role = static_cast<jstring>(env->GetObjectArrayElement(roles, i));
        if (!role) throw std::runtime_error("Prompt role is missing.");
        const auto chars = env->GetStringUTFChars(role, nullptr);
        if (!chars) throw std::runtime_error("Prompt role could not be read.");
        role_strings.emplace_back(chars);
        env->ReleaseStringUTFChars(role, chars);
        env->DeleteLocalRef(role);
        const auto & current_role = role_strings.back();
        if ((i == 0 && current_role != "system") || (i == count - 1 && current_role != "user") ||
            (i > 0 && i < count - 1 && current_role != "user" && current_role != "assistant")) {
            throw std::runtime_error("Prompt roles violate the system/history boundary.");
        }
        const auto body = static_cast<jbyteArray>(env->GetObjectArrayElement(content, i));
        content_strings.push_back(bytes(env, body, 131072));
        env->DeleteLocalRef(body);
    }
    const bool gemma4 = state.architecture == "gemma4";
    std::string prompt;
    if (gemma4) {
        std::vector<std::pair<std::string, std::string>> turns;
        for (jsize i = 0; i < count; ++i) turns.emplace_back(role_strings[i], content_strings[i]);
        prompt = coffee::format_gemma4_text(state.architecture, turns, state.reserved_pieces);
    } else {
        size_t total_bytes = 0;
        for (auto & text : content_strings) {
            text = coffee::escape_reserved(text, state.reserved_pieces);
            total_bytes += text.size();
            if (total_bytes > 131072) throw std::runtime_error("Prompt exceeds the native byte limit.");
        }
        std::vector<llama_chat_message> messages;
        for (jsize i = 0; i < count; ++i) messages.push_back({role_strings[i].c_str(), content_strings[i].c_str()});
        const auto chat_template = llama_model_chat_template(state.model, nullptr);
        const int prompt_bytes = llama_chat_apply_template(chat_template, messages.data(), messages.size(), true, nullptr, 0);
        if (prompt_bytes <= 0 || prompt_bytes > 262144) throw std::runtime_error("Unsupported or oversized model chat template.");
        std::vector<char> formatted(static_cast<size_t>(prompt_bytes) + 1);
        if (llama_chat_apply_template(chat_template, messages.data(), messages.size(), true, formatted.data(),
                                     static_cast<int>(formatted.size())) != prompt_bytes) {
            throw std::runtime_error("Chat template formatting failed.");
        }
        prompt.assign(formatted.data(), static_cast<size_t>(prompt_bytes));
    }
    const auto vocab = llama_model_get_vocab(state.model);
    // Gemma4 renderer includes the canonical BOS; add_special=false avoids inserting it twice.
    const bool add_special = !gemma4;
    const int prompt_bytes = static_cast<int>(prompt.size());
    const int required = -llama_tokenize(vocab, prompt.data(), prompt_bytes, nullptr, 0, add_special, true);
    if (required <= 0 || required > 32768) {
        throw std::runtime_error("Prompt exceeds model context. Shorten the question/history or reduce output tokens.");
    }
    std::vector<llama_token> tokens(static_cast<size_t>(required));
    if (llama_tokenize(vocab, prompt.data(), prompt_bytes, tokens.data(), required, add_special, true) != required) {
        throw std::runtime_error("Model tokenizer failed.");
    }
    return tokens;
}

} // namespace

extern "C" JNIEXPORT jlong JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeCreate(JNIEnv * env, jobject) {
    try {
        std::call_once(backend_once, [] {
            llama_log_set(discard_log, nullptr);
            llama_backend_init();
        });
        auto state = std::make_shared<Runtime>();
        std::lock_guard<std::mutex> lock(registry_mutex);
        if (next_handle == std::numeric_limits<jlong>::max()) throw std::runtime_error("Native handle limit reached.");
        const jlong handle = next_handle++;
        runtimes.emplace(handle, std::move(state));
        return handle;
    } catch (const std::exception & failure) {
        throw_java(env, failure);
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeLoad(
        JNIEnv * env, jobject, jlong handle, jbyteArray path, jint context_size, jint threads, jlong memory_budget) {
    try {
        auto state = runtime(handle);
        std::lock_guard<std::mutex> lock(state->operation);
        if (context_size < 256 || context_size > 1024 || threads < 1 || threads > 4 || memory_budget <= 0) {
            throw std::runtime_error("Invalid native resource limits.");
        }
        if (state->model) throw std::runtime_error("Unload the current model before loading another.");
        const auto local_path = bytes(env, path, 4096);
        const auto bounds = coffee::validate_gguf(local_path, state->cancelled);
        state->architecture = bounds.architecture;
        if (bounds.required_working_bytes(static_cast<uint64_t>(context_size)) > static_cast<uint64_t>(memory_budget)) {
            throw std::runtime_error("Model KV cache exceeds available RAM. Choose a smaller model/context.");
        }
        auto params = llama_model_default_params();
        params.n_gpu_layers = 0;
        params.load_mode = LLAMA_LOAD_MODE_MMAP;
        params.lazy_mode = LLAMA_LAZY_MODE_OFF;
        params.check_tensors = true;
        params.use_extra_bufts = false;
        params.progress_callback = load_progress;
        params.progress_callback_user_data = state.get();
        state->model = llama_model_load_from_file(local_path.c_str(), params);
        if (!state->model) throw std::runtime_error("GGUF model load failed or was cancelled. Verify its checksum and compatibility.");
        if (llama_model_has_encoder(state->model) || !llama_model_has_decoder(state->model) ||
            llama_model_n_embd(state->model) != static_cast<int32_t>(bounds.embedding) ||
            llama_model_n_layer(state->model) != static_cast<int32_t>(bounds.layers) ||
            llama_model_n_ctx_train(state->model) < context_size) {
            throw std::runtime_error("Model is incompatible with the bounded decoder runtime.");
        }
        const auto vocab = llama_model_get_vocab(state->model);
        if (!vocab || llama_vocab_n_tokens(vocab) < 1 || llama_vocab_n_tokens(vocab) > 262144) {
            throw std::runtime_error("Model tokenizer is unsupported.");
        }
        state->reserved_pieces.clear();
        for (llama_token token = 0; token < llama_vocab_n_tokens(vocab); ++token) {
            const auto attributes = llama_vocab_get_attr(vocab, token);
            if (!(attributes & (LLAMA_TOKEN_ATTR_CONTROL | LLAMA_TOKEN_ATTR_USER_DEFINED))) continue;
            char fixed[256];
            int size = llama_token_to_piece(vocab, token, fixed, sizeof(fixed), 0, true);
            std::string piece;
            if (size >= 0) {
                piece.assign(fixed, static_cast<size_t>(size));
            } else {
                if (size < -16384) throw std::runtime_error("Unsupported reserved tokenizer token.");
                std::vector<char> extended(static_cast<size_t>(-size));
                const int written = llama_token_to_piece(vocab, token, extended.data(), -size, 0, true);
                if (written != -size) throw std::runtime_error("Reserved tokenizer token could not be read.");
                piece.assign(extended.data(), static_cast<size_t>(written));
            }
            if (!piece.empty()) state->reserved_pieces.push_back(std::move(piece));
            if (state->reserved_pieces.size() > 1024) throw std::runtime_error("Too many reserved tokenizer pieces.");
        }
        size_t reserved_bytes = 0;
        for (const auto & piece : state->reserved_pieces) reserved_bytes += piece.size();
        if (reserved_bytes > 1048576) throw std::runtime_error("Reserved tokenizer spellings exceed resource limits.");
        std::sort(state->reserved_pieces.begin(), state->reserved_pieces.end(), [](const auto & a, const auto & b) {
            return a.size() != b.size() ? a.size() > b.size() : a < b;
        });
        state->reserved_pieces.erase(std::unique(state->reserved_pieces.begin(), state->reserved_pieces.end()), state->reserved_pieces.end());
        if (state->architecture == "gemma4") {
            for (const std::string marker : {"<bos>", "<|turn>", "<turn|>"}) {
                llama_token token = LLAMA_TOKEN_NULL;
                if (llama_tokenize(vocab, marker.data(), static_cast<int32_t>(marker.size()), &token, 1, false, true) != 1 ||
                    !(llama_vocab_get_attr(vocab, token) & (LLAMA_TOKEN_ATTR_CONTROL | LLAMA_TOKEN_ATTR_USER_DEFINED)) ||
                    (marker == "<bos>" && token != llama_vocab_bos(vocab)) ||
                    (marker == "<turn|>" && !llama_vocab_is_eog(vocab, token))) {
                    throw std::runtime_error("Gemma4 tokenizer lacks the required canonical text turn markers.");
                }
            }
            coffee::format_gemma4_text(state->architecture, {{"system", "Coffee assistant."}, {"user", "Hello."}}, state->reserved_pieces);
        } else {
            const auto chat_template = llama_model_chat_template(state->model, nullptr);
            const llama_chat_message probe[] = {{"system", "Coffee assistant."}, {"user", "Hello."}};
            if (!chat_template || llama_chat_apply_template(chat_template, probe, 2, true, nullptr, 0) <= 0) {
                throw std::runtime_error("Model chat template is unsupported. Choose a supported instruction-tuned GGUF.");
            }
        }
        state->context_size = context_size;
        state->threads = threads;
    } catch (const std::exception & failure) {
        throw_java(env, failure);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativePrepare(JNIEnv * env, jobject, jlong handle) {
    try {
        auto state = runtime(handle);
        std::lock_guard<std::mutex> lock(state->operation);
        state->end();
        state->cancelled.store(false);
    } catch (const std::exception & failure) {
        throw_java(env, failure);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeStart(
        JNIEnv * env, jobject, jlong handle, jobjectArray roles, jobjectArray content,
        jint max_tokens, jfloat temperature, jfloat top_p, jint top_k, jint seed) {
    try {
        auto state = runtime(handle);
        std::lock_guard<std::mutex> lock(state->operation);
        if (!state->model || state->context || !roles || !content || max_tokens < 1 || max_tokens > 256 ||
            !std::isfinite(temperature) || temperature < 0 || temperature > 1.5f ||
            !std::isfinite(top_p) || top_p <= 0 || top_p > 1 || top_k < 1 || top_k > 100) {
            throw std::runtime_error("Invalid native generation state or parameters.");
        }
        auto tokens = tokenize_messages(env, *state, roles, content);
        const int required = static_cast<int>(tokens.size());
        if (required + max_tokens > state->context_size) {
            throw std::runtime_error("Prompt exceeds model context. Shorten the question/history or reduce output tokens.");
        }
        if (state->cancelled.load()) return;
        auto params = llama_context_default_params();
        params.n_ctx = static_cast<uint32_t>(state->context_size);
        params.n_batch = 64;
        params.n_ubatch = 64;
        params.n_seq_max = 1;
        params.n_threads = state->threads;
        params.n_threads_batch = state->threads;
        params.flash_attn_type = LLAMA_FLASH_ATTN_TYPE_DISABLED;
        params.offload_kqv = false;
        params.op_offload = false;
        params.no_perf = true;
        params.abort_callback = abort_decode;
        params.abort_callback_data = state.get();
        state->context = llama_init_from_model(state->model, params);
        if (!state->context) throw std::runtime_error("Native context allocation failed. Choose a smaller model/context.");
        auto sampler_params = llama_sampler_chain_default_params();
        sampler_params.no_perf = true;
        state->sampler = llama_sampler_chain_init(sampler_params);
        if (!state->sampler) throw std::runtime_error("Native sampler allocation failed.");
        if (temperature == 0) {
            llama_sampler_chain_add(state->sampler, llama_sampler_init_greedy());
        } else {
            llama_sampler_chain_add(state->sampler, llama_sampler_init_top_k(top_k));
            llama_sampler_chain_add(state->sampler, llama_sampler_init_top_p(top_p, 1));
            llama_sampler_chain_add(state->sampler, llama_sampler_init_temp(temperature));
            llama_sampler_chain_add(state->sampler, llama_sampler_init_dist(static_cast<uint32_t>(seed)));
        }
        for (int offset = 0; offset < required && !state->cancelled.load(); offset += 64) {
            const int size = std::min(64, required - offset);
            check_decode(*state, llama_batch_get_one(tokens.data() + offset, size));
        }
        state->remaining = max_tokens;
    } catch (const std::exception & failure) {
        throw_java(env, failure);
    }
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeNext(JNIEnv * env, jobject, jlong handle) {
    try {
        auto state = runtime(handle);
        std::lock_guard<std::mutex> lock(state->operation);
        if (state->cancelled.load() || state->remaining <= 0) return nullptr;
        if (!state->model || !state->context || !state->sampler) throw std::runtime_error("Native inference was not started.");
        const auto vocab = llama_model_get_vocab(state->model);
        llama_token token = llama_sampler_sample(state->sampler, state->context, -1);
        if (token < 0 || token >= llama_vocab_n_tokens(vocab)) throw std::runtime_error("Model generated an invalid token.");
        if (llama_vocab_is_eog(vocab, token)) {
            state->remaining = 0;
            return nullptr;
        }
        char fixed[256];
        int size = llama_token_to_piece(vocab, token, fixed, sizeof(fixed), 0, false);
        std::vector<char> extended;
        const char * piece = fixed;
        if (size < 0) {
            if (size == std::numeric_limits<int>::min() || -size > 16384) throw std::runtime_error("Generated token exceeds the byte limit.");
            extended.resize(static_cast<size_t>(-size));
            size = llama_token_to_piece(vocab, token, extended.data(), static_cast<int>(extended.size()), 0, false);
            if (size < 0) throw std::runtime_error("Token decoding failed.");
            piece = extended.data();
        }
        --state->remaining;
        if (state->remaining > 0) check_decode(*state, llama_batch_get_one(&token, 1));
        if (state->cancelled.load()) return nullptr;
        const auto output = env->NewByteArray(size);
        if (output && size > 0) env->SetByteArrayRegion(output, 0, size, reinterpret_cast<const jbyte *>(piece));
        return output;
    } catch (const std::exception & failure) {
        throw_java(env, failure);
        return nullptr;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeEnd(JNIEnv * env, jobject, jlong handle) {
    try {
        auto state = runtime(handle);
        std::lock_guard<std::mutex> lock(state->operation);
        state->end();
    } catch (const std::exception & failure) {
        throw_java(env, failure);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeCancel(JNIEnv *, jobject, jlong handle) {
    std::lock_guard<std::mutex> lock(registry_mutex);
    const auto found = runtimes.find(handle);
    if (found != runtimes.end()) found->second->cancelled.store(true);
}

extern "C" JNIEXPORT void JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeRelease(JNIEnv *, jobject, jlong handle) {
    std::shared_ptr<Runtime> removed;
    {
        std::lock_guard<std::mutex> lock(registry_mutex);
        const auto found = runtimes.find(handle);
        if (found == runtimes.end()) return;
        removed = found->second;
        removed->cancelled.store(true);
        runtimes.erase(found);
    }
    // In-flight calls keep shared ownership; destruction cannot race a decode/cancel.
}

extern "C" JNIEXPORT jint JNICALL
Java_com_coffeecreator_app_core_ai_NativeLlmEngine_nativeTokenCount(
        JNIEnv * env, jobject, jlong handle, jobjectArray roles, jobjectArray content) {
    try {
        auto state = runtime(handle);
        std::lock_guard<std::mutex> lock(state->operation);
        return static_cast<jint>(tokenize_messages(env, *state, roles, content).size());
    } catch (const std::exception & failure) {
        throw_java(env, failure);
        return 0;
    }
}
