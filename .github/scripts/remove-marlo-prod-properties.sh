#!/usr/bin/env bash
# Removes the MARLO PROD properties written by write-marlo-prod-properties.sh. Runs as an `if: always()` step, so it
# also runs when a previous step failed. Never fails the job. The build runs inside `docker build` (Maven in the build
# stage), so no workspace cache holds a copy of the file. Copy of remove-marlo-properties.sh (MARLO DEV); only the file
# name differs.
set -uo pipefail
rm -f "marlo-web/src/main/resources/config/marlo-pro.properties"
echo "MARLO properties removed"
