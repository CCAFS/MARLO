#!/usr/bin/env bash
# Run MARLO with Java 8 in this branch (uses Tomcat 7 Maven plugin, not Cargo).
# Convention: Java 8 run uses HTTPS on port 8443; this script updates marlo-dev.properties
#             so base URLs are https://localhost:8443.
# Usage: ./scripts/run-marlo-java8.sh   (from repo root)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

# ============================================================================
# Stop running server and clean target directory
# ============================================================================
echo "Checking for running MARLO server..."
if pgrep -f "tomcat7:run" > /dev/null; then
  echo "Stopping existing MARLO server..."
  pkill -f "tomcat7:run" || true
  sleep 3
  echo "✅ Server stopped"
else
  echo "No running server found"
fi

echo ""
echo "Cleaning target directory..."
# Function to clean all module target directories with retries if locked
# Cleaning all modules here lets us use 'mvn install' (no clean phase)
# and avoids the race condition where the IDE auto-build recreates
# marlo-web/target/classes while Maven is compiling marlo-data.
clean_target() {
  local max_attempts=5
  local attempt=1
  local dirs="marlo-utils/target marlo-data/target marlo-web/target"

  while [ $attempt -le $max_attempts ]; do
    if rm -rf $dirs 2>/dev/null; then
      echo "✅ Target directories cleaned"
      return 0
    else
      echo "Target directory locked, waiting... (attempt $attempt/$max_attempts)"
      sleep 2
      attempt=$((attempt + 1))
    fi
  done

  echo "⚠️  Warning: Could not clean target directories after $max_attempts attempts"
  echo "   Continuing anyway..."
  return 0
}

clean_target
echo ""
# ============================================================================

# Prefer Java 8 via JAVA_HOME; otherwise try to detect it
if [ -z "$JAVA_HOME" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -qE '"1\.8|"8\.'; then
  if [ -x "/usr/libexec/java_home" ]; then
    export JAVA_HOME="$(/usr/libexec/java_home -v 1.8 2>/dev/null)" || true
  fi
  if [ -z "$JAVA_HOME" ] && [ -d "/usr/lib/jvm/java-8-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-8-openjdk"
  fi
  if [ -z "$JAVA_HOME" ] && [ -d "/usr/lib/jvm/jdk-8" ]; then
    export JAVA_HOME="/usr/lib/jvm/jdk-8"
  fi
  # macOS: look for JDK 8 in common locations
  if [ -z "$JAVA_HOME" ] && [ -d "/Library/Java/JavaVirtualMachines" ]; then
    for jdk in /Library/Java/JavaVirtualMachines/*/Contents/Home; do
      [ -x "$jdk/bin/java" ] || continue
      if "$jdk/bin/java" -version 2>&1 | grep -qE '"1\.8|"8\.'; then
        export JAVA_HOME="$jdk"
        break
      fi
    done
  fi
fi

if [ -n "$JAVA_HOME" ]; then
  export PATH="$JAVA_HOME/bin:$PATH"
fi

# Ensure Java 8 is in use
if ! java -version 2>&1 | grep -qE '"1\.8|"8\.'; then
  echo "ERROR: JDK 8 (Java 1.8) not found."
  echo ""
  echo "Option 1 - If you have JDK 8, set JAVA_HOME and run again:"
  echo "  export JAVA_HOME=\$(/usr/libexec/java_home -v 1.8)"
  echo "  ./scripts/run-marlo-java8.sh"
  echo ""
  echo "Option 2 - Install JDK 8 (macOS): brew install openjdk@8"
  echo "  Then: export JAVA_HOME=\$(/usr/libexec/java_home -v 1.8)"
  exit 1
fi

echo "Using Java: $(java -version 2>&1 | head -1)"
echo "Running: mvn install -DskipTests -pl marlo-web -am"
echo ""

# Use 'install' (no clean phase) because the script already cleaned all targets above.
# This avoids the IDE auto-build race condition where target/classes gets recreated
# in the seconds Maven spends compiling marlo-data before reaching marlo-web's clean.
mvn install -DskipTests -pl marlo-web -am

# Update marlo-dev.properties to HTTPS on port 8443 (Java 8 convention)
if [ -x "$SCRIPT_DIR/update-marlo-dev-java8.sh" ]; then
  echo ""
  "$SCRIPT_DIR/update-marlo-dev-java8.sh"
elif [ -f "$SCRIPT_DIR/update-marlo-dev-java8.sh" ]; then
  echo ""
  bash "$SCRIPT_DIR/update-marlo-dev-java8.sh"
fi

echo ""
echo "Starting MARLO (Tomcat 7 plugin, as before Java 17 migration)..."
echo "  HTTP:  http://localhost:8080/marlo-web/"
echo "  HTTPS: https://localhost:8443/marlo-web/"
echo ""

# Open browser after server has had time to start (macOS: open, Linux: xdg-open)
( sleep 20; if command -v open &>/dev/null; then open "https://localhost:8443/marlo-web/"; elif command -v xdg-open &>/dev/null; then xdg-open "https://localhost:8443/marlo-web/"; fi ) &

mvn -pl marlo-web tomcat7:run -P java8
