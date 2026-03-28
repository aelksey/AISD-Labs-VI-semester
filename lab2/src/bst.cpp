#ifndef BST_CPP
#define BST_CPP

#include "../include/bst.hpp"
#include <cmath>
#include <sstream>

// Node helper functions implementation
template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Node* 
BST<KeyType, DataType>::findNode(const KeyType& key) const {
    Node* current = root;
    while (current != nullptr) {
        if (key == current->key)
            return current;
        else if (key < current->key)
            current = current->left;
        else
            current = current->right;
    }
    return nullptr;
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Node* 
BST<KeyType, DataType>::findMin(Node* node) const {
    if (node == nullptr) return nullptr;
    while (node->left != nullptr)
        node = node->left;
    return node;
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Node* 
BST<KeyType, DataType>::findMax(Node* node) const {
    if (node == nullptr) return nullptr;
    while (node->right != nullptr)
        node = node->right;
    return node;
}

template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::clear(Node* node) {
    if (node == nullptr) return;
    clear(node->left);
    clear(node->right);
    delete node;
}

template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::copyTree(Node*& dest, Node* src) {
    if (src == nullptr) {
        dest = nullptr;
        return;
    }
    dest = new Node(src->key, src->data);
    copyTree(dest->left, src->left);
    copyTree(dest->right, src->right);
}

/**
 * @brief Получение следующего узла в inorder обходе
 * @param node Текущий узел
 * @return Указатель на следующий узел или nullptr
 */
template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Node* 
BST<KeyType, DataType>::getNextNode(Node* node) const {
    if (node == nullptr) return nullptr;
    
    // Если есть правый ребенок, то следующий - самый левый в правом поддереве
    if (node->right != nullptr) {
        Node* next = node->right;
        while (next->left != nullptr) {
            next = next->left;
        }
        return next;
    }
    
    // Иначе поднимаемся вверх, пока не найдем узел, где текущий узел находится в левом поддереве
    Node* current = root;
    Node* successor = nullptr;
    
    while (current != nullptr) {
        if (node->key < current->key) {
            successor = current;
            current = current->left;
        } else if (node->key > current->key) {
            current = current->right;
        } else {
            break;
        }
    }
    
    return successor;
}

template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::inorderTraversal(Node* node, std::vector<KeyType>& result) const {
    if (node == nullptr) return;
    inorderTraversal(node->left, result);
    result.push_back(node->key);
    inorderTraversal(node->right, result);
}

template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::horizontalPrint(Node* node, int space, int indent) const {
    if (node == nullptr) return;
    
    space += indent;
    
    horizontalPrint(node->right, space, indent);
    
    std::cout << std::endl;
    for (int i = indent; i < space; i++)
        std::cout << " ";
    std::cout << node->key << "(" << node->data << ")" << std::endl;
    
    horizontalPrint(node->left, space, indent);
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Node* 
BST<KeyType, DataType>::getSuccessor(Node* node) const {
    if (node == nullptr) return nullptr;
    if (node->right != nullptr)
        return findMin(node->right);
    
    Node* successor = nullptr;
    Node* current = root;
    
    while (current != nullptr) {
        if (node->key < current->key) {
            successor = current;
            current = current->left;
        } else if (node->key > current->key) {
            current = current->right;
        } else {
            break;
        }
    }
    return successor;
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Node* 
BST<KeyType, DataType>::getPredecessor(Node* node) const {
    if (node == nullptr) return nullptr;
    if (node->left != nullptr)
        return findMax(node->left);
    
    Node* predecessor = nullptr;
    Node* current = root;
    
    while (current != nullptr) {
        if (node->key > current->key) {
            predecessor = current;
            current = current->right;
        } else if (node->key < current->key) {
            current = current->left;
        } else {
            break;
        }
    }
    return predecessor;
}

/**
 * @brief Получение следующего итератора (исправленная версия)
 * @return Итератор на следующий элемент
 * 
 * ВАЖНО: Этот метод не должен полагаться на текущий итератор после удаления.
 * Вместо этого он должен найти следующий элемент, используя ключ текущего элемента.
 */
template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator 
BST<KeyType, DataType>::Iterator::getNext() const {
    if (isEnd || current == nullptr) {
        return Iterator(tree, true);
    }
    
    // Сохраняем ключ текущего элемента
    KeyType currentKey;
    try {
        currentKey = current->key;
    } catch (...) {
        return Iterator(tree, true);
    }
    
    // Находим следующий узел через дерево, а не через итератор
    Node* nextNode = tree->getNextNodeByKey(currentKey);
    
    if (nextNode == nullptr) {
        return Iterator(tree, true);
    }
    
    // Создаем новый итератор, указывающий на nextNode
    // Для этого нужно найти позицию nextNode в inorder обходе
    Iterator result(tree, false);
    while (result != Iterator(tree, true) && result.getKey() != nextNode->key) {
        ++result;
    }
    
    return result;
}

/**
 * @brief Получение следующего узла по ключу (без использования итератора)
 * @param key Ключ текущего узла
 * @return Указатель на следующий узел или nullptr
 */
template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Node* 
BST<KeyType, DataType>::getNextNodeByKey(const KeyType& key) const {
    Node* node = findNode(key);
    if (node == nullptr) return nullptr;
    return getNextNode(node);
}

// Iterator implementation
template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::Iterator::fillStack(Node* start) {
    Node* current = start;
    while (current != nullptr) {
        nodeStack.push(current);
        current = current->left;
    }
}

template <typename KeyType, typename DataType>
BST<KeyType, DataType>::Iterator::Iterator() 
    : current(nullptr), tree(nullptr), isEnd(true) {}

template <typename KeyType, typename DataType>
BST<KeyType, DataType>::Iterator::Iterator(BST<KeyType, DataType>* t, bool end)
    : tree(t), isEnd(end) {
    if (end || t == nullptr || t->root == nullptr) {
        current = nullptr;
        return;
    }
    fillStack(t->root);
    if (!nodeStack.empty()) {
        current = nodeStack.top();
    } else {
        current = nullptr;
        isEnd = true;
    }
}

template <typename KeyType, typename DataType>
BST<KeyType, DataType>::Iterator::Iterator(const Iterator& other)
    : current(other.current), nodeStack(other.nodeStack), 
      tree(other.tree), isEnd(other.isEnd) {}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator& 
BST<KeyType, DataType>::Iterator::operator=(const Iterator& other) {
    if (this != &other) {
        current = other.current;
        nodeStack = other.nodeStack;
        tree = other.tree;
        isEnd = other.isEnd;
    }
    return *this;
}

/**
 * @brief Оператор инкремента (префиксный)
 * Перемещает итератор к следующему элементу в порядке inorder обхода
 */
template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator& 
BST<KeyType, DataType>::Iterator::operator++() {
    if (isEnd || nodeStack.empty()) {
        isEnd = true;
        current = nullptr;
        return *this;
    }
    
    Node* node = nodeStack.top();
    nodeStack.pop();
    
    if (node->right != nullptr) {
        Node* temp = node->right;
        while (temp != nullptr) {
            nodeStack.push(temp);
            temp = temp->left;
        }
    }
    
    if (nodeStack.empty()) {
        isEnd = true;
        current = nullptr;
    } else {
        current = nodeStack.top();
        isEnd = false;
    }
    
    return *this;
}

/**
 * @brief Оператор декремента (префиксный)
 * Перемещает итератор к предыдущему элементу в порядке inorder обхода
 */
template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator& 
BST<KeyType, DataType>::Iterator::operator--() {
    // Случай 1: итератор находится в конце (end())
    if (isEnd) {
        if (tree != nullptr && tree->root != nullptr) {
            // Находим максимальный элемент в дереве
            Node* maxNode = tree->findMax(tree->root);
            if (maxNode != nullptr) {
                // Очищаем стек
                while (!nodeStack.empty()) {
                    nodeStack.pop();
                }
                
                // Заполняем стек для достижения максимального элемента
                Node* current = tree->root;
                while (current != nullptr) {
                    nodeStack.push(current);
                    if (maxNode->key < current->key) {
                        current = current->left;
                    } else if (maxNode->key > current->key) {
                        current = current->right;
                    } else {
                        break;
                    }
                }
                
                current = nodeStack.top();
                isEnd = false;
                return *this;
            }
        }
        isEnd = true;
        current = nullptr;
        return *this;
    }
    
    // Случай 2: итератор уже установлен на элемент
    if (nodeStack.empty()) {
        isEnd = true;
        current = nullptr;
        return *this;
    }
    
    Node* node = nodeStack.top();
    nodeStack.pop();
    
    if (node->left != nullptr) {
        Node* temp = node->left;
        while (temp != nullptr) {
            nodeStack.push(temp);
            temp = temp->right;
        }
    }
    
    if (nodeStack.empty()) {
        isEnd = true;
        current = nullptr;
    } else {
        current = nodeStack.top();
        isEnd = false;
    }
    
    return *this;
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator 
BST<KeyType, DataType>::Iterator::operator++(int) {
    Iterator temp = *this;
    ++(*this);
    return temp;
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator 
BST<KeyType, DataType>::Iterator::operator--(int) {
    Iterator temp = *this;
    --(*this);
    return temp;
}

template <typename KeyType, typename DataType>
DataType& BST<KeyType, DataType>::Iterator::operator*() {
    if (current == nullptr || isEnd) {
        throw std::out_of_range("Iterator is not dereferenceable");
    }
    return current->data;
}

template <typename KeyType, typename DataType>
const DataType& BST<KeyType, DataType>::Iterator::operator*() const {
    if (current == nullptr || isEnd) {
        throw std::out_of_range("Iterator is not dereferenceable");
    }
    return current->data;
}

template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::Iterator::operator==(const Iterator& other) const {
    return (isEnd && other.isEnd) || (current == other.current);
}

template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::Iterator::operator!=(const Iterator& other) const {
    return !(*this == other);
}

template <typename KeyType, typename DataType>
KeyType BST<KeyType, DataType>::Iterator::getKey() const {
    if (current == nullptr || isEnd) {
        throw std::out_of_range("Iterator is not dereferenceable");
    }
    return current->key;
}

// BST class implementation
template <typename KeyType, typename DataType>
BST<KeyType, DataType>::BST() : root(nullptr), tree_size(0) {}

template <typename KeyType, typename DataType>
BST<KeyType, DataType>::BST(const BST& other) : root(nullptr), tree_size(other.tree_size) {
    copyTree(root, other.root);
}

template <typename KeyType, typename DataType>
BST<KeyType, DataType>::~BST() {
    clear();
}

template <typename KeyType, typename DataType>
BST<KeyType, DataType>& BST<KeyType, DataType>::operator=(const BST& other) {
    if (this != &other) {
        clear();
        copyTree(root, other.root);
        tree_size = other.tree_size;
    }
    return *this;
}

template <typename KeyType, typename DataType>
size_t BST<KeyType, DataType>::size() const {
    return tree_size;
}

template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::empty() const {
    return tree_size == 0;
}

template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::clear() {
    clear(root);
    root = nullptr;
    tree_size = 0;
}

template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::contains(const KeyType& key) const {
    return findNode(key) != nullptr;
}

template <typename KeyType, typename DataType>
DataType& BST<KeyType, DataType>::getItem(const KeyType& key) {
    Node* node = findNode(key);
    if (node == nullptr) {
        throw std::out_of_range("Key not found in BST");
    }
    return node->data;
}

template <typename KeyType, typename DataType>
const DataType& BST<KeyType, DataType>::getItem(const KeyType& key) const {
    Node* node = findNode(key);
    if (node == nullptr) {
        throw std::out_of_range("Key not found in BST");
    }
    return node->data;
}

template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::insert(const KeyType& key, const DataType& data) {
    if (root == nullptr) {
        root = new Node(key, data);
        tree_size++;
        return true;
    }
    
    Node* current = root;
    Node* parent = nullptr;
    
    while (current != nullptr) {
        parent = current;
        if (key == current->key) {
            return false; // Duplicate key not allowed
        } else if (key < current->key) {
            current = current->left;
        } else {
            current = current->right;
        }
    }
    
    Node* newNode = new Node(key, data);
    if (key < parent->key) {
        parent->left = newNode;
    } else {
        parent->right = newNode;
    }
    tree_size++;
    return true;
}

/**
 * @brief Удаление элемента по итератору (исправленная версия)
 * @param it Итератор на удаляемый элемент
 * @return Итератор на следующий элемент после удаленного
 */
template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator 
BST<KeyType, DataType>::remove(Iterator& it) {
    if (it == end()) {
        return end();
    }
    
    // Сохраняем ключ удаляемого элемента
    KeyType keyToRemove = it.getKey();
    
    // ВАЖНО: Находим следующий элемент ДО удаления, используя ключ
    // Это гарантирует, что мы найдем правильный следующий элемент,
    // даже если узел, на который указывает итератор, будет изменен
    Node* nextNode = getNextNodeByKey(keyToRemove);
    
    // Создаем итератор на следующий элемент
    Iterator nextIt;
    if (nextNode != nullptr) {
        // Создаем новый итератор, указывающий на nextNode
        nextIt = Iterator(this, false);
        while (nextIt != end() && nextIt.getKey() != nextNode->key) {
            ++nextIt;
        }
    } else {
        nextIt = end();
    }
    
    // Выполняем удаление
    remove(keyToRemove);
    
    return nextIt;
}

/**
 * @brief Удаление элемента по ключу (с отладкой)
 * @param key Ключ удаляемого элемента
 * @return true если элемент был удален, false если не найден
 */
template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::remove(const KeyType& key) {    
    Node* current = root;
    Node* parent = nullptr;
    int depth = 0;
    
    // Find node to delete
    while (current != nullptr && current->key != key) {
        parent = current;
        if (key < current->key) {
            current = current->left;
        } else {
            current = current->right;
        }
        depth++;
    }
    
    if (current == nullptr) {
        return false;
    }
    
    // Case 1: Leaf node
    if (current->left == nullptr && current->right == nullptr) {
        
        if (parent == nullptr) {
            root = nullptr;
        } else if (parent->left == current) {
            parent->left = nullptr;
        } else {
            parent->right = nullptr;
        }
        
        delete current;
    }
    // Case 2: Node with only right child
    else if (current->left == nullptr) {
        Node* child = current->right;
        
        if (parent == nullptr) {
            root = child;
        } else if (parent->left == current) {
            parent->left = child;
        } else {
            parent->right = child;
        }
        
        delete current;
    }
    // Case 3: Node with only left child
    else if (current->right == nullptr) {
        Node* child = current->left;
        
        if (parent == nullptr) {
            root = child;
        } else if (parent->left == current) {
            parent->left = child;
        } else {
            parent->right = child;
        }
        
        delete current;
    }
    // Case 4: Node with two children
    else {
        
        // Find inorder successor
        Node* successor = current->right;
        Node* successorParent = current;
        
        while (successor->left != nullptr) {
            successorParent = successor;
            successor = successor->left;
        }
         
        // Копируем данные из successor в current
        current->key = successor->key;
        current->data = successor->data;
        
        // Удаляем successor
        if (successorParent->left == successor) {
            successorParent->left = successor->right;
        } else {
            successorParent->right = successor->right;
        }
    
        delete successor;
        
        // Важно: current НЕ УДАЛЯЕТСЯ, его данные просто заменены
    }
    
    tree_size--;
    
    // Проверка целостности дерева после удаления
    if (root != nullptr) {
        
        // Проверка, что удаленный ключ больше не существует
        Node* check = findNode(key);
        if (check != nullptr) {
        } else {
        }
        
        // Вывод inorder обхода для проверки порядка
        std::vector<KeyType> inorder = inorderTraversal();
        
        // Проверка, что в inorder обходе нет дубликатов
        for (size_t i = 1; i < inorder.size(); ++i) {
            if (inorder[i] == inorder[i-1]) {
                std::cout << "  ERROR: Duplicate key " << inorder[i] << " found in inorder traversal!" << std::endl;
            }
        }
    } else {
        std::cout << "  Tree is empty after removal" << std::endl;
    }
    
    return true;
}

template <typename KeyType, typename DataType>
std::vector<KeyType> BST<KeyType, DataType>::inorderTraversal() const {
    std::vector<KeyType> result;
    inorderTraversal(root, result);
    return result;
}

template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::printTree() const {
    if (root == nullptr) {
        std::cout << "Tree is empty" << std::endl;
        return;
    }
    std::cout << "\nHorizontal tree view (left is higher, right is lower):" << std::endl;
    horizontalPrint(root, 0, 5);
    std::cout << std::endl;
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator BST<KeyType, DataType>::begin() {
    return Iterator(this, false);
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator BST<KeyType, DataType>::end() {
    return Iterator(this, true);
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator BST<KeyType, DataType>::rbegin() {
    Iterator it(this, false);
    Node* max = findMax(root);
    if (max != nullptr) {
        while (it.current != max && !it.isEnd) ++it;
    }
    return it;
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::Iterator BST<KeyType, DataType>::rend() {
    return Iterator(this, true);
}

template <typename KeyType, typename DataType>
int BST<KeyType, DataType>::height() const {
    std::function<int(Node*)> getHeight = [&](Node* node) -> int {
        if (node == nullptr) return 0;
        return 1 + std::max(getHeight(node->left), getHeight(node->right));
    };
    return getHeight(root);
}

template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::isBalanced() const {
    std::function<int(Node*)> checkBalance = [&](Node* node) -> int {
        if (node == nullptr) return 0;
        int leftHeight = checkBalance(node->left);
        int rightHeight = checkBalance(node->right);
        if (leftHeight == -1 || rightHeight == -1) return -1;
        if (std::abs(leftHeight - rightHeight) > 1) return -1;
        return 1 + std::max(leftHeight, rightHeight);
    };
    return checkBalance(root) != -1;
}

template <typename KeyType, typename DataType>
bool BST<KeyType, DataType>::isBST() const {
    std::function<bool(Node*, KeyType*, KeyType*)> checkBST = 
        [&](Node* node, KeyType* min, KeyType* max) -> bool {
        if (node == nullptr) return true;
        if ((min != nullptr && node->key <= *min) ||
            (max != nullptr && node->key >= *max)) {
            return false;
        }
        return checkBST(node->left, min, &node->key) &&
               checkBST(node->right, &node->key, max);
    };
    return checkBST(root, nullptr, nullptr);
}

// Methods for visualization
template <typename KeyType, typename DataType>
void BST<KeyType, DataType>::buildVisualNode(Node* node, VisualNode*& vnode) const {
    if (node == nullptr) {
        vnode = nullptr;
        return;
    }
    
    VisualNode* leftChild = nullptr;
    VisualNode* rightChild = nullptr;
    
    buildVisualNode(node->left, leftChild);
    buildVisualNode(node->right, rightChild);
    
    vnode = new VisualNode(node->key, node->data, leftChild, rightChild);
}

template <typename KeyType, typename DataType>
typename BST<KeyType, DataType>::VisualNode* BST<KeyType, DataType>::getVisualRoot() const {
    VisualNode* vroot = nullptr;
    buildVisualNode(root, vroot);
    return vroot;
}

#endif // BST_CPP