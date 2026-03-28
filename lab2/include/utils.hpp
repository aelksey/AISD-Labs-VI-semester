#ifndef UTILS_HPP
#define UTILS_HPP

#include "bst.hpp"
#include <vector>
#include <random>
#include <functional>
#include <chrono>

namespace BSTUtils {

    // Generate random integer key
    int generateRandomKey(int min, int max);
    
    // Generate random data
    int generateRandomData(int min, int max);
    
    // Fill tree with random elements
    template <typename KeyType, typename DataType>
    void fillTreeRandom(BST<KeyType, DataType>& tree, size_t count, 
                        KeyType minKey, KeyType maxKey,
                        DataType minData, DataType maxData) {
        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_int_distribution<KeyType> keyDist(minKey, maxKey);
        std::uniform_int_distribution<DataType> dataDist(minData, maxData);
        
        for (size_t i = 0; i < count; ++i) {
            KeyType key = keyDist(gen);
            DataType data = dataDist(gen);
            tree.insert(key, data);
        }
    }
    
    // Generate sorted sequence of keys
    template <typename KeyType>
    std::vector<KeyType> generateSortedKeys(KeyType start, size_t count, KeyType step) {
        std::vector<KeyType> keys;
        for (size_t i = 0; i < count; ++i) {
            keys.push_back(start + i * step);
        }
        return keys;
    }
    
    // Create degenerate tree (sorted insertion)
    template <typename KeyType, typename DataType>
    void createDegenerateTree(BST<KeyType, DataType>& tree, 
                              const std::vector<KeyType>& keys,
                              const DataType& data) {
        for (const auto& key : keys) {
            tree.insert(key, data);
        }
    }
    
    // Measure operation time
    template <typename Func>
    long long measureTime(Func func) {
        auto start = std::chrono::high_resolution_clock::now();
        func();
        auto end = std::chrono::high_resolution_clock::now();
        return std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
    }
    
    // Get tree keys in order
    template <typename KeyType, typename DataType>
    std::vector<KeyType> getTreeKeys(const BST<KeyType, DataType>& tree) {
        return tree.inorderTraversal();
    }
    
    // Print tree with additional info
    template <typename KeyType, typename DataType>
    void printTreeInfo(const BST<KeyType, DataType>& tree) {
        std::cout << "Tree size: " << tree.size() << std::endl;
        std::cout << "Tree height: " << tree.height() << std::endl;
        std::cout << "Is balanced: " << (tree.isBalanced() ? "Yes" : "No") << std::endl;
        std::cout << "Is BST: " << (tree.isBST() ? "Yes" : "No") << std::endl;
        tree.printTree();
    }
    
} // namespace BSTUtils

#endif // UTILS_HPP