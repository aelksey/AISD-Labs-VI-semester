package com;

import java.util.*;
import java.util.function.Function;

/**
 * Класс для тестирования трудоёмкости операций хеш-таблицы
 * Адаптирован для варианта №6 (строковые ключи)
 */
public class PerformanceTest {

    private final int tableSize;
    private final double targetLoadFactor;
    private final HashTable<String, String> hashTable;
    private final List<String> keys;
    private final List<String> allKeys;
    private final Random random;

    // Словарь для генерации случайных строк
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String[] SAMPLE_WORDS = {
            "APPLE", "BANANA", "CHERRY", "DATE", "ELDER", "FIG", "GRAPE",
            "HONEY", "KIWI", "LEMON", "MANGO", "NUT", "ORANGE", "PAPAYA",
            "QUINCE", "RASPBERRY", "STRAWBERRY", "TANGERINE", "UGLI",
            "VANILLA", "WATER", "XRAY", "YELLOW", "ZEBRA", "ALPHA", "BETA",
            "GAMMA", "DELTA", "THETA", "OMEGA", "SIGMA", "KAPPA", "LAMBDA"
    };

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

        generateKeys(expectedSize);
    }

    private int findPrimeCapacity(int minCapacity) {
        int candidate = Math.max(minCapacity, 8);
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

    private void generateKeys(int count) {
        for (int i = 0; i < count; i++) {
            String key = generateRandomString(random.nextInt(5) + 1); // длина от 1 до 6
            keys.add(key);
            allKeys.add(key);
        }
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    private void populateTable() {
        hashTable.clear();
        for (String key : keys) {
            hashTable.insert(key, "Data_" + key);
        }
    }

    public double testInsertPerformance(int operations) {
        HashTable<String, String> testTable = new HashTable<>(operations);
        double totalProbes = 0;
        int successfulInserts = 0;

        List<String> testKeys = generateRandomKeys(operations);

        for (String key : testKeys) {
            boolean result = testTable.insert(key, "Test_" + key);
            if (result) {
                totalProbes += testTable.getLastProbeCount();
                successfulInserts++;
            }
        }

        avgInsertProbes = successfulInserts > 0 ? totalProbes / successfulInserts : 0;
        return avgInsertProbes;
    }

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

    public double testDeletePerformance(int operations) {
        populateTable();
        double totalProbes = 0;
        int successfulDeletes = 0;

        List<String> keysToDelete = new ArrayList<>(keys);
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

    public double testUnsuccessfulSearchPerformance(int operations) {
        populateTable();
        double totalProbes = 0;
        int attempts = 0;

        for (int i = 0; i < operations; i++) {
            String key;
            do {
                key = generateRandomString(random.nextInt(6) + 1);
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
            entry.theoreticalSearchSuccess = calculateTheoreticalSearchSuccess(alpha);
            entry.theoreticalSearchUnsuccess = calculateTheoreticalSearchUnsuccess(alpha);

            result.addEntry(entry);
        }

        return result;
    }

    private double calculateTheoreticalSearchSuccess(double alpha) {
        if (alpha >= 1.0) return Double.POSITIVE_INFINITY;
        return -Math.log(1 - alpha) / alpha;
    }

    private double calculateTheoreticalSearchUnsuccess(double alpha) {
        if (alpha >= 1.0) return Double.POSITIVE_INFINITY;
        return 1.0 / (1.0 - alpha);
    }

    private List<String> generateRandomKeys(int count) {
        List<String> newKeys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            newKeys.add(generateRandomString(random.nextInt(6) + 1));
        }
        return newKeys;
    }

    public double getAvgInsertProbes() { return avgInsertProbes; }
    public double getAvgSearchProbes() { return avgSearchProbes; }
    public double getAvgDeleteProbes() { return avgDeleteProbes; }
    public double getAvgUnsuccessfulSearchProbes() { return avgUnsuccessfulSearchProbes; }

    public static class TestResult {
        private final List<ResultEntry> entries = new ArrayList<>();

        public void addEntry(ResultEntry entry) { entries.add(entry); }
        public List<ResultEntry> getEntries() { return entries; }

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