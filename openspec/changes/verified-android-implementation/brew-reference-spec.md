# Additional requirements: Brew sections and sourced catalog

## Brew progressive disclosure (FR-02)

### Scenario: switch a card group without losing draft inputs
- GIVEN a manual recipe with a valid dose and optional measured values absent
- WHEN the user selects Recipe, Measurements, Ingredients or Tools using a chip
- THEN only that group's cards are shown and the selected chip is announced
- AND the existing draft values are retained
- AND Save and the calculated mass ratio remain discoverable
- AND no reference value becomes a measured beverage mass or TDS.

### Scenario: restore a selected group
- GIVEN Measurements is selected and a dose has been edited
- WHEN composition state is recreated or the user returns from another destination
- THEN the selected group and draft remain available
- AND back navigation follows the real Navigation Compose stack.

## Verified reference data (FR-01)

### Scenario: inspect a reference variety offline
- GIVEN a bundled reference entry was extracted from a checked primary source
- WHEN its catalog card is opened in manual offline mode
- THEN the variety identity, reference status and provenance are visible
- AND a supplier lot's producer, roast, density and measurements remain unknown
  unless the user separately supplies those facts.

### Scenario: upgrade an existing catalog
- GIVEN existing custom records or edited/deleted curated records
- WHEN a newer reference dataset is installed
- THEN new stable identifiers are inserted at most once
- AND user edits, deletions, saved recipes and historical revisions are preserved.

### Scenario: reject invented completeness
- GIVEN a bounded reference dataset and an authored QA fixture
- WHEN documentation reports dataset coverage or test results
- THEN it states their scope, version, sources, hashes and limitations
- AND does not claim exhaustive global coverage or independent adjudication.
