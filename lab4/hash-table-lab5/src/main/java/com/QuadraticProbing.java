package com;

/**
 * Класс, реализующий квадратичное зондирование
 * h(k, i) = (h(k) + c₁*i + c₂*i²) mod m
 *
 * Для варианта №5 используются константы c₁ = 1, c₂ = 1
 */
public class QuadraticProbing {

    private final int tableSize;
    private final HashFunction hashFunction;
    private final int c1;
    private final int c2;

    /**
     * Конструктор
     * @param tableSize размер таблицы
     */
    public QuadraticProbing(int tableSize) {
        this.tableSize = tableSize;
        this.hashFunction = new HashFunction(tableSize);
        this.c1 = 1;
        this.c2 = 1;
    }

    /**
     * Конструктор с заданными константами
     * @param tableSize размер таблицы
     * @param c1 константа c₁
     * @param c2 константа c₂
     */
    public QuadraticProbing(int tableSize, int c1, int c2) {
        this.tableSize = tableSize;
        this.hashFunction = new HashFunction(tableSize);
        this.c1 = c1;
        this.c2 = c2;
    }

    /**
     * Вычисление индекса для заданной попытки зондирования
     * @param key преобразованный ключ
     * @param probeNumber номер попытки (начинается с 0)
     * @return индекс ячейки
     */
    public int probe(long key, int probeNumber) {
        int baseHash = hashFunction.hash(key);
        // h(k, i) = (h(k) + c₁*i + c₂*i²) mod m
        return (baseHash + c1 * probeNumber + c2 * probeNumber * probeNumber) % tableSize;
    }

    /**
     * Получить размер таблицы
     * @return размер таблицы
     */
    public int getTableSize() {
        return tableSize;
    }

    /**
     * Получить хеш-функцию
     * @return хеш-функция
     */
    public HashFunction getHashFunction() {
        return hashFunction;
    }
}
