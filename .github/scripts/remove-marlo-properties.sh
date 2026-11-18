#!/usr/bin/env bash
# Removes the MARLO DEV properties written by write-marlo-properties.sh. Runs as an `if: always()` step, so it also
# runs when a previous step failed. Never fails the job. The build runs inside `docker build` (Maven in the build
# stage), so no workspace cache holds a copy of the file. Adapted from remove-frontend-config.sh (onecgiar_pr@ef5597273).
set -uo pipefail
rm -f "marlo-web/src/main/resources/config/marlo-dev.properties"
echo "MARLO properties removed"
