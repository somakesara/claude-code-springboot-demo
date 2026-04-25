@echo off
REM ============================================================
REM  reset-demo.bat — Reset the project to a clean demo state
REM  Usage: reset-demo.bat [activity-number]
REM  Examples:
REM    reset-demo.bat        — full reset (all activities)
REM    reset-demo.bat 1      — reset to Activity 1 start state
REM    reset-demo.bat 2      — reset to Activity 2 start state
REM    reset-demo.bat 3      — reset to Activity 3 start state
REM    reset-demo.bat 4      — reset to Activity 4 start state
REM    reset-demo.bat 5      — reset to Activity 5 start state
REM ============================================================

setlocal enabledelayedexpansion

set ACTIVITY=%1
if "%ACTIVITY%"=="" set ACTIVITY=all

echo.
echo ============================================================
echo   Claude Code Spring Boot Demo — Reset Script
echo ============================================================
echo   Target: Activity %ACTIVITY%
echo ============================================================
echo.

REM ── Step 1: Kill any running Spring Boot process on port 8080 ──
echo [1/4] Stopping any running Spring Boot instance on port 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo       Killing PID %%a
    taskkill /PID %%a /F >nul 2>&1
)
echo       Done.
echo.

REM ── Step 2: Git reset to clean baseline ──
echo [2/4] Resetting git to clean master baseline...
git stash >nul 2>&1
git checkout master >nul 2>&1
git checkout -- . >nul 2>&1
git stash drop >nul 2>&1
echo       Done.
echo.

REM ── Step 3: Clean Maven build artifacts ──
echo [3/4] Cleaning build artifacts...
call mvn clean -q
echo       Done.
echo.

REM ── Step 4: Activity-specific branch setup ──
echo [4/4] Setting up branch for Activity %ACTIVITY%...

if "%ACTIVITY%"=="1" (
    git checkout -B activity-1-feature-dev >nul 2>&1
    echo       Branch: activity-1-feature-dev
    echo       Ready for: POST /api/v1/orders feature development
    echo       Prompt:    docs\prompts\activity1-feature-development.md
)
if "%ACTIVITY%"=="2" (
    git checkout -B activity-2-bug-fix >nul 2>&1
    echo       Branch: activity-2-bug-fix
    echo       Ready for: PaymentServiceImpl NPE bug fix
    echo       Prompt:    docs\prompts\activity2-bug-fix.md
    echo.
    echo       [NOTE] The bug is in:
    echo         src\main\java\com\company\ordermanagement\service\impl\PaymentServiceImpl.java line 87
)
if "%ACTIVITY%"=="3" (
    git checkout -B activity-3-code-review >nul 2>&1
    echo       Branch: activity-3-code-review
    echo       Ready for: Payment retry PR code review
    echo       Prompt:    docs\prompts\activity3-code-review.md
    echo.
    echo       [NOTE] Files to review:
    echo         src\main\java\...\config\KafkaRetryConfig.java
    echo         src\main\java\...\service\impl\RetryablePaymentService.java
)
if "%ACTIVITY%"=="4" (
    git checkout -B activity-4-migration >nul 2>&1
    echo       Branch: activity-4-migration
    echo       Ready for: Java/Spring Boot migration walkthrough (conceptual)
    echo       Prompt:    docs\prompts\activity4-migration.md
)
if "%ACTIVITY%"=="5" (
    git checkout -B activity-5-documentation >nul 2>&1
    echo       Branch: activity-5-documentation
    echo       Ready for: InventoryController API documentation generation
    echo       Prompt:    docs\prompts\activity5-documentation.md
    echo.
    echo       [NOTE] Undocumented controller:
    echo         src\main\java\...\controller\InventoryController.java
)
if "%ACTIVITY%"=="all" (
    echo       Full reset complete. Run with activity number to set up a specific branch.
)

echo.
echo ============================================================
echo   Reset complete. Starting application...
echo   URL:  http://localhost:8080/actuator/health
echo   Auth: engineer / demo-pass  (ROLE_USER)
echo         admin    / admin-pass  (ROLE_ADMIN)
echo ============================================================
echo.

REM ── Start the app ──
echo Starting Spring Boot (H2 in-memory — no Docker required)...
echo Press Ctrl+C to stop.
echo.
call mvn spring-boot:run -Dspring-boot.run.arguments="--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
