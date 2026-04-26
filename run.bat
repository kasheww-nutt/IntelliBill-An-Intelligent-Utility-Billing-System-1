@echo off
setlocal

REM ======================
REM IntelliBill Run Script
REM ======================

set "PROJECT_DIR=%~dp0"
set "OUT_DIR=%PROJECT_DIR%out"

REM Override these values if you want to use external dependency locations.
if not defined JAVAFX_LIB set "JAVAFX_LIB=%PROJECT_DIR%javafx-lib"
if not defined MYSQL_JAR set "MYSQL_JAR=%PROJECT_DIR%lib\mysql-connector-j.jar"

set "MAIN_CLASS=com.intellibill.main.Launcher"

REM JDBC environment variables consumed by DatabaseConnection.java
set "INTELLIBILL_DB_URL=jdbc:mysql://localhost:3306/intellibill_db?useSSL=false&serverTimezone=UTC"
set "INTELLIBILL_DB_USER=root"
set "INTELLIBILL_DB_PASSWORD="

call "%PROJECT_DIR%compile.bat"
if errorlevel 1 exit /b 1

set "RUNTIME_CP=%OUT_DIR%"
if exist "%MYSQL_JAR%" (
  set "RUNTIME_CP=%RUNTIME_CP%;%MYSQL_JAR%"
) else (
  echo [WARN] MySQL Connector/J not found. MySQL mode will be unavailable: %MYSQL_JAR%
)

echo [INFO] Running IntelliBill...
java ^
  --enable-native-access=javafx.graphics ^
  --module-path "%JAVAFX_LIB%" ^
  --add-modules javafx.controls,javafx.fxml ^
  -cp "%RUNTIME_CP%" ^
  %MAIN_CLASS%

exit /b %errorlevel%
