#pragma once
#include <algorithm>
#include <stdexcept>
#include <string>
#include <vector>

namespace coffee {
// Escape vocabulary control spellings in content BEFORE rendering trusted role delimiters.
inline std::string escape_reserved(const std::string & content, const std::vector<std::string> & pieces) {
    const char hex[] = "0123456789abcdef";
    std::string result;
    for (size_t offset = 0; offset < content.size();) {
        const std::string * match = nullptr;
        for (const auto & piece : pieces) {
            if (!piece.empty() && content.compare(offset, piece.size(), piece) == 0) {
                match = &piece;
                break; // pieces are sorted longest first
            }
        }
        if (match) {
            for (unsigned char value : *match) {
                result += "\\x";
                result += hex[value >> 4];
                result += hex[value & 15];
            }
            offset += match->size();
        } else {
            result += content[offset++];
        }
        if (result.size() > 131072) throw std::runtime_error("Escaped prompt exceeds native input budget.");
    }
    for (const auto & piece : pieces) {
        if (!piece.empty() && result.find(piece) != std::string::npos) {
            throw std::runtime_error("Model reserved tokens cannot be isolated safely.");
        }
    }
    return result;
}
}
