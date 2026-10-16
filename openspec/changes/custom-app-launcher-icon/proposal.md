# OpenSpec Proposal: Custom Precision Coffee Lab App Launcher Icon

## Context & Objectives
Previously, `AndroidManifest.xml` referenced the generic Android system fallback icon `@android:drawable/ic_menu_compass`. This change replaces the fallback with a custom, tailored Android Adaptive Launcher Icon matching the **Precision Coffee Laboratory** theme:
1. **Icon Aesthetics**: Minimalist, tactile industrial coffee emblem combining a stylized coffee bean, precision dripper geometry, and a lab extraction droplet in warm espresso, mocha, and bronze/amber hues.
2. **Android Adaptive Icon Support (`mipmap-anydpi-v26`)**:
   - `ic_launcher.xml` and `ic_launcher_round.xml` referencing vector/raster foreground and background.
   - Separate foreground layer with safe zone padding (66dp / 72dp viewport) preventing launcher clipping.
   - Solid warm dark espresso background `#1B130E`.
3. **Legacy Fallbacks**: Full density suite across `mdpi` (48px), `hdpi` (72px), `xhdpi` (96px), `xxhdpi` (144px), `xxxhdpi` (192px), and circular variants (`ic_launcher_round`).
4. **Manifest Integration**: Point `android:icon` and `android:roundIcon` in `AndroidManifest.xml` to `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.
