#ifndef CIRCULAR_LIST_H
#define CIRCULAR_LIST_H

#include <iostream>
#include <stdexcept>

// Шаблонный класс для кольцевого односвязного списка
template <typename T>
class CircularList {
private:
    // Внутренний класс для узла списка
    struct Node {
        T data;         // Данные узла
        Node* next;     // Указатель на следующий узел

        // Конструктор узла
        Node(const T& value) : data(value), next(nullptr) {}
    };

    Node* head;     // Указатель на голову списка (первый элемент)
    int count;      // Текущий размер списка

public:
    // --- Конструкторы и деструктор ---
    CircularList() : head(nullptr), count(0) {}

    // Конструктор копирования
    CircularList(const CircularList<T>& other) : head(nullptr), count(0) {
        if (other.head == nullptr) return;

        Node* current = other.head;
        do {
            this->push_back(current->data);
            current = current->next;
        } while (current != other.head);
    }

    // Деструктор
    ~CircularList() {
        clear();
    }

    // --- Основные операции интерфейса ---
    int size() const { return count; }
    bool empty() const { return count == 0; }

    void clear() {
        if (empty()) return;
        Node* current = head;
        Node* nextNode = nullptr;
        do {
            nextNode = current->next;
            delete current;
            current = nextNode;
        } while (current != head);
        head = nullptr;
        count = 0;
    }

    // Проверка наличия значения
    bool contains(const T& value) const {
        if (empty()) return false;
        Node* current = head;
        do {
            if (current->data == value) return true;
            current = current->next;
        } while (current != head);
        return false;
    }

    // Неконстантная версия (позволяет изменять элемент)
    // Чтение значения по позиции (индексу, начиная с 0)
    T& at(int pos) {
        if (pos < 0 || pos >= count) {
            throw std::out_of_range("Index out of range");
        }
        Node* current = head;
        for (int i = 0; i < pos; ++i) {
            current = current->next;
        }
        return current->data; // Возвращает ссылку, которую можно изменить
    }

    // Константная версия (только для чтения)
    const T& at(int pos) const {
        if (pos < 0 || pos >= count) {
            throw std::out_of_range("Index out of range");
        }
        Node* current = head;
        for (int i = 0; i < pos; ++i) {
            current = current->next;
        }
        return current->data; // Возвращает константную ссылку
    }

    // Изменение значения по позиции
    void set(int pos, const T& value) {
        if (pos < 0 || pos >= count) {
            throw std::out_of_range("Index out of range");
        }
        Node* current = head;
        for (int i = 0; i < pos; ++i) {
            current = current->next;
        }
        current->data = value;
    }

    // Получение позиции первого вхождения значения
    int indexOf(const T& value) const {
        if (empty()) return -1;
        Node* current = head;
        for (int i = 0; i < count; ++i) {
            if (current->data == value) return i;
            current = current->next;
        }
        return -1;
    }

    // Включение нового значения в конец
    void push_back(const T& value) {
        Node* newNode = new Node(value);
        if (empty()) {
            head = newNode;
            head->next = head; // Замыкаем на себя
        } else {
            Node* last = head;
            while (last->next != head) {
                last = last->next;
            }
            last->next = newNode;
            newNode->next = head;
        }
        ++count;
    }

    // Включение нового значения в позицию
    void insert(int pos, const T& value) {
        if (pos < 0 || pos > count) {
            throw std::out_of_range("Insert position out of range");
        }

        Node* newNode = new Node(value);
        if (empty()) {
            head = newNode;
            head->next = head;
        } else if (pos == 0) { // Вставка в начало
            Node* last = head;
            while (last->next != head) {
                last = last->next;
            }
            newNode->next = head;
            head = newNode;
            last->next = head; // Последний теперь указывает на новую голову
        } else { // Вставка в середину или конец
            Node* current = head;
            for (int i = 0; i < pos - 1; ++i) {
                current = current->next;
            }
            newNode->next = current->next;
            current->next = newNode;
        }
        ++count;
    }

    // Удаление первого вхождения значения
    bool remove_value(const T& value) {
        if (empty()) return false;

        Node* current = head;
        Node* prev = nullptr;
        Node* toDelete = nullptr;

        // Поиск элемента с конца предыдущего для облегчения удаления в кольце
        for (int i = 0; i < count; ++i) {
            if (current->data == value) {
                toDelete = current;
                break;
            }
            prev = current;
            current = current->next;
        }

        if (toDelete == nullptr) return false;

        // Если элемент найден
        if (count == 1) {
            delete head;
            head = nullptr;
        } else {
            Node* last = head;
            while (last->next != head) {
                last = last->next;
            }

            if (toDelete == head) { // Удаление головы
                head = head->next;
                last->next = head; // Последний теперь указывает на новую голову
            } else if (toDelete->next == head) { // Удаление последнего элемента
                prev->next = head;
            } else { // Удаление из середины
                prev->next = toDelete->next;
            }
            delete toDelete;
        }

        --count;
        return true;
    }

    // Удаление значения из позиции
    bool remove_at(int pos) {
        if (pos < 0 || pos >= count) return false;

        Node* toDelete = nullptr;

        if (count == 1) {
            toDelete = head;
            head = nullptr;
        } else {
            Node* prev = head;
            for (int i = 0; i < pos - 1; ++i) {
                prev = prev->next;
            }
            toDelete = prev->next;

            Node* last = head;
            while (last->next != head) {
                last = last->next;
            }

            if (pos == 0) { // Удаление головы
                toDelete = head;
                head = head->next;
                last->next = head;
            } else if (toDelete->next == head) { // Удаление последнего
                prev->next = head;
            } else { // Удаление из середины
                prev->next = toDelete->next;
            }
        }
        delete toDelete;
        --count;
        return true;
    }

    // --- Итераторы ---
    class Iterator {
    private:
        Node* current;
        Node* const head; // Нужен для определения конца в кольце
        int steps;        // Счетчик пройденных шагов
        int listSize;     // Размер списка на момент создания итератора

    public:
        Iterator(Node* startNode, Node* listHead, int size) : current(startNode), head(listHead), steps(0), listSize(size) {}

        // Оператор доступа по чтению/записи
        T& operator*() {
            if (current == nullptr) throw std::runtime_error("Iterator is not set");
            return current->data;
        }

        // Префиксный инкремент (переход к следующему)
        Iterator& operator++() {
            if (current == nullptr) return *this;
            current = current->next;
            steps++;
            // Если прошли полный круг, считаем итератор достигшим конца
            if (steps >= listSize) {
                current = nullptr;
            }
            return *this;
        }

        // Постфиксный инкремент
        Iterator operator++(int) {
            Iterator temp = *this;
            ++(*this);
            return temp;
        }

        // Проверка равенства
        bool operator==(const Iterator& other) const {
            return current == other.current;
        }

        // Проверка неравенства
        bool operator!=(const Iterator& other) const {
            return current != other.current;
        }
    };

    // Запрос итератора begin()
    Iterator begin() {
        if (empty()) return end();
        return Iterator(head, head, count);
    }

    // Запрос итератора end()
    Iterator end() {
        return Iterator(nullptr, head, count);
    }

    // Операция вывода для отладки
    void print() const {
        if (empty()) {
            std::cout << "List: [empty]" << std::endl;
            return;
        }
        std::cout << "List (size=" << count << "): [";
        Node* current = head;
        for (int i = 0; i < count; ++i) {
            std::cout << current->data;
            if (i < count - 1) std::cout << ", ";
            current = current->next;
        }
        std::cout << "]" << std::endl;
    }

    // Получить голову "списка"
    Node* getHead() const { return head; }
};

#endif // CIRCULAR_LIST_H