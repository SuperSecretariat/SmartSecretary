#!/bin/bash

echo "Building Angular frontend..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR/frontend" || exit 1

npm install
npm run build -- --output-path="$SCRIPT_DIR/backend/src/main/resources/static" --output-hashing=none

echo "Angular build complete!"
