@echo off
title Workshop Registration and Certificate Issue System - TRIX
echo ==============================================================
echo  Launching TRIX Workshop Registration and Certificate Issue System
echo ==============================================================
echo.

:: 1. Check if the backend is already running on port 8080
netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if %errorlevel% equ 0 (
    echo Workshop System backend is already running on http://localhost:8080
    echo Opening browser...
    start http://localhost:8080
    exit /b 0
)

:: 2. Ensure MySQL is running on port 3306
echo [1/3] Checking MySQL database status (Port 3306)...
netstat -ano | findstr /R /C:":3306 .*LISTENING" >nul
if %errorlevel% neq 0 (
    echo Starting MySQL Server in background...
    start "MySQL Server" /B "C:\Users\Shefeex\Downloads\mysql-8.0.46-winx64\mysql-8.0.46-winx64\bin\mysqld.exe" --defaults-file="C:\Users\Shefeex\Downloads\mysql-8.0.46-winx64\mysql-8.0.46-winx64\my.ini"
    ping 127.0.0.1 -n 4 >nul
) else (
    echo MySQL is running on port 3306.
)

:: 3. Auto open browser after a short delay
echo [2/3] Preparing browser launch...
start "" cmd /c "ping 127.0.0.1 -n 7 >nul && start http://localhost:8080"

:: 4. Launch Spring Boot application
echo [3/3] Starting Spring Boot application on http://localhost:8080 ...
echo.
echo Application logs:
echo --------------------------------------------------------------
cd /d "%~dp0"
java -jar "target\workshop-system-1.0.0.jar"

pause
