#ifndef CIRCULAR_LIST_H
#define CIRCULAR_LIST_H

#include <iostream>
#include <stdexcept>
#include <vector>
#include <algorithm>

// Предварительное объявление
template <typename T>
class CircularList;

// Базовый класс для итератора
template <typename T>
class IteratorBase {
public:
    virtual ~IteratorBase() {}
    virtual void handleNodeDeleted(typename CircularList<T>::Node* deletedNode, 
                                   typename CircularList<T>::Node* nextNode) = 0;
    virtual bool isValid() const = 0;
    virtual typename CircularList<T>::Node* getCurrent() const = 0;
};

// Шаблонный класс для кольцевого односвязного списка
template <typename T>
class CircularList {
public:
    // Внутренний класс для узла списка
    struct Node {
        T data;         // Данные узла
        Node* next;     // Указатель на следующий узел
        bool isDeleted; // Флаг, указывающий, что узел помечен на удаление

        Node(const T& value) : data(value), next(nullptr), isDeleted(false) {}
    };
    Node* getHead() const { return head; }

private:
    Node* head;
    int count;
    mutable std::vector<IteratorBase<T>*> activeIterators;

    // Вспомогательная функция для поиска последнего элемента
    Node* findLast() const {
        if (empty()) return nullptr;
        Node* last = head;
        while (last->next != head) {
            last = last->next;
        }
        return last;
    }

public:
    // --- Конструкторы и деструктор ---
    CircularList() : head(nullptr), count(0) {}

    CircularList(const CircularList<T>& other) : head(nullptr), count(0) {
        if (other.head == nullptr) return;
        Node* current = other.head;
        do {
            this->push_back(current->data);
            current = current->next;
        } while (current != other.head);
    }

    ~CircularList() {
        clear();
    }

    // --- Основные операции ---
    int size() const { return count; }
    bool empty() const { return count == 0; }

    void clear() {
        if (empty()) return;
        Node* current = head;
        Node* nextNode = nullptr;
        for (int i = 0; i < count; ++i) {
            nextNode = current->next;
            delete current;
            current = nextNode;
        }
        head = nullptr;
        count = 0;
        
        // Оповещаем итераторы
        for (auto* iter : activeIterators) {
            if (iter) {
                iter->handleNodeDeleted(nullptr, nullptr);
            }
        }
    }

    bool contains(const T& value) const {
        if (empty()) return false;
        Node* current = head;
        for (int i = 0; i < count; ++i) {
            if (current->data == value && !current->isDeleted) return true;
            current = current->next;
        }
        return false;
    }

    T& at(int pos) {
        if (pos < 0 || pos >= count) {
            throw std::out_of_range("Index out of range");
        }
        Node* current = head;
        for (int i = 0; i < pos; ++i) {
            current = current->next;
        }
        if (current->isDeleted) {
            throw std::runtime_error("Accessing deleted node");
        }
        return current->data;
    }

    const T& at(int pos) const {
        if (pos < 0 || pos >= count) {
            throw std::out_of_range("Index out of range");
        }
        Node* current = head;
        for (int i = 0; i < pos; ++i) {
            current = current->next;
        }
        if (current->isDeleted) {
            throw std::runtime_error("Accessing deleted node");
        }
        return current->data;
    }

    void set(int pos, const T& value) {
        if (pos < 0 || pos >= count) {
            throw std::out_of_range("Index out of range");
        }
        Node* current = head;
        for (int i = 0; i < pos; ++i) {
            current = current->next;
        }
        if (current->isDeleted) {
            throw std::runtime_error("Accessing deleted node");
        }
        current->data = value;
    }

    int indexOf(const T& value) const {
        if (empty()) return -1;
        Node* current = head;
        for (int i = 0; i < count; ++i) {
            if (current->data == value && !current->isDeleted) return i;
            current = current->next;
        }
        return -1;
    }

    void push_back(const T& value) {
        Node* newNode = new Node(value);
        if (empty()) {
            head = newNode;
            head->next = head;
        } else {
            Node* last = findLast();
            last->next = newNode;
            newNode->next = head;
        }
        ++count;
    }

    void insert(int pos, const T& value) {
        if (pos < 0 || pos > count) {
            throw std::out_of_range("Insert position out of range");
        }

        Node* newNode = new Node(value);
        if (empty()) {
            head = newNode;
            head->next = head;
        } else if (pos == 0) {
            Node* last = findLast();
            newNode->next = head;
            head = newNode;
            last->next = head;
        } else {
            Node* current = head;
            for (int i = 0; i < pos - 1; ++i) {
                current = current->next;
            }
            newNode->next = current->next;
            current->next = newNode;
        }
        ++count;
    }

    bool remove_value(const T& value) {
        
        if (empty()) return false;

        Node* current = head;
        Node* prev = nullptr;
        Node* toDelete = nullptr;
        int deletePos = -1;

        // Поиск элемента для удаления
        for (int i = 0; i < count; ++i) {
            if (current->data == value && !current->isDeleted) {
                toDelete = current;
                deletePos = i;
                break;
            }
            prev = current;
            current = current->next;
        }

        if (toDelete == nullptr) return false;

        // Сохраняем следующий элемент (ВАЖНО: делаем это ДО любых изменений)
        Node* nextNode = toDelete->next;

        // ОСОБЫЙ СЛУЧАЙ: если удаляем голову, следующий элемент - новая голова
        if (toDelete == head) {
            nextNode = head->next;
        }

        // Если следующий элемент - голова (удаляем последний элемент)
        if (nextNode == head) {
            // Это нормально, просто запоминаем
        }

        // ШАГ 1: Сначала перестраиваем указатели в списке
        if (count == 1) {
            head = nullptr;
            nextNode = nullptr; // В списке больше нет элементов
        } else {
            Node* last = findLast();

            if (toDelete == head) { // Удаление головы
                head = head->next;           // head теперь указывает на следующий
                last->next = head;            // последний указывает на новую голову
                nextNode = head;              // следующий для итератора - новая голова
            } else if (toDelete->next == head) { // Удаление последнего
                prev->next = head;
                nextNode = nullptr;            // после последнего ничего нет
            } else { // Удаление из середины
                prev->next = toDelete->next;
                // nextNode уже правильный (toDelete->next)
            }
        }

        // ШАГ 2: Помечаем как удаленный
        toDelete->isDeleted = true;

        // ШАГ 3: Оповещаем итераторы
        for (auto* iter : activeIterators) {
            if (iter) {
                iter->handleNodeDeleted(toDelete, nextNode);
            }
        }

        // ШАГ 4: Удаляем узел
        delete toDelete;
        --count;
        return true;
    }

    bool remove_at(int pos) {
        if (pos < 0 || pos >= count) return false;

        Node* toDelete = nullptr;
        Node* prev = nullptr;
        Node* nextNode = nullptr;

        if (count == 1) {
            toDelete = head;
            nextNode = nullptr;
        } else {
            Node* last = findLast();

            if (pos == 0) { // Удаление головы
                toDelete = head;
                nextNode = head->next;        // следующий после головы
                head = head->next;             // обновляем голову
                last->next = head;              // последний указывает на новую голову
            } else {
                prev = head;
                for (int i = 0; i < pos - 1; ++i) {
                    prev = prev->next;
                }
                toDelete = prev->next;
                nextNode = toDelete->next;

                if (nextNode == head) { // Если удаляем последний
                    prev->next = head;
                    nextNode = nullptr;  // после последнего ничего нет
                } else {
                    prev->next = nextNode;
                }
            }
        }

        // Помечаем как удаленный
        toDelete->isDeleted = true;

        // Оповещаем итераторы
        for (auto* iter : activeIterators) {
            if (iter) {
                iter->handleNodeDeleted(toDelete, nextNode);
            }
        }

        delete toDelete;
        --count;
        return true;
    }   

    // --- Методы для управления итераторами ---
    void registerIterator(IteratorBase<T>* iter) const {
        activeIterators.push_back(iter);
    }

    void unregisterIterator(IteratorBase<T>* iter) const {
        auto it = std::find(activeIterators.begin(), activeIterators.end(), iter);
        if (it != activeIterators.end()) {
            activeIterators.erase(it);
        }
    }

    // --- Итераторы ---
    class Iterator : public IteratorBase<T> {
    private:
        CircularList<T>* list;
        Node* current;
        int steps;
        int initialSize;

    public:
        Iterator(CircularList<T>* lst, Node* startNode, int size) 
            : list(lst), current(startNode), steps(0), initialSize(size) {
            if (list) {
                list->registerIterator(this);
            }
            
            // Пропускаем удаленные узлы при инициализации
            if (current != nullptr) {
                while (current != nullptr && current->isDeleted) {
                    current = current->next;
                    if (current == list->head) {
                        current = nullptr;
                        break;
                    }
                }
            }
        }

        ~Iterator() {
            if (list) {
                list->unregisterIterator(this);
            }
        }

        Iterator(const Iterator& other) 
            : list(other.list), current(other.current), 
              steps(other.steps), initialSize(other.initialSize) {
            if (list) {
                list->registerIterator(this);
            }
        }

        Iterator& operator=(const Iterator& other) {
            if (this != &other) {
                if (list) {
                    list->unregisterIterator(this);
                }
                list = other.list;
                current = other.current;
                steps = other.steps;
                initialSize = other.initialSize;
                if (list) {
                    list->registerIterator(this);
                }
            }
            return *this;
        }

        T& operator*() {
            if (current == nullptr) {
                throw std::runtime_error("Iterator is not set or reached end");
            }
            return current->data;
        }

        Iterator& operator++() {
            std::cout << "Increment: current=" << (current ? current->data : 0) 
                      << ", steps=" << steps << ", initialSize=" << initialSize 
                      << ", isHead=" << (current == list->head) << std::endl;
                
            if (current == nullptr || steps >= initialSize) {
                current = nullptr;
                return *this;
            }

            Node* nextNode = current->next;
            steps++;
            current = nextNode;

            if (steps >= initialSize || current == list->head) {
                current = nullptr;
            }

            return *this;
        }

        bool operator==(const Iterator& other) const {
            return current == other.current;
        }

        bool operator!=(const Iterator& other) const {
            return current != other.current;
        }

        bool isValid() const override {
            return current != nullptr;
        }

        Node* getCurrent() const override {
            return current;
        }

    void handleNodeDeleted(Node* deletedNode, Node* nextNode) override {
        
        if (current == deletedNode) {
        // Переходим на следующий элемент
        current = nextNode;
        
        // Если список стал пустым
        if (list->empty()) {
            current = nullptr;
            return;
        }
        
        // Проверяем, не удален ли следующий узел
        if (current != nullptr && current->isDeleted) {
            // Пропускаем удаленные узлы
            Node* temp = current;
            int safetySteps = 0;
            while (temp != nullptr && temp->isDeleted && safetySteps < list->size()) {
                temp = temp->next;
                safetySteps++;
                if (temp == list->getHead()) {
                    temp = nullptr;
                    break;
                }
            }
            current = temp;
        }
        
        // Корректируем steps, так как мы переместились без инкремента
        // Это важно для правильного определения конца
        if (current == nullptr) {
            // Достигли конца
        }
    }
}
    };

    Iterator begin() {
        if (empty()) return end();
        return Iterator(this, head, count);
    }

    Iterator end() {
        return Iterator(this, nullptr, count);
    }

    void print() const {
        if (empty()) {
            std::cout << "List: [empty]" << std::endl;
            return;
        }
        std::cout << "List (size=" << count << "): [";
        Node* current = head;
        for (int i = 0; i < count; ++i) {
            std::cout << current->data;
            if (current->isDeleted) {
                std::cout << "(deleted)";
            }
            if (i < count - 1) std::cout << ", ";
            current = current->next;
        }
        std::cout << "]" << std::endl;
    }
};

#endif // CIRCULAR_LIST_H