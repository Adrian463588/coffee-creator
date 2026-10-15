"""Fetch immutable, checksum-verified build/test assets. Never used by app runtime."""
import argparse
import hashlib
import json
from pathlib import Path
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
ASSETS = [
    ("embedding/model_quantized.onnx", "https://huggingface.co/Xenova/all-MiniLM-L6-v2/resolve/751bff37182d3f1213fa05d7196b954e230abad9/onnx/model_quantized.onnx", "afdb6f1a0e45b715d0bb9b11772f032c399babd23bfc31fed1c170afc848bdb1", 22972370),
    ("embedding/vocab.txt", "https://huggingface.co/Xenova/all-MiniLM-L6-v2/resolve/751bff37182d3f1213fa05d7196b954e230abad9/vocab.txt", "07eced375cec144d27c900241f3e339478dec958f92fddbc551f295c992038a3", 231508),
    ("ocr/eng.traineddata", "https://raw.githubusercontent.com/tesseract-ocr/tessdata_fast/87416418657359cb625c412a48b6e1d6d41c29bd/eng.traineddata", "7d4322bd2a7749724879683fc3912cb542f19906c83bcc1a52132556427170b2", 4113088),
]
MODEL = ("qwen2.5-0.5b-instruct-q4_k_m.gguf", "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/9217f5db79a29953eb74d5343926648285ec7e67/qwen2.5-0.5b-instruct-q4_k_m.gguf", "74a4da8c9fdbcd15bd1f6d01d621410d31c6fc00986f5eb687824e7b93d7a9db", 491400032)

def sha256(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()

def fetch(item, base):
    name, url, expected_hash, expected_size = item
    target = base / name
    target.parent.mkdir(parents=True, exist_ok=True)
    if not target.exists() or (expected_hash and sha256(target) != expected_hash):
        staging = target.with_suffix(target.suffix + ".part")
        request = urllib.request.Request(url, headers={"User-Agent": "CoffeeCreator-build/1.1.0"})
        with urllib.request.urlopen(request, timeout=60) as source, staging.open("wb") as output:
            while chunk := source.read(1024 * 1024):
                output.write(chunk)
        if expected_size and staging.stat().st_size != expected_size:
            raise ValueError(f"Size mismatch: {name}")
        if expected_hash and sha256(staging) != expected_hash:
            raise ValueError(f"SHA-256 mismatch: {name}")
        staging.replace(target)
    digest = sha256(target)
    print(f"Verified {name}: {target.stat().st_size} bytes, SHA-256 {digest}", flush=True)
    return {"path": name, "url": url, "size": target.stat().st_size, "sha256": digest}

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--test-model", action="store_true")
    args = parser.parse_args()
    receipts = [fetch(item, ROOT / "app/src/main/assets") for item in ASSETS]
    (ROOT / "app/src/main/assets/bundled-assets.json").write_text(json.dumps(receipts, indent=2) + "\n", encoding="utf-8")
    if args.test_model:
        fetch(MODEL, ROOT / "models")
