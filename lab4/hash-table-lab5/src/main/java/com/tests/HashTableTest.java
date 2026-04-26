package com.tests;

import com.HashTable;
import com.KeyTransformer;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;

/**
 * Автоматические тесты для хеш-таблицы
 * Вариант №5
 */
public class HashTableTest {

    private HashTable<Double, String> hashTable;

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
    @DisplayName("Тест преобразования ключей - валидные значения")
    void testKeyTransformationValid() {
        assertDoesNotThrow(() -> KeyTransformer.transform(10000.0000));
        assertDoesNotThrow(() -> KeyTransformer.transform(12500.0000));
        assertDoesNotThrow(() -> KeyTransformer.transform(15000.0000));

        long k1 = KeyTransformer.transform(10000.0000);
        long k2 = KeyTransformer.transform(10000.0001);
        long k3 = KeyTransformer.transform(10000.0002);

        // Проверяем, что преобразованные значения в диапазоне [10000, 20000]
        assertTrue(k1 >= 10000 && k1 <= 20000);
        assertTrue(k2 >= 10000 && k2 <= 20000);
        assertTrue(k3 >= 10000 && k3 <= 20000);
    }

    @Test
    @DisplayName("Тест преобразования ключей - невалидные значения")
    void testKeyTransformationInvalid() {
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform(9999.9999));
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform(15000.0001));
        assertThrows(IllegalArgumentException.class, () -> KeyTransformer.transform(0.0));
    }

    @Test
    @DisplayName("Тест проверки валидности ключа")
    void testIsValidKey() {
        assertTrue(KeyTransformer.isValidKey(10000.0000));
        assertTrue(KeyTransformer.isValidKey(12500.0000));
        assertTrue(KeyTransformer.isValidKey(15000.0000));

        assertFalse(KeyTransformer.isValidKey(9999.9999));
        assertFalse(KeyTransformer.isValidKey(15000.0001));
    }

    // ==================== ТЕСТЫ ХЕШ-ТАБЛИЦЫ ====================

    @Test
    @DisplayName("Тест вставки элемента")
    void testInsert() {
        boolean result = hashTable.insert(10000.0000, "TestData");
        assertTrue(result);
        assertEquals(1, hashTable.size());
        assertFalse(hashTable.isEmpty());
    }

    @Test
    @DisplayName("Тест вставки дубликата")
    void testInsertDuplicate() {
        hashTable.insert(10000.0000, "Data1");
        boolean result = hashTable.insert(10000.0000, "Data2");
        assertFalse(result);
        assertEquals(1, hashTable.size());
    }

    @Test
    @DisplayName("Тест вставки с невалидным ключом")
    void testInsertInvalidKey() {
        assertThrows(IllegalArgumentException.class,
                () -> hashTable.insert(9000.0000, "Data"));
        assertEquals(0, hashTable.size());
    }

    @Test
    @DisplayName("Тест поиска элемента")
    void testSearch() {
        hashTable.insert(10000.0000, "Data1");
        hashTable.insert(12000.0000, "Data2");
        hashTable.insert(14000.0000, "Data3");

        String value = hashTable.search(12000.0000);
        assertEquals("Data2", value);

        assertEquals(3, hashTable.size());
    }

    @Test
    @DisplayName("Тест поиска отсутствующего элемента")
    void testSearchNotFound() {
        hashTable.insert(10000.0000, "Data1");

        assertThrows(NoSuchElementException.class,
                () -> hashTable.search(13000.0000));
    }

    @Test
    @DisplayName("Тест удаления элемента")
    void testDelete() {
        hashTable.insert(10000.0000, "Data1");
        hashTable.insert(11000.0000, "Data2");

        boolean result = hashTable.delete(10000.0000);
        assertTrue(result);
        assertEquals(1, hashTable.size());

        assertThrows(NoSuchElementException.class,
                () -> hashTable.search(10000.0000));
    }

    @Test
    @DisplayName("Тест удаления отсутствующего элемента")
    void testDeleteNotFound() {
        hashTable.insert(10000.0000, "Data1");

        boolean result = hashTable.delete(13000.0000);
        assertFalse(result);
        assertEquals(1, hashTable.size());
    }

    @Test
    @DisplayName("Тест очистки таблицы")
    void testClear() {
        hashTable.insert(10000.0000, "Data1");
        hashTable.insert(11000.0000, "Data2");
        hashTable.insert(12000.0000, "Data3");

        assertEquals(3, hashTable.size());
        hashTable.clear();
        assertEquals(0, hashTable.size());
        assertTrue(hashTable.isEmpty());
    }

    @Test
    @DisplayName("Тест containsKey")
    void testContainsKey() {
        hashTable.insert(10000.0000, "Data1");

        assertTrue(hashTable.containsKey(10000.0000));
        assertFalse(hashTable.containsKey(15000.0000));
    }

    @Test
    @DisplayName("Тест получения всех ключей")
    void testGetAllKeys() {
        hashTable.insert(10000.0000, "Data1");
        hashTable.insert(11000.0000, "Data2");
        hashTable.insert(12000.0000, "Data3");

        var keys = hashTable.getAllKeys();
        assertEquals(3, keys.size());
        assertTrue(keys.contains(10000.0000));
        assertTrue(keys.contains(11000.0000));
        assertTrue(keys.contains(12000.0000));
    }

    @Test
    @DisplayName("Тест итератора")
    void testIterator() {
        hashTable.insert(10000.0000, "Data1");
        hashTable.insert(11000.0000, "Data2");
        hashTable.insert(12000.0000, "Data3");

        int count = 0;
        for (HashTable.Entry<Double, String> entry : hashTable) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Тест коэффициента заполнения")
    void testLoadFactor() {
        assertEquals(0.0, hashTable.getLoadFactor(), 1e-6);

        for (int i = 0; i < 10; i++) {
            double key = 10000.0000 + i * 500.0000;
            hashTable.insert(key, "Data" + i);
        }

        double expectedFactor = 10.0 / hashTable.capacity();
        assertEquals(expectedFactor, hashTable.getLoadFactor(), 1e-6);
    }

    // ==================== ТЕСТЫ ПРОИЗВОДИТЕЛЬНОСТИ ====================

    @Test
    @DisplayName("Тест граничных значений ключей")
    void testBoundaryKeys() {
        // Минимальное значение
        assertTrue(hashTable.insert(KeyTransformer.getMinKey(), "MinData"));
        // Максимальное значение
        assertTrue(hashTable.insert(KeyTransformer.getMaxKey(), "MaxData"));

        assertEquals(2, hashTable.size());
        assertEquals("MinData", hashTable.search(KeyTransformer.getMinKey()));
        assertEquals("MaxData", hashTable.search(KeyTransformer.getMaxKey()));
    }
}
