# BDD scenarios

## Fresh profile

Given a local repository with no personal records
When Coffee Creator finishes initialization
Then journal, cafe visits, cupping sessions, physical QC, roast-rest observations, and personal inventory remain empty
And curated material is available only after the user requests its import.

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

## Preservation

Given existing journal, sensory, inventory, model, and SAF records
When the app is upgraded and initialized
Then the records and SAF ownership remain unchanged
And only new synthetic record creation is prevented.
