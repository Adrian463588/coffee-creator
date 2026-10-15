# Connected-device reinstall and acceptance plan

User request on 2026-10-15 resumes actual ADB testing, superseding the earlier
instruction to test without a device. Target: Samsung SM-G988B, Android13/API33,
arm64-v8a, owner/user0. Integrator alone controls ADB and Gradle; independent
subagent reviews test resource ownership without accessing the device.

1. Verify the public v1.1.0 APK checksum/signature and current package/user.
   Record original network/display settings. Privately snapshot durable app
   records/preferences into ignored local artifacts before replacement.
2. Use `adb install --user 0 -r`, never uninstall or clear application data.
   Verify the installed APK bytes/version and unchanged durable records/grants.
3. Install the matching instrumentation APK. Verify actual Qwen weights and
   dedicated QA SAF grant; tests use UUID databases and exact QA source ownership.
   Do not use a user's model folder for SAF tests that remove newly created files.
4. Run repository/migration/OCR, actual ONNX/native/coordinator,100-question RAG
   evaluation and Compose journeys sequentially. Stop each failed journey, retain
   logs and diagnose root cause before fixes/retests. No fake executors or metrics.
5. Run SAF import/integrity/recovery only in the verified idle QA directory.
   Capture real screenshots, model catalog/SAF actions and crash/ANR evidence.
6. If prerequisites permit, execute signed benchmark/Baseline Profile APKs, then
   replace-install the verified distribution debug APK again. Benchmark builds
   and benchmark execution remain separate facts.
7. Restore original network/display configuration, prior draft and model-folder
   selection. Delete only exact generated QA resources. Preserve user records,
   model bundles and Git history. Keep raw snapshots/logs private; publish only
   aggregate evidence and non-sensitive application screenshots.
8. Update requirement checklist with exact pass/fail counts. Fix actual regressions
   with BDD checks; rerun applicable gates and publish a new patch version only if
   application code must change. Do not overwrite historical release assets.

Uninstall/reinstall with private-data loss, multiple SAF providers/RAM tiers,
human answer faithfulness and barista field research are separate acceptance
layers, not inferred from this one device run.

Observed regression before edits: UI suite6/7; immediate test dispatcher Back
finished the activity before Navigation Compose's predictive-back enabled effect
settled. Real system Back after the visible Brew transition returned Home.
Pinned Compose Test1.7.3 documents `runOnIdle` for assertions/actions after idle;
Navigation Compose2.8.3 enables its handler after collecting back-stack state.
Change only the test: wait for visible Brew and run Back after idle, asserting a
registered enabled callback. Preserve application navigation and release bytes.
