@echo off
REM Run MARLO with Java 17 (Cargo / Tomcat embedded).
REM Convention: Java 17 uses HTTP on port 8080.
REM Usage: scripts\run-marlo-java17.bat   (from repo root)

setlocal EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "REPO_ROOT=%SCRIPT_DIR%.."
cd /d "%REPO_ROOT%"

REM ============================================================================
REM Stop running server and clean target directory
REM ============================================================================
echo Checking for running MARLO server...
tasklist /FI "COMMANDLINE eq *cargo:run*" 2>nul | findstr /C:"java.exe" >nul
if not errorlevel 1 (
  echo Stopping existing MARLO server...
  taskkill /F /FI "COMMANDLINE eq *cargo:run*" >nul 2>&1
  timeout /t 3 /nobreak >nul
  echo [92mServer stopped[0m
) else (
  echo No running server found
)

echo.
echo Cleaning target directory...
set "MAX_ATTEMPTS=5"
set "ATTEMPT=1"

:clean_target_loop
if exist "marlo-web\target" (
  rd /s /q "marlo-web\target" 2>nul
  if exist "marlo-web\target" (
    if !ATTEMPT! lss %MAX_ATTEMPTS% (
      echo Target directory locked, waiting... ^(attempt !ATTEMPT!/%MAX_ATTEMPTS%^)
      timeout /t 2 /nobreak >nul
      set /a ATTEMPT+=1
      goto clean_target_loop
    ) else (
      echo [93mWarning: Could not clean target directory after %MAX_ATTEMPTS% attempts[0m
      echo    Continuing anyway...
    )
  ) else (
    echo [92mTarget directory cleaned[0m
  )
) else (
  echo [92mTarget directory already clean[0m
)
echo.
REM ============================================================================

REM Use JAVA_HOME if set
if defined JAVA_HOME (
  set "PATH=%JAVA_HOME%\bin;%PATH%"
)

REM Check Java 17
java -version 2>&1 | findstr /C:"17" >nul
if errorlevel 1 (
  echo ERROR: JDK 17 not found.
  echo.
  echo Set JAVA_HOME to your JDK 17 installation, for example:
  echo   set JAVA_HOME=C:\Program Files\Java\jdk-17
  echo   scripts\run-marlo-java17.bat
  echo.
  exit /b 1
)

echo Using Java:
java -version 2>&1 | findstr /C:"version"
echo.
echo Running: mvn clean install -DskipTests -pl marlo-web -am
echo.

call mvn clean install -DskipTests -pl marlo-web -am
if errorlevel 1 exit /b 1

REM Update marlo-dev.properties to HTTP on port 8080
call "%SCRIPT_DIR%update-marlo-dev-java17.bat"
if errorlevel 1 exit /b 1

echo.
echo Starting MARLO...
echo   HTTP: http://localhost:8080/marlo-web/
echo.

REM Open browser after ~20 seconds (run in background)
start /min cmd /c "timeout /t 20 /nobreak >nul && start http://localhost:8080/marlo-web/"

call mvn -pl marlo-web cargo:run
exit /b 0
