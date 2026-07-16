@echo off
REM Run MARLO (Java 17) in this branch - Windows version
REM Usage: run-marlo.bat   (from repo root)

setlocal enabledelayedexpansion

cd /d "%~dp0"

REM Check if JAVA_HOME is set and points to Java 17
set "JAVA17_FOUND="
if defined JAVA_HOME (
    "%JAVA_HOME%\bin\java.exe" -version 2>&1 | findstr /C:"17" >nul
    if !errorlevel! equ 0 (
        set "JAVA17_FOUND=1"
        echo Found Java 17 in JAVA_HOME: %JAVA_HOME%
    )
)

REM If not found, search common locations
if not defined JAVA17_FOUND (
    echo Searching for Java 17...
    
    REM Check Program Files\Java
    for /d %%d in ("C:\Program Files\Java\jdk-17*") do (
        if exist "%%d\bin\java.exe" (
            set "JAVA_HOME=%%d"
            set "JAVA17_FOUND=1"
            echo Found Java 17: %%d
            goto :java_found
        )
    )
    
    REM Check Program Files\Eclipse Adoptium
    for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do (
        if exist "%%d\bin\java.exe" (
            set "JAVA_HOME=%%d"
            set "JAVA17_FOUND=1"
            echo Found Java 17: %%d
            goto :java_found
        )
    )
    
    REM Check Program Files (x86)\Java
    for /d %%d in ("C:\Program Files (x86)\Java\jdk-17*") do (
        if exist "%%d\bin\java.exe" (
            set "JAVA_HOME=%%d"
            set "JAVA17_FOUND=1"
            echo Found Java 17: %%d
            goto :java_found
        )
    )
)

:java_found
if not defined JAVA17_FOUND (
    echo ERROR: No se encontro JDK 17.
    echo.
    echo Opcion 1 - Configura JAVA_HOME manualmente:
    echo   set JAVA_HOME=C:\Program Files\Java\jdk-17
    echo   run-marlo.bat
    echo.
    echo Opcion 2 - Descarga e instala JDK 17:
    echo   https://adoptium.net/temurin/releases/?version=17
    echo.
    pause
    exit /b 1
)

REM Display Java version
echo.
echo Using Java:
"%JAVA_HOME%\bin\java.exe" -version
echo.

echo Running: mvn clean install -DskipTests -pl marlo-web -am
echo.

call mvn clean install -DskipTests -pl marlo-web -am
if !errorlevel! neq 0 (
    echo.
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo Build successful! Starting MARLO...
echo   HTTP: http://localhost:8080/marlo-web/
echo   HTTPS: https://localhost:8443/marlo-web/
echo.
echo Press Ctrl+C to stop the server
echo.

call mvn -pl marlo-web cargo:run
