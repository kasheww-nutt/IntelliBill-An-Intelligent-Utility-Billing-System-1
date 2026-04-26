@echo off
setlocal EnableDelayedExpansion

REM =========================
REM IntelliBill Compile Script
REM =========================

set "PROJECT_DIR=%~dp0"
set "SRC_DIR=%PROJECT_DIR%src\main\java"
set "RES_DIR=%PROJECT_DIR%src\main\resources"
set "OUT_DIR=%PROJECT_DIR%out"

REM Override JAVAFX_LIB if you want to use a different JavaFX SDK location.
if not defined JAVAFX_LIB set "JAVAFX_LIB=%PROJECT_DIR%javafx-lib"

if not exist "%SRC_DIR%" (
  echo [ERROR] Source directory not found: %SRC_DIR%
  exit /b 1
)

if not exist "%JAVAFX_LIB%" (
  echo [ERROR] JavaFX lib path not found: %JAVAFX_LIB%
  exit /b 1
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

set "SOURCES="
for /r "%SRC_DIR%" %%f in (*.java) do (
  set "SOURCES=!SOURCES! "%%f""
)

echo [INFO] Compiling Java sources...
javac ^
  --module-path "%JAVAFX_LIB%" ^
  --add-modules javafx.controls,javafx.fxml ^
  -d "%OUT_DIR%" ^
  !SOURCES!

if errorlevel 1 (
  echo [ERROR] Compilation failed.
  exit /b 1
)

if exist "%RES_DIR%" (
  echo [INFO] Copying resources...
  xcopy "%RES_DIR%\*" "%OUT_DIR%\" /E /I /Y >nul
)

echo [SUCCESS] Compilation complete. Output: %OUT_DIR%
exit /b 0
