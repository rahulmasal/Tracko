@echo off
REM Tracko Development Setup Script (Windows)
REM ============================================

setlocal enabledelayedexpansion

echo ========================================
echo   Tracko - Development Setup
echo ========================================
echo.

REM Check prerequisites
echo [1/6] Checking prerequisites...

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] Java not found. Install JDK 17+ from https://adoptium.net/
) else (
    java -version 2>&1 | findstr "version" >nul
    echo [OK] Java found
)

where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Node.js not found. Install Node.js 20+ from https://nodejs.org/
    exit /b 1
) else (
    for /f "tokens=1,2,3 delims=." %%a in ('node -v') do set NODE_VER=%%a
    echo [OK] Node.js %NODE_VER% found
)

where npm >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] npm not found.
    exit /b 1
) else (
    echo [OK] npm found
)

where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] Docker not found. Install Docker Desktop from https://www.docker.com/
) else (
    echo [OK] Docker found
)

echo.

REM Copy environment file
echo [2/6] Setting up environment configuration...
if not exist "..\.env" (
    copy "..\.env.example" "..\.env" >nul
    echo [OK] Created .env from .env.example
) else (
    echo [SKIP] .env already exists
)

REM Start Docker services
echo [3/6] Starting Docker services (PostgreSQL, Redis, MinIO)...
docker-compose -f "..\docker-compose.yml" up -d tracko-db tracko-redis tracko-minio
if %ERRORLEVEL% EQU 0 (
    echo [OK] Docker services started
) else (
    echo [WARNING] Failed to start Docker services. Check Docker is running.
)

REM Install npm dependencies
echo [4/6] Installing Manager Portal dependencies...
cd "..\web\manager-portal"
call npm install
if %ERRORLEVEL% EQU 0 (
    echo [OK] Manager Portal dependencies installed
) else (
    echo [ERROR] Failed to install Manager Portal dependencies
    exit /b 1
)

echo [5/6] Installing Admin Portal dependencies...
cd "..\admin-portal"
call npm install
if %ERRORLEVEL% EQU 0 (
    echo [OK] Admin Portal dependencies installed
) else (
    echo [ERROR] Failed to install Admin Portal dependencies
    exit /b 1
)

cd "..\..\scripts"

echo.
echo [6/6] Setup Complete!
echo.
echo ========================================
echo   Services starting on:
echo   -------------------------
echo   Manager Portal:  http://localhost:3000
echo   Admin Portal:    http://localhost:3001
echo   API:             http://localhost:8080/api
echo   PostgreSQL:      localhost:5432
echo   Redis:           localhost:6379
echo   MinIO Console:   http://localhost:9001
echo   -------------------------
echo   Default Login:   admin@tracko.com / Admin@123
echo ========================================
echo.
echo To start the web servers in development mode:
echo   cd web\manager-portal ^&^& npm run dev
echo   cd web\admin-portal ^&^& npm run dev
echo.
pause
