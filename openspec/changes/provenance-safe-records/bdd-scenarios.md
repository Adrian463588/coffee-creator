# BDD scenarios

## Fresh profile

Given a local repository with no personal records
When Coffee Creator finishes initialization
Then journal, cafe visits, cupping sessions, physical QC, roast-rest observations, and personal inventory remain empty
And curated material is available only after the user requests its import.
And the palate profile contains no personal evidence until the user records it.

## Rating-only journal entry

Given a fresh profile with no personal sensory evidence
And a journal entry with a numeric rating but no recorded sensory scores
When the user saves the entry
Then the entry is retained as authored
And no roast, acidity, sweetness, bitterness, or body evidence is inferred
And the personal palate evidence weight remains unchanged.

## Durable workbench records

Given user-created journal, cupping, cafe-cup, physical-QC, roast-rest, inventory, equipment, water, green-lot, and roast-batch records
When those records are saved and the repository is reopened
Then every record is restored with the same user-provided values and provenance.

## Complete backup round trip

Given all supported durable user-record families in the local repository
When the user exports a full backup and restores it into an empty repository
Then every supported record is restored atomically
And temporary selections and incomplete form drafts are not misrepresented as saved records.

## Brew completion

Given a recipe with planned dose, water, and temperature
And no scale measurement, TDS reading, sensory score, or user tasting note
When the brew timer completes
Then the app records the completed timer event and recipe context
And measured mass, TDS, sensory scores, rating, and actual espresso yield remain unknown.

## OCR with missing facts

Given recognized bag text with no package mass or roast date
When the offline parser creates a draft
Then package mass and roast date are unknown
And the parser reports no numeric confidence without a calibrated OCR model.

## Invalid OCR weight

Given recognized text containing an invalid or non-positive package mass
When the offline parser creates a draft
Then it does not substitute a default bag size
And the user must correct or leave the field unknown before inventory creation.

## Ingredient safety

Given an ingredient absent from the local evidence catalog or with unknown food-grade status
When the safety tool audits it
Then the result is not safe for beverage use until verified evidence is supplied.

## AI recommendations without evidence

Given a request to recommend an inventory bean or adjust a recipe
And the required user inventory/profile/recipe measurements are unavailable
When the tool executes
Then it abstains with a clear unavailable-evidence result
And returns no fabricated candidate or numeric recipe.

## Manual palate preferences

Given the user explicitly changes a palate preference slider
And no quiz or qualified tasting session has been completed
When inventory recommendations are requested
Then recommendations may use that declared preference
And the profile remains distinct from quiz and sensory-session evidence.

## Incomplete imported brews

Given a Beanconqueror brew has a missing, malformed, non-positive, or fractional required measurement
When the export is imported
Then that brew is skipped with its unavailable values preserved as unknown
And no plausible dose, water mass, or duration is substituted.

## Balanced extraction assessment

Given an entry has no recognized sensory diagnostic and lacks measured beverage mass, dose, and TDS evidence
When OVAT analysis is requested
Then the analysis abstains instead of claiming the cup is balanced.

Given measured extraction yield and strength fall inside their method-specific reference ranges
When OVAT analysis is requested
Then the balanced result identifies those measured inputs as its basis.

## Safety boundaries

Given a food-grade ingredient exceeds its supplier's stated maximum dosage
When the safety tool audits it
Then beverage use is blocked as a critical dosage-limit violation.

Given a temperature adjustment would exceed the confirmed boiling-point margin or fall below the supported brew range
When OVAT proposes a change
Then it abstains with a reason and never returns a value in the opposite direction.

## Preservation

Given existing journal, sensory, inventory, model, and SAF records
When the app is upgraded and initialized
Then the records and SAF ownership remain unchanged
And only new synthetic record creation is prevented.
