@echo off
REM PreToolUse hook — block destructive Bash commands.
REM Runs before any Bash tool call. Exit 1 blocks the command.

set COMMAND=%CLAUDE_COMMAND%

REM Check each blocked pattern
echo %COMMAND% | findstr /I "rm -rf"           >nul && goto BLOCKED
echo %COMMAND% | findstr /I "DROP TABLE"       >nul && goto BLOCKED
echo %COMMAND% | findstr /I "TRUNCATE"         >nul && goto BLOCKED
echo %COMMAND% | findstr /I "DELETE FROM"      >nul && goto BLOCKED
echo %COMMAND% | findstr /I "chmod 777"        >nul && goto BLOCKED
echo %COMMAND% | findstr /I "git push --force" >nul && goto BLOCKED
echo %COMMAND% | findstr /I "git reset --hard" >nul && goto BLOCKED
echo %COMMAND% | findstr /I "git clean -f"     >nul && goto BLOCKED
echo %COMMAND% | findstr /I "DROP DATABASE"    >nul && goto BLOCKED

exit /b 0

:BLOCKED
echo [guardrail] BLOCKED: Command matches a destructive pattern.
echo [guardrail] Command: %COMMAND%
echo [guardrail] Request human approval before running this command.
exit /b 1
