@echo off
REM ============================================================
REM  setup-demo-branches.bat
REM  Run ONCE before the presentation to create the demo-baseline
REM  branch that reset-demo.bat returns to between activities.
REM ============================================================

echo.
echo [setup] Creating demo-baseline branch from current HEAD...
git checkout -B demo-baseline
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to create demo-baseline branch.
    exit /b 1
)
echo [setup] demo-baseline created.
echo.
echo [setup] Verifying compile...
call mvn compile -q
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compile failed. Fix errors before running the demo.
    exit /b 1
)
echo [setup] Compile OK.
echo.
echo ============================================================
echo   Setup complete.
echo   Run before each activity:   scripts\reset-demo.bat [1-5]
echo   Example for Activity 1:     scripts\reset-demo.bat 1
echo ============================================================
