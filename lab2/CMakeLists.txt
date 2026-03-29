cmake_minimum_required(VERSION 3.14)
project(BST_Project VERSION 1.0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -O2")

# Find Qt5 components
find_package(Qt5 REQUIRED COMPONENTS Core Widgets Gui)

# Set CMAKE_AUTOMOC to handle Q_OBJECT macros
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

# Include directories
include_directories(include)

# Main library
add_library(bst_lib STATIC
    src/bst.cpp
    src/utils.cpp
)

target_include_directories(bst_lib PUBLIC include)

# GUI application
add_executable(bst_gui
    gui/main_gui.cpp
)

# Link Qt5 libraries
target_link_libraries(bst_gui 
    bst_lib 
    Qt5::Core 
    Qt5::Widgets 
    Qt5::Gui
)

# Enable AUTOMOC for the GUI target
set_target_properties(bst_gui PROPERTIES AUTOMOC ON)

# Benchmark program
add_executable(bst_benchmark
    benchmark/benchmark.cpp
)

target_link_libraries(bst_benchmark 
    bst_lib
)

# Unit tests
enable_testing()
find_package(GTest REQUIRED)

add_executable(bst_tests
    tests/test_bst.cpp
)

target_link_libraries(bst_tests 
    bst_lib 
    GTest::gtest 
    GTest::gtest_main
    pthread
)

add_test(NAME BST_Tests COMMAND bst_tests)

# Optional: Add a custom target to run benchmark
add_custom_target(run_benchmark
    COMMAND ${CMAKE_BINARY_DIR}/bst_benchmark
    DEPENDS bst_benchmark
    COMMENT "Running BST benchmark"
)

# Optional: Add a custom target to run benchmark with specific parameters
add_custom_target(run_benchmark_full
    COMMAND ${CMAKE_BINARY_DIR}/bst_benchmark 5000 500 5
    DEPENDS bst_benchmark
    COMMENT "Running full BST benchmark"
)