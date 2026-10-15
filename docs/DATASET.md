# Offline coffee reference dataset

Plan recorded before implementation, 2026-10-15. Scope: PRD FR-01 curated,
versioned catalog and the requested verified internet research.

1. Research primary sources and reuse terms; collect a bounded selection of
   variety names, genetic groups and parentage facts. Do not copy descriptions,
   photographs, artwork, scores, growing advice or supplier records.
2. Retain per-field source URL, retrieval date and SHA-256 receipt. Bundle only
   normalized factual records. No application runtime fetch or scraping.
3. Keep producer, lot origin, roast, process, altitude, density, allergens and
   food-grade certification unknown. Variety genetics cannot certify a lot or
   predict its measured sensory profile.
4. Add deterministic catalog tests before code: unique stable IDs, complete
   provenance, source-receipt match, scientifically unknown lot metadata and
   malformed dataset rejection.
5. Add an atomic data-only seed upgrade. Insert newly introduced IDs only once;
   preserve edits and ID collisions, deleted references, recipes and settings.
   Add Room upgrade/reopen/delete/restore regression scenarios.
6. Integrator executes Gradle checks. This worker does not build or use ADB.

Rollback: revert this scoped commit. No user database reset or destructive
replacement. Source licenses and research receipts are detailed below once
collection and review finish.

## Included data and scientific boundaries

Dataset revision **3**, retrieved **2026-10-15** (Asia/Jakarta), adds **32**
variety references to the two existing Arabica history references. Fresh
installation therefore has **34** reference cards. This is a selected reference
collection, not exhaustive worldwide coverage and not inventory of coffee for
sale. It includes Java, Kartika 1, S795, Bourbon, Typica, Caturra, Catuai,
Mundo Novo, Pacamara, SL selections, Catimor/Sarchimor descendants and selected
F1 hybrids. Exact names, IDs and all 32 primary URLs are in
[`varieties-v3.json`](../app/src/main/assets/reference/varieties-v3.json).

Only the variety name, broad genetic group and short parentage/alias facts are
mapped. Labels are normalized into application wording without changing named
parents or the source's uncertainty (for example, `Bourbon-like`). The published
Starmaya parent spelling `Marsallesa` is retained rather than silently corrected.
The source field under Typica contains aliases, so the interface explicitly
labels this information **Parentage/aliases** rather than presenting aliases as
biological parents.

Every new card retains **Unknown** for lot country/region, producer, process,
roast, allergens and certification; altitude, roast date, density, food-grade
status and supplier limits remain absent. Genetic classification is not DNA
authentication of a user's bag. No tasting scores, guaranteed flavors, optimum
altitude, recommended dosage or measured TDS are inferred from these records.
Users can edit references or create their own entries; the original bundled
receipt remains available for audit.

## Primary research and reuse

- [World Coffee Research variety catalog](https://varieties.worldcoffeeresearch.org/arabica/varieties):
  32 individually retrieved variety detail pages. Examples:
  [Caturra](https://varieties.worldcoffeeresearch.org/varieties/caturra),
  [Kartika 1](https://varieties.worldcoffeeresearch.org/varieties/kartika-1),
  [Java](https://varieties.worldcoffeeresearch.org/varieties/java),
  [SL14](https://varieties.worldcoffeeresearch.org/varieties/sl14).
  The development script checks the actual value adjacent to the heading;
  explanatory modals, neighboring varieties and generic option lists are not
  mistaken for the selected variety's value.
- WCR's [published catalog, page 2](https://cdn2.assets-servd.host/worldcoffee-research/production/pdf/catalogEnglish/Combined/complete-1769602232.pdf?dm=1769602232)
  explicitly uses **CC BY-NC-ND 4.0**, **not CC BY**. Its text, selection/arrangement,
  illustrations and photographs are not relicensed by this application. We do
  not bundle that PDF, copy its descriptive prose, quality scores or artwork,
  or distribute an altered WCR catalog. The bundled collection is an independent
  bounded selection of short factual names, group labels and parentage assertions,
  with attribution and direct source links. Do not extend this into bulk copying
  of the original catalog under an assumed permissive text/data license.
  See the [license terms](https://creativecommons.org/licenses/by-nc-nd/4.0/).
- [Millet et al. (2024), PLOS ONE 19(4): e0299493](https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0299493)
  and its [S2 accession table](https://journals.plos.org/plosone/article/file?id=10.1371/journal.pone.0299493.s002&type=supplementary)
  provide an independent primary research cross-check for named accessions of
  **10** included references. The article explicitly carries
  [CC0-1.0](https://creativecommons.org/publicdomain/zero/1.0/).
  These checks establish existence of named research accessions only. Their
  collection locations and sample genetic clusters do not certify all plants
  bearing that variety name. In particular, accession passport country is never
  copied into a purchased lot's country field. The receipt records accession
  code/name/holding institution, source checksum, scope and CC0 attribution.

No third-party pictures, flavor-wheel graphics, supplier lots or additive safety
limits were scraped. No runtime HTTP dependency or new network permission is
introduced. Research ran on the development machine only.

## Provenance and reproducibility

The UTF-8/LF receipt is **27,190 bytes**, SHA-256:

```
db9b36a93a3b4bbc9c7d514c0293cdf8bf8f01d0ceb5bf1de913fb3a108d2ad2
```

Each record includes exact primary URL, retrieval date, raw HTML SHA-256 and
individual SHA-256 hashes of the whitespace-normalized source `name`,
`Genetic Description` and `Lineage` values. The JSON records their field mapping.
Source HTML is retained only in ignored developer research cache; the APK
contains short factual records and receipts, not source pages. Independent S2
DOCX SHA-256:

```
e3613a229bf965a749146a533f846ba07b75b919b384cd7a452094609294a775
```

Development-only commands (Python 3.12, `requests`, `beautifulsoup4`):

```powershell
python scripts/research_reference_catalog.py --verify
python scripts/research_reference_catalog.py --verify --refresh
python scripts/research_reference_catalog.py --generate-kotlin
python scripts/research_reference_catalog.py --accession-check
```

The first command rechecks cached primary values and hashes, fetching only cache
misses; `--refresh` explicitly retrieves all 32 current primary pages. Changed
factual field hashes fail verification even when a normalization rule would
otherwise hide a wording change. Source HTML can legitimately change because
of site navigation/footer updates; the field receipts distinguish that from
changed claims. The script never automatically replaces an existing reviewed
dataset. Kotlin runtime records are generated from the reviewed JSON; JVM tests
detect drift between both representations. Updating the receipt requires review,
regeneration, checksum/document updates and a new dataset revision.

## Non-destructive upgrade contract

- New database: install all 34 references and record reference revision 3.
- Existing initialized database: add only IDs introduced after its reference
  revision, in the initialization transaction. Legacy installations without a
  separate reference marker are conservatively treated as revision 2.
- Existing ID wins, including custom collisions and edited reference entries.
  Existing recipes, settings and deleted legacy entries are preserved.
- A committed revision marker prevents deleted new references from reappearing
  on restart. Introduction revisions are append-only; a future dataset must not
  rename/reuse an ID or move its introduction revision forward.
- Explicit backup restore and application-record deletion retain the local
  revision marker, so reopening cannot undo the user's restored/deleted catalog.
- No Room table/schema change, database reset or destructive migration is needed.

## Validation evidence

- **PASS:** actual development-time retrieval of 32 primary detail pages.
- **PASS:** `python scripts/research_reference_catalog.py --verify` — 32 reviewed
  source values match their cached primary pages and field hashes.
- **PASS:** `python scripts/research_reference_catalog.py --accession-check` —
  named accession checks for 10 references against the real CC0 S2 document.
- **PASS:** `git diff --check` at the worker checkpoint.
- **AUTHORED, NOT EXECUTED BY WORKER:** four JVM BDD scenarios for source
  completeness, receipt/runtime equality, malformed metadata, and introduction
  revisions; two Room instrumentation scenarios for legacy edits/deletions,
  collisions, restart, backup restore and explicit record deletion.
- **NOT RUN BY WORKER:** Gradle, Compose, emulator/physical device. Integrator
  owns these gates; writing test source is not a passing test result.

Source classifications may change as research advances. This snapshot does not
replace supplier documentation, laboratory measurements or scientific adjudication
of a specific cultivar/lot. It provides auditable reference facts with explicit
unknown fields and versioned update behavior.
