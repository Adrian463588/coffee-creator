#pragma once

#include "prompt_guard.h"

#include <algorithm>
#include <array>
#include <atomic>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <fstream>
#include <limits>
#include <set>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

namespace coffee {

constexpr uint64_t mib = 1024ULL * 1024;
constexpr uint64_t max_model_bytes = 4ULL * 1024 * mib;

struct ModelBounds {
    std::string architecture;
    uint64_t file_bytes = 0;
    uint64_t embedding = 0;
    uint64_t layers = 0;
    uint64_t heads = 0;
    uint64_t kv_heads = 0;
    uint64_t key_length = 0;
    uint64_t value_length = 0;

    uint64_t required_working_bytes(uint64_t context) const {
        const uint64_t head_width = embedding / heads;
        const uint64_t keys = key_length ? key_length : head_width;
        const uint64_t values = value_length ? value_length : head_width;
        const uint64_t kv = 2 * context * layers * kv_heads * (keys + values);
        return file_bytes + file_bytes / 4 + std::max(384 * mib, kv + 256 * mib);
    }
};

class GgufReader {
public:
    GgufReader(const std::string & path, const std::atomic_bool & cancelled)
        : input_(path, std::ios::binary), cancelled_(cancelled) {
        if (!input_) throw std::runtime_error("Model file is unreadable.");
        input_.seekg(0, std::ios::end);
        const auto length = input_.tellg();
        if (length < 24 || static_cast<uint64_t>(length) > max_model_bytes) {
            throw std::runtime_error("Model is truncated or exceeds the 4 GiB safety limit.");
        }
        size_ = static_cast<uint64_t>(length);
        input_.seekg(0);
    }

    uint64_t integer(size_t width) {
        require_remaining(width);
        std::array<unsigned char, 8> bytes{};
        input_.read(reinterpret_cast<char *>(bytes.data()), static_cast<std::streamsize>(width));
        if (!input_) throw std::runtime_error("Model header is truncated.");
        position_ += width;
        uint64_t result = 0;
        for (size_t i = 0; i < width; ++i) result |= static_cast<uint64_t>(bytes[i]) << (8 * i);
        return result;
    }

    std::string string(uint64_t limit = mib) {
        const uint64_t length = integer(8);
        if (length > limit) throw std::runtime_error("GGUF string exceeds the safety limit.");
        require_remaining(length);
        std::string result(static_cast<size_t>(length), '\0');
        input_.read(result.data(), static_cast<std::streamsize>(length));
        if (!input_) throw std::runtime_error("Model header is truncated.");
        position_ += length;
        if (result.find('\0') != std::string::npos) throw std::runtime_error("GGUF text contains a null character.");
        return result;
    }

    void skip_string() {
        const uint64_t length = integer(8);
        if (length > mib) throw std::runtime_error("GGUF string exceeds the safety limit.");
        skip(length);
    }

    void skip(uint64_t length) {
        require_remaining(length);
        input_.seekg(static_cast<std::streamoff>(length), std::ios::cur);
        if (!input_) throw std::runtime_error("Model header is truncated.");
        position_ += length;
    }

    uint64_t size() const { return size_; }
    uint64_t position() const { return position_; }

private:
    std::ifstream input_;
    const std::atomic_bool & cancelled_;
    uint64_t size_ = 0;
    uint64_t position_ = 0;

    void require_remaining(uint64_t length) const {
        if (cancelled_.load()) throw std::runtime_error("Model loading cancelled.");
        if (position_ > 64 * mib || length > 64 * mib - position_ || length > size_ - position_) {
            throw std::runtime_error("GGUF metadata exceeds bounded size or file length.");
        }
    }
};

struct MetadataValue {
    bool is_integer = false;
    int64_t integer = 0;
    std::string text;
};

inline MetadataValue read_value(GgufReader & reader, uint32_t type, bool allow_array = true) {
    MetadataValue value;
    switch (type) {
        case 0: case 2: case 4: case 10: {
            const size_t width = type == 0 ? 1 : type == 2 ? 2 : type == 4 ? 4 : 8;
            const uint64_t number = reader.integer(width);
            if (number > static_cast<uint64_t>(std::numeric_limits<int64_t>::max())) {
                throw std::runtime_error("GGUF integer is outside the supported range.");
            }
            value.is_integer = true;
            value.integer = static_cast<int64_t>(number);
            break;
        }
        case 1: case 3: case 5: case 11: {
            const size_t width = type == 1 ? 1 : type == 3 ? 2 : type == 5 ? 4 : 8;
            const uint64_t bits = reader.integer(width);
            value.is_integer = true;
            value.integer = width == 8 ? static_cast<int64_t>(bits) :
                static_cast<int64_t>(bits << (64 - 8 * width)) >> (64 - 8 * width);
            break;
        }
        case 6: {
            const uint32_t bits = static_cast<uint32_t>(reader.integer(4));
            float number;
            std::memcpy(&number, &bits, sizeof(number));
            if (!std::isfinite(number)) throw std::runtime_error("GGUF metadata contains a nonfinite number.");
            break;
        }
        case 7:
            if (reader.integer(1) > 1) throw std::runtime_error("GGUF boolean is invalid.");
            break;
        case 8:
            value.text = reader.string();
            break;
        case 9: {
            if (!allow_array) throw std::runtime_error("Nested GGUF arrays are unsupported.");
            const uint32_t item_type = static_cast<uint32_t>(reader.integer(4));
            const uint64_t count = reader.integer(8);
            if (count > 262144 || item_type > 12 || item_type == 9) {
                throw std::runtime_error("GGUF array exceeds the safety limit.");
            }
            for (uint64_t i = 0; i < count; ++i) {
                if (item_type == 8) reader.skip_string();
                else read_value(reader, item_type, false);
            }
            break;
        }
        case 12: {
            const uint64_t bits = reader.integer(8);
            double number;
            std::memcpy(&number, &bits, sizeof(number));
            if (!std::isfinite(number)) throw std::runtime_error("GGUF metadata contains a nonfinite number.");
            break;
        }
        default: throw std::runtime_error("GGUF metadata type is unsupported.");
    }
    return value;
}

inline bool ends_with(const std::string & text, const std::string & suffix) {
    return text.size() >= suffix.size() && text.compare(text.size() - suffix.size(), suffix.size(), suffix) == 0;
}

inline uint64_t positive_bound(const MetadataValue & value, uint64_t limit) {
    if (!value.is_integer || value.integer < 1 || static_cast<uint64_t>(value.integer) > limit) {
        throw std::runtime_error("Model dimensions exceed the supported small-model limits.");
    }
    return static_cast<uint64_t>(value.integer);
}

// Classic GGML types with fixed, audited block layouts; experimental formats are rejected.
inline std::pair<uint64_t, uint64_t> tensor_layout(uint32_t type) {
    switch (type) {
        case 0: return {1, 4};
        case 1: case 30: return {1, 2};
        case 2: return {32, 18};
        case 3: return {32, 20};
        case 6: return {32, 22};
        case 7: return {32, 24};
        case 8: return {32, 34};
        case 10: return {256, 84};
        case 11: return {256, 110};
        case 12: return {256, 144};
        case 13: return {256, 176};
        case 14: return {256, 210};
        default: throw std::runtime_error("Unsupported GGUF tensor quantization. Use F16, BF16 or classic Q/K formats.");
    }
}

inline ModelBounds validate_gguf(const std::string & path, const std::atomic_bool & cancelled) {
    GgufReader reader(path, cancelled);
    if (reader.integer(4) != 0x46554747) throw std::runtime_error("File is not GGUF.");
    const uint64_t version = reader.integer(4);
    if (version < 2 || version > 3) throw std::runtime_error("Only GGUF versions 2 and 3 are supported.");
    const uint64_t tensors = reader.integer(8);
    const uint64_t metadata = reader.integer(8);
    if (!tensors || !metadata || tensors > 4096 || metadata > 4096) {
        throw std::runtime_error("GGUF header counts exceed the safety limit.");
    }
    ModelBounds bounds;
    bounds.file_bytes = reader.size();
    uint64_t alignment = 32;
    std::string architecture;
    std::string feed_forward_array_key;
    std::vector<uint64_t> feed_forward_widths;
    bool has_template = false;
    std::set<std::string> keys;
    for (uint64_t i = 0; i < metadata; ++i) {
        const std::string key = reader.string(256);
        if (key.empty() || !keys.insert(key).second) throw std::runtime_error("GGUF metadata keys are empty or duplicated.");
        const auto type = static_cast<uint32_t>(reader.integer(4));
        if (ends_with(key, ".feed_forward_length") && type == 9) {
            if (!feed_forward_array_key.empty()) throw std::runtime_error("Duplicate per-layer feed-forward dimensions.");
            feed_forward_array_key = key;
            const auto subtype = static_cast<uint32_t>(reader.integer(4));
            const uint64_t count = reader.integer(8);
            const bool integer_type = subtype <= 5 || subtype == 10 || subtype == 11;
            if (!integer_type || count < 1 || count > 64) {
                throw std::runtime_error("Per-layer feed-forward dimensions must be a bounded integer array.");
            }
            for (uint64_t layer = 0; layer < count; ++layer) {
                feed_forward_widths.push_back(positive_bound(read_value(reader, subtype, false), 32768));
            }
            continue;
        }
        const auto value = read_value(reader, type);
        if (key == "general.architecture") architecture = value.text;
        if (key == "tokenizer.chat_template") has_template = !value.text.empty();
        if (key == "general.alignment") {
            alignment = positive_bound(value, 4096);
            if ((alignment & (alignment - 1)) != 0) throw std::runtime_error("GGUF alignment is invalid.");
        }
        if (key == "split.count" && positive_bound(value, 65535) != 1) {
            throw std::runtime_error("Split GGUF models are unsupported. Import a single-file model.");
        }
        if (ends_with(key, ".embedding_length")) bounds.embedding = positive_bound(value, 4096);
        if (ends_with(key, ".block_count")) bounds.layers = positive_bound(value, 64);
        if (ends_with(key, ".attention.head_count")) bounds.heads = positive_bound(value, 128);
        if (ends_with(key, ".attention.head_count_kv")) bounds.kv_heads = positive_bound(value, 128);
        if (ends_with(key, ".attention.key_length")) bounds.key_length = positive_bound(value, 512);
        if (ends_with(key, ".attention.value_length")) bounds.value_length = positive_bound(value, 512);
        if (ends_with(key, ".feed_forward_length")) positive_bound(value, 32768);
        if (ends_with(key, ".expert_count") && (!value.is_integer || value.integer != 0)) {
            throw std::runtime_error("Mixture-of-experts models are outside this runtime's RAM contract.");
        }
    }
    const std::set<std::string> supported = {"llama", "qwen2", "gemma", "gemma2", "gemma4", "phi3"};
    if (!supported.count(architecture)) throw std::runtime_error("Unsupported architecture. Choose a small dense Llama, Qwen2, Gemma or Phi3 chat model.");
    if (!feed_forward_array_key.empty() &&
        (architecture != "gemma4" || feed_forward_array_key != "gemma4.feed_forward_length" ||
         feed_forward_widths.size() != bounds.layers)) {
        throw std::runtime_error("Per-layer feed-forward dimensions must match Gemma4 architecture and layer count.");
    }
    bounds.architecture = architecture;
    if (!has_template) throw std::runtime_error("Model has no chat template. Choose an instruction-tuned chat GGUF.");
    if (!bounds.kv_heads) bounds.kv_heads = bounds.heads;
    if (!bounds.embedding || !bounds.layers || !bounds.heads || bounds.embedding % bounds.heads ||
        bounds.kv_heads > bounds.heads || bounds.heads % bounds.kv_heads || bounds.embedding / bounds.heads > 512) {
        throw std::runtime_error("Model attention dimensions are invalid or unsupported.");
    }

    std::set<std::string> names;
    std::vector<std::pair<uint64_t, uint64_t>> ranges;
    for (uint64_t i = 0; i < tensors; ++i) {
        const std::string name = reader.string(63);
        if (name.empty() || !names.insert(name).second) throw std::runtime_error("GGUF tensor names are empty or duplicated.");
        const uint64_t dimensions = reader.integer(4);
        if (!dimensions || dimensions > 4) throw std::runtime_error("GGUF tensor rank is invalid.");
        uint64_t elements = 1;
        uint64_t first = 0;
        for (uint64_t d = 0; d < dimensions; ++d) {
            const uint64_t width = reader.integer(8);
            if (!width || width > 262144 || elements > max_model_bytes * 8 / width) {
                throw std::runtime_error("GGUF tensor dimensions exceed the safety limit.");
            }
            if (d == 0) first = width;
            elements *= width;
        }
        const auto layout = tensor_layout(static_cast<uint32_t>(reader.integer(4)));
        if (first % layout.first) throw std::runtime_error("GGUF quantized tensor block size is invalid.");
        const uint64_t bytes = elements / layout.first * layout.second;
        const uint64_t offset = reader.integer(8);
        if (offset > bounds.file_bytes || bytes > bounds.file_bytes - offset || offset % alignment) {
            throw std::runtime_error("GGUF tensor data lies outside the file or is misaligned.");
        }
        ranges.emplace_back(offset, offset + bytes);
    }
    const uint64_t data_start = (reader.position() + alignment - 1) / alignment * alignment;
    if (data_start > bounds.file_bytes) throw std::runtime_error("GGUF tensor data is missing.");
    std::sort(ranges.begin(), ranges.end());
    uint64_t previous_end = 0;
    for (const auto & range : ranges) {
        if (range.first < previous_end || range.second > bounds.file_bytes - data_start) {
            throw std::runtime_error("GGUF tensor data overlaps or is truncated.");
        }
        previous_end = range.second;
    }
    return bounds;
}

// Text-only specialization of Google's pinned Gemma4 template, with tools/thinking disabled.
// Source: google/gemma-4-E2B-it@3e22461f65e89153144f8adb70e3b8c2cc9845a7/chat_template.jinja (Apache2.0).
// Keep role delimiters trusted; all content, including assistant history and system preferences, is escaped first.
inline std::string format_gemma4_text(const std::string & architecture,
                                     const std::vector<std::pair<std::string, std::string>> & messages,
                                     const std::vector<std::string> & reserved_pieces) {
    if (architecture != "gemma4" || messages.size() < 2 || messages.size() > 14 || reserved_pieces.size() > 1024) {
        throw std::runtime_error("Invalid Gemma4 text conversation or tokenizer bounds.");
    }
    size_t reserved_bytes = 0;
    for (const auto & piece : reserved_pieces) {
        if (piece.size() > 16384) throw std::runtime_error("Unsupported reserved tokenizer token.");
        reserved_bytes += piece.size();
        if (reserved_bytes > 1048576) throw std::runtime_error("Reserved tokenizer spellings exceed resource limits.");
    }
    std::vector<std::string> controls = reserved_pieces;
    const std::vector<std::string> fixed_controls = {
        "<bos>", "<eos>", "<|turn>", "<turn|>", "<|channel>", "<channel|>", "<|think|>",
        "<|tool>", "<tool|>", "<|tool_call>", "<tool_call|>", "<|tool_response>", "<tool_response|>"
    };
    controls.insert(controls.end(), fixed_controls.begin(), fixed_controls.end());
    std::sort(controls.begin(), controls.end(), [](const auto & left, const auto & right) {
        return left.size() != right.size() ? left.size() > right.size() : left < right;
    });
    controls.erase(std::unique(controls.begin(), controls.end()), controls.end());
    size_t content_bytes = 0;
    std::string prompt = "<bos>";
    for (size_t index = 0; index < messages.size(); ++index) {
        const auto & role = messages[index].first;
        if ((index == 0 && role != "system") || (index + 1 == messages.size() && role != "user") ||
            (index > 0 && index + 1 < messages.size() && role != "user" && role != "assistant")) {
            throw std::runtime_error("Gemma4 roles violate the system/history boundary.");
        }
        const auto & raw = messages[index].second;
        if (raw.size() > 131072 || raw.find('\0') != std::string::npos) throw std::runtime_error("Invalid Gemma4 message size/text.");
        const auto escaped = escape_reserved(raw, controls);
        content_bytes += escaped.size();
        if (content_bytes > 131072) throw std::runtime_error("Prompt exceeds the native byte limit.");
        const auto first = escaped.find_first_not_of(" \t\n\r\f\v");
        if (first == std::string::npos) throw std::runtime_error("Gemma4 messages must contain text.");
        const auto last = escaped.find_last_not_of(" \t\n\r\f\v");
        const bool continuation = role == "assistant" && index > 0 && messages[index - 1].first == "assistant";
        if (!continuation) prompt += "<|turn>" + (role == "assistant" ? std::string("model") : role) + "\n";
        prompt += escaped.substr(first, last - first + 1);
        const bool continues_next = role == "assistant" && index + 1 < messages.size() && messages[index + 1].first == "assistant";
        if (!continues_next) prompt += "<turn|>\n";
        if (prompt.size() > 262144) throw std::runtime_error("Gemma4 prompt exceeds the formatted byte limit.");
    }
    prompt += "<|turn>model\n";
    return prompt;
}

} // namespace coffee
