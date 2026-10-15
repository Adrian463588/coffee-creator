# OpenSpec Design: Epic S2-E03 — Active Brew Timer, BLE and Live Feedback

## 1. Architectural Invariants
1. **Pure Kotlin Engine**: Core timing, rolling regression, artifact filtering, and proximity calculation are pure Kotlin with zero Android/Compose dependencies.
2. **Background-Safe Monotonic Chronometer**: Session duration binds to monotonic hardware uptime (`SystemClock.elapsedRealtime()`) and cumulative state, completely immune to wall-clock skew.
3. **Hardware Isolation**: Scale communication interfaces (`BleScaleDriver`, `BleScaleManager`) are abstracted to support physical Bluetooth GATT scales and deterministic `SimulatedBleScaleDriver` for offline unit testing and hardware-less environments.
4. **Zero Silent Data Loss**: Completed brews automatically write into Room-backed local Journal. Sessions snapshot state so crashes or background eviction can be recovered.

## 2. Mathematical Models
- **Rolling Flow Rate**: Ordinary Least Squares linear regression slope over sliding 0.6–1.2s window ($N \approx 6\text{–}10$ samples):
  $$\text{FlowRate}(t) = \frac{\sum (t_i - \bar{t})(w_i - \bar{w})}{\sum (t_i - \bar{t})^2} \quad [\text{g/s}]$$
- **Artifact Filter**:
  - Lift-off / removal: $\Delta w < -5.0\text{g}$ in $<250\text{ms} \implies$ hold previous weight, flag `VESSEL_REMOVAL`.
  - Agitation / swirl: $|\dot{w}| > 25.0\text{g/s} \implies$ median filter, flag `AGITATION_ARTIFACT`.
  - Resettle: $|\dot{w}| < 0.3\text{g/s}$ for $>400\text{ms} \implies$ clear hold.
- **Proximity Indicator**:
  - $>10\text{g}$ remaining: `NORMAL`
  - $2\text{–}10\text{g}$ remaining: `APPROACHING_10G`
  - $0\text{–}2\text{g}$ remaining: `IMMINENT_2G`
  - $|\Delta w| \le 0.5\text{g}$: `TARGET_MATCHED`
  - $\Delta w < -1.0\text{g}$: `OVERSHOOT`
- **Curve Alignment**:
  - Piecewise ideal ramp connecting step targets.
  - Root Mean Square Error (RMSE) against resampled live points.
