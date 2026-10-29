#!/bin/bash
# Usage: diff.sh <baseline-dir> <new-dir>   exit 0 and print nothing when identical; otherwise names every differing page.
[ $# = 2 ] || { echo "usage: diff.sh <baseline-dir> <new-dir>" >&2; exit 2; }
diff -rq "$1" "$2" && echo "IDENTICAL: $(ls "$1" | wc -l | tr -d ' ') files compared" >&2
