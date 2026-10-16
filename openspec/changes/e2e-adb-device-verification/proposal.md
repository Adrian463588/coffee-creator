# Change Proposal: E2E ADB Device Verification (Samsung Galaxy S20 Ultra)

## Problem Statement
Local JVM Robolectric suites (202 unit tests) validate logical invariants and state machines in headless emulation, but fail to prove physical hardware execution realities:
1. **Choreographer 120Hz Pacing & Frame Drops**: High-refresh-rate Compose re-composition during live chronometer and pulse animations (`LivePulseIndicator`).
2. **Physical Touch Event Latency & Tactile Bounds**: Spring physics (`Modifier.tactilePress(0.96f)`) and minimum 48dp touch targets under OneUI gesture navigation.
3. **Hardware SQLite & Storage Sandbox**: Real flash latency, Room / SQLite index commits, and SAF permission lifetime across process recreation.
4. **Thermal & Memory Footprint**: Exynos 990 ARM64 runtime stability under sustained live telemetry, preventing memory leaks and background ANRs.

## Objectives
1. Formulate exhaustive BDD Gherkin test specification spanning 8 user journeys.
2. Verify all UI components, mathematical equations, tactile controls, and data persistence on physical device `Samsung SM-G988B`.
3. Assert zero uncaught exceptions, zero memory leaks, and zero StrictMode thread violations via ADB Logcat.
4. Execute and pass automated Compose instrumented test suite (`connectedDebugAndroidTest`).

## Target Hardware Specifications
- **Device Model**: Samsung Galaxy S20 Ultra 5G (`SM-G988B / z3s`)
- **Serial / Transport ID**: `RRCN3008VYE` (USB Debugging authorized)
- **Architecture**: ARM64-v8a (64-bit Exynos 990)
- **OS / Platform**: Android 13.0 (API Level 33, OneUI 5.1, Build `TP1A.220624.014`)
- **Display**: 1440x3200 physical, 1080x2400 override (FHD+ 120Hz Dynamic AMOLED), Density 420dpi (xhdpi)
- **App Namespace**: `com.coffeecreator.app` (Test Runner: `com.coffeecreator.app.test`)

## Invariants (Hard Non-Negotiables)
1. **Mathematical Invariant**: Extraction Yield ($EY$) must strictly obey mass balance:
   $$EY = \frac{\text{Beverage Mass (g)} \times \text{TDS (\%)}}{\text{Dose (g)}}$$
   Estimated EY must never masquerade as measured data.
2. **Offline-First Zero-Cloud Rule**: No HTTP outbound requests, cloud APIs, telemetry, or remote inference. Network strictly disabled/mocked.
3. **Monotonic Timing & Thread Safety**: Chronometer must rely on monotonic time (`System.nanoTime()`); zero UI thread blocking during scale polling or calculations.
4. **Data Durability**: Bag mass deductions, recipe revisions, cafe cups, and cupping sessions must persist across activity recreation and app restart.

## Non-Goals
- Remote cloud backup synchronization (prohibited by PRD privacy gate).
- Physical Bluetooth Low Energy peripheral hardware attachment (mock scale adapter verified instead).
- Desktop / Tablet widescreen layout verification (covered in separate multi-window spec).
