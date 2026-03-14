cmake_minimum_required(VERSION 3.10)
project(CircularListLab)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Опции для сборки
option(BUILD_TESTS "Build tests" ON)
option(BUILD_DEMO "Build demo program" ON)

# Директория с заголовочными файлами
include_directories(include)

# Демонстрационная программа
if(BUILD_DEMO)
    add_executable(menu_demo examples/menu_demo.cpp)
    target_include_directories(menu_demo PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)
endif()

# Тесты
if(BUILD_TESTS)
    # Находим Google Test
    find_package(GTest REQUIRED)
    
    add_executable(run_tests tests/test_main.cpp)
    target_include_directories(run_tests PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)
    target_link_libraries(run_tests GTest::GTest GTest::Main pthread)
    
    # Регистрируем тест
    enable_testing()
    add_test(NAME CircularListTests COMMAND run_tests)
endif()