package com;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Итератор для хеш-таблицы
 * Обеспечивает последовательный доступ к элементам таблицы
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashTableIterator<K, V> implements Iterator<HashTable.Entry<K, V>> {

    private final TableCell<K, V>[] table;
    private final int size;
    private int currentIndex;
    private int elementsReturned;

    /**
     * Конструктор итератора
     * @param table массив ячеек таблицы
     * @param tableSize размер таблицы
     */
    @SuppressWarnings("unchecked")
    public HashTableIterator(TableCell<K, V>[] table, int tableSize) {
        this.table = table;
        this.size = tableSize;
        this.currentIndex = 0;
        this.elementsReturned = 0;

        // Найти первый занятый элемент
        advanceToNextActive();
    }

    /**
     * Продвижение к следующему активному элементу
     */
    private void advanceToNextActive() {
        while (currentIndex < size &&
                (table[currentIndex] == null || !table[currentIndex].isActive())) {
            currentIndex++;
        }
    }

    @Override
    public boolean hasNext() {
        return currentIndex < size;
    }

    @Override
    public HashTable.Entry<K, V> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in the hash table");
        }

        TableCell<K, V> cell = table[currentIndex];
        HashTable.Entry<K, V> entry = new HashTable.Entry<>(cell.getKey(), cell.getValue());

        currentIndex++;
        advanceToNextActive();
        elementsReturned++;

        return entry;
    }

    /**
     * Получить количество возвращённых элементов
     * @return количество элементов
     */
    public int getElementsReturned() {
        return elementsReturned;
    }
}