cmake_minimum_required(VERSION 3.14)
project(BST_Project VERSION 1.0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra")

# Find Qt5 components with explicit paths
find_package(Qt5 REQUIRED COMPONENTS Core Widgets Gui)

# Set CMAKE_AUTOMOC to handle Q_OBJECT macros
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

# В CMakeLists.txt добавьте:
if(UNIX)
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static-libgcc -static-libstdc++ -Wl,-Bstatic -lpthread -Wl,-Bdynamic")
endif()

# Explicitly set library paths to avoid Snap
if(UNIX)
    set(CMAKE_SKIP_RPATH ON)
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,-rpath,/usr/lib/x86_64-linux-gnu")
endif()

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

# Link Qt5 libraries with explicit paths
target_link_libraries(bst_gui 
    bst_lib 
    Qt5::Core 
    Qt5::Widgets 
    Qt5::Gui
    pthread
)

# Enable AUTOMOC for the GUI target
set_target_properties(bst_gui PROPERTIES AUTOMOC ON)

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