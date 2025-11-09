#!/usr/bin/env bash
# Build and assemble an efa distribution locally
#
# This script compiles the sources, creates efa.jar and efahelp.jar,
# and assembles a runnable distribution under dist/efa matching the
# structure expected by root/runefa.sh.
#
# Usage:
#   ./build.sh            # normal build
#   ./build.sh --clean    # clean previous build outputs then build
#
# Requirements:
#   - JDK (javac/java) 8+ (11 or 17 recommended)
#   - Bash, Unix-like environment (macOS/Linux)

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

PROG="$(basename "$0")"
DIST_DIR="$ROOT_DIR/dist/efa"
BUILD_CLASSES="$ROOT_DIR/build/classes"
PROGRAM_DIR="$ROOT_DIR/program"
PROGRAM_PLUGINS="$PROGRAM_DIR/plugins"
DIST_PROGRAM="$DIST_DIR/program"
DIST_PLUGINS="$DIST_PROGRAM/plugins"

# ---------------------------------------------
# Helpers
# ---------------------------------------------
log(){ echo "[$(date +%Y-%m-%d_%H:%M:%S) $PROG] $*"; }

need_cmd(){ command -v "$1" >/dev/null 2>&1 || { echo "Error: required command '$1' not found in PATH" >&2; exit 2; }; }

clean(){
  log "Cleaning previous build outputs ..."
  rm -rf "$BUILD_CLASSES" "$PROGRAM_DIR" "$DIST_DIR"
}

if [[ ${1-} == "--clean" ]]; then
  clean
fi

need_cmd javac
need_cmd jar
need_cmd java

# ---------------------------------------------
# Compose compile-time classpath from plugins
# ---------------------------------------------
# Include all jars in plugins/*/*.jar
PLUGINS_CP=""
if compgen -G "plugins/*/*.jar" > /dev/null; then
  # Build a colon-separated list
  while IFS= read -r -d '' jarfile; do
    if [[ -z "$PLUGINS_CP" ]]; then
      PLUGINS_CP="$jarfile"
    else
      PLUGINS_CP="$PLUGINS_CP:$jarfile"
    fi
  done < <(find plugins -type f -name "*.jar" -print0 | sort -z)
else
  log "Warning: No plugin jars found under plugins/*/*.jar — compilation may fail if dependencies are missing."
fi

# ---------------------------------------------
# Compile sources
# ---------------------------------------------
log "Compiling Java sources ..."
mkdir -p "$BUILD_CLASSES" "$PROGRAM_PLUGINS" "$DIST_PLUGINS"

# Collect sources
find de -name "*.java" > build-sources.list

# Compile
if [[ -n "$PLUGINS_CP" ]]; then
  javac -d "$BUILD_CLASSES" -cp "$PLUGINS_CP" @build-sources.list
else
  javac -d "$BUILD_CLASSES" @build-sources.list
fi

# ---------------------------------------------
# Package JARs
# ---------------------------------------------
log "Packaging program/efa.jar and program/efahelp.jar ..."
mkdir -p "$PROGRAM_DIR"

# Main application JAR, using provided MANIFEST
if [[ ! -f META-INF/MANIFEST.MF ]]; then
  echo "Error: META-INF/MANIFEST.MF not found. Ensure it exists and declares Main-Class: de.nmichael.efa.base.Main" >&2
  exit 3
fi

# Add resource property files explicitly
jar cfm "$PROGRAM_DIR/efa.jar" META-INF/MANIFEST.MF \
  -C "$BUILD_CLASSES" . \
  efa_da.properties efa_de.properties efa_el.properties efa_en.properties \
  efa_fr.properties efa_it.properties efa_nl.properties

# Help JAR from help/
if [[ -d help ]]; then
  jar cf "$PROGRAM_DIR/efahelp.jar" -C help .
else
  log "Warning: help/ directory not found — efahelp.jar will be empty."
  jar cf "$PROGRAM_DIR/efahelp.jar" -C "$PROGRAM_DIR" . >/dev/null 2>&1 || true
fi

# ---------------------------------------------
# Assemble distribution directory
# ---------------------------------------------
log "Assembling distribution under $DIST_DIR ..."
mkdir -p "$DIST_PLUGINS"

# Copy runtime jars
cp -f "$PROGRAM_DIR/efa.jar" "$PROGRAM_DIR/efahelp.jar" "$DIST_PROGRAM" 2>/dev/null || {
  mkdir -p "$DIST_PROGRAM"
  cp -f "$PROGRAM_DIR/efa.jar" "$PROGRAM_DIR/efahelp.jar" "$DIST_PROGRAM"
}

# Copy plugin jars as-is
if compgen -G "plugins/*/*.jar" > /dev/null; then
  cp -f plugins/*/*.jar "$DIST_PLUGINS"/
else
  log "Warning: No plugin jars copied (plugins/*/*.jar not found). The app may not run without them."
fi

# Copy startup scripts
cp -f root/runefa.sh root/efaBase.sh root/efaCLI.sh "$DIST_DIR"/
chmod +x "$DIST_DIR"/*.sh || true

# ---------------------------------------------
# Done
# ---------------------------------------------
log "Build finished. Distribution layout:"
cat <<EOF
$DIST_DIR/
  runefa.sh
  efaBase.sh
  efaCLI.sh
  program/
    efa.jar
    efahelp.jar
    plugins/
      (all required plugin jars)
EOF

log "To run the application:"
echo "  (cd $DIST_DIR && ./efaBase.sh)"

log "To create a compressed archive (optional):"
echo "  (cd dist && tar czf efa-$(date +%Y%m%d).tar.gz efa)"
