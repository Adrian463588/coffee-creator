# OpenSpec Tasks: Custom App Launcher Icon & Adaptive Android Spec

- [x] **Task 1: Generate Coffee Creator Icon Artwork via Subagent**
  - Prompt `image-generator` subagent for high-resolution 1024x1024 master icon featuring minimalist coffee laboratory emblem (stylized coffee bean + dripper flask + golden extraction drop, dark warm espresso background).
  - Extract and separate foreground and background layers.

- [x] **Task 2: Generate Android Icon Density Buckets**
  - Create `mipmap-mdpi` (48x48), `mipmap-hdpi` (72x72), `mipmap-xhdpi` (96x96), `mipmap-xxhdpi` (144x144), and `mipmap-xxxhdpi` (192x192) for `ic_launcher.png` and `ic_launcher_round.png`.
  - Create `mipmap-anydpi-v26/ic_launcher.xml` and `mipmap-anydpi-v26/ic_launcher_round.xml` with adaptive icon `<adaptive-icon>` specification.
  - Create `values/ic_launcher_background.xml` (#1B120C) and `ic_launcher_foreground.png` density buckets (108x108 up to 432x432).

- [x] **Task 3: Update AndroidManifest.xml**
  - Replace `@android:drawable/ic_menu_compass` with `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.

- [x] **Task 4: Quality Gates & Verification**
  - Verify all 210 JVM unit tests pass 100% (`./gradlew.bat testDebugUnitTest`).
  - Verify build compilation (`./gradlew.bat assembleDebug`).
  - Verify on connected physical ADB device (`Samsung Galaxy S20 Ultra 5G`, `SM-G988B`).

- [x] **Task 5: Version Bump, Git Commit, Push & GitHub Release**
  - Bump to `v1.10.1` (`versionCode = 17`, `versionName = "1.10.1"`).
  - Update `README.md`, `SHA256SUMS.txt`, and release notes `docs/releases/v1.10.1.md`.
  - Commit with conventional commit, push to `origin/main`, and publish GitHub Release `v1.10.1`.
