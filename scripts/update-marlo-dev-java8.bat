@echo off
REM Updates marlo-dev.properties to HTTPS on port 8443 (for Java 8 / run-marlo-java8.sh).
REM Usage: scripts\update-marlo-dev-java8.bat   (from repo root)
REM    or: scripts\update-marlo-dev-java8.bat C:\path\to\marlo-dev.properties

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

REM Replace http://localhost:8080 and localhost:8080 with https://localhost:8443 (Java 8: HTTPS on 8443)
powershell -NoProfile -ExecutionPolicy Bypass -Command "$p=$env:PROPS_FILE; $c=[System.IO.File]::ReadAllText($p); $c=$c.Replace('http://localhost:8080','https://localhost:8443').Replace('localhost:8080','https://localhost:8443'); [System.IO.File]::WriteAllText($p,$c)"

if errorlevel 1 (
  echo ERROR: PowerShell replace failed.
  exit /b 1
)

echo Updated: %PROPS_FILE% (HTTP/8080 -^> HTTPS/8443 for localhost).
exit /b 0
