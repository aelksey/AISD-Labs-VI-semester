/**
 * @file test_bst.cpp
 * @brief Модульные тесты для класса BST (Binary Search Tree)
 * 
 * Данный файл содержит набор тестов для проверки корректности работы
 * всех методов класса BST, включая основные операции (вставка, удаление,
 * поиск), итераторы, обход дерева и другие вспомогательные функции.
 * 
 * Тесты используют фреймворк Google Test (gtest) и покрывают как
 * базовые сценарии, так и граничные случаи.
 * 
 * @author Student
 * @version 1.0
 * @date 2024
 */

#include <gtest/gtest.h>
#include <set>
#include <random>
#include "../include/bst.hpp"
#include "../include/utils.hpp"

/**
 * @class BSTTest
 * @brief Класс-фикстура для тестирования BST
 * 
 * Обеспечивает создание нового пустого дерева перед каждым тестом
 * и его корректное удаление после завершения теста.
 */
class BSTTest : public ::testing::Test {
protected:
    /**
     * @brief Метод, выполняемый перед каждым тестом
     * 
     * Создает новое пустое дерево для изолированного тестирования.
     */
    void SetUp() override {
        tree = new BST<int, int>();
    }
    
    /**
     * @brief Метод, выполняемый после каждого теста
     * 
     * Удаляет дерево и освобождает выделенную память.
     */
    void TearDown() override {
        delete tree;
    }
    
    BST<int, int>* tree;  ///< Указатель на тестируемое дерево
};

// ============================================================================
// Тесты базовых операций вставки и поиска
// ============================================================================

/**
 * @test InsertAndContains
 * @brief Проверка вставки элементов и поиска по ключу
 * 
 * Сценарий:
 * 1. Вставка элемента с ключом 10 и данными 100
 * 2. Проверка, что элемент существует
 * 3. Проверка, что несуществующий элемент не найден
 * 4. Проверка корректного размера дерева
 */
TEST_F(BSTTest, InsertAndContains) {
    EXPECT_TRUE(tree->insert(10, 100));
    EXPECT_TRUE(tree->contains(10));
    EXPECT_FALSE(tree->contains(20));
    EXPECT_EQ(tree->size(), 1);
}

/**
 * @test InsertDuplicate
 * @brief Проверка обработки дубликатов при вставке
 * 
 * Сценарий:
 * 1. Вставка элемента с ключом 10
 * 2. Попытка вставить элемент с тем же ключом (должна вернуть false)
 * 3. Проверка, что данные не изменились
 * 4. Проверка, что размер дерева не увеличился
 */
TEST_F(BSTTest, InsertDuplicate) {
    EXPECT_TRUE(tree->insert(10, 100));
    EXPECT_FALSE(tree->insert(10, 200));
    EXPECT_EQ(tree->getItem(10), 100);
    EXPECT_EQ(tree->size(), 1);
}

/**
 * @test GetItem
 * @brief Проверка доступа к данным по ключу
 * 
 * Сценарий:
 * 1. Вставка нескольких элементов
 * 2. Проверка корректности данных для существующих ключей
 * 3. Проверка, что при запросе несуществующего ключа выбрасывается исключение
 */
TEST_F(BSTTest, GetItem) {
    tree->insert(5, 50);
    tree->insert(3, 30);
    tree->insert(7, 70);
    
    EXPECT_EQ(tree->getItem(5), 50);
    EXPECT_EQ(tree->getItem(3), 30);
    EXPECT_EQ(tree->getItem(7), 70);
    
    EXPECT_THROW(tree->getItem(10), std::out_of_range);
}

// ============================================================================
// Тесты удаления элементов (различные случаи)
// ============================================================================

/**
 * @test RemoveLeaf
 * @brief Удаление листового узла (узла без детей)
 * 
 * Сценарий:
 * 1. Создание дерева с корнем и двумя листьями
 * 2. Удаление листового узла (5)
 * 3. Проверка, что узел удален
 * 4. Проверка, что остальные узлы сохранились
 * 5. Проверка корректного уменьшения размера
 */
TEST_F(BSTTest, RemoveLeaf) {
    tree->insert(10, 100);
    tree->insert(5, 50);
    tree->insert(15, 150);
    
    tree->remove(5);
    EXPECT_FALSE(tree->contains(5));
    EXPECT_EQ(tree->size(), 2);
    EXPECT_TRUE(tree->contains(10));
    EXPECT_TRUE(tree->contains(15));
}

/**
 * @test RemoveNodeWithOneChild
 * @brief Удаление узла с одним ребенком
 * 
 * Сценарий:
 * 1. Создание дерева, где узел 5 имеет одного левого ребенка
 * 2. Удаление узла 5
 * 3. Проверка, что узел удален, а его ребенок сохранен
 * 4. Проверка корректного размера
 */
TEST_F(BSTTest, RemoveNodeWithOneChild) {
    tree->insert(10, 100);
    tree->insert(5, 50);
    tree->insert(3, 30);
    
    EXPECT_TRUE(tree->remove(5));
    EXPECT_FALSE(tree->contains(5));
    EXPECT_TRUE(tree->contains(3));
    EXPECT_TRUE(tree->contains(10));
    EXPECT_EQ(tree->size(), 2);
}

/**
 * @test RemoveRoot
 * @brief Удаление корневого узла
 * 
 * Сценарий:
 * 1. Создание дерева с корнем и двумя детьми
 * 2. Удаление корня
 * 3. Проверка, что корень удален, дети сохранены
 * 4. Новый корень должен быть корректно выбран (successor)
 */
TEST_F(BSTTest, RemoveRoot) {
    tree->insert(10, 100);
    tree->insert(5, 50);
    tree->insert(15, 150);
    
    EXPECT_TRUE(tree->remove(10));
    EXPECT_FALSE(tree->contains(10));
    EXPECT_TRUE(tree->contains(5));
    EXPECT_TRUE(tree->contains(15));
    EXPECT_EQ(tree->size(), 2);
}

/**
 * @test RemoveNodeWithTwoChildren
 * @brief Удаление узла с двумя детьми
 * 
 * Сценарий:
 * 1. Создание дерева, где узел 15 имеет двух детей (12 и 18)
 * 2. Удаление узла 15
 * 3. Проверка, что узел удален, а его дети корректно перестроены
 * 4. Successor (наименьший в правом поддереве) должен занять место удаленного
 */
TEST_F(BSTTest, RemoveNodeWithTwoChildren) {
    tree->insert(10, 100);
    tree->insert(5, 50);
    tree->insert(15, 150);
    tree->insert(12, 120);
    tree->insert(18, 180);
    
    EXPECT_TRUE(tree->remove(15));
    EXPECT_FALSE(tree->contains(15));
    EXPECT_TRUE(tree->contains(10));
    EXPECT_TRUE(tree->contains(5));
    EXPECT_TRUE(tree->contains(12));
    EXPECT_TRUE(tree->contains(18));
    EXPECT_EQ(tree->size(), 4);
}

/**
 * @test RemoveNonExistent
 * @brief Попытка удаления несуществующего ключа
 * 
 * Сценарий:
 * 1. Вставка элемента
 * 2. Попытка удалить несуществующий ключ
 * 3. Проверка, что метод вернул false
 * 4. Проверка, что размер дерева не изменился
 */
TEST_F(BSTTest, RemoveNonExistent) {
    tree->insert(10, 100);
    EXPECT_FALSE(tree->remove(20));
    EXPECT_EQ(tree->size(), 1);
}

// ============================================================================
// Тесты итераторов
// ============================================================================

/**
 * @test IteratorIncrement
 * @brief Проверка инкремента итератора (++оператор)
 * 
 * Сценарий:
 * 1. Вставка нескольких элементов
 * 2. Последовательное перемещение итератора от begin() к end()
 * 3. Проверка, что ключи посещаются в порядке возрастания (inorder обход)
 */
TEST_F(BSTTest, IteratorIncrement) {
    tree->insert(5, 50);
    tree->insert(3, 30);
    tree->insert(7, 70);
    tree->insert(4, 40);
    tree->insert(6, 60);
    
    auto it = tree->begin();
    EXPECT_EQ(it.getKey(), 3);
    ++it;
    EXPECT_EQ(it.getKey(), 4);
    ++it;
    EXPECT_EQ(it.getKey(), 5);
    ++it;
    EXPECT_EQ(it.getKey(), 6);
    ++it;
    EXPECT_EQ(it.getKey(), 7);
    ++it;
    EXPECT_EQ(it, tree->end());
}

/**
 * @test IteratorDereference
 * @brief Проверка оператора разыменования (*)
 * 
 * Сценарий:
 * 1. Вставка элемента
 * 2. Получение итератора на элемент
 * 3. Проверка чтения значения через итератор
 * 4. Проверка записи значения через итератор (изменение данных)
 */
TEST_F(BSTTest, IteratorDereference) {
    tree->insert(10, 100);
    auto it = tree->begin();
    EXPECT_EQ(*it, 100);
    *it = 200;
    EXPECT_EQ(tree->getItem(10), 200);
}

/**
 * @test IteratorEndState
 * @brief Проверка состояния итератора после достижения конца
 * 
 * Сценарий:
 * 1. Вставка элемента
 * 2. Перемещение итератора до end()
 * 3. Проверка, что итератор равен end()
 * 4. Проверка, что разыменование end() выбрасывает исключение
 */
TEST_F(BSTTest, IteratorEndState) {
    tree->insert(5, 50);
    auto it = tree->begin();
    ++it;
    EXPECT_EQ(it, tree->end());
    EXPECT_THROW(*it, std::out_of_range);
    EXPECT_THROW(it.getKey(), std::out_of_range);
}

/**
 * @test IteratorPostIncrement
 * @brief Проверка пост-инкремента (it++)
 * 
 * Сценарий:
 * 1. Вставка элементов
 * 2. Использование пост-инкремента
 * 3. Проверка, что возвращается старое значение итератора
 */
TEST_F(BSTTest, IteratorPostIncrement) {
    tree->insert(5, 50);
    tree->insert(3, 30);
    tree->insert(7, 70);
    
    auto it = tree->begin();
    auto old = it++;
    EXPECT_EQ(old.getKey(), 3);
    EXPECT_EQ(it.getKey(), 5);
}

/**
 * @test IteratorEquality
 * @brief Проверка операторов сравнения итераторов
 * 
 * Сценарий:
 * 1. Создание двух итераторов на одно и то же место
 * 2. Проверка равенства
 * 3. Изменение одного итератора
 * 4. Проверка неравенства
 */
TEST_F(BSTTest, IteratorEquality) {
    tree->insert(5, 50);
    auto it1 = tree->begin();
    auto it2 = tree->begin();
    EXPECT_EQ(it1, it2);
    
    ++it1;
    EXPECT_NE(it1, it2);
}

// ============================================================================
// Тесты состояния дерева и вспомогательных методов
// ============================================================================

/**
 * @test EmptyTree
 * @brief Проверка работы с пустым деревом
 * 
 * Сценарий:
 * 1. Проверка, что дерево пусто
 * 2. Проверка размера
 * 3. Проверка, что поиск в пустом дереве возвращает false
 * 4. Проверка, что итераторы begin() и end() равны
 * 5. Проверка, что getItem выбрасывает исключение
 */
TEST_F(BSTTest, EmptyTree) {
    EXPECT_TRUE(tree->empty());
    EXPECT_EQ(tree->size(), 0);
    EXPECT_FALSE(tree->contains(10));
    EXPECT_THROW(tree->getItem(10), std::out_of_range);
    EXPECT_EQ(tree->begin(), tree->end());
}

/**
 * @test ClearTree
 * @brief Проверка очистки дерева
 * 
 * Сценарий:
 * 1. Вставка нескольких элементов
 * 2. Очистка дерева
 * 3. Проверка, что дерево пусто
 * 4. Проверка, что все элементы удалены
 */
TEST_F(BSTTest, ClearTree) {
    tree->insert(1, 10);
    tree->insert(2, 20);
    tree->insert(3, 30);
    EXPECT_EQ(tree->size(), 3);
    
    tree->clear();
    EXPECT_TRUE(tree->empty());
    EXPECT_EQ(tree->size(), 0);
    EXPECT_FALSE(tree->contains(1));
}

/**
 * @test CopyConstructor
 * @brief Проверка конструктора копирования
 * 
 * Сценарий:
 * 1. Создание дерева с элементами
 * 2. Копирование дерева через конструктор копирования
 * 3. Проверка, что копия содержит те же элементы
 * 4. Изменение исходного дерева не должно влиять на копию (глубокое копирование)
 */
TEST_F(BSTTest, CopyConstructor) {
    tree->insert(5, 50);
    tree->insert(3, 30);
    tree->insert(7, 70);
    
    BST<int, int> copy(*tree);
    EXPECT_EQ(copy.size(), tree->size());
    EXPECT_TRUE(copy.contains(5));
    EXPECT_TRUE(copy.contains(3));
    EXPECT_TRUE(copy.contains(7));
    
    tree->insert(10, 100);
    EXPECT_EQ(copy.size(), 3);
    EXPECT_FALSE(copy.contains(10));
}

/**
 * @test AssignmentOperator
 * @brief Проверка оператора присваивания
 * 
 * Сценарий:
 * 1. Создание дерева с элементами
 * 2. Присваивание другому дереву
 * 3. Проверка, что целевое дерево содержит копию элементов
 */
TEST_F(BSTTest, AssignmentOperator) {
    tree->insert(5, 50);
    tree->insert(3, 30);
    
    BST<int, int> copy;
    copy = *tree;
    EXPECT_EQ(copy.size(), tree->size());
    EXPECT_TRUE(copy.contains(5));
    EXPECT_TRUE(copy.contains(3));
}

/**
 * @test InorderTraversal
 * @brief Проверка симметричного обхода дерева
 * 
 * Сценарий:
 * 1. Вставка элементов в случайном порядке
 * 2. Получение списка ключей через inorderTraversal
 * 3. Проверка, что ключи возвращены в порядке возрастания
 */
TEST_F(BSTTest, InorderTraversal) {
    tree->insert(5, 50);
    tree->insert(3, 30);
    tree->insert(7, 70);
    tree->insert(4, 40);
    tree->insert(6, 60);
    
    std::vector<int> keys = tree->inorderTraversal();
    std::vector<int> expected = {3, 4, 5, 6, 7};
    EXPECT_EQ(keys, expected);
}

/**
 * @test HeightCalculation
 * @brief Проверка вычисления высоты дерева
 * 
 * Сценарий:
 * 1. Пустое дерево имеет высоту 0
 * 2. После вставки корня высота = 1
 * 3. После добавления левого ребенка высота = 2
 * 4. После добавления правого ребенка высота не увеличивается
 * 5. После добавления узла на глубину 3 высота = 3
 */
TEST_F(BSTTest, HeightCalculation) {
    EXPECT_EQ(tree->height(), 0);
    
    tree->insert(10, 100);
    EXPECT_EQ(tree->height(), 1);
    
    tree->insert(5, 50);
    EXPECT_EQ(tree->height(), 2);
    
    tree->insert(15, 150);
    EXPECT_EQ(tree->height(), 2);
    
    tree->insert(3, 30);
    EXPECT_EQ(tree->height(), 3);
}

/**
 * @test IsBSTProperty
 * @brief Проверка свойства бинарного дерева поиска
 * 
 * Сценарий:
 * 1. Пустое дерево является BST
 * 2. Дерево с корректными вставками сохраняет свойство BST
 * 3. Метод isBST() всегда возвращает true для корректного BST
 */
TEST_F(BSTTest, IsBSTProperty) {
    EXPECT_TRUE(tree->isBST());
    
    tree->insert(10, 100);
    tree->insert(5, 50);
    tree->insert(15, 150);
    EXPECT_TRUE(tree->isBST());
    
    tree->clear();
    tree->insert(10, 100);
    EXPECT_TRUE(tree->isBST());
}

// ============================================================================
// Нагрузочные и стресс-тесты
// ============================================================================

/**
 * @test LargeTreeInsertion
 * @brief Проверка вставки большого количества элементов
 * 
 * Сценарий:
 * 1. Вставка 1000 элементов в возрастающем порядке
 * 2. Проверка размера дерева
 * 3. Проверка наличия первого и последнего элемента
 * 
 * @note В вырожденном случае дерево может стать списком,
 *       но функциональность должна сохраняться
 */
TEST_F(BSTTest, LargeTreeInsertion) {
    const int NUM_ELEMENTS = 1000;
    for (int i = 0; i < NUM_ELEMENTS; ++i) {
        EXPECT_TRUE(tree->insert(i, i * 10));
    }
    EXPECT_EQ(tree->size(), NUM_ELEMENTS);
    EXPECT_TRUE(tree->contains(0));
    EXPECT_TRUE(tree->contains(NUM_ELEMENTS - 1));
}

/**
 * @test RandomOperations
 * @brief Проверка случайных операций вставки и удаления
 * 
 * Сценарий:
 * 1. Генерация 100 уникальных случайных ключей с помощью std::set
 * 2. Вставка всех ключей в дерево
 * 3. Удаление половины ключей
 * 4. Проверка, что оставшиеся ключи присутствуют
 * 
 * @note Используем std::set для гарантии уникальности ключей
 */
TEST_F(BSTTest, RandomOperations) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(1, 500);
    std::set<int> uniqueKeys;
    
    // Генерируем 100 уникальных ключей
    while (uniqueKeys.size() < 100) {
        uniqueKeys.insert(dis(gen));
    }
    
    // Копируем ключи в вектор для упорядоченного доступа
    std::vector<int> keys(uniqueKeys.begin(), uniqueKeys.end());
    
    // Вставляем все ключи в дерево
    for (int key : keys) {
        EXPECT_TRUE(tree->insert(key, key * 10));
    }
    EXPECT_EQ(tree->size(), 100);
    
    // Удаляем первую половину ключей (50 штук)
    for (size_t i = 0; i < keys.size() / 2; ++i) {
        EXPECT_TRUE(tree->remove(keys[i])) 
            << "Failed to remove key: " << keys[i];
    }
    EXPECT_EQ(tree->size(), 50);
    
    // Проверяем, что оставшиеся ключи присутствуют
    for (size_t i = keys.size() / 2; i < keys.size(); ++i) {
        EXPECT_TRUE(tree->contains(keys[i])) 
            << "Key " << keys[i] << " should be present but is not";
    }
    
    // Проверяем, что удаленные ключи отсутствуют
    for (size_t i = 0; i < keys.size() / 2; ++i) {
        EXPECT_FALSE(tree->contains(keys[i])) 
            << "Key " << keys[i] << " should be removed but is still present";
    }
}

/**
 * @test RandomOperationsWithDuplicates
 * @brief Проверка работы с дубликатами при случайных операциях
 * 
 * Сценарий:
 * 1. Генерация 200 случайных ключей с возможными дубликатами
 * 2. Вставка всех ключей (дубликаты должны игнорироваться)
 * 3. Удаление уникальных ключей из множества
 * 4. Проверка корректности
 */
TEST_F(BSTTest, RandomOperationsWithDuplicates) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(1, 100);
    std::map<int, int> keyCount;
    
    // Генерируем 200 ключей с возможными дубликатами
    for (int i = 0; i < 200; ++i) {
        int key = dis(gen);
        keyCount[key]++;
        tree->insert(key, key * 10);
    }
    
    // Размер дерева должен быть равен количеству уникальных ключей
    EXPECT_EQ(tree->size(), keyCount.size());
    
    // Проверяем, что все уникальные ключи присутствуют
    for (const auto& pair : keyCount) {
        EXPECT_TRUE(tree->contains(pair.first));
    }
    
    // Удаляем половину уникальных ключей
    auto it = keyCount.begin();
    for (size_t i = 0; i < keyCount.size() / 2 && it != keyCount.end(); ++i, ++it) {
        EXPECT_TRUE(tree->remove(it->first));
    }
    
    // Проверяем обновленный размер
    EXPECT_EQ(tree->size(), keyCount.size() - keyCount.size() / 2);
}

// ============================================================================
// Тесты граничных случаев
// ============================================================================

/**
 * @test IteratorOnEmptyTree
 * @brief Проверка итераторов на пустом дереве
 * 
 * Сценарий:
 * 1. Пустое дерево
 * 2. begin() и end() должны быть равны
 * 3. Разные итераторы на пустом дереве должны быть равны
 */
TEST_F(BSTTest, IteratorOnEmptyTree) {
    auto begin = tree->begin();
    auto end = tree->end();
    EXPECT_EQ(begin, end);
    
    auto anotherBegin = tree->begin();
    EXPECT_EQ(begin, anotherBegin);
}

/**
 * @test SingleElementTree
 * @brief Проверка дерева с одним элементом
 * 
 * Сценарий:
 * 1. Дерево с одним элементом
 * 2. Проверка высоты
 * 3. Проверка итераторов
 */
TEST_F(BSTTest, SingleElementTree) {
    tree->insert(42, 4242);
    
    EXPECT_EQ(tree->size(), 1);
    EXPECT_EQ(tree->height(), 1);
    
    auto it = tree->begin();
    EXPECT_EQ(it.getKey(), 42);
    EXPECT_EQ(*it, 4242);
    
    ++it;
    EXPECT_EQ(it, tree->end());
}

/**
 * @test RemoveAllElementsSequentially
 * @brief Последовательное удаление всех элементов
 * 
 * Сценарий:
 * 1. Вставка нескольких элементов
 * 2. Последовательное удаление всех элементов
 * 3. Проверка, что дерево становится пустым
 */
TEST_F(BSTTest, RemoveAllElementsSequentially) {
    std::vector<int> keys = {10, 5, 15, 3, 7, 12, 18};
    
    for (int key : keys) {
        tree->insert(key, key * 10);
    }
    EXPECT_EQ(tree->size(), keys.size());
    
    // Удаляем в обратном порядке
    for (auto it = keys.rbegin(); it != keys.rend(); ++it) {
        EXPECT_TRUE(tree->remove(*it));
    }
    
    EXPECT_TRUE(tree->empty());
    EXPECT_EQ(tree->size(), 0);
}

// ============================================================================
// Тесты производительности
// ============================================================================

/**
 * @test PerformanceInsertion
 * @brief Тест производительности вставки
 * 
 * Сценарий:
 * 1. Вставка 5000 элементов
 * 2. Проверка, что все элементы успешно вставлены
 * 
 * @note Этот тест проверяет, что дерево не падает при большом количестве элементов
 */
TEST_F(BSTTest, PerformanceInsertion) {
    const int NUM_ELEMENTS = 5000;
    
    for (int i = 0; i < NUM_ELEMENTS; ++i) {
        EXPECT_TRUE(tree->insert(i, i * 10));
    }
    
    EXPECT_EQ(tree->size(), NUM_ELEMENTS);
    
    // Проверка случайных элементов
    EXPECT_TRUE(tree->contains(0));
    EXPECT_TRUE(tree->contains(NUM_ELEMENTS / 2));
    EXPECT_TRUE(tree->contains(NUM_ELEMENTS - 1));
}

/**
 * @test IteratorRemoveFirstElement
 * @brief Проверка удаления первого элемента (на который указывает begin())
 * 
 * Сценарий:
 * 1. Вставка элементов
 * 2. Получение итератора на первый элемент через begin()
 * 3. Удаление первого элемента
 * 4. Проверка, что итератор теперь указывает на новый первый элемент
 */
TEST_F(BSTTest, IteratorRemoveFirstElement) {
    tree->insert(10, 100);
    tree->insert(5, 50);
    tree->insert(15, 150);
    tree->insert(3, 30);
    tree->insert(7, 70);
    
    // Получаем итератор на первый элемент (ключ 3)
    auto it = tree->begin();
    EXPECT_EQ(it.getKey(), 3);
    
    // Удаляем первый элемент
    auto newIt = tree->remove(it);
    
    // Проверяем, что новый итератор указывает на следующий элемент (ключ 5)
    EXPECT_EQ(newIt.getKey(), 5);

    EXPECT_EQ(newIt, tree->begin());
    
    // Проверяем, что первый элемент удален
    EXPECT_FALSE(tree->contains(3));
    
    // Проверяем корректный размер
    EXPECT_EQ(tree->size(), 4);
    
    // Проверяем порядок оставшихся элементов
    std::vector<int> expected = {5, 7, 10, 15};
    EXPECT_EQ(tree->inorderTraversal(), expected);

}

/**
 * @test RemoveAfterSecondElement
 * @brief Проверка удаления элемента, следующего за вторым элементом
 */
TEST_F(BSTTest, RemoveAfterSecondElement) {
    // Вставляем элементы в дерево
    tree->insert(10, 100);
    tree->insert(5, 50);
    tree->insert(15, 150);
    tree->insert(3, 30);
    tree->insert(7, 70);
    tree->insert(12, 120);
    tree->insert(18, 180);
    
    // Проверяем начальное состояние
    std::vector<int> initial = tree->inorderTraversal();
    std::vector<int> expectedInitial = {3, 5, 7, 10, 12, 15, 18};
    EXPECT_EQ(initial, expectedInitial);
    
    // Получаем итератор на начало
    auto it = tree->begin();
    EXPECT_EQ(it.getKey(), 3);
    
    // Перемещаемся ко второму элементу (5)
    ++it;
    EXPECT_EQ(it.getKey(), 5);
    
    // Перемещаемся к третьему элементу (7)
    ++it;
    EXPECT_EQ(it.getKey(), 7);
    
    // Удаляем третий элемент (7)
    auto newIt = tree->remove(it);
    
    // Проверяем, что новый итератор указывает на следующий элемент (10)
    EXPECT_EQ(newIt.getKey(), 10);
    
    // Проверяем, что удаленный элемент больше не существует
    EXPECT_FALSE(tree->contains(7));
    
    // Проверяем порядок оставшихся элементов
    std::vector<int> remaining = tree->inorderTraversal();
    std::vector<int> expectedRemaining = {3, 5, 10, 12, 15, 18};
    EXPECT_EQ(remaining, expectedRemaining);

}

/**
 * @brief Точка входа в программу тестирования
 * 
 * Инициализирует Google Test и запускает все зарегистрированные тесты.
 * 
 * @param argc Количество аргументов командной строки
 * @param argv Массив аргументов командной строки
 * @return int Результат выполнения тестов (0 - успех, ненулевое значение - ошибка)
 */
int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}