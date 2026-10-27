# Repository description and release documentation plan

Baseline: `beecea5d27df3da212f9948484a39525bfcd212a` on `main`; latest published APK: `v1.14.7`.

## Scope and acceptance

1. Compare README feature, privacy, test and release claims with current source, CI and published assets. Record uncertainty rather than treating historical test reports as current proof.
2. Keep existing releases and historical information accessible. Distinguish the current manual timer from historical BLE-related code and avoid claims of working Bluetooth device support.
3. Describe explicit limits: manually entered weights, OS-dependent reminders, provisional CVA forms, optional local AI and user-selected downloads, and app-private records versus SAF assets/exports.
4. Provide an accurate direct link, exact published SHA-256 and version history, while preserving version/signing and existing releases. A documentation-only update does not require a new APK.
5. Update repository description and topics to match current capabilities. Add a small offline documentation consistency check to the existing CI so version/link drift is caught before merge.
6. Verify source-backed claims, Markdown local references, release asset availability/checksum, check behavior and final GitHub metadata. Report CI/device gaps honestly; no ADB tests are authorized for this task.

## Invariants and rollback

No application behavior, dependency, storage, signing, original worktree, historical release or user data changes. Roll back the documentation commit with a normal revert; restore repository metadata through `gh repo edit` if needed.

## BDD checks and evidence

- **Given** a version label differs from `versionName`, **when** the README check runs, **then** it fails; this checks source version independently of the published release version.
- **Given** a missing relative target or an invalid PNG signature, **when** the README check runs, **then** it fails. It does not prove that external URLs, image pixels, features or claims are valid.
- **Given** a release is prepared, **when** the maintainer updates the README, **then** compare feature claims with routes and tests, confirm each GitHub asset name and digest, and review repository description/topics against the source. Keep older notes explicitly historical.
- The audit found unavailable physical BLE connection, manual inventory deduction, temporary CVA drafts, unsupported LiteRT-LM inference, app-private records, and OS-dependent timer alerts. Source references: `CoffeeViewModelBrewSession.kt`, `CoffeeApp.kt`, `SensorySprint04Screens.kt`, `BuiltInAssets.kt` and `CoffeeViewModelDurableRecords.kt`.
- GitHub API reported 33 releases; all 33 README APK links now match an asset name in the corresponding release. Four old assets are named `app-debug.apk` (v1.3.0, v1.4.0, v1.5.0 and v1.9.4). The latest asset digest and published checksum agree; the full remote APK download did not complete during this audit. A same-size local debug APK has the same digest and an Android Debug certificate.
- CI run [40224043529](https://github.com/Adrian463588/coffee-creator/actions/runs/40224043529) on baseline `beecea5` failed one of 429 tests; later native checks were skipped. No device, instrumentation or full acceptance test was run in this documentation change.
- Local Windows run `./gradlew.bat :app:testDebugUnitTest :app:ktlintCheck :app:detekt :app:lintDebug :app:assembleDebug --continue --no-parallel --max-workers=1 --console=plain` also executed 429 JVM tests, with the same `CompactBrewLocalTest` timeout at line 38 (428 passed, 1 failed). Other task results from this run must be read from the completed Gradle output; they are not inferred from this test result.
- `python scripts/check_readme_consistency.py` passed after the README repair; `python -m unittest discover -s scripts -p test_check_readme_consistency.py` passed six checks. The pre-repair check reported the missing license link and three invalid PNG signatures used by the old README.

## References

- [OpenSpec spec-driven artifacts](https://github.com/Fission-AI/OpenSpec/blob/main/docs-lab/reference/schemas/spec-driven/index.md) and [BMAD methodology](https://github.com/bmad-code-org/BMAD-METHOD): plan, implementation and evidence separated; this change does not claim either CLI ran.
- [GitHub README-writing skill](https://github.com/adewale/good-readme) and [GitHub topics guidance](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/classifying-your-repository-with-topics): source-grounded README and relevant, accurate classification.
- The global `android-compose-anti-slop` skill was already present. Existing screenshots were checked for this README; no application UI was changed and no new device screenshot was claimed.
