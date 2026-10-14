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
    assert(std::remove(path.c_str()) == 0);
    std::cout << "GGUF guard: 9 structural/adversarial checks passed.\n";
}
