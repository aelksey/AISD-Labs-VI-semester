package com;

import java.util.*;
import java.util.function.Function;

/**
 * Класс для тестирования трудоёмкости операций хеш-таблицы
 * Измеряет количество зондирований для операций поиска, вставки и удаления
 */
public class PerformanceTest {

    private final int tableSize;
    private final double targetLoadFactor;
    private final HashTable<Double, String> hashTable;
    private final List<Double> keys;
    private final List<Double> allKeys;
    private final Random random;

    // Результаты тестирования
    private double avgInsertProbes;
    private double avgSearchProbes;
    private double avgDeleteProbes;
    private double avgUnsuccessfulSearchProbes;

    public PerformanceTest(int expectedSize, double targetLoadFactor) {
        this.targetLoadFactor = targetLoadFactor;
        int capacity = (int)(expectedSize / targetLoadFactor);
        this.tableSize = findPrimeCapacity(capacity);
        this.hashTable = new HashTable<>(expectedSize);
        this.keys = new ArrayList<>();
        this.allKeys = new ArrayList<>();
        this.random = new Random(42); // Фиксированный seed для воспроизводимости

        // Генерация ключей
        generateKeys(expectedSize);
    }

    /**
     * Поиск простого размера таблицы
     */
    private int findPrimeCapacity(int minCapacity) {
        int candidate = minCapacity;
        while (!isPrime(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /**
     * Генерация тестовых ключей
     */
    private void generateKeys(int count) {
        double minKey = KeyTransformer.getMinKey();
        double maxKey = KeyTransformer.getMaxKey();

        for (int i = 0; i < count; i++) {
            // Равномерное распределение в диапазоне
            double key = minKey + (maxKey - minKey) * random.nextDouble();
            // Округление до 4 знаков
            key = Math.round(key * 10000.0) / 10000.0;
            keys.add(key);
            allKeys.add(key);
        }
    }

    /**
     * Заполнение таблицы элементами
     */
    private void populateTable() {
        hashTable.clear();
        for (Double key : keys) {
            hashTable.insert(key, "Data_" + key);
        }
    }

    /**
     * Тестирование трудоёмкости вставки
     * @param operations количество операций
     * @return среднее количество зондирований
     */
    public double testInsertPerformance(int operations) {
        HashTable<Double, String> testTable = new HashTable<>(operations);
        double totalProbes = 0;
        int successfulInserts = 0;

        // Создаём копию ключей для вставки
        List<Double> testKeys = generateRandomKeys(operations);

        for (Double key : testKeys) {
            boolean result = testTable.insert(key, "Test_" + key);
            if (result) {
                totalProbes += testTable.getLastProbeCount();
                successfulInserts++;
            }
        }

        avgInsertProbes = successfulInserts > 0 ? totalProbes / successfulInserts : 0;
        return avgInsertProbes;
    }

    /**
     * Тестирование трудоёмкости поиска (успешного)
     * @param operations количество операций
     * @return среднее количество зондирований
     */
    public double testSearchPerformance(int operations) {
        populateTable();
        double totalProbes = 0;
        int successfulSearches = 0;

        for (int i = 0; i < operations && i < keys.size(); i++) {
            try {
                hashTable.search(keys.get(i));
                totalProbes += hashTable.getLastProbeCount();
                successfulSearches++;
            } catch (NoSuchElementException e) {
                // Пропускаем
            }
        }

        avgSearchProbes = successfulSearches > 0 ? totalProbes / successfulSearches : 0;
        return avgSearchProbes;
    }

    /**
     * Тестирование трудоёмкости удаления
     * @param operations количество операций
     * @return среднее количество зондирований
     */
    public double testDeletePerformance(int operations) {
        populateTable();
        double totalProbes = 0;
        int successfulDeletes = 0;

        // Создаём копию ключей для удаления
        List<Double> keysToDelete = new ArrayList<>(keys);
        Collections.shuffle(keysToDelete, random);

        for (int i = 0; i < Math.min(operations, keysToDelete.size()); i++) {
            if (hashTable.delete(keysToDelete.get(i))) {
                totalProbes += hashTable.getLastProbeCount();
                successfulDeletes++;
            }
        }

        avgDeleteProbes = successfulDeletes > 0 ? totalProbes / successfulDeletes : 0;
        return avgDeleteProbes;
    }

    /**
     * Тестирование трудоёмкости неуспешного поиска
     * @param operations количество операций
     * @return среднее количество зондирований
     */
    public double testUnsuccessfulSearchPerformance(int operations) {
        populateTable();
        double totalProbes = 0;
        int attempts = 0;

        // Генерация ключей, которых нет в таблице
        double minKey = KeyTransformer.getMinKey();
        double maxKey = KeyTransformer.getMaxKey();

        for (int i = 0; i < operations; i++) {
            // Генерируем ключ, точно отсутствующий в таблице
            double key;
            do {
                key = minKey + (maxKey - minKey) * random.nextDouble();
                key = Math.round(key * 10000.0) / 10000.0;
            } while (hashTable.containsKey(key));

            try {
                hashTable.search(key);
            } catch (NoSuchElementException e) {
                totalProbes += hashTable.getLastProbeCount();
                attempts++;
            }
        }

        avgUnsuccessfulSearchProbes = attempts > 0 ? totalProbes / attempts : 0;
        return avgUnsuccessfulSearchProbes;
    }

    /**
     * Запуск полного тестирования для различных коэффициентов заполнения
     * @param loadFactors массив коэффициентов заполнения
     * @return результаты тестирования
     */
    public TestResult runFullTest(double[] loadFactors, int elementsPerTest) {
        TestResult result = new TestResult();

        for (double alpha : loadFactors) {
            int expectedSize = (int)(tableSize * alpha);
            PerformanceTest test = new PerformanceTest(expectedSize, alpha);

            ResultEntry entry = new ResultEntry();
            entry.loadFactor = alpha;
            entry.insertProbes = test.testInsertPerformance(elementsPerTest);
            entry.searchProbes = test.testSearchPerformance(elementsPerTest);
            entry.deleteProbes = test.testDeletePerformance(elementsPerTest);
            entry.unsuccessfulSearchProbes = test.testUnsuccessfulSearchPerformance(elementsPerTest);

            // Теоретические оценки
            entry.theoreticalSearchSuccess = calculateTheoreticalSearchSuccess(alpha);
            entry.theoreticalSearchUnsuccess = calculateTheoreticalSearchUnsuccess(alpha);

            result.addEntry(entry);
        }

        return result;
    }

    /**
     * Теоретическая оценка для успешного поиска (квадратичное зондирование)
     * ~ -ln(1-α)/α
     */
    private double calculateTheoreticalSearchSuccess(double alpha) {
        if (alpha >= 1.0) return Double.POSITIVE_INFINITY;
        return -Math.log(1 - alpha) / alpha;
    }

    /**
     * Теоретическая оценка для неуспешного поиска (квадратичное зондирование)
     * ~ 1/(1-α)
     */
    private double calculateTheoreticalSearchUnsuccess(double alpha) {
        if (alpha >= 1.0) return Double.POSITIVE_INFINITY;
        return 1.0 / (1.0 - alpha);
    }

    /**
     * Генерация случайных ключей
     */
    private List<Double> generateRandomKeys(int count) {
        List<Double> newKeys = new ArrayList<>();
        double minKey = KeyTransformer.getMinKey();
        double maxKey = KeyTransformer.getMaxKey();

        for (int i = 0; i < count; i++) {
            double key = minKey + (maxKey - minKey) * random.nextDouble();
            key = Math.round(key * 10000.0) / 10000.0;
            newKeys.add(key);
        }
        return newKeys;
    }

    public double getAvgInsertProbes() { return avgInsertProbes; }
    public double getAvgSearchProbes() { return avgSearchProbes; }
    public double getAvgDeleteProbes() { return avgDeleteProbes; }
    public double getAvgUnsuccessfulSearchProbes() { return avgUnsuccessfulSearchProbes; }

    /**
     * Внутренний класс для хранения результатов тестирования
     */
    public static class TestResult {
        private final List<ResultEntry> entries = new ArrayList<>();

        public void addEntry(ResultEntry entry) {
            entries.add(entry);
        }

        public List<ResultEntry> getEntries() {
            return entries;
        }

        public double[] getLoadFactors() {
            return entries.stream().mapToDouble(e -> e.loadFactor).toArray();
        }

        public double[] getInsertProbes() {
            return entries.stream().mapToDouble(e -> e.insertProbes).toArray();
        }

        public double[] getSearchProbes() {
            return entries.stream().mapToDouble(e -> e.searchProbes).toArray();
        }

        public double[] getDeleteProbes() {
            return entries.stream().mapToDouble(e -> e.deleteProbes).toArray();
        }

        public double[] getTheoreticalSearchSuccess() {
            return entries.stream().mapToDouble(e -> e.theoreticalSearchSuccess).toArray();
        }

        public double[] getTheoreticalSearchUnsuccess() {
            return entries.stream().mapToDouble(e -> e.theoreticalSearchUnsuccess).toArray();
        }
    }

    /**
     * Внутренний класс для записи результата
     */
    public static class ResultEntry {
        public double loadFactor;
        public double insertProbes;
        public double searchProbes;
        public double deleteProbes;
        public double unsuccessfulSearchProbes;
        public double theoreticalSearchSuccess;
        public double theoreticalSearchUnsuccess;
    }
}
