#!/usr/bin/env bash
# ============================================================
#  reset-demo.sh — Reset the project to a clean demo state
#  Usage: ./scripts/reset-demo.sh [activity-number]
#  Examples:
#    ./scripts/reset-demo.sh        — full reset (all activities)
#    ./scripts/reset-demo.sh 1      — reset to Activity 1 start state
#    ./scripts/reset-demo.sh 2      — reset to Activity 2 start state
#    ./scripts/reset-demo.sh 3      — reset to Activity 3 start state
#    ./scripts/reset-demo.sh 4      — reset to Activity 4 start state
#    ./scripts/reset-demo.sh 5      — reset to Activity 5 start state
# ============================================================

set -euo pipefail

ACTIVITY="${1:-all}"
BOLD="\033[1m"
GREEN="\033[32m"
YELLOW="\033[33m"
CYAN="\033[36m"
RESET="\033[0m"

echo ""
echo -e "${BOLD}============================================================${RESET}"
echo -e "${BOLD}  Claude Code Spring Boot Demo — Reset Script${RESET}"
echo -e "${BOLD}============================================================${RESET}"
echo -e "  Target: Activity ${CYAN}${ACTIVITY}${RESET}"
echo ""

# ── Step 1: Kill any running Spring Boot on port 8080 ──────────────────────
echo -e "${BOLD}[1/4]${RESET} Stopping any running Spring Boot instance on port 8080..."
if command -v lsof &>/dev/null; then
    PID=$(lsof -ti tcp:8080 2>/dev/null || true)
    [ -n "$PID" ] && kill "$PID" && echo "      Killed PID $PID" || echo "      Nothing running."
else
    # Windows Git Bash fallback
    PIDS=$(netstat -ano 2>/dev/null | grep ":8080 " | grep LISTENING | awk '{print $5}' || true)
    for PID in $PIDS; do
        taskkill //PID "$PID" //F &>/dev/null && echo "      Killed PID $PID"
    done
    [ -z "$PIDS" ] && echo "      Nothing running."
fi
echo ""

# ── Step 2: Git reset to clean baseline ────────────────────────────────────
echo -e "${BOLD}[2/4]${RESET} Resetting git to clean baseline..."
git stash &>/dev/null || true
if git show-ref --verify --quiet refs/heads/demo-baseline; then
    git checkout demo-baseline &>/dev/null
else
    echo "      demo-baseline branch not found — using current HEAD."
    git checkout master &>/dev/null || git checkout main &>/dev/null || true
fi
git stash drop &>/dev/null || true
echo -e "      ${GREEN}Done.${RESET}"
echo ""

# ── Step 3: Clean build ─────────────────────────────────────────────────────
echo -e "${BOLD}[3/4]${RESET} Cleaning build artifacts..."
mvn clean -q
echo -e "      ${GREEN}Done.${RESET}"
echo ""

# ── Step 4: Activity-specific branch ───────────────────────────────────────
echo -e "${BOLD}[4/4]${RESET} Setting up branch for Activity ${ACTIVITY}..."

case "$ACTIVITY" in
1)
    git checkout -B activity-1-feature-dev &>/dev/null
    echo -e "      Branch:  ${CYAN}activity-1-feature-dev${RESET}"
    echo    "      Ready for: POST /api/v1/orders feature development"
    echo    "      Prompt:    docs/prompts/activity1-feature-development.md"
    ;;
2)
    git checkout -B activity-2-bug-fix &>/dev/null
    echo -e "      Branch:  ${CYAN}activity-2-bug-fix${RESET}"
    echo    "      Ready for: PaymentServiceImpl NPE bug fix"
    echo    "      Prompt:    docs/prompts/activity2-bug-fix.md"
    echo ""
    echo -e "      ${YELLOW}[NOTE]${RESET} The bug is at:"
    echo    "        src/main/java/.../service/impl/PaymentServiceImpl.java:87"
    ;;
3)
    git checkout -B activity-3-code-review &>/dev/null
    echo -e "      Branch:  ${CYAN}activity-3-code-review${RESET}"
    echo    "      Ready for: Payment retry PR code review"
    echo    "      Prompt:    docs/prompts/activity3-code-review.md"
    echo ""
    echo -e "      ${YELLOW}[NOTE]${RESET} Files to review:"
    echo    "        src/main/java/.../config/KafkaRetryConfig.java"
    echo    "        src/main/java/.../service/impl/RetryablePaymentService.java"
    ;;
4)
    git checkout -B activity-4-migration &>/dev/null
    echo -e "      Branch:  ${CYAN}activity-4-migration${RESET}"
    echo    "      Ready for: Java 11 + SB 2.7 → 17 + 3.2 migration walkthrough"
    echo    "      Prompt:    docs/prompts/activity4-migration.md"
    ;;
5)
    git checkout -B activity-5-documentation &>/dev/null
    echo -e "      Branch:  ${CYAN}activity-5-documentation${RESET}"
    echo    "      Ready for: InventoryController API documentation generation"
    echo    "      Prompt:    docs/prompts/activity5-documentation.md"
    echo ""
    echo -e "      ${YELLOW}[NOTE]${RESET} Undocumented controller:"
    echo    "        src/main/java/.../controller/InventoryController.java"
    ;;
all)
    echo    "      Full reset complete."
    echo    "      Run with an activity number (1-5) to set up a specific branch."
    ;;
*)
    echo "Unknown activity: $ACTIVITY. Use 1, 2, 3, 4, 5, or omit for full reset."
    exit 1
    ;;
esac

echo ""
echo -e "${BOLD}============================================================${RESET}"
echo    "  Reset complete. Starting application..."
echo -e "  URL:  ${CYAN}http://localhost:8080/actuator/health${RESET}"
echo    "  Auth: engineer / demo-pass  (ROLE_USER)"
echo    "        admin    / admin-pass  (ROLE_ADMIN)"
echo -e "${BOLD}============================================================${RESET}"
echo ""

# ── Start the app ───────────────────────────────────────────────────────────
echo "Starting Spring Boot (H2 in-memory — no Docker required)..."
echo "Press Ctrl+C to stop."
echo ""
mvn spring-boot:run \
    -Dspring-boot.run.arguments="--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
