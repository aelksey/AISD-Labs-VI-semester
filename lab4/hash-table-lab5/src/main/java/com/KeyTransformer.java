package com;

/**
 * Преобразователь ключей для варианта №5
 *
 * Тип ключа: вещественное число на интервале [10000.0000, 15000.0000]
 * Преобразование: с точностью 10⁻⁴ и свёртка на интервал [10000, 20000]
 */
public class KeyTransformer {

    private static final double MIN_KEY = 10000.0000;
    private static final double MAX_KEY = 15000.0000;
    private static final double TARGET_MIN = 10000.0;
    private static final double TARGET_MAX = 20000.0;
    private static final int PRECISION = 10000; // 10⁻⁴

    /**
     * Преобразование вещественного ключа к целому значению k'
     *
     * @param key исходный ключ (вещественное число)
     * @return преобразованное целое значение k' в диапазоне [10000, 20000]
     * @throws IllegalArgumentException если ключ вне допустимого диапазона
     */
    public static long transform(double key) {
        // Проверка диапазона
        if (key < MIN_KEY - 1e-6 || key > MAX_KEY + 1e-6) {
            throw new IllegalArgumentException(
                    String.format("Ключ %.4f вне допустимого диапазона [%.4f, %.4f]",
                            key, MIN_KEY, MAX_KEY)
            );
        }

        // Шаг 1: Округление до 4 знаков после запятой (точность 10⁻⁴)
        long scaledKey = Math.round(key * PRECISION);

        // Шаг 2: Нормализация в диапазон [0, (MAX-MIN)*PRECISION]
        long minScaled = Math.round(MIN_KEY * PRECISION);
        long rangeScaled = Math.round((MAX_KEY - MIN_KEY) * PRECISION);
        long normalized = scaledKey - minScaled;

        // Шаг 3: Свёртка (fold) в диапазон [10000, 20000]
        long targetRange = (long)(TARGET_MAX - TARGET_MIN);
        long folded = normalized % targetRange;
        long result = (long)TARGET_MIN + folded;

        // Дополнительная свёртка для уменьшения (метод сложения цифр)
        result = additionalFold(result);

        return result;
    }

    /**
     * Дополнительная свёртка - метод сложения цифр
     * @param value исходное значение
     * @return свёрнутое значение
     */
    private static long additionalFold(long value) {
        long sum = 0;
        long temp = value;
        while (temp > 0) {
            sum += temp % 10;
            temp /= 10;
        }

        // Если сумма всё ещё велика, повторяем
        while (sum >= 10000) {
            long newSum = 0;
            while (sum > 0) {
                newSum += sum % 10;
                sum /= 10;
            }
            sum = newSum;
        }

        return sum + 10000;
    }

    /**
     * Метод для получения исходного масштабированного значения ключа
     * @param key исходный ключ
     * @return масштабированное значение
     */
    public static long getScaledKey(double key) {
        return Math.round(key * PRECISION);
    }

    /**
     * Проверка валидности ключа
     * @param key проверяемый ключ
     * @return true если ключ в допустимом диапазоне
     */
    public static boolean isValidKey(double key) {
        return key >= MIN_KEY - 1e-6 && key <= MAX_KEY + 1e-6;
    }

    /**
     * Получение минимального допустимого ключа
     * @return минимальный ключ
     */
    public static double getMinKey() { return MIN_KEY; }

    /**
     * Получение максимального допустимого ключа
     * @return максимальный ключ
     */
    public static double getMaxKey() { return MAX_KEY; }
}
