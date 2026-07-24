$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
Set-Location $root

Write-Host "=== Backend tests (Gradle + JaCoCo) ===" -ForegroundColor Cyan
Push-Location "$root\backend"
if (Test-Path ".\gradlew.bat") {
    .\gradlew.bat test --quiet
} else {
    gradle test --quiet
}
$backendExit = $LASTEXITCODE
Pop-Location

Write-Host ""
Write-Host "=== Frontend tests (Jest + coverage) ===" -ForegroundColor Cyan
Push-Location "$root\frontend"
if (-not (Test-Path "node_modules")) {
    npm install
}
npm test
$frontendExit = $LASTEXITCODE
Pop-Location

Write-Host ""
Write-Host "=== Test reports ===" -ForegroundColor Cyan
Write-Host "Backend Test     : $root\backend\build\reports\tests\test\index.html"
Write-Host "Backend Coverage : $root\backend\build\reports\jacoco\test\html\index.html"
Write-Host "Frontend Coverage: $root\frontend\coverage\lcov-report\index.html"
Write-Host "Frontend JUnit   : $root\frontend\test-results\junit.xml"

if ($backendExit -ne 0 -or $frontendExit -ne 0) {
    Write-Host ""
    Write-Host "Tests FAILED (backend=$backendExit, frontend=$frontendExit)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "All tests PASSED" -ForegroundColor Green
