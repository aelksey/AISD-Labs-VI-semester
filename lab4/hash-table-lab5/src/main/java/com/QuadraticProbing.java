package com;

/**
 * Класс, реализующий квадратичное зондирование
 * h(k, i) = (h(k) + c₁*i + c₂*i²) mod m
 *
 * Для варианта №6: c₁ = 1, c₂ = 1
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
        // Для i=0: baseHash
        // Для i=1: baseHash + 1 + 1 = baseHash + 2
        // Для i=2: baseHash + 2 + 4 = baseHash + 6
        // Для i=3: baseHash + 3 + 9 = baseHash + 12
        int offset = c1 * probeNumber + c2 * probeNumber * probeNumber;
        return (baseHash + offset) % tableSize;
    }

    /**
     * Вычисление индекса для отрицательных попыток (для обратного итератора)
     * @param key преобразованный ключ
     * @param probeNumber номер попытки (отрицательный)
     * @return индекс ячейки
     */
    public int probeNegative(long key, int probeNumber) {
        int baseHash = hashFunction.hash(key);
        // Для отрицательных попыток используем квадратичную формулу с отрицательным i
        int offset = c1 * probeNumber + c2 * probeNumber * probeNumber;
        int result = (baseHash + offset) % tableSize;
        if (result < 0) result += tableSize;
        return result;
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

    /**
     * Получить константу c₁
     */
    public int getC1() { return c1; }

    /**
     * Получить константу c₂
     */
    public int getC2() { return c2; }
}
