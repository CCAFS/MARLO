@echo off
REM Updates marlo-dev.properties to HTTP on port 8080 (for Java 17 / run-marlo-java17.sh).
REM Usage: scripts\update-marlo-dev-java17.bat   (from repo root)
REM    or: scripts\update-marlo-dev-java17.bat C:\path\to\marlo-dev.properties

setlocal EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "REPO_ROOT=%SCRIPT_DIR%.."
set "DEFAULT_PROPS=%REPO_ROOT%\marlo-web\src\main\resources\config\marlo-dev.properties"

if "%~1"=="" (
  set "PROPS_FILE=%DEFAULT_PROPS%"
) else (
  set "PROPS_FILE=%~1"
)

if not exist "%PROPS_FILE%" (
  echo ERROR: File not found: %PROPS_FILE%
  exit /b 1
)

REM Replace localhost variants with http://localhost:8080 (Java 17: HTTP on 8080)
powershell -NoProfile -ExecutionPolicy Bypass -Command "$p=$env:PROPS_FILE; $c=[System.IO.File]::ReadAllText($p); $c=$c.Replace('https://localhost:8443','localhost:8080').Replace('http://localhost:8080','localhost:8080'); [System.IO.File]::WriteAllText($p,$c)"

if errorlevel 1 (
  echo ERROR: PowerShell replace failed.
  exit /b 1
)

echo Updated: %PROPS_FILE% (localhost URLs normalized to http://localhost:8080).
exit /b 0
