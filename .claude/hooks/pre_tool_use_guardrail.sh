#!/usr/bin/env bash
# PreToolUse hook — block destructive Bash commands.
# Runs before any Bash tool call. Exit 1 blocks the command and explains why.

COMMAND="${CLAUDE_COMMAND:-}"

BLOCKED_PATTERNS=(
    "rm -rf"
    "DROP TABLE"
    "TRUNCATE"
    "DELETE FROM"
    "curl.*|.*sh"
    "chmod 777"
    "sudo"
    "git push --force"
    "git reset --hard"
    "git clean -f"
    "DROP DATABASE"
)

for pattern in "${BLOCKED_PATTERNS[@]}"; do
    if echo "$COMMAND" | grep -qiE "$pattern"; then
        echo "[guardrail] BLOCKED: Command matches destructive pattern '$pattern'"
        echo "[guardrail] Command: $COMMAND"
        echo "[guardrail] Request human approval before running this command."
        exit 1
    fi
done

exit 0
