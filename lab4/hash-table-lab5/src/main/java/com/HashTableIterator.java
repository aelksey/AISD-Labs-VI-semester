package com;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Итератор для хеш-таблицы
 * Обеспечивает последовательный доступ к элементам таблицы
 * Поддерживает корректное поведение при удалении элементов через таблицу
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashTableIterator<K, V> implements Iterator<HashTable.Entry<K, V>> {

    private final TableCell<K, V>[] table;
    private final int tableSize;      // Размер таблицы (ёмкость)
    private int currentIndex;         // Текущий индекс при обходе
    private int lastReturnedIndex;    // Индекс последнего возвращённого элемента
    private int elementsReturned;
    private boolean canRemove;        // Разрешено ли удаление через итератор

    /**
     * Конструктор итератора
     * @param table массив ячеек таблицы
     * @param tableSize размер таблицы
     */
    @SuppressWarnings("unchecked")
    public HashTableIterator(TableCell<K, V>[] table, int tableSize) {
        this.table = table;
        this.tableSize = tableSize;
        this.currentIndex = 0;
        this.lastReturnedIndex = -1;
        this.elementsReturned = 0;
        this.canRemove = false;

        // Найти первый активный элемент
        advanceToNextActive();
    }

    /**
     * Продвижение к следующему активному элементу
     */
    private void advanceToNextActive() {
        while (currentIndex < tableSize &&
                (table[currentIndex] == null || !table[currentIndex].isActive())) {
            currentIndex++;
        }
    }

    /**
     * Проверка, изменилась ли таблица (нужно для синхронизации итератора)
     * Если последний возвращённый индекс больше не указывает на активный элемент,
     * итератор считается невалидным
     */
    public boolean isValid() {
        if (lastReturnedIndex >= 0 && lastReturnedIndex < tableSize) {
            TableCell<K, V> cell = table[lastReturnedIndex];
            return cell != null && cell.isActive();
        }
        return true;
    }

    /**
     * Получить последний возвращённый индекс
     */
    public int getLastReturnedIndex() {
        return lastReturnedIndex;
    }

    /**
     * Получить текущий индекс (для синхронизации)
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Получить следующий индекс (для синхронизации после удаления)
     */
    public int getNextIndex() {
        int tempIndex = currentIndex;
        while (tempIndex < tableSize &&
                (table[tempIndex] == null || !table[tempIndex].isActive())) {
            tempIndex++;
        }
        return tempIndex < tableSize ? tempIndex : -1;
    }

    /**
     * Синхронизировать итератор после внешнего удаления элемента
     * @param deletedIndex индекс удалённого элемента
     * @return true если итератор был обновлён, false если итератор стал невалидным
     */
    public boolean syncAfterDeletion(int deletedIndex) {
        // Если удалённый элемент - это последний возвращённый
        if (deletedIndex == lastReturnedIndex) {
            // Перемещаемся к следующему элементу
            currentIndex = lastReturnedIndex + 1;
            advanceToNextActive();
            lastReturnedIndex = -1;
            canRemove = false;
            return true;
        }

        // Если удалённый элемент находится перед последним возвращённым,
        // нужно скорректировать индексы
        if (deletedIndex < lastReturnedIndex) {
            lastReturnedIndex--;
            currentIndex = lastReturnedIndex + 1;
        }

        return true;
    }

    /**
     * Сбросить итератор на начало
     */
    public void reset() {
        currentIndex = 0;
        lastReturnedIndex = -1;
        elementsReturned = 0;
        canRemove = false;
        advanceToNextActive();
    }

    @Override
    public boolean hasNext() {
        return currentIndex < tableSize;
    }

    @Override
    public HashTable.Entry<K, V> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in the hash table");
        }

        TableCell<K, V> cell = table[currentIndex];
        if (cell == null || !cell.isActive()) {
            // Пропускаем неактивные ячейки
            advanceToNextActive();
            if (currentIndex >= tableSize) {
                throw new NoSuchElementException("No more elements in the hash table");
            }
            cell = table[currentIndex];
        }

        lastReturnedIndex = currentIndex;
        HashTable.Entry<K, V> entry = new HashTable.Entry<>(cell.getKey(), cell.getValue());

        currentIndex++;
        advanceToNextActive();
        elementsReturned++;
        canRemove = true;

        return entry;
    }

    /**
     * Удаление текущего элемента через итератор
     * (соответствует стандартному поведению Iterator.remove())
     */
    @Override
    public void remove() {
        if (!canRemove) {
            throw new IllegalStateException("remove() can only be called after next()");
        }

        if (lastReturnedIndex < 0 || lastReturnedIndex >= tableSize) {
            throw new IllegalStateException("No element to remove");
        }

        TableCell<K, V> cell = table[lastReturnedIndex];
        if (cell == null || !cell.isActive()) {
            throw new IllegalStateException("Element already removed");
        }

        // Помечаем ячейку как DELETED
        cell.setState(TableCell.State.DELETED);
        cell.setKey(null);
        cell.setValue(null);

        canRemove = false;

        // Обновляем позицию итератора
        currentIndex = lastReturnedIndex;
        advanceToNextActive();
    }

    /**
     * Получить количество возвращённых элементов
     * @return количество элементов
     */
    public int getElementsReturned() {
        return elementsReturned;
    }
}