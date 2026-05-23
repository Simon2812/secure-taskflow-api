#!/usr/bin/env sh
set -e

MAVEN_VERSION="${MAVEN_VERSION:-3.9.9}"
BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_DIR="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION"
MAVEN_BIN="$MAVEN_DIR/bin/mvn"

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

if [ ! -x "$MAVEN_BIN" ]; then
  mkdir -p "$BASE_DIR/.mvn"
  ARCHIVE="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  echo "Maven not found. Downloading Maven $MAVEN_VERSION..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$ARCHIVE"
  else
    echo "Install Maven manually or install curl/wget so this helper can download it." >&2
    exit 1
  fi
  tar -xzf "$ARCHIVE" -C "$BASE_DIR/.mvn"
fi

exec "$MAVEN_BIN" "$@"
