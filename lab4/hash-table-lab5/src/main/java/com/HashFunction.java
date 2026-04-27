package com;

/**
 * Класс, реализующий хеш-функции для хеш-таблицы
 * ВАРИАНТ №6: мультипликативное хеширование
 *
 * Формула: h(k) = floor(m * (k * A mod 1))
 * где A = (√5 - 1)/2 ≈ 0.6180339887498949 (золотое сечение)
 */
public class HashFunction {

    private final int tableSize;

    // Константа A для мультипликативного хеширования (золотое сечение)
    // A = (√5 - 1)/2 ≈ 0.6180339887498949
    private static final double A = (Math.sqrt(5) - 1) / 2;

    /**
     * Конструктор
     * @param tableSize размер таблицы (рекомендуется степень двойки)
     */
    public HashFunction(int tableSize) {
        this.tableSize = tableSize;
    }

    /**
     * Мультипликативная хеш-функция
     * h(k) = floor(m * (k * A mod 1))
     *
     * Преимущество: качество мало зависит от выбора m
     * Рекомендуется выбирать m как степень двойки для быстрого вычисления
     *
     * @param key преобразованный ключ k'
     * @return индекс в таблице
     */
    public int hash(long key) {
        // Вычисление дробной части произведения key * A
        double product = key * A;
        double fractionalPart = product - Math.floor(product);

        // Вычисление индекса
        return (int) (tableSize * fractionalPart);
    }

    /**
     * Альтернативная реализация (без использования double)
     * Для ключей и таблиц небольшого размера
     */
    public int hashFast(long key) {
        // Используем только младшие биты (быстро, но менее равномерно)
        return (int) ((key * 0x9e3779b97f4a7c15L) >>> (64 - log2(tableSize)));
    }

    /**
     * Вычисление логарифма по основанию 2
     */
    private int log2(int n) {
        return 32 - Integer.numberOfLeadingZeros(n - 1);
    }

    /**
     * Получить размер таблицы
     * @return размер таблицы
     */
    public int getTableSize() {
        return tableSize;
    }

    /**
     * Получить константу A
     */
    public static double getA() {
        return A;
    }
}
