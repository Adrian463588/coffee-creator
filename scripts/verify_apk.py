"""Inspect an actual APK; structural evidence is distinct from a device run."""
import argparse
import hashlib
import json
from pathlib import Path
import struct
import sys
import zipfile


def inspect(path: Path) -> dict:
    libraries = []
    with zipfile.ZipFile(path) as archive:
        assert archive.testzip() is None, "APK ZIP integrity failed"
        names = archive.namelist()
        for name in sorted(n for n in names if n.endswith(".so")):
            data = archive.read(name)
            assert data[:4] == b"\x7fELF", f"Not ELF: {name}"
            is64 = data[4] == 2
            endian = "<" if data[5] == 1 else ">"
            offset = struct.unpack_from(endian + ("Q" if is64 else "I"), data, 32 if is64 else 28)[0]
            entry_size, count = struct.unpack_from(endian + "HH", data, 54 if is64 else 42)
            segments = [
                struct.unpack_from(endian + ("QQ" if is64 else "II"), data, offset + i * entry_size + (8 if is64 else 4))
                + (struct.unpack_from(endian + ("Q" if is64 else "I"), data, offset + i * entry_size + (48 if is64 else 28))[0],)
                for i in range(count)
                if struct.unpack_from(endian + "I", data, offset + i * entry_size)[0] == 1
            ]
            assert segments, f"No loadable ELF segments: {name}"
            minimum = min(s[2] for s in segments)
            congruent = all(s[0] % 16384 == s[1] % 16384 for s in segments)
            libraries.append({"path": name, "minimum_load_alignment": minimum, "offsets_congruent_16k": congruent,
                              "aligned_16k": minimum >= 16384 and congruent})
        assert libraries, "APK has no native runtime"
        assert all(n.startswith(("lib/arm64-v8a/", "lib/x86_64/")) for n in names if n.endswith(".so")), "Unexpected ABI"
        assert not any(n.lower().endswith((".gguf", ".jks", ".keystore")) for n in names), "Unexpected model/signing secret"
        return {
            "apk": path.name, "bytes": path.stat().st_size,
            "sha256": hashlib.file_digest(path.open("rb"), "sha256").hexdigest(),
            "zip_integrity": "passed", "libraries": libraries,
            "all_native_libraries_aligned_16k": all(x["aligned_16k"] for x in libraries),
            "device_runtime": "not_proven_by_static_inspection",
        }


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("apk", type=Path)
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    report = inspect(args.apk)
    rendered = json.dumps(report, indent=2) + "\n"
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(rendered, encoding="utf-8")
    print(rendered)
    sys.exit(0 if report["all_native_libraries_aligned_16k"] else 1)
