param([Parameter(Mandatory=$true)][ValidatePattern('^v\d+\.\d+\.\d+$')][string]$Version)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $projectRoot
$appGradle = Get-Content -LiteralPath 'app/build.gradle.kts' -Raw
if ($appGradle -notmatch ('versionName\s*=\s*"' + [regex]::Escape($Version.Substring(1)) + '"')) {
    throw 'Release version must match app versionName.'
}
$dirty = & git status --porcelain
if ($LASTEXITCODE -ne 0 -or $dirty) { throw 'Commit reviewed source and evidence before releasing.' }
& .\gradlew.bat :app:detekt :app:ktlintCheck :app:lintDebug :app:testDebugUnitTest :app:assembleDebug --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Android quality gates failed.' }
$commit = (& git rev-parse HEAD).Trim()
& git push origin HEAD:main
if ($LASTEXITCODE -ne 0) { throw 'Source push failed.' }
$distribution = Join-Path $projectRoot 'artifacts/distribution'
New-Item -ItemType Directory -Path $distribution -Force | Out-Null
$assetName = "coffee-creator-$Version.apk"
$apk = Join-Path $distribution $assetName
Copy-Item -LiteralPath 'app/build/outputs/apk/debug/app-debug.apk' -Destination $apk
$checksum = (Get-FileHash -LiteralPath $apk -Algorithm SHA256).Hash.ToLowerInvariant()
$checksumFile = Join-Path $distribution 'SHA256SUMS.txt'
[IO.File]::WriteAllText($checksumFile, "$checksum  $assetName`n", [Text.UTF8Encoding]::new($false))
$notes = Join-Path $projectRoot "docs/releases/$Version.md"
if (!(Test-Path -LiteralPath $notes)) { throw 'Reviewed release notes are required.' }
& gh release create $Version $apk $checksumFile --target $commit --title "Coffee Creator $Version" --notes-file $notes --latest
if ($LASTEXITCODE -ne 0) { throw 'Release publication failed; do not claim a download exists.' }
& gh release view $Version --json url,targetCommitish,assets
if ($LASTEXITCODE -ne 0) { throw 'Release verification failed.' }
