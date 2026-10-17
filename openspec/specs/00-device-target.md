# OpenSpec: Designated ADB Hardware Target

## Designated Hardware Specifications
- **Hardware Serial / ADB Identifier**: `RRCN3008VYE`
- **Product Name**: `z3sxxx`
- **Model Number**: `SM-G988B` (Samsung Galaxy S20 Ultra 5G)
- **SoC Architecture**: Exynos 990 / ARM64-v8a
- **Android OS Version**: Android 13 (API 33) / One UI 5.1
- **Designated Transport ID**: `16` (dynamic)
- **Display Resolution & Density**: 1440 x 3200 (511 dpi), 120Hz dynamic refresh

## BMAD / OpenSpec Invariant Directive
1. **Mandatory Device Targeting**: All ADB operations (`adb install`, `adb push`, `adb shell`, `connectedDebugAndroidTest`, screen captures, and logcat audits) for this project MUST explicitly specify the target serial flag:
   ```bash
   adb -s RRCN3008VYE <command>
   ```
2. **Multi-Device Isolation**: When multiple ADB devices or emulators are connected simultaneously, scripts and agents MUST NOT execute unflagged commands (`adb install ...`), preventing accidental deployment to secondary devices (e.g. `QSWSEMRKNFZ9LJRC` or WearOS emulators).
3. **Zero-Data-Loss Installation Standard**: Reinstalls on the designated device must preserve app-private Room databases and user documents using:
   ```bash
   adb -s RRCN3008VYE install -r -d coffee-creator-v1.13.0.apk
   ```
4. **Offline Hardware Validation**: Hardware verification on this device must confirm offline operation, haptic feedback engine, BLE scale scanning abstraction, and 48dp touch target accessibility on high-density Samsung One UI display.
