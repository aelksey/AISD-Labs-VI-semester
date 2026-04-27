package com;

/**
 * Преобразователь ключей для варианта №6
 *
 * Тип ключа: строка произвольной длины (символы - заглавные латинские буквы A-Z)
 * Преобразование: метод конкатенации битовых образов символов
 *
 * NBSP;Каждый символ преобразуется в 5-битный код (A=00001, B=00010, ..., Z=11010)
 * Затем все битовые образы конкатенируются в одно большое целое число
 */
public class KeyTransformer {

    // Алфавит: только заглавные латинские буквы
    private static final String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BITS_PER_CHAR = 5;  // 2^5 = 32, достаточно для 26 букв
    private static final int MAX_KEY_LENGTH = 12; // Максимальная длина ключа (для предотвращения переполнения)

    /**
     * Преобразование строкового ключа в натуральное число k'
     * Метод конкатенации битовых образов символов
     *
     * @param key исходный ключ (строка из заглавных латинских букв)
     * @return преобразованное целое значение k'
     * @throws IllegalArgumentException если ключ содержит недопустимые символы
     */
    public static long transform(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Ключ не может быть пустым");
        }

        // Проверка на допустимую длину
        if (key.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Ключ слишком длинный (макс. %d символов)", MAX_KEY_LENGTH));
        }

        long result = 0;

        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);

            // Проверка, что символ - заглавная латинская буква
            if (c < 'A' || c > 'Z') {
                throw new IllegalArgumentException(
                        String.format("Недопустимый символ '%c'. Разрешены только заглавные латинские буквы A-Z", c));
            }

            // Преобразование буквы в число: A=1, B=2, ..., Z=26
            // Используем 5-битное представление (1 = 00001, 2 = 00010, ...)
            int charCode = (c - 'A' + 1);  // A=1, B=2, ..., Z=26

            // Конкатенация: сдвигаем текущий результат влево на 5 бит и добавляем код символа
            result = (result << BITS_PER_CHAR) | charCode;
        }

        return result;
    }

    /**
     * Альтернативный метод преобразования с битовой маской для ограничения размера
     * Используется для предотвращения переполнения при длинных ключах
     */
    public static long transformWithMask(String key, int maxBits) {
        long result = 0;
        long mask = (1L << maxBits) - 1;

        for (int i = 0; i < key.length() && i < MAX_KEY_LENGTH; i++) {
            char c = key.charAt(i);
            if (c < 'A' || c > 'Z') {
                throw new IllegalArgumentException("Недопустимый символ: " + c);
            }
            int charCode = (c - 'A' + 1);
            result = ((result << BITS_PER_CHAR) | charCode) & mask;
        }

        return result;
    }

    /**
     * Получение битового представления строки в виде двоичной строки (для отладки)
     */
    public static String toBinaryString(String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            int charCode = (c - 'A' + 1);
            String binary = String.format("%5s", Integer.toBinaryString(charCode)).replace(' ', '0');
            sb.append(binary);
            if (i < key.length() - 1) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Проверка валидности ключа
     * @param key проверяемый ключ
     * @return true если ключ состоит только из заглавных латинских букв
     */
    public static boolean isValidKey(String key) {
        if (key == null || key.isEmpty()) return false;
        for (char c : key.toCharArray()) {
            if (c < 'A' || c > 'Z') return false;
        }
        return true;
    }

    /**
     * Получение максимальной длины ключа
     */
    public static int getMaxKeyLength() {
        return MAX_KEY_LENGTH;
    }

    /**
     * Демонстрация преобразования (для отладки)
     */
    public static String demonstrateTransformation(String key) {
        if (!isValidKey(key)) {
            return "Неверный ключ: " + key;
        }

        StringBuilder result = new StringBuilder();
        result.append("Ключ: ").append(key).append("\n");
        result.append("Битовые образы:\n");

        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            int charCode = c - 'A' + 1;
            String binary = String.format("%5s", Integer.toBinaryString(charCode)).replace(' ', '0');
            result.append(String.format("  '%c' → %d → %s\n", c, charCode, binary));
        }

        result.append("Конкатенация: ");
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            int charCode = c - 'A' + 1;
            result.append(String.format("%5s", Integer.toBinaryString(charCode)).replace(' ', '0'));
            if (i < key.length() - 1) result.append("");
        }
        result.append("\n");

        long transformed = transform(key);
        result.append("Преобразованное значение k' = ").append(transformed);

        return result.toString();
    }
}
