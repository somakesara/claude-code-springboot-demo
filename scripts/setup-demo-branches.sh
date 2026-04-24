#!/usr/bin/env bash
# ============================================================
#  setup-demo-branches.sh
#  Run ONCE before the presentation to create the demo-baseline
#  branch that reset-demo.sh returns to between activities.
# ============================================================

set -euo pipefail

echo ""
echo "[setup] Creating demo-baseline branch from current HEAD..."
git checkout -B demo-baseline
echo "[setup] demo-baseline created."
echo ""
echo "[setup] Verifying compile..."
mvn compile -q
echo "[setup] Compile OK."
echo ""
echo "============================================================"
echo "  Setup complete."
echo "  Run before each activity:   ./scripts/reset-demo.sh [1-5]"
echo "  Example for Activity 1:     ./scripts/reset-demo.sh 1"
echo "============================================================"
