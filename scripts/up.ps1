$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)
Write-Host "Starting Receivables Management stack..." -ForegroundColor Cyan
docker compose up -d --build
Write-Host ""
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Green
Write-Host "Backend : http://localhost:8080/api/health" -ForegroundColor Green
Write-Host "Oracle  : localhost:1521 / XEPDB1 (receivables/receivables)" -ForegroundColor Green
