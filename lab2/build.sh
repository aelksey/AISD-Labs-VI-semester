#!/bin/bash

echo "=== BST Project Build Script (Simple) ==="
echo "Current directory: $(pwd)"

# Определение директорий
PROJECT_DIR="$(pwd)"
BUILD_DIR="${PROJECT_DIR}/build"

# Установка зависимостей
echo "Installing dependencies..."
sudo apt update
sudo apt install -y build-essential cmake qt5-default libgtest-dev

# Компиляция Google Test
cd /usr/src/gtest
sudo cmake .
sudo make
sudo cp lib/*.a /usr/lib

# Возврат в директорию проекта
cd "${PROJECT_DIR}"

# Создание директории сборки
echo "Creating build directory..."
rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}"

# Сборка проекта
echo "Building project..."
cd "${BUILD_DIR}"
cmake "${PROJECT_DIR}"
make -j$(nproc)

echo "=== Build Complete ==="
echo "Build directory: ${BUILD_DIR}"
echo ""
echo "To run GUI: ${BUILD_DIR}/bst_gui"
echo "To run tests: ${BUILD_DIR}/bst_tests"
echo "To run profiler: ${BUILD_DIR}/bst_benchmark"
echo "To clean: rm -rf ${BUILD_DIR}"