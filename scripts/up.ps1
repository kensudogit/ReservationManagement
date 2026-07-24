$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)

Write-Host "Starting Receivables Management stack..." -ForegroundColor Cyan

# docker compose build が buildx context 不整合で失敗する場合があるため docker build を使用
docker buildx use desktop-linux 2>$null | Out-Null
docker build -t receivablesmanagement-backend ./backend
docker build -t receivablesmanagement-frontend ./frontend
docker compose up -d

Write-Host ""
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Green
Write-Host "Backend : http://localhost:8080/api/health" -ForegroundColor Green
Write-Host "Postgres: localhost:5432 / receivables (receivables/receivables)" -ForegroundColor Green
