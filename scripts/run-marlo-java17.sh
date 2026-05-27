#!/usr/bin/env bash
# Run MARLO with Java 17 in this branch (Cargo / Tomcat embedded).
# Convention: Java 17 run uses HTTP on port 8080; this script updates marlo-dev.properties
#             so base URLs are http://localhost:8080 (or localhost:8080).
# Usage: ./scripts/run-marlo-java17.sh   (from repo root)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

# ============================================================================
# Stop running server and clean target directory
# ============================================================================
echo "Checking for running MARLO server..."
if pgrep -f "cargo:run" > /dev/null; then
  echo "Stopping existing MARLO server..."
  pkill -f "cargo:run" || true
  sleep 3
  echo "✅ Server stopped"
else
  echo "No running server found"
fi

echo ""
echo "Cleaning target directory..."
# Function to clean target with retries if directory is locked
clean_target() {
  local max_attempts=5
  local attempt=1
  
  while [ $attempt -le $max_attempts ]; do
    if rm -rf marlo-web/target 2>/dev/null; then
      echo "✅ Target directory cleaned"
      return 0
    else
      echo "Target directory locked, waiting... (attempt $attempt/$max_attempts)"
      sleep 2
      attempt=$((attempt + 1))
    fi
  done
  
  echo "⚠️  Warning: Could not clean target directory after $max_attempts attempts"
  echo "   Continuing anyway..."
  return 0
}

clean_target
echo ""
# ============================================================================

# Prefer Java 17 via JAVA_HOME; otherwise try to detect it
if [ -z "$JAVA_HOME" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"17'; then
  if [ -x "/usr/libexec/java_home" ]; then
    export JAVA_HOME="$(/usr/libexec/java_home -v 17 2>/dev/null)" || true
  fi
  if [ -z "$JAVA_HOME" ] && [ -d "/usr/lib/jvm/java-17-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
  fi
  # macOS: look for JDK 17 in common locations
  if [ -z "$JAVA_HOME" ] && [ -d "/Library/Java/JavaVirtualMachines" ]; then
    for jdk in /Library/Java/JavaVirtualMachines/*/Contents/Home; do
      if "$jdk/bin/java" -version 2>&1 | grep -q '"17'; then
        export JAVA_HOME="$jdk"
        break
      fi
    done
  fi
fi

# Ensure Java 17 is in use
if [ -z "$JAVA_HOME" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"17'; then
  echo "ERROR: JDK 17 not found."
  echo ""
  echo "Option 1 - Set JAVA_HOME and run again:"
  echo "  export JAVA_HOME=/path/to/jdk-17"
  echo "  ./scripts/run-marlo-java17.sh"
  echo ""
  echo "Option 2 - Install JDK 17 (macOS): brew install openjdk@17"
  echo "  Then: export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
  exit 1
fi

echo "Using Java: $(java -version 2>&1 | head -1)"
echo "Running: mvn clean install -DskipTests -pl marlo-web -am"
echo ""

# Normalize marlo-dev.properties to HTTP on port 8080 before build/run
if [ -x "$SCRIPT_DIR/update-marlo-dev-java17.sh" ]; then
  echo ""
  "$SCRIPT_DIR/update-marlo-dev-java17.sh"
elif [ -f "$SCRIPT_DIR/update-marlo-dev-java17.sh" ]; then
  echo ""
  bash "$SCRIPT_DIR/update-marlo-dev-java17.sh"
fi

mvn clean install -DskipTests -pl marlo-web -am

echo ""
echo "Starting MARLO..."
echo "  HTTP: http://localhost:8080/marlo-web/"
echo ""

# Open browser once the server is actually responding (macOS: open, Linux: xdg-open)
(
  echo "Waiting for MARLO to be ready..."
  until curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/marlo-web/" 2>/dev/null | grep -qE "^[23]"; do
    sleep 3
  done
  echo "✅ MARLO is ready — opening browser"
  if command -v open &>/dev/null; then
    open "http://localhost:8080/marlo-web/"
  elif command -v xdg-open &>/dev/null; then
    xdg-open "http://localhost:8080/marlo-web/"
  fi
) &

mvn -pl marlo-web cargo:run
