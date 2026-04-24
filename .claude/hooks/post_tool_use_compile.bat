@echo off
REM PostToolUse hook — compile after every Java file edit.
REM Filters Maven output to [ERROR] lines only.
REM Exit 1 feeds errors back into the agent loop for self-correction.

set FILE_PATH=%CLAUDE_FILE_PATH%

REM Only run on Java file edits
if not "%FILE_PATH:~-5%"==".java" exit /b 0

echo [hook] Compiling after edit to: %FILE_PATH%

mvn compile -q 2>&1 > "%TEMP%\mvn_compile_out.txt"
set EXIT_CODE=%ERRORLEVEL%

if %EXIT_CODE% neq 0 (
    findstr /R "^\[ERROR\]" "%TEMP%\mvn_compile_out.txt"
    if %ERRORLEVEL% neq 0 (
        REM No [ERROR] lines found — show last 20 lines
        powershell -Command "Get-Content '%TEMP%\mvn_compile_out.txt' | Select-Object -Last 20"
    )
    del "%TEMP%\mvn_compile_out.txt"
    exit /b 1
)

echo [hook] Compile: OK
del "%TEMP%\mvn_compile_out.txt"
exit /b 0
