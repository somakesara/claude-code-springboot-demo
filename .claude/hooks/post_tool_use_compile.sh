#!/usr/bin/env bash
# PostToolUse hook — compile after every Java file edit.
# Filters Maven output to [ERROR] lines only (~50-300 tokens vs 3000-15000 raw).
# Exit 1 feeds errors back into the agent loop for self-correction.

TOOL_NAME="${CLAUDE_TOOL_NAME:-}"
FILE_PATH="${CLAUDE_FILE_PATH:-}"

# Only run on Java file edits
if [[ "$FILE_PATH" != *.java ]]; then
    exit 0
fi

echo "[hook] Compiling after edit to: $FILE_PATH"

OUTPUT=$(mvn compile -q 2>&1)
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    ERROR_LINES=$(echo "$OUTPUT" | grep -E "^\[ERROR\]" | head -30)
    if [ -z "$ERROR_LINES" ]; then
        echo "$OUTPUT" | tail -20
    else
        echo "$ERROR_LINES"
    fi
    exit 1
fi

echo "[hook] Compile: OK"
exit 0
