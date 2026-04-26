package com;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * GUI приложение для работы с хеш-таблицей
 */
public class HashTableGUI extends JFrame {

    private HashTable<Double, String> hashTable;

    // Компоненты GUI
    private JTextField keyField;
    private JTextField valueField;
    private JTextField searchKeyField;
    private JTextField deleteKeyField;
    private JTextArea displayArea;
    private JTable statsTable;
    private DefaultTableModel statsTableModel;
    private JLabel statusLabel;
    private JLabel sizeLabel;
    private JLabel loadFactorLabel;
    private JLabel statsLabel;

    public HashTableGUI() {
        initComponents();
        initializeHashTable();
        updateDisplay();
    }

    private void initComponents() {
        setTitle("Хеш-таблица с открытой адресацией - Вариант №5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Главная панель с табуляцией
        JTabbedPane tabbedPane = new JTabbedPane();

        // Вкладка операций
        tabbedPane.addTab("Операции", createOperationsPanel());

        // Вкладка отображения
        tabbedPane.addTab("Структура таблицы", createDisplayPanel());

        // Вкладка статистики
        tabbedPane.addTab("Статистика", createStatsPanel());

        add(tabbedPane);

        // Панель статуса
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Готов к работе");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // Установка стиля
        setLookAndFeel();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель вставки
        JPanel insertPanel = createInputPanel("Вставка элемента",
                new String[]{"Ключ (10000.0000 - 15000.0000):", "Значение:"},
                new JComponent[]{keyField = new JTextField(15), valueField = new JTextField(15)},
                e -> insertElement());

        // Панель поиска
        JPanel searchPanel = createInputPanel("Поиск элемента",
                new String[]{"Ключ для поиска:"},
                new JComponent[]{searchKeyField = new JTextField(15)},
                e -> searchElement());

        // Панель удаления
        JPanel deletePanel = createInputPanel("Удаление элемента",
                new String[]{"Ключ для удаления:"},
                new JComponent[]{deleteKeyField = new JTextField(15)},
                e -> deleteElement());

        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление"));

        JButton clearButton = new JButton("Очистить таблицу");
        clearButton.addActionListener(e -> clearTable());
        JButton randomButton = new JButton("Добавить случайные данные (10 шт)");
        randomButton.addActionListener(e -> addRandomData());
        JButton refreshButton = new JButton("Обновить отображение");
        refreshButton.addActionListener(e -> updateDisplay());

        controlPanel.add(clearButton);
        controlPanel.add(randomButton);
        controlPanel.add(refreshButton);

        // Информационная панель
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Информация"));

        sizeLabel = new JLabel("0");
        loadFactorLabel = new JLabel("0.0000");
        statsLabel = new JLabel("Нет операций");

        infoPanel.add(new JLabel("Количество элементов:"));
        infoPanel.add(sizeLabel);
        infoPanel.add(new JLabel("Коэффициент заполнения α:"));
        infoPanel.add(loadFactorLabel);
        infoPanel.add(new JLabel("Статистика последней операции:"));
        infoPanel.add(statsLabel);

        // Сборка панели
        JPanel operationsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        operationsPanel.add(insertPanel, gbc);
        gbc.gridy = 1;
        operationsPanel.add(searchPanel, gbc);
        gbc.gridy = 2;
        operationsPanel.add(deletePanel, gbc);
        gbc.gridy = 3;
        operationsPanel.add(controlPanel, gbc);
        gbc.gridy = 4;
        operationsPanel.add(infoPanel, gbc);

        panel.add(operationsPanel, BorderLayout.NORTH);

        // Область вывода
        displayArea = new JTextArea(20, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Результаты операций"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel(String title, String[] labels, JComponent[] fields, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            fieldsPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            fieldsPanel.add(fields[i], gbc);
        }

        JButton actionButton = new JButton(title.split(" ")[0]);
        actionButton.addActionListener(action);

        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(actionButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea structureArea = new JTextArea(30, 80);
        structureArea.setEditable(false);
        structureArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JButton refreshButton = new JButton("Обновить структуру");
        refreshButton.addActionListener(e -> {
            if (hashTable != null) {
                structureArea.setText(hashTable.displayTable());
            }
        });

        panel.add(new JScrollPane(structureArea), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Таблица статистики
        String[] columns = {"Параметр", "Значение"};
        statsTableModel = new DefaultTableModel(columns, 0);
        statsTable = new JTable(statsTableModel);
        statsTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JButton refreshButton = new JButton("Обновить статистику");
        refreshButton.addActionListener(e -> updateStatsTable());

        panel.add(new JScrollPane(statsTable), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void initializeHashTable() {
        // Инициализация с ожидаемой ёмкостью 100
        hashTable = new HashTable<>(50);
        updateStatsTable();
    }

    private void insertElement() {
        try {
            String keyStr = keyField.getText().trim();
            String value = valueField.getText().trim();

            if (keyStr.isEmpty()) {
                showError("Введите ключ");
                return;
            }
            if (value.isEmpty()) {
                showError("Введите значение");
                return;
            }

            double key = Double.parseDouble(keyStr);

            if (!KeyTransformer.isValidKey(key)) {
                showError(String.format("Ключ должен быть в диапазоне [%.4f, %.4f]",
                        KeyTransformer.getMinKey(), KeyTransformer.getMaxKey()));
                return;
            }

            boolean result = hashTable.insert(key, value);

            if (result) {
                statusLabel.setText("Вставка успешна");
                displayArea.append(String.format("✓ Вставлен: %.4f → %s\n", key, value));
                displayArea.append(hashTable.getLastOperationStats() + "\n\n");
                keyField.setText("");
                valueField.setText("");
            } else {
                showError("Элемент с таким ключом уже существует");
                displayArea.append(String.format("✗ Ошибка вставки: ключ %.4f уже существует\n", key));
                displayArea.append(hashTable.getLastOperationStats() + "\n\n");
            }

            updateDisplay();

        } catch (NumberFormatException e) {
            showError("Некорректный формат ключа. Введите вещественное число");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    private void searchElement() {
        try {
            String keyStr = searchKeyField.getText().trim();

            if (keyStr.isEmpty()) {
                showError("Введите ключ для поиска");
                return;
            }

            double key = Double.parseDouble(keyStr);
            String value = hashTable.search(key);

            statusLabel.setText("Поиск успешен");
            displayArea.append(String.format("🔍 Найдено: %.4f → %s\n", key, value));
            displayArea.append(hashTable.getLastOperationStats() + "\n\n");
            searchKeyField.setText("");

        } catch (NoSuchElementException e) {
            statusLabel.setText("Поиск: элемент не найден");
            displayArea.append(String.format("✗ Элемент с ключом %s не найден\n", searchKeyField.getText()));
            displayArea.append(hashTable.getLastOperationStats() + "\n\n");
        } catch (NumberFormatException e) {
            showError("Некорректный формат ключа");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    private void deleteElement() {
        try {
            String keyStr = deleteKeyField.getText().trim();

            if (keyStr.isEmpty()) {
                showError("Введите ключ для удаления");
                return;
            }

            double key = Double.parseDouble(keyStr);
            boolean result = hashTable.delete(key);

            if (result) {
                statusLabel.setText("Удаление успешно");
                displayArea.append(String.format("🗑 Удалён: %.4f\n", key));
                displayArea.append(hashTable.getLastOperationStats() + "\n\n");
                deleteKeyField.setText("");
            } else {
                statusLabel.setText("Удаление: элемент не найден");
                displayArea.append(String.format("✗ Элемент с ключом %s не найден\n", deleteKeyField.getText()));
                displayArea.append(hashTable.getLastOperationStats() + "\n\n");
            }

            updateDisplay();

        } catch (NumberFormatException e) {
            showError("Некорректный формат ключа");
        }
    }

    private void clearTable() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите очистить всю таблицу?",
                "Подтверждение очистки",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            hashTable.clear();
            statusLabel.setText("Таблица очищена");
            displayArea.append("=== Таблица очищена ===\n\n");
            updateDisplay();
        }
    }

    private void addRandomData() {
        java.util.Random rand = new java.util.Random();
        int added = 0;

        double minKey = KeyTransformer.getMinKey();
        double maxKey = KeyTransformer.getMaxKey();

        for (int i = 0; i < 10; i++) {
            double key = minKey + rand.nextDouble() * (maxKey - minKey);
            // Округление до 4 знаков
            key = Math.round(key * 10000.0) / 10000.0;
            String value = "Data_" + (int)(rand.nextDouble() * 10000);

            if (hashTable.insert(key, value)) {
                added++;
                displayArea.append(String.format("Добавлено случайное: %.4f → %s\n", key, value));
            }
        }

        statusLabel.setText("Добавлено " + added + " случайных элементов");
        displayArea.append("\n");
        updateDisplay();
    }

    private void updateDisplay() {
        if (hashTable != null) {
            sizeLabel.setText(String.valueOf(hashTable.size()));
            loadFactorLabel.setText(String.format("%.4f", hashTable.getLoadFactor()));

            // Обновление статистической информации
            if (statsTableModel != null) {
                updateStatsTable();
            }
        }
    }

    private void updateStatsTable() {
        if (statsTableModel == null || hashTable == null) return;

        statsTableModel.setRowCount(0);

        statsTableModel.addRow(new Object[]{"Размер таблицы (ёмкость)", hashTable.capacity()});
        statsTableModel.addRow(new Object[]{"Количество элементов", hashTable.size()});
        statsTableModel.addRow(new Object[]{"Коэффициент заполнения α", String.format("%.6f", hashTable.getLoadFactor())});
        statsTableModel.addRow(new Object[]{"Диапазон ключей",
                String.format("[%.4f, %.4f]", KeyTransformer.getMinKey(), KeyTransformer.getMaxKey())});
        statsTableModel.addRow(new Object[]{"Метод хеширования", "Модульный (k mod m)"});
        statsTableModel.addRow(new Object[]{"Метод разрешения коллизий", "Квадратичное зондирование (c₁=1, c₂=1)"});
        statsTableModel.addRow(new Object[]{"Преобразование ключей", "Свёртка + метод сложения цифр"});

        // Список ключей
        List<Double> keys = hashTable.getAllKeys();
        if (!keys.isEmpty()) {
            StringBuilder keysStr = new StringBuilder();
            for (int i = 0; i < Math.min(keys.size(), 10); i++) {
                if (i > 0) keysStr.append(", ");
                keysStr.append(String.format("%.4f", keys.get(i)));
            }
            if (keys.size() > 10) keysStr.append(", ...");
            statsTableModel.addRow(new Object[]{"Ключи в таблице", keysStr.toString()});
        }

        // Хеш-статистика
        statsTableModel.addRow(new Object[]{"Максимальное зондирование", getMaxProbeCount()});
        statsTableModel.addRow(new Object[]{"Среднее зондирование", String.format("%.2f", getAverageProbeCount())});
    }

    private int getMaxProbeCount() {
        // Простой подсчёт максимального количества зондирований
        // В реальном приложении можно хранить статистику
        return 0;
    }

    private double getAverageProbeCount() {
        return 0;
    }

    private void showError(String message) {
        statusLabel.setText("Ошибка: " + message);
        displayArea.append("✗ " + message + "\n\n");
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
