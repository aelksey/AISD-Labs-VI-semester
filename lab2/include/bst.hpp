#ifndef BST_HPP
#define BST_HPP

#include <iostream>
#include <vector>
#include <memory>
#include <stack>
#include <queue>
#include <stdexcept>
#include <functional>
#include <algorithm>

template <typename KeyType, typename DataType>
class BST {
private:
    struct Node {
        KeyType key;
        DataType data;
        Node* left;
        Node* right;
        
        Node(const KeyType& k, const DataType& d)
            : key(k), data(d), left(nullptr), right(nullptr) {}
    };
    
    Node* root;
    size_t tree_size;
    
    // Helper functions
    Node* findNode(const KeyType& key) const;
    Node* findMin(Node* node) const;
    Node* findMax(Node* node) const;
    void clear(Node* node);
    void copyTree(Node*& dest, Node* src);
    void inorderTraversal(Node* node, std::vector<KeyType>& result) const;
    void horizontalPrint(Node* node, int space, int indent) const;
    Node* getSuccessor(Node* node) const;
    Node* getPredecessor(Node* node) const;
    Node* getNextNode(Node* node) const;
    
public:
    // В публичной секции класса BST
    Node* getNextNodeByKey(const KeyType& key) const;
    // Структура для визуализации
    struct VisualNode {
        KeyType key;
        DataType data;
        const VisualNode* left;
        const VisualNode* right;
        
        VisualNode(const KeyType& k, const DataType& d, 
                   const VisualNode* l = nullptr, 
                   const VisualNode* r = nullptr)
            : key(k), data(d), left(l), right(r) {}
    };
    
    // Iterator class
    class Iterator {
    private:
        Node* current;
        std::stack<Node*> nodeStack;
        BST<KeyType, DataType>* tree;
        bool isEnd;
        
        void fillStack(Node* start);
        
    public:
        Iterator();
        Iterator(BST<KeyType, DataType>* t, bool end);
        Iterator(const Iterator& other);
        ~Iterator() = default;
        
        Iterator& operator=(const Iterator& other);
        Iterator& operator++();
        Iterator& operator--();
        Iterator operator++(int);
        Iterator operator--(int);
        DataType& operator*();
        const DataType& operator*() const;
        bool operator==(const Iterator& other) const;
        bool operator!=(const Iterator& other) const;
        KeyType getKey() const; 
        
        // Метод для получения следующего узла (используется при удалении)
        Iterator getNext() const;
        bool isValid() const { return current != nullptr && !isEnd; }
    };
    
    // Constructors and destructor
    BST();
    BST(const BST& other);
    ~BST();
    
    // Assignment operator
    BST& operator=(const BST& other);
    
    // Basic operations
    size_t size() const;
    bool empty() const;
    void clear();
    bool contains(const KeyType& key) const;
    
    // Core operations
    DataType& getItem(const KeyType& key);
    const DataType& getItem(const KeyType& key) const;
    bool insert(const KeyType& key, const DataType& data);
    
    // Новый метод remove, который возвращает следующий итератор
    Iterator remove(Iterator& it);
    // Старый метод remove для обратной совместимости
    bool remove(const KeyType& key);
    
    // Traversal
    std::vector<KeyType> inorderTraversal() const;
    
    // Additional operation for variant 6: horizontal tree printing
    void printTree() const;
    
    // Iterator methods
    Iterator begin();
    Iterator end();
    Iterator rbegin();
    Iterator rend();
    
    // Additional helpers for testing
    bool isBalanced() const;
    int height() const;
    bool isBST() const;
    
    // Methods for visualization
    VisualNode* getVisualRoot() const;
    void buildVisualNode(Node* node, VisualNode*& vnode) const;
    
    // Make Iterator a friend to access private members
    friend class Iterator;
};

#include "../src/bst.cpp"

#endif // BST_HPP