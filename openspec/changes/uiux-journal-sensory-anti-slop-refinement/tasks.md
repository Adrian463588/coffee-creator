# Implementation Tasks: Journal & Sensory Anti-Slop Tactile Navigation

## Phase 1: Specification & Design
- [x] Task 1.1: Identify multi-line FlowRow chip clusters in Journal and Sensory screens
- [x] Task 1.2: Author OpenSpec proposal, design document, and BDD Gherkin scenarios

## Phase 2: Jetpack Compose Minimal-Diff Implementation
- [ ] Task 2.1: Refactor Journal navigation tabs in `CoffeeApp.kt` from `FlowRow` to `Row + horizontalScroll` with `TactileFilterChip`, M3 icons, and 48dp touch targets
- [ ] Task 2.2: Refactor Journal method filter row in `CoffeeApp.kt` from `FlowRow` to `Row + horizontalScroll` with `TactileFilterChip`
- [ ] Task 2.3: Refactor Sensory navigation tabs in `CoffeeApp.kt` from `FlowRow` to `Row + horizontalScroll` with `TactileFilterChip`
- [ ] Task 2.4: Refactor Sensory flavor taxonomy filter row in `CoffeeApp.kt` from `FlowRow` to `Row + horizontalScroll` with `TactileFilterChip`

## Phase 3: Verification & Quality Gates
- [ ] Task 3.1: Run `./gradlew.bat testDebugUnitTest` and ensure 100% test pass (202+ tests)
- [ ] Task 3.2: Assemble APK `./gradlew.bat assembleDebug` and bump version to v1.9.2 (code 11)
- [ ] Task 3.3: Deploy to physical hardware (`Samsung SM-G988B`) via ADB and capture live screenshots (`screencap`)
- [ ] Task 3.4: Verify single-line scroll behavior on Journal and Sensory screens

## Phase 4: Release & Documentation
- [ ] Task 4.1: Update `README.md` with v1.9.2 historical download links and updated release table
- [ ] Task 4.2: Update `SHA256SUMS.txt` with verified cryptographic hash of `coffee-creator-v1.9.2.apk`
- [ ] Task 4.3: Author release notes `docs/releases/v1.9.2.md`
- [ ] Task 4.4: Commit to git, push to GitHub repository, and create GitHub release v1.9.2
