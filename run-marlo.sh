#!/usr/bin/env bash
# Run MARLO (Java 17) in this branch.
# Usage: ./run-marlo.sh   (from repo root)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Prefer Java 17 via JAVA_HOME; otherwise try to detect it
if [ -z "$JAVA_HOME" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"17'; then
  if [ -x "/usr/libexec/java_home" ]; then
    export JAVA_HOME="$(/usr/libexec/java_home -v 17 2>/dev/null)" || true
  fi
  if [ -z "$JAVA_HOME" ] && [ -d "/usr/lib/jvm/java-17-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
  fi
  # macOS: scan common JDK 17 locations
  if [ -z "$JAVA_HOME" ] && [ -d "/Library/Java/JavaVirtualMachines" ]; then
    for jdk in /Library/Java/JavaVirtualMachines/*/Contents/Home; do
      if "$jdk/bin/java" -version 2>&1 | grep -q '"17'; then
        export JAVA_HOME="$jdk"
        break
      fi
    done
  fi
fi

# Verify Java 17 is selected
if [ -z "$JAVA_HOME" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"17'; then
  echo "ERROR: No se encontró JDK 17."
  echo ""
  echo "Opción 1 - Configura JAVA_HOME manualmente:"
  echo "  export JAVA_HOME=/ruta/a/jdk-17"
  echo "  ./run-marlo.sh"
  echo ""
  echo "Opción 2 - Instala JDK 17 (macOS): brew install openjdk@17"
  echo "  Luego: export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
  exit 1
fi

echo "Using Java: $(java -version 2>&1 | head -1)"
echo "Running: mvn clean install -DskipTests -pl marlo-web -am"
echo ""

mvn clean install -DskipTests -pl marlo-web -am

echo ""
echo "Starting MARLO..."
echo "  HTTP: http://localhost:8080/marlo-web/"
echo ""

mvn -pl marlo-web cargo:run
