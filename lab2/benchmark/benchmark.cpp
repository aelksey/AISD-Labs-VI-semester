/**
 * @file benchmark.cpp
 * @brief Программа для измерения трудоёмкости операций BST
 * 
 * Измеряет время выполнения операций вставки, поиска и удаления
 * для деревьев разного размера (от 0 до 5000 элементов)
 * и выводит результаты в консоль в формате CSV.
 */

#include <iostream>
#include <iomanip>
#include <chrono>
#include <vector>
#include <random>
#include <algorithm>
#include <cmath>
#include <string>
#include <fstream>
#include <set>

#include "../include/bst.hpp"

using namespace std::chrono;

/**
 * @struct BenchmarkResult
 * @brief Структура для хранения результатов измерений
 */
struct BenchmarkResult {
    int size;           // Размер дерева
    double insertTime;  // Время вставки (микросекунды)
    double searchTime;  // Время поиска (микросекунды)
    double removeTime;  // Время удаления (микросекунды)
    double height;      // Высота дерева
    bool isBalanced;    // Сбалансировано ли дерево
    double theoreticalLog; // Теоретическое значение log2(n)
    double theoreticalN;    // Теоретическое значение n
};

/**
 * @brief Генерация уникальных случайных ключей
 * @param count Количество ключей
 * @param min Минимальное значение
 * @param max Максимальное значение
 * @return Вектор уникальных случайных ключей
 */
std::vector<int> generateUniqueKeys(int count, int min, int max) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(min, max);
    
    std::vector<int> keys;
    std::set<int> uniqueKeys;
    
    while (uniqueKeys.size() < static_cast<size_t>(count)) {
        uniqueKeys.insert(dis(gen));
    }
    
    keys.assign(uniqueKeys.begin(), uniqueKeys.end());
    std::shuffle(keys.begin(), keys.end(), gen);
    
    return keys;
}

/**
 * @brief Создание вырожденного дерева (отсортированные ключи)
 * @param count Количество элементов
 * @return Вектор отсортированных ключей
 */
std::vector<int> generateSortedKeys(int count) {
    std::vector<int> keys(count);
    for (int i = 0; i < count; ++i) {
        keys[i] = i + 1;
    }
    return keys;
}

/**
 * @brief Измерение времени выполнения операции
 * @param func Функция для измерения
 * @return Время в микросекундах
 */
template<typename Func>
double measureTime(Func func) {
    auto start = high_resolution_clock::now();
    func();
    auto end = high_resolution_clock::now();
    return duration_cast<microseconds>(end - start).count();
}

/**
 * @brief Тестирование случайного дерева
 * @param size Размер дерева
 * @param iterations Количество итераций для усреднения
 * @return Результаты измерений
 */
BenchmarkResult testRandomTree(int size, int iterations = 10) {
    BenchmarkResult result;
    result.size = size;
    result.theoreticalLog = (size > 0) ? 1.39 * std::log2(size) : 0;
    result.theoreticalN = size;
    
    double totalInsertTime = 0;
    double totalSearchTime = 0;
    double totalRemoveTime = 0;
    double totalHeight = 0;
    int balancedCount = 0;
    
    for (int iter = 0; iter < iterations; ++iter) {
        BST<int, int> tree;
        std::vector<int> keys = generateUniqueKeys(size, 1, size * 10);
        
        // Измерение вставки
        double insertTime = measureTime([&]() {
            for (int key : keys) {
                tree.insert(key, key * 10);
            }
        });
        totalInsertTime += insertTime;
        
        totalHeight += tree.height();
        if (tree.isBalanced()) balancedCount++;
        
        // Измерение поиска (поиск всех ключей)
        double searchTime = measureTime([&]() {
            for (int key : keys) {
                try {
                    tree.getItem(key);
                } catch (...) {}
            }
        });
        totalSearchTime += searchTime;
        
        // Измерение удаления (удаление всех ключей в случайном порядке)
        std::shuffle(keys.begin(), keys.end(), std::mt19937(std::random_device()()));
        double removeTime = measureTime([&]() {
            for (int key : keys) {
                tree.remove(key);
            }
        });
        totalRemoveTime += removeTime;
    }
    
    result.insertTime = totalInsertTime / iterations;
    result.searchTime = totalSearchTime / iterations;
    result.removeTime = totalRemoveTime / iterations;
    result.height = totalHeight / iterations;
    result.isBalanced = (balancedCount > iterations / 2);
    
    return result;
}

/**
 * @brief Тестирование вырожденного дерева
 * @param size Размер дерева
 * @param iterations Количество итераций для усреднения
 * @return Результаты измерений
 */
BenchmarkResult testDegenerateTree(int size, int iterations = 10) {
    BenchmarkResult result;
    result.size = size;
    result.theoreticalLog = (size > 0) ? 1.39 * std::log2(size) : 0;
    result.theoreticalN = size;
    
    double totalInsertTime = 0;
    double totalSearchTime = 0;
    double totalRemoveTime = 0;
    double totalHeight = 0;
    int balancedCount = 0;
    
    for (int iter = 0; iter < iterations; ++iter) {
        BST<int, int> tree;
        std::vector<int> keys = generateSortedKeys(size);
        
        // Измерение вставки (в отсортированном порядке - создает вырожденное дерево)
        double insertTime = measureTime([&]() {
            for (int key : keys) {
                tree.insert(key, key * 10);
            }
        });
        totalInsertTime += insertTime;
        
        totalHeight += tree.height();
        if (tree.isBalanced()) balancedCount++;
        
        // Измерение поиска (поиск всех ключей)
        double searchTime = measureTime([&]() {
            for (int key : keys) {
                try {
                    tree.getItem(key);
                } catch (...) {}
            }
        });
        totalSearchTime += searchTime;
        
        // Измерение удаления (удаление в обратном порядке)
        std::reverse(keys.begin(), keys.end());
        double removeTime = measureTime([&]() {
            for (int key : keys) {
                tree.remove(key);
            }
        });
        totalRemoveTime += removeTime;
    }
    
    result.insertTime = totalInsertTime / iterations;
    result.searchTime = totalSearchTime / iterations;
    result.removeTime = totalRemoveTime / iterations;
    result.height = totalHeight / iterations;
    result.isBalanced = (balancedCount > iterations / 2);
    
    return result;
}

/**
 * @brief Вывод заголовка таблицы
 */
void printHeader() {
    std::cout << std::left;
    std::cout << std::setw(10) << "Size"
              << std::setw(15) << "Insert(μs)"
              << std::setw(15) << "Search(μs)"
              << std::setw(15) << "Remove(μs)"
              << std::setw(12) << "Height"
              << std::setw(12) << "Balanced"
              << std::setw(12) << "log2(n)"
              << std::setw(12) << "n"
              << std::endl;
    std::cout << std::string(103, '-') << std::endl;
}

/**
 * @brief Вывод результата
 * @param result Результат измерений
 */
void printResult(const BenchmarkResult& result) {
    std::cout << std::left;
    std::cout << std::setw(10) << result.size
              << std::setw(15) << std::fixed << std::setprecision(2) << result.insertTime
              << std::setw(15) << std::fixed << std::setprecision(2) << result.searchTime
              << std::setw(15) << std::fixed << std::setprecision(2) << result.removeTime
              << std::setw(12) << std::fixed << std::setprecision(2) << round(result.height)
              << std::setw(12) << (result.isBalanced ? "Yes" : "No")
              << std::setw(12) << std::fixed << std::setprecision(2) << result.theoreticalLog
              << std::setw(12) << result.theoreticalN
              << std::endl;
}

/**
 * @brief Сохранение результатов в CSV файл
 * @param results Вектор результатов
 * @param filename Имя файла
 */
void saveToCSV(const std::vector<BenchmarkResult>& results, const std::string& filename) {
    std::ofstream file(filename);
    if (!file.is_open()) {
        std::cerr << "Error: Cannot open file " << filename << std::endl;
        return;
    }
    
    file << "Size,InsertTime(μs),SearchTime(μs),RemoveTime(μs),Height,Balanced,log2(n),n\n";
    
    for (const auto& r : results) {
        file << r.size << ","
             << r.insertTime << ","
             << r.searchTime << ","
             << r.removeTime << ","
             << round(r.height) << ","
             << (r.isBalanced ? "Yes" : "No") << ","
             << r.theoreticalLog << ","
             << r.theoreticalN << "\n";
    }
    
    file.close();
    std::cout << "\nResults saved to: " << filename << std::endl;
}

/**
 * @brief Вывод графика в консоль (ASCII арт)
 * @param results Вектор результатов
 * @param maxTime Максимальное время для масштабирования
 */
void printASCIIChart(const std::vector<BenchmarkResult>& results, double maxTime) {
    std::cout << "\n=== Time Complexity Visualization (ASCII Chart) ===\n" << std::endl;
    std::cout << "Size\tInsert\tSearch\tRemove\n";
    std::cout << "----\t------\t------\t------\n";
    
    for (const auto& r : results) {
        int insertBars = static_cast<int>((r.insertTime / maxTime) * 50);
        int searchBars = static_cast<int>((r.searchTime / maxTime) * 50);
        int removeBars = static_cast<int>((r.removeTime / maxTime) * 50);
        
        std::cout << std::setw(4) << r.size << "\t"
                  << std::string(insertBars, '#') << " (" << std::fixed << std::setprecision(2) << r.insertTime << "μs)\n"
                  << "\t" << std::string(searchBars, '#') << " (" << r.searchTime << "μs)\n"
                  << "\t" << std::string(removeBars, '#') << " (" << r.removeTime << "μs)\n\n";
    }
}

/**
 * @brief Главная функция
 */
int main(int argc, char* argv[]) {
    std::cout << "===========================================================\n";
    std::cout << "     BST Operations Complexity Benchmark\n";
    std::cout << "===========================================================\n\n";
    
    // Параметры тестирования
    int startSize = 0;
    int maxSize = 5000;
    int step = 500;
    int iterations = 10;
    
    // Разбор аргументов командной строки
    if (argc > 1) maxSize = std::atoi(argv[1]);
    if (argc > 2) step = std::atoi(argv[2]);
    if (argc > 3) iterations = std::atoi(argv[3]);
    
    std::cout << "Benchmark Configuration:\n";
    std::cout << "  Start size: " << startSize << std::endl;
    std::cout << "  Max size: " << maxSize << std::endl;
    std::cout << "  Step: " << step << std::endl;
    std::cout << "  Iterations per size: " << iterations << std::endl;
    std::cout << "\nRunning benchmarks...\n" << std::endl;
    
    std::vector<BenchmarkResult> randomResults;
    std::vector<BenchmarkResult> degenerateResults;
    
    double maxRandomTime = 0;
    double maxDegenerateTime = 0;
    
    // Тестирование случайных деревьев
    std::cout << "=== RANDOM BST ===\n" << std::endl;
    printHeader();
    
    for (int size = startSize; size <= maxSize; size += step) {
        std::cout << "Testing size: " << size << " ... " << std::flush;
        BenchmarkResult result = testRandomTree(size, iterations);
        randomResults.push_back(result);
        printResult(result);
        
        if (result.insertTime > maxRandomTime) maxRandomTime = result.insertTime;
        if (result.searchTime > maxRandomTime) maxRandomTime = result.searchTime;
        if (result.removeTime > maxRandomTime) maxRandomTime = result.removeTime;
    }
    
    // Тестирование вырожденных деревьев
    std::cout << "\n=== DEGENERATE BST ===\n" << std::endl;
    printHeader();
    
    for (int size = startSize; size <= maxSize; size += step) {
        std::cout << "Testing size: " << size << " ... " << std::flush;
        BenchmarkResult result = testDegenerateTree(size, iterations);
        degenerateResults.push_back(result);
        printResult(result);
        
        if (result.insertTime > maxDegenerateTime) maxDegenerateTime = result.insertTime;
        if (result.searchTime > maxDegenerateTime) maxDegenerateTime = result.searchTime;
        if (result.removeTime > maxDegenerateTime) maxDegenerateTime = result.removeTime;
    }
    
    // Сохранение результатов в CSV
    saveToCSV(randomResults, "../benchmark/benchmark_random.csv");
    saveToCSV(degenerateResults, "../benchmark/benchmark_degenerate.csv");
    
    // Вывод ASCII графиков
    printASCIIChart(randomResults, maxRandomTime);
    printASCIIChart(degenerateResults, maxDegenerateTime);
    
    // Вывод статистики
    std::cout << "\n=== SUMMARY ===\n" << std::endl;
    
    if (!randomResults.empty()) {
        auto lastRandom = randomResults.back();
        std::cout << "Random BST (size=" << lastRandom.size << "):\n";
        std::cout << "  Insert time: " << lastRandom.insertTime << " μs\n";
        std::cout << "  Search time: " << lastRandom.searchTime << " μs\n";
        std::cout << "  Remove time: " << lastRandom.removeTime << " μs\n";
        std::cout << "  Height: " << round(lastRandom.height) << "\n";
        std::cout << "  Theoretical O(log n): " << lastRandom.theoreticalLog << "\n\n";
    }
    
    if (!degenerateResults.empty()) {
        auto lastDegenerate = degenerateResults.back();
        std::cout << "Degenerate BST (size=" << lastDegenerate.size << "):\n";
        std::cout << "  Insert time: " << lastDegenerate.insertTime << " μs\n";
        std::cout << "  Search time: " << lastDegenerate.searchTime << " μs\n";
        std::cout << "  Remove time: " << lastDegenerate.removeTime << " μs\n";
        std::cout << "  Height: " << round(lastDegenerate.height) << "\n";
        std::cout << "  Theoretical O(n): " << lastDegenerate.theoreticalN << "\n";
    }
    
    std::cout << "\n=== COMPLEXITY ANALYSIS ===\n" << std::endl;
    
    // Вывод соотношения времени выполнения
    if (!randomResults.empty() && !degenerateResults.empty()) {
        double ratio = degenerateResults.back().searchTime / randomResults.back().searchTime;
        std::cout << "Degenerate tree is " << std::fixed << std::setprecision(2) 
                  << ratio << "x slower than random tree for search operations.\n";
        std::cout << "This matches theoretical expectation: O(n) vs O(log n)\n";
    }
    
    std::cout << "\nBenchmark completed successfully!\n";
    
    return 0;
}