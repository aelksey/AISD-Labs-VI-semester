#include <gtest/gtest.h>
#include "circular_list.h"

// Тест конструктора по умолчанию и базовых операций
TEST(CircularListTest, ConstructorAndBasicOps) {
    CircularList<int> list;
    EXPECT_TRUE(list.empty());
    EXPECT_EQ(list.size(), 0);
}

// Тест вставки в конец (push_back) и доступа по индексу (at)
TEST(CircularListTest, PushBackAndAt) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);

    EXPECT_EQ(list.size(), 3);
    EXPECT_FALSE(list.empty());
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 20);
    EXPECT_EQ(list.at(2), 30);

    // Проверка на исключение при выходе за границы
    EXPECT_THROW(list.at(3), std::out_of_range);
}

// Тест вставки в произвольную позицию (insert)
TEST(CircularListTest, Insert) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(30); // Список: 10, 30

    list.insert(1, 20); // Вставка в середину
    EXPECT_EQ(list.size(), 3);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 20);
    EXPECT_EQ(list.at(2), 30);

    list.insert(0, 5); // Вставка в начало
    EXPECT_EQ(list.size(), 4);
    EXPECT_EQ(list.at(0), 5);
    EXPECT_EQ(list.at(1), 10);

    list.insert(4, 40); // Вставка в конец
    EXPECT_EQ(list.size(), 5);
    EXPECT_EQ(list.at(4), 40);

    // Проверка кольцевой структуры (последний элемент указывает на первый)
    // Не можем проверить напрямую, но можем пройти через итератор
}

// Тест изменения значения (set)
TEST(CircularListTest, Set) {
    CircularList<int> list;
    list.push_back(1);
    list.push_back(2);
    list.push_back(3);

    list.set(1, 99);
    EXPECT_EQ(list.at(0), 1);
    EXPECT_EQ(list.at(1), 99);
    EXPECT_EQ(list.at(2), 3);

    EXPECT_THROW(list.set(3, 100), std::out_of_range);
}

// Тест поиска индекса по значению (indexOf)
TEST(CircularListTest, IndexOf) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);
    list.push_back(20);

    EXPECT_EQ(list.indexOf(20), 1); // Первое вхождение
    EXPECT_EQ(list.indexOf(30), 2);
    EXPECT_EQ(list.indexOf(40), -1); // Не найдено
}

// Тест проверки наличия значения (contains)
TEST(CircularListTest, Contains) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);

    EXPECT_TRUE(list.contains(10));
    EXPECT_TRUE(list.contains(20));
    EXPECT_FALSE(list.contains(30));
}

// Тест удаления по значению (remove_value)
TEST(CircularListTest, RemoveValue) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);
    list.push_back(20); // Список: 10, 20, 30, 20

    // Удаление первого вхождения
    EXPECT_TRUE(list.remove_value(20));
    EXPECT_EQ(list.size(), 3);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 30);
    EXPECT_EQ(list.at(2), 20);

    // Удаление элемента, который теперь первый
    EXPECT_TRUE(list.remove_value(10));
    EXPECT_EQ(list.size(), 2);
    EXPECT_EQ(list.at(0), 30);
    EXPECT_EQ(list.at(1), 20);

    // Удаление последнего элемента
    EXPECT_TRUE(list.remove_value(20));
    EXPECT_EQ(list.size(), 1);
    EXPECT_EQ(list.at(0), 30);

    // Удаление единственного элемента
    EXPECT_TRUE(list.remove_value(30));
    EXPECT_TRUE(list.empty());

    // Попытка удалить несуществующий элемент
    EXPECT_FALSE(list.remove_value(100));
}

// Тест удаления по позиции (remove_at)
TEST(CircularListTest, RemoveAt) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30); // Список: 10, 20, 30

    // Удаление из середины
    EXPECT_TRUE(list.remove_at(1));
    EXPECT_EQ(list.size(), 2);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 30);

    list.push_back(40); // Список: 10, 30, 40
    // Удаление последнего
    EXPECT_TRUE(list.remove_at(2));
    EXPECT_EQ(list.size(), 2);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 30);

    // Удаление первого
    EXPECT_TRUE(list.remove_at(0));
    EXPECT_EQ(list.size(), 1);
    EXPECT_EQ(list.at(0), 30);

    // Удаление единственного
    EXPECT_TRUE(list.remove_at(0));
    EXPECT_TRUE(list.empty());

    // Неверная позиция
    EXPECT_FALSE(list.remove_at(0));
}

// Тест конструктора копирования
TEST(CircularListTest, CopyConstructor) {
    CircularList<int> list1;
    list1.push_back(1);
    list1.push_back(2);
    list1.push_back(3);

    CircularList<int> list2(list1);

    EXPECT_EQ(list2.size(), 3);
    EXPECT_EQ(list2.at(0), 1);
    EXPECT_EQ(list2.at(1), 2);
    EXPECT_EQ(list2.at(2), 3);

    // Проверка, что списки независимы
    list2.set(1, 99);
    EXPECT_EQ(list1.at(1), 2);
    EXPECT_EQ(list2.at(1), 99);
}

// Тест очистки списка (clear)
TEST(CircularListTest, Clear) {
    CircularList<int> list;
    list.push_back(1);
    list.push_back(2);
    list.clear();

    EXPECT_TRUE(list.empty());
    EXPECT_EQ(list.size(), 0);
}

// Тест итератора
TEST(CircularListTest, Iterator) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);

    int expected[] = {10, 20, 30};
    int i = 0;
    for (auto it = list.begin(); it != list.end(); ++it) {
        EXPECT_EQ(*it, expected[i]);
        i++;
    }
    EXPECT_EQ(i, 3);

    // Проверка на пустом списке
    CircularList<int> emptyList;
    EXPECT_TRUE(emptyList.begin() == emptyList.end());
}

int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}