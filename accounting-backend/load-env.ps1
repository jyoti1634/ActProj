# Load env vars from .env.local into current PowerShell session
# Usage: .\load-env.ps1    (run in the accounting-backend folder)
$envFile = Join-Path $PSScriptRoot '.env.local'
if (!(Test-Path $envFile)) {
  Write-Host ".env.local not found; copy .env.local.sample to .env.local and edit it with your values." -ForegroundColor Yellow
  return
}
Get-Content $envFile | ForEach-Object {
  $line = $_.Trim()
  if ($line -eq '' -or $line.StartsWith('#')) { return }
  $pair = $line -split '=', 2
  if ($pair.Length -ne 2) { return }
  $name = $pair[0].Trim()
  $value = $pair[1].Trim()
  if ($value.StartsWith('"') -and $value.EndsWith('"')) { $value = $value.Substring(1, $value.Length-2) }
  Set-Item -Path Env:$name -Value $value
  Write-Host "Set $name" -ForegroundColor Green
}
Write-Host "Loaded env from $envFile" -ForegroundColor Cyan
