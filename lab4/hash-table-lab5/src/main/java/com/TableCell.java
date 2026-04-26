package com;
/**
 * Класс, представляющий ячейку хеш-таблицы
 * Состояния: FREE (свободна), BUSY (занята), DELETED (удалена)
 */
public class TableCell<K, V> {

    /**
     * Состояния ячейки
     */
    public enum State {
        FREE,      // Свободная ячейка
        BUSY,      // Занятая ячейка
        DELETED    // Удалена (свободна после удаления)
    }

    private State state;    // Состояние ячейки
    private K key;          // Ключ
    private V value;        // Данные

    /**
     * Конструктор по умолчанию - создаёт свободную ячейку
     */
    public TableCell() {
        this.state = State.FREE;
        this.key = null;
        this.value = null;
    }

    /**
     * Конструктор с параметрами
     * @param key ключ
     * @param value значение
     */
    public TableCell(K key, V value) {
        this.state = State.BUSY;
        this.key = key;
        this.value = value;
    }

    // Геттеры и сеттеры
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public K getKey() { return key; }
    public void setKey(K key) { this.key = key; }
    public V getValue() { return value; }
    public void setValue(V value) { this.value = value; }

    /**
     * Проверка, является ли ячейка свободной (FREE или DELETED)
     * @return true если ячейка доступна для вставки
     */
    public boolean isAvailable() {
        return state == State.FREE || state == State.DELETED;
    }

    /**
     * Проверка, является ли ячейка активной (BUSY)
     * @return true если ячейка занята
     */
    public boolean isActive() {
        return state == State.BUSY;
    }

    @Override
    public String toString() {
        if (state == State.BUSY) {
            return String.format("[%s : %s]", key, value);
        } else if (state == State.DELETED) {
            return "[DELETED]";
        } else {
            return "[FREE]";
        }
    }
}