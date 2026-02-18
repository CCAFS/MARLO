@echo off
REM Run MARLO with Java 8 (Tomcat 7 Maven plugin, not Cargo).
REM Convention: Java 8 uses HTTPS on port 8443.
REM Usage: scripts\run-marlo-java8.bat   (from repo root)

setlocal EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "REPO_ROOT=%SCRIPT_DIR%.."
cd /d "%REPO_ROOT%"

REM Use JAVA_HOME if set
if defined JAVA_HOME (
  set "PATH=%JAVA_HOME%\bin;%PATH%"
)

REM Check Java 8 (1.8)
java -version 2>&1 | findstr /C:"1.8" >nul
if errorlevel 1 (
  echo ERROR: JDK 8 ^(Java 1.8^) not found.
  echo.
  echo Set JAVA_HOME to your JDK 8 installation, for example:
  echo   set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_xxx
  echo   scripts\run-marlo-java8.bat
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

REM Update marlo-dev.properties to HTTPS on port 8443
call "%SCRIPT_DIR%update-marlo-dev-java8.bat"
if errorlevel 1 exit /b 1

echo.
echo Starting MARLO ^(Tomcat 7 plugin^)...
echo   HTTP:  http://localhost:8080/marlo-web/
echo   HTTPS: https://localhost:8443/marlo-web/
echo.

REM Open browser after ~20 seconds (run in background)
start /min cmd /c "timeout /t 20 /nobreak >nul && start https://localhost:8443/marlo-web/"

call mvn -pl marlo-web tomcat7:run -P java8
exit /b 0
