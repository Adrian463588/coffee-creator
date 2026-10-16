# BDD scenarios

## Recognize a captured or selected bean photo

Given the user captures or selects a readable supported coffee-bag image
When the app completes bundled offline English OCR
Then it shows an editable bean draft with the granted source URI attached
And missing package mass, roast date, or other facts remain unknown
And no confidence score is shown unless a calibrated model supplies it.

## Draft equipment metadata

Given the user captures or selects a readable supported equipment-label image
When the app completes bundled offline English OCR
Then it shows only explicitly labeled brand and model values for editing
And equipment category is required from the user before save
And capacity, material, and safety properties remain unknown unless explicitly corrected.

## Correct before saving

Given an OCR draft contains a misread or missing bean field
When the user edits the field and confirms the draft
Then the saved bean or equipment record contains the user's corrected values and retained source photo URI
And an inventory bag is created only when package mass and remaining mass are both explicitly valid.

## Unsupported or oversized image

Given the selected URI is unreadable, has an unsupported MIME type, is malformed, or exceeds the byte/dimension limit
When OCR validation runs
Then the app returns a recoverable error
And does not allocate an unbounded bitmap, write a partial bean, or crash.

## Cancellation and revoked grant

Given OCR is running or the provider grant has been revoked
When the user cancels or the provider returns an access error
Then OCR resources are released and the UI exposes a retry/reselect action
And existing records and saved user corrections remain unchanged.

## Cancel camera capture

Given the system camera receives an app-created pending MediaStore image URI
When the user cancels capture or camera launch/publication fails
Then the pending image row is deleted
And no catalog record is created.

## Cancel OCR processing

Given image decoding completed and local recognition is still running
When the user cancels recognition
Then the decoded bitmap is recycled after recognition unwinds
And no catalog record is created.

## Airplane mode

Given the device is in airplane mode and the selected photo is locally available
When the user runs OCR and reviews the draft
Then recognition and correction work without a network request or camera/storage permission prompt.
