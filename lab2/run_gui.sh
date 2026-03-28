#!/bin/bash

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="${PROJECT_DIR}/build"

cd "${BUILD_DIR}"

# Находим правильную pthread
PTHREAD_PATH=$(find /lib /usr/lib -name "libpthread*.so*" -type f 2>/dev/null | grep -v snap | head -1)

if [ -z "$PTHREAD_PATH" ]; then
    echo "Cannot find pthread library"
    exit 1
fi

echo "Using pthread: $PTHREAD_PATH"

# Запускаем с предзагрузкой правильной библиотеки
LD_PRELOAD="$PTHREAD_PATH" \
LD_LIBRARY_PATH="/usr/lib/x86_64-linux-gnu:/lib/x86_64-linux-gnu" \
QT_QPA_PLATFORM="xcb" \
./bst_gui