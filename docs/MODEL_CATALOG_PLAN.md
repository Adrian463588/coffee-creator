# Additional model catalog slice

User request2026-10-15: add Kafi Llama3.2-1B fine-tune and Gemma4 E2B IT as
explicit downloads into the selected SAF folder. Continue testing without ADB.

- Verify primary Hugging Face metadata/model cards, immutable commit, actual GGUF
  name, LFS SHA256/size, public download and license. Approximate advertised sizes
  do not override real artifact metadata. Do not auto-download weights.
- Keep Qwen as the small existing choice. Register new catalog entries without
  replacing installed/downloaded state or user-imported records.
- Show model scope/limitations, real size, license, required SAF folder and RAM
  admission. Kafi publisher claims are not independent coffee-science validation.
  Gemma text inference must not imply supported image/audio or advertised context.
- Raise the shared bounded file ceiling only as needed for the verified Gemma
  GGUF. Keep storage reserve, byte limits, strong resume identity, SHA verification,
  private working copy and native memory admission. SAF publication must complete
  and be re-read before Installed; never label a catalog entry loaded/ready.
- Tests: three verified unique manifests; exact size/hash/immutable URL; larger
  GGUF accepted within ceiling, over-ceiling and low-RAM rejected; existing
  model state retained; download unavailable without directory; transfer policy
  and final APK/native structure checks. No fake transfer or inference evidence.
- Full multi-GB download/native-model execution remains not run without a device.
  Failures remain recoverable and manual tools remain usable.

Independent read-only researcher verifies metadata/runtime compatibility; root
integrator owns application changes and sequential gates. Rollback a scoped
commit, never delete user SAF weights or rewrite Git history.
