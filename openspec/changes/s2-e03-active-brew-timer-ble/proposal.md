# OpenSpec Proposal: Epic S2-E03 — Active Brew Timer, BLE and Live Feedback

## Summary
Epic S2-E03 provides a professional active brewing engine with background-safe monotonic chronometry, multi-step pour progression, Bluetooth LE scale abstraction with live weight and flow rate ($g/s$), swirl/lift artifact rejection, weight-triggered auto-start, espresso flow auto-stop, proximity haptics, and zero-cloud journal auto-logging.

## Scope
- S2-027: Accessible large countdown/elapsed timer with monotonic clock (`elapsedRealtime`)
- S2-028: Step-by-step guidance (current/next pour, cumulative target, action instructions)
- S2-029: Pause/resume/skip/restart and interrupted-session recovery
- S2-030: Haptic/audio cues, screen-awake opt-in, notification controls
- S2-031: Automatic completed-session logging into Journal with subsequent manual edit support
- S2-032: Bluetooth LE device abstraction, scanning/pairing lifecycle, and mock scale provider for JVM tests
- S2-033: Real-time weight & rolling flow rate ($g/s$) via linear regression, time-series logging
- S2-034: Tare command, weight-triggered timer start ($>0.5$g), automatic espresso flow-stop ($<0.2$g/s)
- S2-035: Transient scale artifact filter (swirl/stir/vessel removal detection)
- S2-036: Target-weight proximity visual & haptic indicator (approaching, imminent, matched, overshoot)
- S2-037: On-device brew curve reference & comparison overlays (ideal curve vs actual curve RMSE)
- S2-038: Android ongoing notification contract for active brewing
