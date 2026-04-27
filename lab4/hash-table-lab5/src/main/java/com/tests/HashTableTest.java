package com.tests;

import com.HashFunction;
import com.HashTable;
import com.KeyTransformer;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;

/**
 * Автоматические тесты для хеш-таблицы
 * ВАРИАНТ №6
 *
 * Тип ключа: String (заглавные латинские буквы A-Z)
 * Преобразование: конкатенация битовых образов символов
 * Метод хеширования: мультипликативный
 * Разрешение коллизий: квадратичное зондирование (c₁=1, c₂=1)
 */
public class HashTableTest {

    private HashTable<String, String> hashTable;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable<>(20);
    }

    @AfterEach
    void tearDown() {
        hashTable = null;
    }

    // ==================== ТЕСТЫ ПРЕОБРАЗОВАНИЯ КЛЮЧЕЙ ====================

    @Test
    @DisplayName("Тест преобразования ключей - валидные строки")
    void testKeyTransformationValid() {
        // Валидные строки из заглавных латинских букв
        assertDoesNotThrow(() -> KeyTransformer.transform("A"));
        assertDoesNotThrow(() -> KeyTransformer.transform("HELLO"));
        assertDoesNotThrow(() -> KeyTransformer.transform("WORLD"));
        assertDoesNotThrow(() -> KeyTransformer.transform("JAVA"));
        assertDoesNotThrow(() -> KeyTransformer.transform("PROGRAMMING"));

        // Проверяем, что преобразование возвращает положительное число
        long result1 = KeyTransformer.transform("A");
        long result2 = KeyTransformer.transform("B");
        long result3 = KeyTransformer.transform("Z");

        assertTrue(result1 > 0);
        assertTrue(result2 > 0);
        assertTrue(result3 > 0);

        // A и B должны давать разные результаты
        assertNotEquals(result1, result2);
    }

    @Test
    @DisplayName("Тест преобразования ключей - невалидные символы")
    void testKeyTransformationInvalid() {
        // Строки с недопустимыми символами
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform("hello"));      // строчные буквы
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform("Hello123"));  // цифры
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform("HELLO!"));    // спецсимволы
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform(""));          // пустая строка
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform(null));        // null
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform("A B"));       // пробел
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform("HELLO-WORLD")); // дефис
    }

    @Test
    @DisplayName("Тест преобразования ключей - слишком длинные строки")
    void testKeyTransformationTooLong() {
        // Строка, превышающая максимальную длину
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < KeyTransformer.getMaxKeyLength() + 1; i++) {
            sb.append('A');
        }
        String longKey = sb.toString();

        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform(longKey));
    }

    @Test
    @DisplayName("Тест проверки валидности ключа")
    void testIsValidKey() {
        // Валидные ключи
        assertTrue(KeyTransformer.isValidKey("A"));
        assertTrue(KeyTransformer.isValidKey("HELLO"));
        assertTrue(KeyTransformer.isValidKey("WORLD"));
        assertTrue(KeyTransformer.isValidKey("JAVA"));
        assertTrue(KeyTransformer.isValidKey("XYZ"));

        // Невалидные ключи
        assertFalse(KeyTransformer.isValidKey("hello"));
        assertFalse(KeyTransformer.isValidKey("Hello123"));
        assertFalse(KeyTransformer.isValidKey("HELLO!"));
        assertFalse(KeyTransformer.isValidKey(""));
        assertFalse(KeyTransformer.isValidKey(null));
        assertFalse(KeyTransformer.isValidKey("A B"));
    }

    @Test
    @DisplayName("Тест битового представления строки")
    void testBinaryRepresentation() {
        // A = 1 = 00001
        assertEquals("00001", KeyTransformer.toBinaryString("A").replace(" ", ""));

        // AB: A=00001, B=00010 -> 0000100010
        String binaryAB = KeyTransformer.toBinaryString("AB").replace(" ", "");
        assertTrue(binaryAB.length() == 10);

        // Проверяем, что битовые образы разных букв различаются
        String binaryA = KeyTransformer.toBinaryString("A");
        String binaryB = KeyTransformer.toBinaryString("B");
        assertNotEquals(binaryA, binaryB);
    }

    @Test
    @DisplayName("Тест демонстрации преобразования")
    void testDemonstrateTransformation() {
        String demo = KeyTransformer.demonstrateTransformation("ABC");
        assertNotNull(demo);
        assertTrue(demo.contains("A"));
        assertTrue(demo.contains("B"));
        assertTrue(demo.contains("C"));

        // Невалидный ключ
        String invalidDemo = KeyTransformer.demonstrateTransformation("abc");
        assertTrue(invalidDemo.contains("Неверный ключ"));
    }

    // ==================== ТЕСТЫ ХЕШ-ФУНКЦИИ ====================

    @Test
    @DisplayName("Тест мультипликативной хеш-функции")
    void testHashFunction() {
        HashFunction hashFunc = new HashFunction(64);

        long key1 = KeyTransformer.transform("A");
        long key2 = KeyTransformer.transform("B");
        long key3 = KeyTransformer.transform("Z");

        int hash1 = hashFunc.hash(key1);
        int hash2 = hashFunc.hash(key2);
        int hash3 = hashFunc.hash(key3);

        // Все хеши должны быть в диапазоне [0, 63]
        assertTrue(hash1 >= 0 && hash1 < 64);
        assertTrue(hash2 >= 0 && hash2 < 64);
        assertTrue(hash3 >= 0 && hash3 < 64);
    }

    // ==================== ТЕСТЫ ХЕШ-ТАБЛИЦЫ ====================

    @Test
    @DisplayName("Тест вставки элемента с валидным ключом")
    void testInsertValid() {
        boolean result = hashTable.insert("APPLE", "Red fruit");
        assertTrue(result);
        assertEquals(1, hashTable.size());
        assertFalse(hashTable.isEmpty());
    }

    @Test
    @DisplayName("Тест вставки нескольких элементов")
    void testMultipleInserts() {
        assertTrue(hashTable.insert("APPLE", "Red fruit"));
        assertTrue(hashTable.insert("BANANA", "Yellow fruit"));
        assertTrue(hashTable.insert("CHERRY", "Red berry"));
        assertTrue(hashTable.insert("DATE", "Brown fruit"));

        assertEquals(4, hashTable.size());
    }

    @Test
    @DisplayName("Тест вставки дубликата")
    void testInsertDuplicate() {
        hashTable.insert("APPLE", "Red fruit");
        boolean result = hashTable.insert("APPLE", "Green apple");

        assertFalse(result);
        assertEquals(1, hashTable.size());

        // Проверяем, что значение не изменилось
        String value = hashTable.search("APPLE");
        assertEquals("Red fruit", value);
    }

    @Test
    @DisplayName("Тест вставки с невалидным ключом")
    void testInsertInvalidKey() {
        assertThrows(IllegalArgumentException.class,
                () -> hashTable.insert("apple", "Small fruit"));

        assertThrows(IllegalArgumentException.class,
                () -> hashTable.insert("APPLE123", "Fruit with numbers"));

        assertThrows(IllegalArgumentException.class,
                () -> hashTable.insert("", "Empty key"));

        assertEquals(0, hashTable.size());
    }

    @Test
    @DisplayName("Тест вставки с ключом, содержащим недопустимые символы")
    void testInsertWithInvalidCharacters() {
        String[] invalidKeys = {"HELLO!", "WORLD@", "JAVA#", "PROG$", "TEST%", "A1B2C3"};

        for (String key : invalidKeys) {
            assertThrows(IllegalArgumentException.class,
                    () -> hashTable.insert(key, "Data"));
        }
        assertEquals(0, hashTable.size());
    }

    @Test
    @DisplayName("Тест поиска существующего элемента")
    void testSearchExisting() {
        hashTable.insert("ORANGE", "Citrus fruit");
        hashTable.insert("MANGO", "Tropical fruit");
        hashTable.insert("KIWI", "Green fruit");

        String value = hashTable.search("ORANGE");
        assertEquals("Citrus fruit", value);

        value = hashTable.search("MANGO");
        assertEquals("Tropical fruit", value);

        assertEquals(3, hashTable.size());
    }

    @Test
    @DisplayName("Тест поиска отсутствующего элемента")
    void testSearchNotFound() {
        hashTable.insert("APPLE", "Red fruit");

        assertThrows(NoSuchElementException.class,
                () -> hashTable.search("BANANA"));

        assertThrows(NoSuchElementException.class,
                () -> hashTable.search("CHERRY"));

        assertThrows(NoSuchElementException.class,
                () -> hashTable.search("XYZ"));
    }

    @Test
    @DisplayName("Тест удаления элемента")
    void testDelete() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");

        boolean result = hashTable.delete("APPLE");
        assertTrue(result);
        assertEquals(1, hashTable.size());

        assertThrows(NoSuchElementException.class,
                () -> hashTable.search("APPLE"));

        // Второй элемент должен остаться
        assertEquals("Yellow fruit", hashTable.search("BANANA"));
    }

    @Test
    @DisplayName("Тест удаления отсутствующего элемента")
    void testDeleteNotFound() {
        hashTable.insert("APPLE", "Red fruit");

        boolean result = hashTable.delete("BANANA");
        assertFalse(result);
        assertEquals(1, hashTable.size());

        result = hashTable.delete("CHERRY");
        assertFalse(result);
        assertEquals(1, hashTable.size());
    }

    @Test
    @DisplayName("Тест удаления с последующей вставкой")
    void testDeleteThenInsert() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");

        hashTable.delete("APPLE");
        assertEquals(1, hashTable.size());

        // Вставляем новый элемент с другим ключом
        boolean result = hashTable.insert("CHERRY", "Red berry");
        assertTrue(result);
        assertEquals(2, hashTable.size());

        // Старый ключ не должен восстановиться
        assertThrows(NoSuchElementException.class,
                () -> hashTable.search("APPLE"));
    }

    @Test
    @DisplayName("Тест очистки таблицы")
    void testClear() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");
        hashTable.insert("CHERRY", "Red berry");
        hashTable.insert("DATE", "Brown fruit");

        assertEquals(4, hashTable.size());
        hashTable.clear();
        assertEquals(0, hashTable.size());
        assertTrue(hashTable.isEmpty());

        // Проверяем, что элементы действительно удалены
        assertThrows(NoSuchElementException.class,
                () -> hashTable.search("APPLE"));
    }

    @Test
    @DisplayName("Тест containsKey")
    void testContainsKey() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");

        assertTrue(hashTable.containsKey("APPLE"));
        assertTrue(hashTable.containsKey("BANANA"));
        assertFalse(hashTable.containsKey("CHERRY"));
        assertFalse(hashTable.containsKey("KIWI"));
    }

    @Test
    @DisplayName("Тест получения всех ключей")
    void testGetAllKeys() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");
        hashTable.insert("CHERRY", "Red berry");
        hashTable.insert("DATE", "Brown fruit");

        var keys = hashTable.getAllKeys();
        assertEquals(4, keys.size());
        assertTrue(keys.contains("APPLE"));
        assertTrue(keys.contains("BANANA"));
        assertTrue(keys.contains("CHERRY"));
        assertTrue(keys.contains("DATE"));
    }

    @Test
    @DisplayName("Тест получения всех записей")
    void testGetAllEntries() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");

        var entries = hashTable.getAllEntries();
        assertEquals(2, entries.size());

        // Проверяем содержимое
        boolean hasApple = false;
        boolean hasBanana = false;
        for (var entry : entries) {
            if (entry.getKey().equals("APPLE")) {
                assertEquals("Red fruit", entry.getValue());
                hasApple = true;
            }
            if (entry.getKey().equals("BANANA")) {
                assertEquals("Yellow fruit", entry.getValue());
                hasBanana = true;
            }
        }
        assertTrue(hasApple && hasBanana);
    }

    @Test
    @DisplayName("Тест итератора")
    void testIterator() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");
        hashTable.insert("CHERRY", "Red berry");

        int count = 0;
        for (HashTable.Entry<String, String> entry : hashTable) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Тест итератора на пустой таблице")
    void testIteratorEmpty() {
        int count = 0;
        for (HashTable.Entry<String, String> entry : hashTable) {
            count++;
        }
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Тест коэффициента заполнения")
    void testLoadFactor() {
        assertEquals(0.0, hashTable.getLoadFactor(), 1e-6);

        // Добавляем элементы
        for (int i = 0; i < 10; i++) {
            String key = String.valueOf((char)('A' + i));
            hashTable.insert(key, "Data" + i);
        }

        double expectedFactor = 10.0 / hashTable.capacity();
        assertEquals(expectedFactor, hashTable.getLoadFactor(), 1e-6);
    }

    @Test
    @DisplayName("Тест ёмкости таблицы (степень двойки)")
    void testCapacityIsPowerOfTwo() {
        int capacity = hashTable.capacity();
        // Проверяем, что ёмкость - степень двойки
        assertTrue((capacity & (capacity - 1)) == 0);
    }

    @Test
    @DisplayName("Тест статистики последней операции")
    void testLastOperationStats() {
        hashTable.insert("APPLE", "Red fruit");
        String stats = hashTable.getLastOperationStats();
        assertNotNull(stats);
        assertTrue(stats.contains("APPLE"));
        assertTrue(stats.contains("k'"));
        assertTrue(stats.contains("Хеш-индекс"));

        hashTable.search("APPLE");
        stats = hashTable.getLastOperationStats();
        assertNotNull(stats);

        hashTable.delete("APPLE");
        stats = hashTable.getLastOperationStats();
        assertNotNull(stats);
    }

    @Test
    @DisplayName("Тест строкового представления элементов")
    void testElementsToString() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");

        String elements = hashTable.elementsToString();
        assertTrue(elements.contains("APPLE"));
        assertTrue(elements.contains("BANANA"));
        assertTrue(elements.contains("Red fruit"));
        assertTrue(elements.contains("Yellow fruit"));
    }

    // ==================== ТЕСТЫ КОЛЛИЗИЙ ====================

    @Test
    @DisplayName("Тест разрешения коллизий (квадратичное зондирование)")
    void testCollisionResolution() {
        // Создаём таблицу маленького размера для гарантии коллизий
        HashTable<String, String> smallTable = new HashTable<>(4);

        // Вставляем элементы, которые могут вызвать коллизию
        smallTable.insert("A", "Value1");
        smallTable.insert("B", "Value2");
        smallTable.insert("C", "Value3");
        smallTable.insert("D", "Value4");

        // Все вставки должны быть успешны, несмотря на коллизии
        assertEquals(4, smallTable.size());

        // Проверяем, что все значения корректно извлекаются
        assertEquals("Value1", smallTable.search("A"));
        assertEquals("Value2", smallTable.search("B"));
        assertEquals("Value3", smallTable.search("C"));
        assertEquals("Value4", smallTable.search("D"));
    }

    // ==================== СТРЕСС-ТЕСТЫ ====================

    @Test
    @DisplayName("Стресс-тест: множество вставок")
    void testStressInsert() {
        int count = 50;
        String[] words = {
                "APPLE", "BANANA", "CHERRY", "DATE", "ELDER", "FIG", "GRAPE",
                "HONEY", "KIWI", "LEMON", "MANGO", "NUT", "ORANGE", "PAPAYA",
                "QUINCE", "RASPBERRY", "STRAWBERRY", "TANGERINE", "UGLI", "VANILLA",
                "WATER", "XRAY", "YELLOW", "ZEBRA", "ALPHA", "BETA", "GAMMA"
        };

        for (int i = 0; i < count && i < words.length; i++) {
            assertTrue(hashTable.insert(words[i], "Data" + i));
        }

        assertEquals(Math.min(count, words.length), hashTable.size());

        // Проверяем, что все ключи присутствуют
        for (int i = 0; i < count && i < words.length; i++) {
            assertTrue(hashTable.containsKey(words[i]));
        }
    }

    @Test
    @DisplayName("Стресс-тест: вставка и удаление")
    void testStressInsertDelete() {
        String[] keys = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        // Вставляем все
        for (String key : keys) {
            hashTable.insert(key, "Data_" + key);
        }
        assertEquals(keys.length, hashTable.size());

        // Удаляем половину
        for (int i = 0; i < keys.length / 2; i++) {
            assertTrue(hashTable.delete(keys[i]));
        }

        assertEquals(keys.length - keys.length / 2, hashTable.size());

        // Оставшиеся должны быть на месте
        for (int i = keys.length / 2; i < keys.length; i++) {
            assertTrue(hashTable.containsKey(keys[i]));
        }

        // Удалённые не должны находиться
        for (int i = 0; i < keys.length / 2; i++) {
            assertFalse(hashTable.containsKey(keys[i]));
        }
    }

    @Test
    @DisplayName("Стресс-тест: последовательность операций")
    void testStressOperationsSequence() {
        String[] insertKeys = {"APPLE", "BANANA", "CHERRY", "DATE", "ELDERBERRY"};
        String[] searchKeys = {"APPLE", "CHERRY", "FIG", "GRAPE"};
        String[] deleteKeys = {"BANANA", "DATE"};

        // Вставка
        for (String key : insertKeys) {
            hashTable.insert(key, "Data_" + key);
        }
        assertEquals(insertKeys.length, hashTable.size());

        // Поиск
        for (String key : searchKeys) {
            if (key.equals("APPLE") || key.equals("CHERRY")) {
                assertDoesNotThrow(() -> hashTable.search(key));
            } else {
                assertThrows(NoSuchElementException.class, () -> hashTable.search(key));
            }
        }

        // Удаление
        for (String key : deleteKeys) {
            assertTrue(hashTable.delete(key));
        }

        // Финальная проверка
        assertTrue(hashTable.containsKey("APPLE"));
        assertFalse(hashTable.containsKey("BANANA"));
        assertTrue(hashTable.containsKey("CHERRY"));
        assertFalse(hashTable.containsKey("DATE"));
        assertTrue(hashTable.containsKey("ELDERBERRY"));

        assertEquals(insertKeys.length - deleteKeys.length, hashTable.size());
    }

    @Test
    @DisplayName("Тест регистрозависимости ключей")
    void testCaseSensitivity() {
        // Ключи должны быть только заглавными, но если пользователь ввёл строчные,
        // мы их преобразуем в GUI, а здесь проверяем поведение

        assertThrows(IllegalArgumentException.class,
                () -> hashTable.insert("apple", "Lowercase"));

        assertDoesNotThrow(() -> hashTable.insert("APPLE", "Uppercase"));

        // Должны работать только заглавные
        assertTrue(hashTable.containsKey("APPLE"));
        assertFalse(hashTable.containsKey("apple"));
    }

    @Test
    @DisplayName("Тест однобуквенных ключей")
    void testSingleLetterKeys() {
        for (char c = 'A'; c <= 'Z'; c++) {
            String key = String.valueOf(c);
            assertTrue(hashTable.insert(key, "Letter_" + c));
        }

        assertEquals(26, hashTable.size());

        // Проверяем несколько букв
        assertEquals("Letter_A", hashTable.search("A"));
        assertEquals("Letter_M", hashTable.search("M"));
        assertEquals("Letter_Z", hashTable.search("Z"));
    }

    @Test
    @DisplayName("Тест граничных значений длины ключа")
    void testKeyLengthBoundaries() {
        // Минимальная длина (1 символ)
        assertTrue(hashTable.insert("A", "Single char"));

        // Максимальная допустимая длина
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < KeyTransformer.getMaxKeyLength(); i++) {
            sb.append('X');
        }
        String maxKey = sb.toString();
        assertTrue(hashTable.insert(maxKey, "Max length key"));

        assertEquals(2, hashTable.size());

        // Проверяем, что ключи сохранились
        assertEquals("Single char", hashTable.search("A"));
        assertEquals("Max length key", hashTable.search(maxKey));
    }

    @Test
    @DisplayName("Тест отображения структуры таблицы")
    void testDisplayTable() {
        hashTable.insert("APPLE", "Red fruit");
        hashTable.insert("BANANA", "Yellow fruit");

        String display = hashTable.displayTable();
        assertNotNull(display);
        assertTrue(display.contains("APPLE"));
        assertTrue(display.contains("BANANA"));
        assertTrue(display.contains("мультипликативный"));
        assertTrue(display.contains("квадратичное"));
    }

}