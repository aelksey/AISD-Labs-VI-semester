package com;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Хеш-таблица с открытой адресацией и квадратичным зондированием
 * ВАРИАНТ №6: ключ - строка (заглавные латинские буквы)
 *
 * @param <K> тип ключа (String)
 * @param <V> тип значения
 */
public class HashTable<K extends String, V> implements Iterable<HashTable.Entry<K, V>> {

    // Константы для квадратичного зондирования
    private static final int C1 = 1;
    private static final int C2 = 1;

    private TableCell<K, V>[] table;
    private int size;           // Количество элементов в таблице
    private int tableSize;      // Размер таблицы (ёмкость)
    private QuadraticProbing probing;
    private KeyTransformer transformer;

    // Статистика для последней операции
    private long lastTransformedKey;
    private int lastHashIndex;
    private int lastProbeCount;
    private String lastOriginalKey;

    /**
     * Вспомогательный класс для хранения пары ключ-значение
     */
    public static class Entry<K, V> {
        private final K key;
        private final V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Конструктор с заданным количеством элементов
     * Для мультипликативного хеширования размер таблицы выбираем как степень двойки
     * @param capacity ожидаемое количество элементов
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        // Для мультипликативного хеширования выбираем размер как степень двойки
        this.tableSize = nextPowerOfTwo((int)(capacity / 0.75));
        this.table = new TableCell[tableSize];
        this.size = 0;
        this.probing = new QuadraticProbing(tableSize, C1, C2);
        this.transformer = new KeyTransformer();

        // Инициализация ячеек
        for (int i = 0; i < tableSize; i++) {
            table[i] = new TableCell<>();
        }

        resetStats();
    }

    /**
     * Конструктор по умолчанию (ёмкость 100 элементов)
     */
    public HashTable() {
        this(100);
    }

    /**
     * Нахождение следующей степени двойки
     * @param n исходное число
     * @return ближайшая степень двойки, не меньшая n
     */
    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power <<= 1;
        }
        return power;
    }

    /**
     * Сброс статистики последней операции
     */
    private void resetStats() {
        lastTransformedKey = 0;
        lastHashIndex = -1;
        lastProbeCount = 0;
        lastOriginalKey = null;
    }

    /**
     * Вставка элемента в хеш-таблицу
     * @param key ключ (строка из заглавных латинских букв)
     * @param value значение
     * @return true если вставка успешна, false если ключ уже существует
     * @throws IllegalArgumentException если ключ содержит недопустимые символы
     */
    public boolean insert(K key, V value) {
        // Проверка валидности ключа
        if (!KeyTransformer.isValidKey(key)) {
            throw new IllegalArgumentException(
                    "Ключ должен состоять только из заглавных латинских букв A-Z. Получен: " + key);
        }

        if (key.length() > KeyTransformer.getMaxKeyLength()) {
            throw new IllegalArgumentException(
                    String.format("Ключ слишком длинный (макс. %d символов)", KeyTransformer.getMaxKeyLength()));
        }

        lastOriginalKey = key;

        // Преобразование ключа
        long transformedKey = KeyTransformer.transform(key);
        lastTransformedKey = transformedKey;

        // Поиск позиции для вставки
        int probeCount = 0;
        int firstDeleted = -1;

        while (probeCount < tableSize) {
            int index = probing.probe(transformedKey, probeCount);
            lastHashIndex = index;

            TableCell<K, V> cell = table[index];

            // Если нашли существующий ключ - ошибка
            if (cell.isActive() && cell.getKey().equals(key)) {
                lastProbeCount = probeCount + 1;
                return false;
            }

            // Запоминаем первую удалённую ячейку
            if (cell.getState() == TableCell.State.DELETED && firstDeleted == -1) {
                firstDeleted = index;
            }

            // Если нашли свободную ячейку
            if (cell.getState() == TableCell.State.FREE) {
                int targetIndex = (firstDeleted != -1) ? firstDeleted : index;
                table[targetIndex].setKey(key);
                table[targetIndex].setValue(value);
                table[targetIndex].setState(TableCell.State.BUSY);
                size++;
                lastProbeCount = probeCount + 1;
                return true;
            }

            probeCount++;
        }

        // Если нашли удалённую ячейку, используем её
        if (firstDeleted != -1) {
            table[firstDeleted].setKey(key);
            table[firstDeleted].setValue(value);
            table[firstDeleted].setState(TableCell.State.BUSY);
            size++;
            lastProbeCount = probeCount;
            return true;
        }

        lastProbeCount = probeCount;
        return false; // Таблица заполнена
    }

    /**
     * Поиск элемента по ключу
     * @param key ключ
     * @return значение элемента
     * @throws NoSuchElementException если элемент не найден
     */
    public V search(K key) {
        if (!KeyTransformer.isValidKey(key)) {
            throw new NoSuchElementException("Ключ содержит недопустимые символы: " + key);
        }

        lastOriginalKey = key;
        long transformedKey = KeyTransformer.transform(key);
        lastTransformedKey = transformedKey;

        int probeCount = 0;
        while (probeCount < tableSize) {
            int index = probing.probe(transformedKey, probeCount);
            lastHashIndex = index;

            TableCell<K, V> cell = table[index];

            // Если ячейка свободна - элемент отсутствует
            if (cell.getState() == TableCell.State.FREE) {
                lastProbeCount = probeCount + 1;
                throw new NoSuchElementException("Элемент с ключом '" + key + "' не найден");
            }

            // Если ячейка занята и ключ совпадает
            if (cell.isActive() && cell.getKey().equals(key)) {
                lastProbeCount = probeCount + 1;
                return cell.getValue();
            }

            probeCount++;
        }

        lastProbeCount = probeCount;
        throw new NoSuchElementException("Элемент с ключом '" + key + "' не найден");
    }

    /**
     * Проверка наличия ключа в таблице
     * @param key ключ
     * @return true если ключ присутствует
     */
    public boolean containsKey(K key) {
        try {
            search(key);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Удаление элемента по ключу
     * @param key ключ
     * @return true если удаление успешно, false если элемент не найден
     */
    public boolean delete(K key) {
        if (!KeyTransformer.isValidKey(key)) {
            return false;
        }

        lastOriginalKey = key;
        long transformedKey = KeyTransformer.transform(key);
        lastTransformedKey = transformedKey;

        int probeCount = 0;
        while (probeCount < tableSize) {
            int index = probing.probe(transformedKey, probeCount);
            lastHashIndex = index;

            TableCell<K, V> cell = table[index];

            // Если ячейка свободна - элемент отсутствует
            if (cell.getState() == TableCell.State.FREE) {
                lastProbeCount = probeCount + 1;
                return false;
            }

            // Если ячейка занята и ключ совпадает
            if (cell.isActive() && cell.getKey().equals(key)) {
                cell.setState(TableCell.State.DELETED);
                cell.setKey(null);
                cell.setValue(null);
                size--;
                lastProbeCount = probeCount + 1;
                return true;
            }

            probeCount++;
        }

        lastProbeCount = probeCount;
        return false;
    }

    /**
     * Очистка таблицы
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        for (int i = 0; i < tableSize; i++) {
            table[i] = new TableCell<>();
        }
        size = 0;
        resetStats();
    }

    /**
     * Получение размера таблицы
     * @return количество элементов
     */
    public int size() {
        return size;
    }

    /**
     * Получение ёмкости таблицы
     * @return размер массива
     */
    public int capacity() {
        return tableSize;
    }

    /**
     * Проверка, пуста ли таблица
     * @return true если таблица пуста
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Получение коэффициента заполнения
     * @return коэффициент заполнения α = n/m
     */
    public double getLoadFactor() {
        return (double) size / tableSize;
    }

    /**
     * Получение списка всех ключей
     * @return список ключей
     */
    public List<K> getAllKeys() {
        List<K> keys = new ArrayList<>();
        for (int i = 0; i < tableSize; i++) {
            if (table[i].isActive()) {
                keys.add(table[i].getKey());
            }
        }
        return keys;
    }

    /**
     * Получение списка всех записей
     * @return список записей
     */
    public List<Entry<K, V>> getAllEntries() {
        List<Entry<K, V>> entries = new ArrayList<>();
        for (int i = 0; i < tableSize; i++) {
            if (table[i].isActive()) {
                entries.add(new Entry<>(table[i].getKey(), table[i].getValue()));
            }
        }
        return entries;
    }

    /**
     * Вывод структуры таблицы на экран
     * @return строковое представление таблицы
     */
    public String displayTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Хеш-таблица (размер: ").append(tableSize);
        sb.append(", элементов: ").append(size);
        sb.append(", α = ").append(String.format("%.4f", getLoadFactor()));
        sb.append(") ===\n");
        sb.append("Метод хеширования: мультипликативный (A = ").append(String.format("%.6f", HashFunction.getA())).append(")\n");
        sb.append("Разрешение коллизий: квадратичное зондирование (c₁=").append(C1).append(", c₂=").append(C2).append(")\n");
        sb.append("Преобразование ключей: конкатенация 5-битных образов\n");
        sb.append("-----------------------------------------------------------\n");

        for (int i = 0; i < tableSize; i++) {
            sb.append(String.format("[%4d] ", i));
            if (table[i].isActive()) {
                sb.append(String.format("%-12s : %s",
                        table[i].getKey(),
                        table[i].getValue()));
                // Показываем хеш-значение для активных элементов
                long transformed = KeyTransformer.transform(table[i].getKey());
                sb.append(String.format(" (k'=%d, h=%d)", transformed, probing.probe(transformed, 0)));
            } else if (table[i].getState() == TableCell.State.DELETED) {
                sb.append("DELETED");
            } else {
                sb.append("FREE");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Получение статистики последней операции
     * @return строка со статистикой
     */
    public String getLastOperationStats() {
        return String.format(
                "Ключ: %s | k' = %d | Хеш-индекс h(k') = %d | Число зондирований = %d",
                lastOriginalKey != null ? lastOriginalKey : "-",
                lastTransformedKey,
                lastHashIndex,
                lastProbeCount
        );
    }

    /**
     * Получение количества зондирований последней операции
     * @return количество зондирований
     */
    public int getLastProbeCount() {
        return lastProbeCount;
    }

    /**
     * Получение преобразованного ключа последней операции
     */
    public long getLastTransformedKey() {
        return lastTransformedKey;
    }

    /**
     * Получение исходного ключа последней операции
     */
    public String getLastOriginalKey() {
        return lastOriginalKey;
    }

    /**
     * Получение внутреннего массива таблицы (для итератора)
     * @return массив ячеек
     */
    public TableCell<K, V>[] getTable() {
        return table;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new HashTableIterator<>(table, tableSize);
    }

    /**
     * Получение строкового представления всех элементов
     * @return строка с элементами
     */
    public String elementsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Entry<K, V> entry : this) {
            if (!first) sb.append(", ");
            sb.append(entry);
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Показать битовое представление ключа (для отладки)
     */
    public String showKeyBinary(String key) {
        return KeyTransformer.toBinaryString(key);
    }
}