#!/usr/bin/env python3
"""Build a source-indexed, explicitly unverified requirement matrix from the blueprint.

This utility records traceability leads only. It does not infer runtime PASS from
source/test presence; test execution status remains NOT_RUN until an audit run writes
an execution receipt.
"""

from __future__ import annotations

import csv
import re
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
BLUEPRINT = ROOT / "coffee-creator-blueprint"
QA = ROOT / "docs" / "qa"

SPEC_FILES = {
    "FR": BLUEPRINT / "PRD.md",
    "S2": BLUEPRINT / "SPRINT_02_COMPETITIVE_FEATURE_IMPLEMENTATION.md",
    "S3": BLUEPRINT / "SPRINT_03_MANUAL_BREW_GRINDER_INTELLIGENCE.md",
    "S4": BLUEPRINT / "SPRINT_04_PROFESSIONAL_SENSORY_CUPPING_CVA.md",
}

# Historic shipped source paths removed during consolidation/refinement. These
# rows are preservation checks, not new blueprint requirements.
LEGACY_FEATURES = [
    ("LEGACY-01", "Home screen", "Home", "app/src/main/java/com/coffeecreator/app/ui/screens/HomeScreen.kt",
     "Current Home destination remains reachable and preserves the primary actions documented by the old screen; verify parity on a disposable profile."),
    ("LEGACY-02", "Recipe builder screen", "Formulate", "app/src/main/java/com/coffeecreator/app/ui/screens/RecipeBuilderScreen.kt",
     "Current Formulate destination preserves manual recipe creation, validation and saved-recipe navigation from the old screen."),
    ("LEGACY-03", "AI chat screen", "AI Chat", "app/src/main/java/com/coffeecreator/app/ui/screens/AiChatScreen.kt",
     "Current AI Chat destination preserves local-model status, conversation and manual recovery behavior from the old screen."),
    ("LEGACY-04", "Coffee library screen", "Library", "app/src/main/java/com/coffeecreator/app/ui/screens/CoffeeLibraryScreen.kt",
     "Current Library destination preserves catalog browse/search/edit behavior and user-owned entries from the old screen."),
    ("LEGACY-05", "Brew journal screen", "Journal", "app/src/main/java/com/coffeecreator/app/ui/screens/BrewJournalScreen.kt",
     "Current Journal destination preserves saved brew history, search and record links from the old screen."),
    ("LEGACY-06", "Knowledge library screen", "Knowledge", "app/src/main/java/com/coffeecreator/app/ui/screens/KnowledgeLibraryScreen.kt",
     "Current Knowledge destination preserves local document import, indexing, citation and revoked-grant recovery behavior."),
    ("LEGACY-07", "Model manager screen", "Models", "app/src/main/java/com/coffeecreator/app/ui/screens/ModelManagerScreen.kt",
     "Current Models destination preserves user-requested asset selection, verification, progress and recovery behavior."),
    ("LEGACY-08", "Sensory screen", "Sensory", "app/src/main/java/com/coffeecreator/app/ui/screens/SensoryScreen.kt",
     "Current Sensory destination preserves legacy wheel, observation and cupping entry points without changing saved descriptor IDs."),
    ("LEGACY-09", "Settings screen", "Settings", "app/src/main/java/com/coffeecreator/app/ui/screens/SettingsScreen.kt",
     "Current Settings destination preserves privacy, accessibility, storage, export and restore controls."),
    ("LEGACY-10", "Curated recipe corpus and stable records", "Library / Formulate",
     "app/src/main/java/com/coffeecreator/app/core/data/CuratedCoffeeCatalog.kt; app/src/main/java/com/coffeecreator/app/core/data/SeedData.kt; app/src/main/java/com/coffeecreator/app/core/recipes/champion/WorldChampionRecipeCatalog.kt",
     "Reconcile every v1.9.5 recipe ID, step and source with current catalogs and historical imports; retain verified recipe content, mark unsupported measurements unknown, and do not inject synthetic test fixtures into production."),
]

# Release/delivery conditions explicitly requested by the user, tracked apart
# from blueprint IDs and historical app-feature preservation rows.
DELIVERY_REQUIREMENTS = [
    ("DELIVERY-01", "Latest APK download metadata", "P1",
     "The latest release download points to the canonical repository and reports the actual uploaded asset size and SHA-256."),
    ("DELIVERY-02", "Published release claims match tagged source", "P0",
     "Published release behavior, test, measurement, and device claims are supported by the exact tagged source and commit-bound evidence."),
]

SOURCE_MAP = {
    "FR-01": "app/src/main/java/com/coffeecreator/app/core/catalog/CatalogModels.kt; CoffeeViewModelCatalog.kt",
    "FR-02": "app/src/main/java/com/coffeecreator/app/core/model/Models.kt; RecipeDraft.kt; CoffeeViewModelRecipe.kt",
    "FR-03": "app/src/main/java/com/coffeecreator/app/core/engine/CoffeeScienceEngine.kt",
    "FR-04": "app/src/main/java/com/coffeecreator/app/core/engine/SensoryRulesEngine.kt; CoffeeViewModelCupping.kt",
    "FR-05": "app/src/main/java/com/coffeecreator/app/core/ai/LocalLlmCoordinator.kt; CoffeeViewModelChat.kt",
    "FR-06": "app/src/main/java/com/coffeecreator/app/core/ai/HybridRagEngine.kt; DataRepository.kt",
    "FR-07": "app/src/main/java/com/coffeecreator/app/core/ai/ToolExecutor.kt",
    "FR-08": "app/src/main/java/com/coffeecreator/app/core/filesystem/AssetManager.kt; AssetDownloadWorker.kt",
    "FR-09": "app/src/main/java/com/coffeecreator/app/core/data/BackupCodec.kt; DataRepository.kt",
    "FR-10": "app/src/main/java/com/coffeecreator/app/core/model/CoffeeSettings.kt; ui/screens/CoffeeApp.kt",
}

TEST_ROOTS = [ROOT / "app" / "src" / name for name in ("test", "androidTest", "sharedTest")]
TEST_FILES = [path for base in TEST_ROOTS if base.exists() for path in base.rglob("*.kt")]
TEST_LINES = {path: path.read_text(encoding="utf-8", errors="replace").splitlines() for path in TEST_FILES}


def source_for(req_id: str) -> tuple[str, str, str, str]:
    legacy = next((item for item in LEGACY_FEATURES if item[0] == req_id), None)
    if legacy:
        _, _, route, historical_path, _ = legacy
        current_path = "app/src/main/java/com/coffeecreator/app/ui/screens/CoffeeApp.kt"
        if req_id == "LEGACY-10":
            return historical_path, route, "Recipe records in StoredRecord JSON; current bundled champion templates are static", "No disposable profile or historical record snapshot available"
        return f"Historical: {historical_path}; current candidate: {current_path}", route, "Navigation/action state; verify any linked StoredRecord IDs", "No disposable profile; route parity not run"
    if any(item[0] == req_id for item in DELIVERY_REQUIREMENTS):
        return (
            "docs/releases/v1.14.5.md; README.md; GitHub release metadata and tagged release notes/source",
            "GitHub Releases / README download history",
            "Published APK asset metadata and release documentation; no app database",
            "Network metadata lookup required; no app/device dependency",
        )
    if req_id in SOURCE_MAP:
        return SOURCE_MAP[req_id], "Home → primary destination (see route inventory)", "StoredRecord JSON / domain record; verify exact linkage", "No disposable profile; persistence not observed"
    prefix, number_text = req_id.split("-")
    number = int(number_text)
    if prefix == "S2":
        if number <= 12:
            source = "app/src/main/java/com/coffeecreator/app/core/catalog/CatalogModels.kt; CoffeeViewModelCatalog.kt; ui/screens/CoffeeApp.kt"
            route = "Library / Brew / Journal (exact screen action per row not independently traced)"
            storage = "Catalog-domain records in StoredRecord JSON; exact row link not captured"
        elif number <= 26:
            source = "app/src/main/java/com/coffeecreator/app/core/recipe/RecipeComposerEngine.kt; core/engine/CoffeeScienceEngine.kt; CoffeeViewModelRecipe.kt"
            route = "Formulate / Brew"
            storage = "Recipe records in StoredRecord JSON"
        elif number <= 38:
            source = "app/src/main/java/com/coffeecreator/app/ui/viewmodel/CoffeeViewModelBrewSession.kt; core/brew/BrewTimerEngine.kt; ui/screens/CoffeeApp.kt"
            route = "Brew"
            storage = "Brew-session records; process-death persistence linkage requires verification"
        elif number <= 50:
            source = "app/src/main/java/com/coffeecreator/app/ui/viewmodel/CoffeeViewModelJournal.kt; core/model/Models.kt"
            route = "Journal"
            storage = "Brew journal records in StoredRecord JSON"
        elif number <= 62:
            source = "app/src/main/java/com/coffeecreator/app/core/catalog/FlavorWheelTaxonomy.kt; CoffeeViewModelCupping.kt; ui/screens/SensoryWheelVisualizer.kt"
            route = "Sensory"
            storage = "Sensory observations / cupping records; per-descriptor source attribution not present in FlavorDescriptor"
        elif number <= 74:
            source = "app/src/main/java/com/coffeecreator/app/core/ai/HybridRagEngine.kt; ToolExecutor.kt; CoffeeViewModelChat.kt; CoffeeViewModelCupping.kt"
            route = "AI Chat / Formulate / Sensory"
            storage = "Chat, documents, profiles and recipe records in local Room/asset stores"
        else:
            source = "app/src/main/java/com/coffeecreator/app/core/filesystem/AssetManager.kt; AssetDownloadWorker.kt; core/data/BackupCodec.kt; DataRepository.kt"
            route = "Models / Settings / app-wide recovery"
            storage = "User-owned SAF assets plus Room StoredRecord/StoredChunk records"
        return source, route, storage, "No feature-specific physical acceptance recorded in this audit"
    if prefix == "GR":
        return (
            "app/src/main/java/com/coffeecreator/app/core/catalog/GrinderIntelligenceModels.kt; ui/viewmodel/CoffeeViewModelCatalog.kt",
            "Library / Brew",
            "GrinderProfile and brew-session JSON records; persistence per flow not verified",
            "No compatible physical grinder measurement/device test recorded",
        )
    if prefix == "BR":
        return (
            "app/src/main/java/com/coffeecreator/app/core/catalog/DripperIntelligenceModels.kt; core/brew/BrewTimerEngine.kt; ui/viewmodel/CoffeeViewModelBrewSession.kt; ui/screens/CoffeeApp.kt",
            "Brew / Journal / Sensory",
            "Recipe and brew-session records; recovery/persistence per flow not verified",
            "BLE only for BR-07/16/18-adjacent cases; no compatible scale connected for acceptance",
        )
    if prefix == "S4":
        if number <= 10:
            source = "app/src/main/java/com/coffeecreator/app/core/sensory/academy/AcademyLessons.kt; AcademyModels.kt; ui/screens/SensorySprint04Screens.kt"
            storage = "Static lesson corpus; progress persistence not linked from this section"
        elif number <= 26:
            source = "app/src/main/java/com/coffeecreator/app/core/sensory/cva/CvaModels.kt; CvaServices.kt; ui/screens/SensorySprint04Screens.kt"
            storage = "CVA domain models exist; Sprint 04 screen is not linked to durable ViewModel/repository records"
        elif number <= 40:
            source = "app/src/main/java/com/coffeecreator/app/core/sensory/training/TriangleStatisticsEngine.kt; ui/screens/SensorySprint04Screens.kt"
            storage = "Training domain models/engines; screen persistence linkage not verified"
        elif number <= 51:
            source = "app/src/main/java/com/coffeecreator/app/core/sensory/greengrading/GreenGradingModels.kt; ui/screens/SensorySprint04Screens.kt"
            storage = "Green grading domain records; screen currently holds values in local Compose state"
        else:
            source = "app/src/main/java/com/coffeecreator/app/ui/screens/SensorySprint04Screens.kt; core/sensory/cva/CvaServices.kt"
            storage = "No durable panel/operation record link found from the section"
        return source, "Sensory", storage, "Physical sensory / instrument tests require actual user-provided samples and gear"
    if prefix == "EDU":
        return (
            "app/src/main/java/com/coffeecreator/app/core/sensory/academy/AcademyLessons.kt; AcademyModels.kt; ui/screens/SensorySprint04Screens.kt",
            "Sensory → Learn",
            "Bilingual bundled lesson content; mastery evidence persistence not verified",
            "Human sensory exercises require user-owned physical references; not run",
        )
    raise ValueError(f"Unknown requirement {req_id}")


def parse_specs() -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    epic = ""
    for family, path in SPEC_FILES.items():
        for line_number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
            heading = re.match(r"^#{2,4}\s+(EPIC\s+[^—-]+|Epic\s+S2-E\d+[^—-]*|Epic\s+S2-E\d+.*)", line, re.I)
            if heading:
                epic = heading.group(1).strip()
            if family == "FR":
                match = re.match(r"^###\s+(FR-\d+)\s+(.+)$", line)
                if not match:
                    continue
                req_id, full_title = match.groups()
                priority_match = re.search(r"\((P\d[^)]*)\)$", full_title)
                priority = priority_match.group(1) if priority_match else "Unspecified"
                title = full_title[: priority_match.start()].strip() if priority_match else full_title.strip()
                spec_lines = path.read_text(encoding="utf-8").splitlines()
                end = next((i for i in range(line_number, len(spec_lines)) if spec_lines[i].startswith("### ")), len(spec_lines))
                block = spec_lines[line_number:end]
                acceptance = next((re.sub(r"^\*\*Acceptance:\*\*\s*", "", candidate).strip()
                                   for candidate in block if candidate.startswith("**Acceptance:**")), title)
                rows.append({"id": req_id, "sprint": "Sprint 01 / PRD", "epic": "PRD functional requirements", "feature": title,
                             "acceptance": acceptance, "priority": priority,
                             "spec": f"coffee-creator-blueprint/PRD.md:{line_number}"})
                continue
            if not line.startswith("|"):
                continue
            cells = [cell.strip() for cell in line.strip().strip("|").split("|")]
            if not cells:
                continue
            req_match = re.fullmatch(r"(S2-\d{3}|GR-\d{2}|BR-\d{2}|S4-\d{3}|EDU-\d{2})", cells[0])
            if not req_match:
                continue
            req_id = cells[0]
            if family == "S2":
                feature = cells[1]
                priority = cells[2]
                acceptance = cells[1]
                sprint = "Sprint 02"
            elif family == "S3":
                feature = cells[1]
                acceptance = cells[2] if len(cells) >= 4 else feature
                priority = cells[-1]
                sprint = "Sprint 03"
            else:
                sprint = "Sprint 04"
                if req_id.startswith("EDU-"):
                    feature = cells[1] + (f"; Learning outcome: {cells[2]}" if len(cells) > 2 else "")
                    acceptance = cells[2] if len(cells) > 2 else feature
                    priority = "P1 (educational content)"
                else:
                    priority = cells[1]
                    feature = cells[2]
                    acceptance = cells[2]
            epic_for_row = epic or ("PRD functional requirements" if family == "FR" else "Epic to verify")
            rows.append({"id": req_id, "sprint": sprint, "epic": epic_for_row, "feature": feature,
                         "acceptance": acceptance, "priority": priority,
                         "spec": f"coffee-creator-blueprint/{path.name}:{line_number}"})
    return rows


def test_evidence(req_id: str) -> tuple[str, str]:
    matching: list[str] = []
    layers: set[str] = set()
    pattern = re.compile(rf"(?<![A-Z0-9]){re.escape(req_id)}(?![A-Z0-9])")
    for path, lines in TEST_LINES.items():
        if any(pattern.search(line) for line in lines):
            matching.append(path.relative_to(ROOT).as_posix())
            relative = path.relative_to(ROOT / "app" / "src").parts[0]
            layers.add({"test": "JVM", "sharedTest": "Shared/JVM + Android", "androidTest": "Instrumented Android"}.get(relative, relative))
    return ("; ".join(matching) if matching else "No explicit ID marker found; inspect suite semantics", "; ".join(sorted(layers)) if layers else "None located")


def findings(req_id: str) -> tuple[str, str, str, str]:
    verdict = "IMPLEMENTED_UNVERIFIED"
    defect = ""
    actual = "Source/test candidate located; user journey and acceptance dimensions not executed in this audit."
    owner = "Unassigned"
    legacy = next((item for item in LEGACY_FEATURES if item[0] == req_id), None)
    if req_id == "DELIVERY-01":
        verdict, defect = "PARTIAL", "D-014"
        actual = "Current GitHub asset metadata is valid, but the checked-in v1.14.5 release note reports a different size and names a non-canonical repository owner in its download command."
    elif req_id == "DELIVERY-02":
        verdict, defect = "BROKEN", "D-015"
        actual = "Public v1.14.3/v1.14.4 behavior claims conflict with the implementation at those exact tags; no commit-bound device receipts support the claims."
    elif legacy and req_id == "LEGACY-10":
        verdict, defect = "SPEC_CONFLICT", "D-013"
        actual = "v1.9.5 production catalog and its test were removed in v1.9.6; recipe-shaped data survives only as a test fixture there, while current SeedData.sampleRecipes is empty and the separate 80-entry champion catalog is not reconciled by stable old IDs. Provenance and no-fabrication rules conflict with blindly restoring its historical measured values."
    elif legacy:
        verdict = "IMPLEMENTED_UNVERIFIED"
        actual = "The corresponding current destination is present in the unified navigation source; historical behavior parity and state/data preservation were not exercised on a disposable profile."
    elif req_id in {"S4-023", "S4-024", "S4-050", "S4-060"}:
        verdict, defect = "BROKEN", "D-002"
        actual = "Reachable action reports lock/export/seal success without locking the persisted record or publishing the requested export; see feature inventory evidence."
        if req_id in {"S4-023", "S4-024", "S4-050"}:
            defect += "; D-015"
    elif req_id in {"S4-045", "S4-046", "S4-051", "S4-057"}:
        verdict, defect = "BROKEN", "D-001"
        actual = "UI initializes measurements, panel scores, or citation-like evidence as prefilled hardcoded values; no user observation/source record is present."
        if req_id in {"S4-045", "S4-046"}:
            defect += "; D-015"
    elif req_id == "S4-065":
        verdict, defect = "BROKEN", "D-011"
        actual = "The sharing-consent state initializes true, so an export can appear consented before the user gives affirmative consent."
    elif req_id in {"S4-010"}:
        verdict, defect = "PARTIAL", "D-012"
        actual = "Lesson source links exist, but the required content source/license register is absent."
    elif req_id in {"S4-011", "S4-017", "S4-018", "S4-019", "S4-020", "S4-021", "S4-022", "S4-025", "S4-026", "S4-041", "S4-042", "S4-043", "S4-044", "S4-047", "S4-048", "S4-049", "S4-053", "S4-055", "S4-056", "S4-058", "S4-059"}:
        verdict, defect = "PARTIAL", "D-003"
        actual = "Domain support exists, but current Sprint 04 UI state is not connected to durable ViewModel/repository records."
        if req_id in {"S4-019", "S4-053"}:
            defect += "; D-015"
    elif req_id in {"S4-012", "S4-013", "S4-014", "S4-015", "S4-016"}:
        verdict, defect = "BLOCKED", "D-008"
        actual = "Current CVA label/field mapping cannot be verified from required source register; S4 specification explicitly blocks production labeling without mapping."
    elif req_id in {"FR-09", "S2-077"}:
        verdict, defect = "PARTIAL", "D-004"
        actual = "Versioned codec and tests exist; old nested recipe/chat JSON compatibility is not handled by the inspected schema-v2 path."
    elif req_id in {"S2-032", "S2-033", "S2-034", "S2-035", "S2-036"}:
        verdict, defect = "PARTIAL", "D-005"
        actual = "Simulator/abstraction exists; no production BLE GATT driver or compatible hardware acceptance was found."
    elif req_id == "BR-07":
        verdict, defect = "PARTIAL", "D-006"
        actual = "Timer exists; snapshot restore helpers were not found wired to durable lifecycle persistence."
    elif req_id == "BR-17":
        verdict, defect = "BLOCKED", "D-007"
        actual = "Source status remains PENDING_VIDEO_REVIEW; no timestamped evidence inventory was located."
    elif req_id in {"FR-06", "S2-071"}:
        verdict, defect = "PARTIAL", "D-009"
        actual = "RAG implementation and tests exist; audit found conflicting historical device receipts and no current receipt bound to this commit."
    elif req_id in {"S2-008", "S2-067"}:
        verdict, defect = "IMPLEMENTED_UNVERIFIED", "D-010"
        actual = "OCR implementation and synthetic image tests exist; real coffee-label photography is not accepted by the source checklist and was not run."
    elif req_id == "S4-054":
        defect = "D-015"
        actual = "Legacy taxonomy and recipe entry points remain in source, but the v1.14.4 release preservation/restore claim is not supported by tagged acceptance evidence."
    return verdict, defect, actual, owner


def grouped_test_rows(path: Path, prefix: str) -> list[tuple[str, str, str, str]]:
    result = []
    for line_number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
        if not line.startswith("|"):
            continue
        cells = [cell.strip() for cell in line.strip().strip("|").split("|")]
        if cells and re.fullmatch(re.escape(prefix) + r"-\d{3}", cells[0]):
            result.append((cells[0], cells[1], cells[2] if len(cells) > 2 else "", f"{path.relative_to(ROOT).as_posix()}:{line_number}"))
    return result


def write_master_test_cases(rows: list[dict[str, str]]) -> None:
    out: list[str] = [
        "# Master BDD Test Cases — Sprint 01–04",
        "",
        "All cases below are BDD-oriented test designs, not executed tests. Their status is **NOT_RUN** until a receipt is recorded. A source marker, unrelated test report, or screenshot does not change that status. Several dimensions still need case-specific fixture values, observable oracles, and justified N/A decisions before execution; no dimension coverage is credited from these prompts alone. Ten legacy rows record historical screen/data preservation checks and two delivery rows record current release requirements in addition to the 209 blueprint IDs.",
        "",
        "## How to execute safely",
        "",
        "Use a disposable seeded app profile and a copy of each supported historical database. Keep the installed user profile, database, SAF trees, and model files untouched. Start offline except for cases explicitly testing a user-requested transfer. Capture device/build identity, test report, logs, and before/after IDs for every stateful case.",
        "",
        "## Cross-cutting cases",
        "",
    ]
    cross = [
        ("TC-CROSS-01", "All offline requirements", "Airplane mode and no-model degradation", "With the app started in airplane mode and no local LLM installed, complete catalog lookup, manual recipe, brew timer, journal, sensory observation, and cupping. Expected: manual paths remain usable; AI actions state unavailable and never fabricate output.", "Compose UI + instrumented device"),
        ("TC-CROSS-02", "All stateful requirements", "Persistence and process recreation", "Create one record of each affected kind, record IDs/counts, background the app, rotate, force-stop only the disposable app, relaunch, and compare all values and relationships. Expected: saved data remains exact; drafts recover according to spec; no duplicate submissions.", "Instrumented Android + Room"),
        ("TC-CROSS-03", "All persisted requirements", "Sequential database upgrade and backup compatibility", "Restore each supported schema fixture in sequence into a disposable database, run migrations, then verify stable IDs, counts, row JSON, foreign keys, revisions, and seed overrides. Decode schema-v2 and current backups containing non-empty recipes and chat messages. Expected: additive migration, full compatibility, no destructive fallback or lost rows.", "Room migration + JVM/Android instrumentation"),
        ("TC-CROSS-04", "Every visible route and action", "Navigation, back, and deep link", "Visit every route, invoke each primary action, navigate back, and launch each documented deep link if any. Expected: destination and transient state are correct; no undocumented route is assumed; no deep-link capability is marked PASS when none exists.", "Compose UI + instrumented Android"),
        ("TC-CROSS-05", "All primary workflows", "TalkBack, touch targets, 200% font, and adaptive layout", "Run the critical brew and sensory journeys on compact, medium, and expanded windows at default and 200% font scale; use TalkBack and keyboard/switch navigation. Measure actionable bounds. Expected: reading order, labels, contrast, non-color cues, 48dp targets, and core actions remain operable.", "Instrumented Android + manual TalkBack"),
        ("TC-CROSS-06", "Import, AI, backup, and model features", "Untrusted files and prompt injection", "Import malformed PDF/archive/JSON and a document containing instructions to ignore policy, reveal secrets, or call a network tool. Query with and without evidence. Expected: bounded parser errors, document remains data, allowlisted tools only, no arbitrary execution/network, citations only to retrieved source spans, abstention when unsupported.", "JVM adversarial tests + instrumented SAF"),
        ("TC-CROSS-07", "All measurement and calculation features", "Units, uncertainty, and measurement authority", "Run golden/boundary inputs including 18g dose × 270g beverage × 1.30% measured TDS, zero/negative dose, density-free mL↔g, unmeasured TDS/EY, and user-observed vs predicted sensory values. Expected: 19.50% approximate EY for the golden case; invalid conversions are rejected; unknown remains unknown; estimates are labeled.", "JVM golden/property tests"),
        ("TC-CROSS-08", "SAF import/export/model/backup features", "URI grant loss and provider capability", "Use a disposable SAF provider with persistable grants, revoke permission, switch to a provider without seek support, and retry. Expected: regrant/recovery is explicit, no path inferred from URI, partial file is never published as Installed, and unsupported resume restarts safely.", "Instrumented Android + manual provider matrix"),
        ("TC-CROSS-09", "BLE-dependent requirements", "Unsupported and disconnected scale", "Without a compatible scale, open brew and try scale actions; then disconnect a supported test fixture during a real hardware run. Expected: manual weighing remains available, simulated readings are never labeled real, and unsupported BLE acceptance remains BLOCKED.", "JVM simulator + compatible BLE hardware required"),
        ("TC-CROSS-10", "Download and local inference features", "Transfer and inference interruption", "For a user-requested model download, interrupt at several byte offsets, change ETag, return invalid Content-Range, cancel, and simulate low storage. Load/cancel an actual compatible model under a recorded device memory profile. Expected: resume only on validated identity/range, verified SHA/size before publication, cancellation releases resources, and unsupported/OOM outcomes recover without app crash.", "Transfer unit tests + real SAF/provider and device AI"),
        ("TC-CROSS-11", "Performance claims", "Measured startup, scrolling, timer, and AI memory", "Run cold start, screen scroll, timer drift, and inference memory measurements on a recorded release build/device; repeat enough samples to report distribution and conditions. Expected: report measurements and uncertainty only; do not invent budgets or infer sensory quality.", "Macrobenchmark/device instrumentation"),
        ("TC-CROSS-12", "Privacy and settings", "Network, consent, deletion, and local logs", "Capture network activity during airplane/offline use and a user-requested transfer; inspect manifest, dependencies, logs, export/delete behavior and explicit consent. Expected: no telemetry/cloud inference; asset networking only after user action; deletion/export effects are accurate and local.", "Static security review + instrumented/manual"),
    ]
    for tc_id, reqs, title, steps, automation in cross:
        out.extend([f"### {tc_id} — {title}", "", f"- Linked requirement: {reqs}", f"- Title: {title}", "- Priority: P0 unless the linked requirement is explicitly lower.", "- Preconditions: pristine disposable seeded profile; record baseline manifest; use local-only fixtures.", "- Fixtures: exact fixture named by the linked specification; no live user data.", f"- Test steps (BDD): Given the stated environment, when {steps.split('. Expected:')[0].replace('Expected:', '').strip().lower()}, then {steps.split('. Expected:')[-1].strip() if '. Expected:' in steps else 'the stated acceptance holds.'}", f"- Expected result: {steps.split('. Expected:')[-1].strip() if '. Expected:' in steps else steps}", "- Actual result: Not executed.", "- Execution environment: Not run; device/profile-specific.", f"- Automation type: {automation}.", "- Screenshot/log/test-report evidence: Not generated; see docs/qa/TEST_EXECUTION_REPORT.md.", "- Result: NOT_RUN.", "- Defect ID: See docs/qa/DEFECT_REGISTER.csv after execution.", ""])

    out.extend(["## Cross-sprint end-to-end journeys", ""])
    journeys = [
        ("E2E-01", "Home → Bean → Grinder → Dripper → Recipe → Timer → Journal → Roda Rasa → Cupping → Report → Backup → Restore", "All linked records and IDs persist through export/restore; history is immutable."),
        ("E2E-02", "Resep Juara → filter and inspect source → duplicate → change dose → scale staged pours → brew offline → compare → sensory note", "Original template stays immutable; copied revision and journal preserve attribution and units."),
        ("E2E-03", "Roda Rasa → search descriptor → select → save → reopen → reuse in CVA descriptive", "Descriptor identity and meaning remain stable; existing observation is not overwritten."),
        ("E2E-04", "CVA → blind samples → descriptive → affective → permissioned extrinsic reveal → lock → amend with reason → export → reopen/compare", "Blindness audit, immutable revisions, standards provenance, and real file exports are verifiable."),
        ("E2E-05", "Green lot QC → actual defects/moisture with instrument metadata → link sample to cupping → local report", "Only measured values are recorded; unsupported inputs remain explicit unknown/out-of-scope states."),
        ("E2E-06", "Airplane mode + no model + no BLE → manual brew → timer → sensory observation → cupping → export", "Every manual path works without model/BLE/network and does not invent measurements."),
        ("E2E-07", "Upgrade an old Room fixture through every supported schema version", "IDs/counts/revisions/templates/taxonomy/cuppings/documents/manifests/foreign keys remain valid."),
        ("E2E-08", "Background, rotate, process death, low storage, grant revoke, malformed file, failed download, cancelled inference", "Recovery is explicit and no durable record or asset is corrupted or partially published."),
        ("E2E-09", "Switch language/theme, 200% font, TalkBack, narrow and expanded layouts", "Primary actions remain accessible and user data remains unchanged."),
        ("E2E-10", "Export/import roundtrip and repeat seed import with conflicting local edits", "Roundtrip is lossless; conflict handling is explicit; import is idempotent and preserves user changes."),
    ]
    for tc_id, steps, expected in journeys:
        out.extend([f"### {tc_id}", "", f"- Linked requirement: cross-sprint (see linked FR/S2/GR/BR/S4/EDU rows in RTM).", f"- Title: {steps}", "- Priority: P0.", "- Preconditions: disposable seeded profile plus immutable baseline snapshot; all referenced source IDs recorded.", "- Fixtures: only verified fixtures/catalog rows; actual user samples are required for sensory or instrument measurements.", f"- Test steps (BDD): Given a clean disposable profile and the stated starting route, when the operator follows {steps}, then {expected}", f"- Expected result: {expected}", "- Actual result: Not executed.", "- Execution environment: Not run; disposable app profile and full device matrix not available.", "- Automation type: Compose UI + repository/Room instrumentation; manual sensory/device steps where required.", "- Screenshot/log/test-report evidence: Not generated.", "- Result: NOT_RUN.", "- Defect ID: See defect register.", ""])

    out.extend(["## Blueprint and delivery requirement cases", "", "Each requirement below receives a linked BDD acceptance case with explicit checks for every requested dimension. Shared cases above provide common execution detail; mark an inapplicable dimension with its reason in the execution record.", ""])
    for row in rows:
        if row["id"].startswith("LEGACY-"):
            continue
        req_id = row["id"]
        source, route, _, device = source_for(req_id)
        verdict, defect, _, _ = findings(req_id)
        test_id = f"TC-REQ-{req_id}"
        feature = row["feature"]
        acceptance = row["acceptance"]
        acceptance_for_step = acceptance.rstrip().rstrip(".")
        dimensions = [
            f"Happy path: execute the row-specific action and confirm the acceptance criterion: {acceptance}",
            "Negative/boundary: repeat with the lowest/highest supported values and one unsupported boundary; retain the exact observed result and reject unsafe values.",
            "Invalid input: submit malformed, missing, or out-of-range values wherever this requirement accepts user input; otherwise record N/A because the row is read-only/static.",
            "UI states: verify success plus empty/loading/offline/error/retry or recovery on the owning route; record N/A for a pure domain contract with no screen.",
            "Offline/no-model: execute TC-CROSS-01; use airplane mode and no installed model, and verify the manual path or explicit unavailable state required by this row.",
            "Persistence/relaunch: create or edit the row's user-owned record, relaunch the disposable app, and compare IDs and values; record N/A for immutable bundled reference data.",
            "Navigation/back/deep-link: enter from the documented route, complete the action, navigate Back, and reopen; deep-link is N/A because no deep-link declaration was found.",
            "Migration/regression: apply supported schema/seed upgrade to a copied historical fixture and compare IDs/counts/links; record N/A when no persisted data is involved.",
            "Accessibility: verify TalkBack label/order, keyboard focus, 48dp targets, contrast and 200% font via TC-CROSS-05; record N/A for a pure domain contract.",
            "Security: exercise the relevant trust/permission boundary, malformed input, consent, and local-log behavior via TC-CROSS-06/12; record N/A when the requirement exposes no such boundary.",
            "Performance: measure the actual screen/operation under recorded build/device conditions via TC-CROSS-11; report measurements without inventing an acceptance budget.",
        ]
        out.extend([
            f"### {test_id} — {req_id}: {feature}",
            "",
            f"- Linked requirement: {req_id} ({row['spec']}).",
            f"- Title: Verify {feature} and its failure/recovery behavior.",
            f"- Priority: {row['priority']}.",
            f"- Preconditions: clean disposable profile with its immutable manifest; navigate using RTM route '{route}'; airplane mode unless the requirement is an explicit user-requested transfer.",
            f"- Fixtures: documented, provenance-bearing values for {req_id}; include valid, boundary, invalid, empty, and revoked/unavailable inputs where the requirement accepts them. Do not use fabricated measurements or copyrighted material.",
            f"- Test steps (BDD): Given the row-specific, provenance-bearing fixture and current supported build, when the user completes '{feature}', then {acceptance_for_step}. Execute each applicable dimension check below; compare persisted identifiers and values only in a disposable profile.",
            "- Dimension checks:",
            *[f"  - {dimension}" for dimension in dimensions],
            f"- Expected result: {acceptance}",
            "- Actual result: Not executed; see static finding in the feature inventory when applicable.",
            f"- Execution environment: Not run; hardware need: {device}.",
            "- Automation type: JVM unit/property/golden for pure deterministic contracts; repository/Room migration tests for persistence; Compose/instrumented Android for navigation, lifecycle, SAF, and accessibility; manual device/human evaluation for physical sensory and hardware behavior.",
            "- Screenshot/log/test-report evidence: Not generated; add commit-bound XML/log/screenshot paths after execution.",
            f"- Result: NOT_RUN. - Defect ID: {defect or 'None assigned before execution'}. - Static verdict lead: {verdict}.",
            "",
        ])

    out.extend(["## Shipped legacy preservation cases", "", "These ten entries are derived from deleted production paths in tagged history. The current route shell has matching destinations, but that source-level correspondence does not establish behavior or data parity.", ""])
    for legacy_id, feature, route, historical_path, acceptance in LEGACY_FEATURES:
        case_id = f"TC-REQ-{legacy_id}"
        history_ref = "v1.9.5..v1.9.6" if legacy_id == "LEGACY-10" else "v1.0.0..v1.1.0"
        out.extend([
            f"### {case_id} — {feature}",
            "",
            f"- Linked requirement: shipped legacy feature {legacy_id}; historical source `{historical_path}`; Git range `{history_ref}`.",
            f"- Title: Verify preservation of {feature}.",
            "- Priority: P0 under the no-feature-loss/no-data-loss audit invariant.",
            f"- Preconditions: build the current supported app and use a disposable seeded profile; inspect historical source at its exact tagged revision; never migrate or edit the installed user app.",
            f"- Fixtures: documented route behavior from `{historical_path}`; for recipe corpus, a read-only v1.9.5 source manifest and copied backup fixture with IDs/content hashes.",
            f"- Test steps (BDD): Given the historical behavior and a current disposable profile, when the user enters the current `{route}` destination and performs each historical primary action, then {acceptance}",
            "- Dimension checks:",
            "  - Happy path: exercise each old primary action with a valid, provenance-bearing fixture and compare the old/current acceptance outcome.",
            "  - Negative/boundary: exercise each applicable historical boundary and invalid state; record N/A only when the historical action has no input boundary.",
            "  - Invalid input: submit malformed or unsupported values on each applicable form; static routes with no input are N/A.",
            "  - UI states: verify empty, loading, success, offline, error and recovery states for the current destination; mark unavailable states N/A only with source rationale.",
            "  - Offline/no-model: perform the manual workflow in airplane mode with no model installed; the legacy feature must remain usable or clearly report unavailable AI.",
            "  - Persistence/relaunch: save a representative record, relaunch the disposable app, and compare IDs, payloads and links; for recipe history, compare every old catalog identity.",
            "  - Navigation/back/deep-link: enter from documented navigation, use Back, reopen; deep-link is N/A because none is declared.",
            "  - Migration/regression: use copied supported historic records and compare ordered IDs/counts/content hashes before and after; no app data reset.",
            "  - Accessibility: verify TalkBack, focus order, 48dp targets, contrast and 200% font using TC-CROSS-05.",
            "  - Security: inspect local-only behavior, permissions, consent and export ownership where relevant using TC-CROSS-06/12.",
            "  - Performance: measure route entry/scroll or catalog search on a recorded build/device using TC-CROSS-11; do not invent a threshold.",
            f"- Expected result: {acceptance}",
            "- Actual result: NOT_EXECUTED; static history/source observation only.",
            "- Execution environment: no disposable profile or historical database fixture; device test NOT_RUN.",
            "- Automation type: Git-history manifest comparison + JVM/repository tests + Compose/instrumented tests on disposable profile.",
            "- Screenshot/log/test-report evidence: Not generated; see `FEATURE_PRESERVATION_DIFF.md`.",
            f"- Result: NOT_RUN. - Defect ID: {'D-013' if legacy_id == 'LEGACY-10' else 'None assigned pending route parity'}. - Static verdict lead: {'SPEC_CONFLICT' if legacy_id == 'LEGACY-10' else 'IMPLEMENTED_UNVERIFIED'}.",
            "",
        ])

    s3 = grouped_test_rows(SPEC_FILES["S3"], "TC")
    s4 = grouped_test_rows(SPEC_FILES["S4"], "QA")
    out.extend(["## Existing named test procedures from sprint specs", ""])
    for tc_id, title, expected, location in s3 + s4:
        linked = "Sprint 03" if tc_id.startswith("TC-") else "Sprint 04"
        out.extend([f"### {tc_id} — {title}", "", f"- Linked requirement: {linked}; see {location}.", f"- Title: {title}.", "- Priority: P0/P1 per linked story; verify exact priority in the requirement matrix.", "- Preconditions: disposable seeded profile; use only fixture/source files with provenance.", "- Fixtures: values specified in the sprint case; hardware and legal resources only when supplied/authorized.", f"- Test steps (BDD): Given the specified preconditions, when the operator performs '{title}', then {expected}.", f"- Expected result: {expected}.", "- Actual result: Not executed during this audit.", "- Execution environment: Not run.", "- Automation type: Unit, repository, Compose, instrumented, or manual as appropriate to the case.", "- Screenshot/log/test-report evidence: Not generated.", "- Result: NOT_RUN. - Defect ID: See defect register.", ""])
    expected_case_ids = {
        *(f"TC-CROSS-{number:02d}" for number in range(1, 13)),
        *(f"E2E-{number:02d}" for number in range(1, 11)),
        *(f"TC-REQ-FR-{number:02d}" for number in range(1, 11)),
        *(f"TC-REQ-S2-{number:03d}" for number in range(1, 83)),
        *(f"TC-REQ-GR-{number:02d}" for number in range(1, 13)),
        *(f"TC-REQ-BR-{number:02d}" for number in range(1, 19)),
        *(f"TC-REQ-S4-{number:03d}" for number in range(1, 66)),
        *(f"TC-REQ-EDU-{number:02d}" for number in range(1, 23)),
        *(f"TC-REQ-LEGACY-{number:02d}" for number in range(1, 11)),
        "TC-REQ-DELIVERY-01",
        "TC-REQ-DELIVERY-02",
        *(f"TC-{number:03d}" for number in range(301, 325)),
        *(f"QA-{number:03d}" for number in range(1, 31)),
    }
    actual_case_ids = [
        match.group(1)
        for line in out
        if (match := re.match(r"^###\s+([A-Z0-9-]+)", line))
    ]
    if len(actual_case_ids) != len(set(actual_case_ids)):
        raise SystemExit("Duplicate master test-case IDs found")
    if set(actual_case_ids) != expected_case_ids:
        missing = sorted(expected_case_ids - set(actual_case_ids))
        extra = sorted(set(actual_case_ids) - expected_case_ids)
        raise SystemExit(f"Master test-case ID drift; missing={missing}; extra={extra}")
    dimension_sections = sum(line == "- Dimension checks:" for line in out)
    if dimension_sections != 221:
        raise SystemExit(f"Expected 221 requirement/legacy/delivery dimension sections, found {dimension_sections}")
    dimension_counts = []
    for index, line in enumerate(out):
        if line != "- Dimension checks:":
            continue
        count = 0
        for candidate in out[index + 1 :]:
            if not candidate.startswith("  - "):
                break
            count += 1
        dimension_counts.append(count)
    if dimension_counts != [11] * 221:
        raise SystemExit("Every blueprint, legacy, and delivery requirement case must contain exactly 11 dimension checks")
    QA.mkdir(parents=True, exist_ok=True)
    (QA / "MASTER_TEST_CASES_S01_S04.md").write_text("\n".join(out), encoding="utf-8")


def main() -> None:
    rows = parse_specs()
    expected_counts = {"FR": 10, "S2": 82, "GR": 12, "BR": 18, "S4": 65, "EDU": 22}
    counts = {prefix: sum(row["id"].startswith(prefix + "-") for row in rows) for prefix in expected_counts}
    if counts != expected_counts:
        raise SystemExit(f"Requirement count mismatch: {counts}")
    expected_blueprint_ids = {
        *(f"FR-{number:02d}" for number in range(1, 11)),
        *(f"S2-{number:03d}" for number in range(1, 83)),
        *(f"GR-{number:02d}" for number in range(1, 13)),
        *(f"BR-{number:02d}" for number in range(1, 19)),
        *(f"S4-{number:03d}" for number in range(1, 66)),
        *(f"EDU-{number:02d}" for number in range(1, 23)),
    }
    blueprint_ids = [row["id"] for row in rows]
    if len(blueprint_ids) != len(set(blueprint_ids)) or set(blueprint_ids) != expected_blueprint_ids:
        missing = sorted(expected_blueprint_ids - set(blueprint_ids))
        extra = sorted(set(blueprint_ids) - expected_blueprint_ids)
        raise SystemExit(f"Blueprint requirement ID drift; missing={missing}; extra={extra}")
    if len({item[0] for item in LEGACY_FEATURES}) != len(LEGACY_FEATURES):
        raise SystemExit("Duplicate legacy preservation IDs found")
    rows.extend({
        "id": item[0], "sprint": "Legacy / historical releases", "epic": "Shipped legacy feature preservation",
        "feature": item[1], "acceptance": item[4], "priority": "P0 (preservation invariant)",
        "spec": f"Git history {'v1.9.5..v1.9.6' if item[0] == 'LEGACY-10' else 'v1.0.0..v1.1.0'}:{item[3]}; current route reference coffee-creator-blueprint/DESIGN.md:13",
    } for item in LEGACY_FEATURES)
    rows.extend({
        "id": item[0], "sprint": "Release / delivery", "epic": "User-requested release requirements",
        "feature": item[1], "acceptance": item[3], "priority": item[2],
        "spec": "User request (2026-10-18): GitHub release/download requirements; evidence in docs/qa/DEFECT_REGISTER.csv",
    } for item in DELIVERY_REQUIREMENTS)
    if len(rows) != 221 or len({row["id"] for row in rows}) != 221:
        raise SystemExit(f"Expected 221 unique blueprint, legacy, and delivery RTM rows; found {len(rows)}")

    columns = ["Requirement ID", "Sprint", "Epic", "Feature", "Priority", "Exact spec location", "Source file/symbol",
               "Route", "Storage entities", "Baseline behavior", "Test ID", "Test layer", "Device dependency",
               "Expected result", "Actual result", "Evidence path", "Defect ID", "Owner", "Verdict",
               "Source test marker candidate", "Test status"]
    output = QA / "REQUIREMENTS_TRACEABILITY_MATRIX.csv"
    with output.open("w", newline="", encoding="utf-8-sig") as stream:
        writer = csv.DictWriter(stream, fieldnames=columns)
        writer.writeheader()
        for row in rows:
            source, route, storage, device = source_for(row["id"])
            test_id, layer = test_evidence(row["id"])
            verdict, defect, actual, owner = findings(row["id"])
            planned_test_id = f"TC-REQ-{row['id']}"
            existing_test_id = test_id
            test_id = f"{planned_test_id}; source tests: {existing_test_id}"
            if "No explicit ID marker" in test_id:
                mapped_test_status = "NO_EXPLICIT_MARKER_FOUND"
            else:
                mapped_test_status = "CANDIDATE_MATCH_ONLY; ASSERTION_RELEVANCE_NOT_VERIFIED"
            writer.writerow({
                "Requirement ID": row["id"], "Sprint": row["sprint"], "Epic": row["epic"], "Feature": row["feature"],
                "Priority": row["priority"], "Exact spec location": row["spec"], "Source file/symbol": source,
                "Route": route, "Storage entities": storage,
                "Baseline behavior": "NOT_OBSERVED: no disposable seeded profile; installed application data was not accessed",
                "Test ID": test_id, "Test layer": layer, "Device dependency": device,
                "Expected result": row["acceptance"], "Actual result": actual,
                "Evidence path": f"{row['spec']}; see docs/qa/FEATURE_INVENTORY_S01_S04.md and source/test paths above",
                "Defect ID": defect, "Owner": owner, "Verdict": verdict,
                "Source test marker candidate": mapped_test_status, "Test status": "NOT_RUN",
            })
    write_master_test_cases(rows)
    print(f"wrote {len(rows)} RTM rows: 209 blueprint IDs + {len(LEGACY_FEATURES)} legacy checks + {len(DELIVERY_REQUIREMENTS)} delivery requirements; counts={counts}; explicit source markers={sum('No explicit ID marker' not in test_evidence(row['id'])[0] for row in rows)}")


if __name__ == "__main__":
    main()
