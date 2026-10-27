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
