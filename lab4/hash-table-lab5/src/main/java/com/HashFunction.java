package com;

/**
 * Класс, реализующий хеш-функции для хеш-таблицы
 * Вариант №5: модульное хеширование
 */
public class HashFunction {

    private final int tableSize;

    /**
     * Конструктор
     * @param tableSize размер таблицы
     */
    public HashFunction(int tableSize) {
        this.tableSize = tableSize;
    }

    /**
     * Модульная хеш-функция
     * h(k) = k mod m, где m - простое число (размер таблицы)
     *
     * @param key преобразованный ключ k'
     * @return индекс в таблице
     */
    public int hash(long key) {
        return (int)(key % tableSize);
    }

    /**
     * Получить размер таблицы
     * @return размер таблицы
     */
    public int getTableSize() {
        return tableSize;
    }
}
