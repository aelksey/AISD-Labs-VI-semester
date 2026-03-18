#include <gtest/gtest.h>
#include "circular_list.h"
#include <vector>

// Тест конструктора по умолчанию
TEST(CircularListTest, Constructor) {
    CircularList<int> list;
    EXPECT_TRUE(list.empty());
    EXPECT_EQ(list.size(), 0);
}

// Тест push_back и доступа по индексу
TEST(CircularListTest, PushBackAndAt) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);

    EXPECT_EQ(list.size(), 3);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 20);
    EXPECT_EQ(list.at(2), 30);
    EXPECT_THROW(list.at(3), std::out_of_range);
}

// Тест вставки
TEST(CircularListTest, Insert) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(30);

    list.insert(1, 20);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 20);
    EXPECT_EQ(list.at(2), 30);

    list.insert(0, 5);
    EXPECT_EQ(list.at(0), 5);
    EXPECT_EQ(list.at(1), 10);

    list.insert(4, 40);
    EXPECT_EQ(list.at(4), 40);
}

// Тест изменения значения
TEST(CircularListTest, Set) {
    CircularList<int> list;
    list.push_back(1);
    list.push_back(2);
    list.push_back(3);

    list.set(1, 99);
    EXPECT_EQ(list.at(0), 1);
    EXPECT_EQ(list.at(1), 99);
    EXPECT_EQ(list.at(2), 3);
}

// Тест indexOf
TEST(CircularListTest, IndexOf) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);
    list.push_back(20);

    EXPECT_EQ(list.indexOf(20), 1);
    EXPECT_EQ(list.indexOf(30), 2);
    EXPECT_EQ(list.indexOf(40), -1);
}

// Тест contains
TEST(CircularListTest, Contains) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);

    EXPECT_TRUE(list.contains(10));
    EXPECT_TRUE(list.contains(20));
    EXPECT_FALSE(list.contains(30));
}

// Тест remove_value
TEST(CircularListTest, RemoveValue) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);
    list.push_back(20);

    EXPECT_TRUE(list.remove_value(20));
    EXPECT_EQ(list.size(), 3);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 30);
    EXPECT_EQ(list.at(2), 20);

    EXPECT_TRUE(list.remove_value(10));
    EXPECT_EQ(list.size(), 2);
    EXPECT_EQ(list.at(0), 30);
    EXPECT_EQ(list.at(1), 20);

    EXPECT_TRUE(list.remove_value(20));
    EXPECT_EQ(list.size(), 1);
    EXPECT_EQ(list.at(0), 30);

    EXPECT_TRUE(list.remove_value(30));
    EXPECT_TRUE(list.empty());

    EXPECT_FALSE(list.remove_value(100));
}

// Тест remove_at
TEST(CircularListTest, RemoveAt) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);

    EXPECT_TRUE(list.remove_at(1));
    EXPECT_EQ(list.size(), 2);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 30);

    list.push_back(40);
    EXPECT_TRUE(list.remove_at(2));
    EXPECT_EQ(list.size(), 2);
    EXPECT_EQ(list.at(0), 10);
    EXPECT_EQ(list.at(1), 30);

    EXPECT_TRUE(list.remove_at(0));
    EXPECT_EQ(list.size(), 1);
    EXPECT_EQ(list.at(0), 30);

    EXPECT_TRUE(list.remove_at(0));
    EXPECT_TRUE(list.empty());

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

    list2.set(1, 99);
    EXPECT_EQ(list1.at(1), 2);
    EXPECT_EQ(list2.at(1), 99);
}

// Тест clear
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

    std::vector<int> result;
    for (auto it = list.begin(); it != list.end(); ++it) {
        result.push_back(*it);
    }

    std::vector<int> expected = {10, 20, 30};
    EXPECT_EQ(result, expected);
}

// Тест: удаление элемента, на который указывает итератор
TEST(CircularListTest, IteratorAfterRemoveCurrent) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);
    list.push_back(40);
    
    auto it = list.begin();
    ++it; // теперь указывает на 20
    
    EXPECT_EQ(*it, 20);
    
    list.remove_value(20);
    
    // Итератор должен теперь указывать на следующий элемент (30)
    EXPECT_EQ(*it, 30);
    
    std::vector<int> result;
    for (auto iter = list.begin(); iter != list.end(); ++iter) {
        result.push_back(*iter);
    }
    std::vector<int> expected = {10, 30, 40};
    EXPECT_EQ(result, expected);
}

// Тест: удаление второго элемента
TEST(CircularListTest, IteratorRemoveSecondElem) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);
    
    auto it = list.begin();
    ++it;
    EXPECT_EQ(*it, 20);
    
    list.remove_value(20);
    
    EXPECT_EQ(*it, 30);
}

// Тест: удаление первого элемента
TEST(CircularListTest, IteratorRemoveFirstElem) {
    CircularList<int> list;
    list.push_back(10);
    list.push_back(20);
    list.push_back(30);

    auto it = list.begin();
    EXPECT_EQ(*it, 10);
    
    list.remove_value(10);
    
    EXPECT_EQ(*it, 20);
}

int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}