# OpenSpec Proposal: Epic S2-E04 — Brew Journal, Espresso Dial-In, and Local Analytics

## Objective
Implement a local-first, zero-cloud brew journal, espresso dial-in tracker, side-by-side comparative analysis, single-variable adjustment engine (OVAT), calendar & streak stats, cafe cup diary, caffeine estimator, and Beanconqueror JSON migration.

## Scope (Stories S2-039 through S2-050)
- **S2-039**: Detailed brew journal (dose, water, ratio, grind, temp, time, drawdown, TDS, taste, note, photo URI).
- **S2-040**: Search/filter by bean, method, date range, rating (1–10 / 1–5), tags, equipment.
- **S2-041**: Side-by-side comparison of two brew sessions with deterministic numeric deltas.
- **S2-042**: Espresso target vs actual (dose, yield, time, flow rate) and annotated dial-in history.
- **S2-043**: One-variable-at-a-time (OVAT) adjustment suggestions with extraction physics reasoning.
- **S2-044**: Interactive charts data models and aggregation for weight/time, flow rate, EY, TDS.
- **S2-045**: Calendar, brew streak calculation, and monthly statistics computed locally.
- **S2-046**: Brew template save/reuse and repeat last brew mechanism.
- **S2-047**: CSV/JSON export, full backup and import conflict preview (3-way hash/id comparison).
- **S2-048**: Beanconqueror export importer via documented JSON schema mapping.
- **S2-049**: Cafe cup journal with manual cafe information and photo URIs (zero remote maps).
- **S2-050**: Manual optional caffeine consumption estimate with explicit uncertainty labeling.

## Non-Goals
- No remote telemetry or cloud sync.
- No remote map SDK or GPS tracking for cafe check-ins.
- No fabricated laboratory claims for caffeine or extraction yield.
