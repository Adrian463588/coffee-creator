#include "../gguf_guard.h"
#include <cassert>
#include <cstdio>
#include <iostream>

namespace {

void integer(std::vector<char> & bytes, uint64_t value, size_t width) {
    for (size_t i = 0; i < width; ++i) bytes.push_back(static_cast<char>((value >> (8 * i)) & 255));
}

void text(std::vector<char> & bytes, const std::string & value) {
    integer(bytes, value.size(), 8);
    bytes.insert(bytes.end(), value.begin(), value.end());
}

void string_field(std::vector<char> & bytes, const std::string & key, const std::string & value) {
    text(bytes, key);
    integer(bytes, 8, 4);
    text(bytes, value);
}

void number_field(std::vector<char> & bytes, const std::string & key, uint32_t value) {
    text(bytes, key);
    integer(bytes, 4, 4);
    integer(bytes, value, 4);
}

// Parser fixture only: structurally bounded data, never supplied to model inference.
std::vector<char> fixture(uint32_t embedding = 64, uint32_t tensor_type = 0) {
    std::vector<char> bytes;
    integer(bytes, 0x46554747, 4);
    integer(bytes, 3, 4);
    integer(bytes, 1, 8);
    integer(bytes, 6, 8);
    string_field(bytes, "general.architecture", "qwen2");
    string_field(bytes, "tokenizer.chat_template", "chatml");
    number_field(bytes, "qwen2.embedding_length", embedding);
    number_field(bytes, "qwen2.block_count", 1);
    number_field(bytes, "qwen2.attention.head_count", 1);
    number_field(bytes, "qwen2.attention.head_count_kv", 1);
    text(bytes, "test.weight");
    integer(bytes, 1, 4);
    integer(bytes, 1, 8);
    integer(bytes, tensor_type, 4);
    integer(bytes, 0, 8);
    while (bytes.size() % 32) bytes.push_back(0);
    integer(bytes, 0, 4);
    return bytes;
}

// Dimension metadata matches the verified Gemma4 E2B prefix; this is not inference data.
std::vector<char> gemma_fixture(const std::vector<int64_t> & widths, uint32_t subtype = 4,
                              const std::string & architecture = "gemma4") {
    std::vector<char> bytes;
    integer(bytes, 0x46554747, 4);
    integer(bytes, 3, 4);
    integer(bytes, 1, 8);
    integer(bytes, 9, 8);
    string_field(bytes, "general.architecture", architecture);
    string_field(bytes, "tokenizer.chat_template", "<|turn>");
    number_field(bytes, architecture + ".embedding_length", 1536);
    number_field(bytes, architecture + ".block_count", 35);
    number_field(bytes, architecture + ".attention.head_count", 8);
    number_field(bytes, architecture + ".attention.head_count_kv", 1);
    number_field(bytes, architecture + ".attention.key_length", 512);
    number_field(bytes, architecture + ".attention.value_length", 512);
    text(bytes, architecture + ".feed_forward_length");
    integer(bytes, 9, 4);
    integer(bytes, subtype, 4);
    integer(bytes, widths.size(), 8);
    for (const auto width : widths) integer(bytes, static_cast<uint64_t>(width), 4);
    text(bytes, "test.weight");
    integer(bytes, 1, 4);
    integer(bytes, 1, 8);
    integer(bytes, 0, 4);
    integer(bytes, 0, 8);
    while (bytes.size() % 32) bytes.push_back(0);
    integer(bytes, 0, 4);
    return bytes;
}

void write(const std::string & path, const std::vector<char> & bytes) {
    std::ofstream output(path, std::ios::binary | std::ios::trunc);
    output.write(bytes.data(), static_cast<std::streamsize>(bytes.size()));
    assert(output.good());
}

void rejects(const std::string & path, const std::vector<char> & bytes) {
    write(path, bytes);
    const std::atomic_bool cancelled{false};
    bool failed = false;
    try { coffee::validate_gguf(path, cancelled); }
    catch (const std::runtime_error &) { failed = true; }
    assert(failed);
}

} // namespace

int main(int argc, char ** argv) {
    assert(argc == 2);
    const std::string path = argv[1];
    const std::atomic_bool cancelled{false};
    write(path, fixture());
    const auto accepted = coffee::validate_gguf(path, cancelled);
    assert(accepted.embedding == 64 && accepted.layers == 1);
    assert(accepted.required_working_bytes(1024) > accepted.file_bytes + 384 * coffee::mib);

    auto bytes = fixture();
    bytes[0] = 0;
    rejects(path, bytes);
    bytes = fixture();
    bytes[4] = 1;
    rejects(path, bytes);
    bytes = fixture();
    for (int i = 8; i < 16; ++i) bytes[i] = static_cast<char>(255);
    rejects(path, bytes);
    rejects(path, fixture(65535));
    rejects(path, fixture(64, 42));
    bytes = fixture();
    bytes.pop_back();
    rejects(path, bytes);

    // A hostile string length is refused without allocating it.
    bytes = fixture();
    for (int i = 24; i < 32; ++i) bytes[i] = static_cast<char>(255);
    rejects(path, bytes);

    write(path, fixture());
    const std::atomic_bool stop{true};
    bool stopped = false;
    try { coffee::validate_gguf(path, stop); }
    catch (const std::runtime_error &) { stopped = true; }
    assert(stopped);

    std::vector<int64_t> widths(35, 6144);
    std::fill(widths.begin() + 15, widths.end(), 12288);
    write(path, gemma_fixture(widths));
    const auto gemma = coffee::validate_gguf(path, cancelled);
    assert(gemma.architecture == "gemma4" && gemma.layers == 35 && gemma.embedding == 1536);
    assert(gemma.required_working_bytes(1024) > gemma.file_bytes + 384 * coffee::mib);
    rejects(path, gemma_fixture({}));
    rejects(path, gemma_fixture(std::vector<int64_t>(34, 6144)));
    rejects(path, gemma_fixture(std::vector<int64_t>(65, 6144)));
    rejects(path, gemma_fixture(std::vector<int64_t>(35, 0)));
    rejects(path, gemma_fixture(std::vector<int64_t>(35, 32769)));
    rejects(path, gemma_fixture(std::vector<int64_t>(35, -1), 5));
    rejects(path, gemma_fixture(widths, 6));
    rejects(path, gemma_fixture(widths, 9));
    rejects(path, gemma_fixture(widths, 4, "qwen2"));
    rejects(path, gemma_fixture(widths, 4, "unknown"));
    static_assert(coffee::max_model_bytes == 4ULL * 1024 * coffee::mib, "Native file budget must match Kotlin's 4 GiB cap.");
    assert(std::remove(path.c_str()) == 0);
    std::cout << "GGUF guard: 20 structural/adversarial checks passed.\n";
}
