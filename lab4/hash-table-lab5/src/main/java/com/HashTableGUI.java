package com;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * GUI приложение для работы с хеш-таблицей
 * ВАРИАНТ №6: ключ - строка (заглавные латинские буквы)
 */
public class HashTableGUI extends JFrame {

    private HashTable<String, String> hashTable;

    // Компоненты
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

    // Компоненты для тестирования производительности
    private PerformanceChartPanel chartPanel;
    private JComboBox<String> chartTypeCombo;
    private JTextField loadFactorsField;
    private JTextField testOperationsField;
    private JTextArea performanceOutput;
    private PerformanceTest.TestResult lastTestResult;

    // Компонент для итератора
    private IteratorDemoPanel iteratorPanel;

    public HashTableGUI() {
        initializeHashTable();
        initComponents();
        updateDisplay();
    }

    private void initComponents() {
        setTitle("Хеш-таблица с открытой адресацией - ВАРИАНТ №6");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 900);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Операции", createOperationsPanel());
        tabbedPane.addTab("Структура таблицы", createDisplayPanel());
        tabbedPane.addTab("Статистика", createStatsPanel());
        tabbedPane.addTab("Графики трудоёмкости", createPerformancePanel());
        tabbedPane.addTab("Итератор", createIteratorPanel());

        add(tabbedPane);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Готов к работе. Вариант №6: строковые ключи (A-Z)");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

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
                new String[]{"Ключ (A-Z, заглавные):", "Значение:"},
                new JComponent[]{keyField = new JTextField(15), valueField = new JTextField(15)},
                e -> insertElement());

        // Панель для отображения преобразования ключа
        JPanel transformPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transformPanel.setBorder(BorderFactory.createTitledBorder("Преобразование ключа"));
        JTextArea transformArea = new JTextArea(3, 50);
        transformArea.setEditable(false);
        transformArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JButton showTransformButton = new JButton("Показать преобразование");
        showTransformButton.addActionListener(e -> {
            String key = keyField.getText().trim().toUpperCase();
            if (!key.isEmpty()) {
                transformArea.setText(KeyTransformer.demonstrateTransformation(key));
            }
        });
        transformPanel.add(showTransformButton);
        transformPanel.add(new JScrollPane(transformArea));

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
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Информация"));

        sizeLabel = new JLabel("0");
        loadFactorLabel = new JLabel("0.0000");
        statsLabel = new JLabel("Нет операций");

        infoPanel.add(new JLabel("Количество элементов:"));
        infoPanel.add(sizeLabel);
        infoPanel.add(new JLabel("Коэффициент заполнения α:"));
        infoPanel.add(loadFactorLabel);
        infoPanel.add(new JLabel("Метод хеширования:"));
        infoPanel.add(new JLabel("Мультипликативный (A≈0.618)"));
        infoPanel.add(new JLabel("Статистика последней операции:"));
        infoPanel.add(statsLabel);

        // Сборка
        JPanel operationsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        operationsPanel.add(insertPanel, gbc);
        gbc.gridy = 1;
        operationsPanel.add(transformPanel, gbc);
        gbc.gridy = 2;
        operationsPanel.add(searchPanel, gbc);
        gbc.gridy = 3;
        operationsPanel.add(deletePanel, gbc);
        gbc.gridy = 4;
        operationsPanel.add(controlPanel, gbc);
        gbc.gridy = 5;
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
        structureArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

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

    private JPanel createPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Настройки тестирования"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Коэффициенты α (через запятую):"), gbc);
        gbc.gridx = 1;
        loadFactorsField = new JTextField("0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9", 30);
        controlPanel.add(loadFactorsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Операций на тест:"), gbc);
        gbc.gridx = 1;
        testOperationsField = new JTextField("1000", 10);
        controlPanel.add(testOperationsField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton runTestButton = new JButton("Запустить тестирование");
        runTestButton.addActionListener(e -> runPerformanceTest());
        JButton exportButton = new JButton("Экспорт результатов");
        exportButton.addActionListener(e -> exportResults());

        buttonPanel.add(runTestButton);
        buttonPanel.add(exportButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(buttonPanel, gbc);

        JPanel chartControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        chartControlPanel.setBorder(BorderFactory.createTitledBorder("Тип графика"));
        chartTypeCombo = new JComboBox<>(new String[]{
                "Сравнение операций (эксперимент)",
                "Поиск: эксперимент vs теория",
                "Неуспешный поиск: эксперимент vs теория",
                "Все операции с теоретическими кривыми"
        });
        chartTypeCombo.addActionListener(e -> {
            if (chartPanel != null && lastTestResult != null) {
                PerformanceChartPanel.ChartType type =
                        PerformanceChartPanel.ChartType.values()[chartTypeCombo.getSelectedIndex()];
                chartPanel.setChartType(type);
            }
        });
        chartControlPanel.add(chartTypeCombo);

        chartPanel = new PerformanceChartPanel();
        chartPanel.setPreferredSize(new Dimension(800, 450));

        performanceOutput = new JTextArea(8, 50);
        performanceOutput.setEditable(false);
        performanceOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane outputScroll = new JScrollPane(performanceOutput);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Результаты тестирования"));
        outputScroll.setPreferredSize(new Dimension(800, 150));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(chartControlPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(outputScroll, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createIteratorPanel() {
        iteratorPanel = new IteratorDemoPanel();
        iteratorPanel.setHashTable(hashTable);

        JButton syncButton = new JButton("Синхронизировать с текущей таблицей");
        syncButton.addActionListener(e -> {
            if (iteratorPanel != null && hashTable != null) {
                iteratorPanel.setHashTable(hashTable);
                statusLabel.setText("Итератор синхронизирован с таблицей");
            }
        });

        JPanel syncPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        syncPanel.add(syncButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(iteratorPanel, BorderLayout.CENTER);
        mainPanel.add(syncPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void runPerformanceTest() {
        try {
            String[] alphaStrs = loadFactorsField.getText().trim().split(",");
            double[] loadFactors = new double[alphaStrs.length];
            for (int i = 0; i < alphaStrs.length; i++) {
                loadFactors[i] = Double.parseDouble(alphaStrs[i].trim());
                if (loadFactors[i] <= 0 || loadFactors[i] >= 1) {
                    throw new IllegalArgumentException("α должен быть в интервале (0, 1)");
                }
            }

            int operations = Integer.parseInt(testOperationsField.getText().trim());

            statusLabel.setText("Выполняется тестирование производительности...");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            SwingWorker<PerformanceTest.TestResult, Void> worker = new SwingWorker<>() {
                @Override
                protected PerformanceTest.TestResult doInBackground() {
                    PerformanceTest test = new PerformanceTest(200, 0.5);
                    return test.runFullTest(loadFactors, operations);
                }

                @Override
                protected void done() {
                    try {
                        lastTestResult = get();
                        chartPanel.setTestResult(lastTestResult);
                        displayPerformanceResults(lastTestResult);
                        statusLabel.setText("Тестирование завершено");
                    } catch (Exception e) {
                        statusLabel.setText("Ошибка тестирования: " + e.getMessage());
                        JOptionPane.showMessageDialog(HashTableGUI.this,
                                "Ошибка тестирования: " + e.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Некорректный формат чисел",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPerformanceResults(PerformanceTest.TestResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ ТРУДОЁМКОСТИ ==========\n");
        sb.append("Тип ключа: String (заглавные латинские буквы A-Z)\n");
        sb.append("Преобразование: конкатенация 5-битных образов символов\n");
        sb.append("Метод хеширования: мультипликативный\n");
        sb.append("Разрешение коллизий: квадратичное зондирование (c₁=1, c₂=1)\n");
        sb.append("Теоретические оценки:\n");
        sb.append("  - Успешный поиск: ~ -ln(1-α)/α\n");
        sb.append("  - Неуспешный поиск: ~ 1/(1-α)\n");
        sb.append("----------------------------------------------------------------\n");
        sb.append(String.format("%-10s %-12s %-12s %-12s %-12s %-12s %-15s\n",
                "α", "Вставка", "Поиск", "Удаление", "Поиск(неуд)", "Теор.усп", "Теор.неуд"));
        sb.append("----------------------------------------------------------------\n");

        for (PerformanceTest.ResultEntry entry : result.getEntries()) {
            sb.append(String.format("%-10.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-15.2f\n",
                    entry.loadFactor,
                    entry.insertProbes,
                    entry.searchProbes,
                    entry.deleteProbes,
                    entry.unsuccessfulSearchProbes,
                    entry.theoreticalSearchSuccess,
                    entry.theoreticalSearchUnsuccess));
        }

        sb.append("----------------------------------------------------------------\n");
        sb.append("Вывод: При увеличении коэффициента заполнения α трудоёмкость\n");
        sb.append("возрастает. Рекомендуется поддерживать α ≤ 0.7 для обеспечения\n");
        sb.append("эффективной работы хеш-таблицы.\n");
        sb.append("================================================================");

        performanceOutput.setText(sb.toString());
    }

    private void exportResults() {
        if (lastTestResult == null) {
            JOptionPane.showMessageDialog(this,
                    "Нет результатов тестирования. Сначала выполните тест.",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("hash_table_performance_variant6.csv"));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("alpha,insert_probes,search_probes,delete_probes,unsuccessful_probes,theoretical_success,theoretical_unsuccess");
                for (PerformanceTest.ResultEntry entry : lastTestResult.getEntries()) {
                    writer.printf("%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f\n",
                            entry.loadFactor,
                            entry.insertProbes,
                            entry.searchProbes,
                            entry.deleteProbes,
                            entry.unsuccessfulSearchProbes,
                            entry.theoreticalSearchSuccess,
                            entry.theoreticalSearchUnsuccess);
                }
                statusLabel.setText("Результаты экспортированы в " + fileChooser.getSelectedFile().getName());
                JOptionPane.showMessageDialog(this,
                        "Результаты успешно экспортированы!",
                        "Экспорт", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка сохранения файла: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initializeHashTable() {
        hashTable = new HashTable<>(50);
    }

    private void insertElement() {
        try {
            String key = keyField.getText().trim().toUpperCase();
            String value = valueField.getText().trim();

            if (key.isEmpty()) {
                showError("Введите ключ (заглавные латинские буквы A-Z)");
                return;
            }
            if (value.isEmpty()) {
                showError("Введите значение");
                return;
            }

            if (!KeyTransformer.isValidKey(key)) {
                showError("Ключ может содержать только заглавные латинские буквы A-Z");
                return;
            }

            if (key.length() > KeyTransformer.getMaxKeyLength()) {
                showError(String.format("Ключ слишком длинный (макс. %d символов)",
                        KeyTransformer.getMaxKeyLength()));
                return;
            }

            boolean result = hashTable.insert(key, value);

            if (result) {
                statusLabel.setText("Вставка успешна");
                displayArea.append(String.format("✓ Вставлен: %s → %s\n", key, value));
                displayArea.append(hashTable.getLastOperationStats() + "\n");
                displayArea.append("Битовое представление: " + hashTable.showKeyBinary(key) + "\n\n");
                keyField.setText("");
                valueField.setText("");
                if (iteratorPanel != null) {
                    iteratorPanel.setHashTable(hashTable);
                }
            } else {
                showError("Элемент с таким ключом уже существует");
                displayArea.append(String.format("✗ Ошибка вставки: ключ %s уже существует\n", key));
                displayArea.append(hashTable.getLastOperationStats() + "\n\n");
            }

            updateDisplay();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    private void searchElement() {
        try {
            String key = searchKeyField.getText().trim().toUpperCase();

            if (key.isEmpty()) {
                showError("Введите ключ для поиска");
                return;
            }

            String value = hashTable.search(key);

            statusLabel.setText("Поиск успешен");
            displayArea.append(String.format("🔍 Найдено: %s → %s\n", key, value));
            displayArea.append(hashTable.getLastOperationStats() + "\n\n");
            searchKeyField.setText("");

        } catch (NoSuchElementException e) {
            statusLabel.setText("Поиск: элемент не найден");
            displayArea.append(String.format("✗ Элемент с ключом '%s' не найден\n", searchKeyField.getText()));
            displayArea.append(hashTable.getLastOperationStats() + "\n\n");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void deleteElement() {
        try {
            String key = deleteKeyField.getText().trim().toUpperCase();

            if (key.isEmpty()) {
                showError("Введите ключ для удаления");
                return;
            }

            boolean result = hashTable.delete(key);

            if (result) {
                statusLabel.setText("Удаление успешно");
                displayArea.append(String.format("🗑 Удалён: %s\n", key));
                displayArea.append(hashTable.getLastOperationStats() + "\n\n");
                deleteKeyField.setText("");
                if (iteratorPanel != null) {
                    iteratorPanel.setHashTable(hashTable);
                }
            } else {
                statusLabel.setText("Удаление: элемент не найден");
                displayArea.append(String.format("✗ Элемент с ключом '%s' не найден\n", deleteKeyField.getText()));
                displayArea.append(hashTable.getLastOperationStats() + "\n\n");
            }

            updateDisplay();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
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
            if (iteratorPanel != null) {
                iteratorPanel.setHashTable(hashTable);
            }
            updateDisplay();
        }
    }

    private void addRandomData() {
        java.util.Random rand = new java.util.Random();
        int added = 0;

        String[] sampleWords = {
                "APPLE", "BANANA", "CHERRY", "DATE", "ELDERBERRY",
                "FIG", "GRAPE", "HONEYDEW", "KIWI", "LEMON",
                "MANGO", "NECTARINE", "ORANGE", "PAPAYA", "QUINCE",
                "RASPBERRY", "STRAWBERRY", "TANGERINE", "UGLI", "VANILLA",
                "WATERMELON", "XANTHIUM", "YAM", "ZUCCHINI", "ALPHA"
        };

        String[] sampleValues = {
                "Data1", "Data2", "Data3", "Data4", "Data5",
                "Data6", "Data7", "Data8", "Data9", "Data10"
        };

        for (int i = 0; i < 10; i++) {
            String key = sampleWords[rand.nextInt(sampleWords.length)];
            String value = sampleValues[rand.nextInt(sampleValues.length)] + "_" + (rand.nextInt(1000));

            if (hashTable.insert(key, value)) {
                added++;
                displayArea.append(String.format("Добавлено случайное: %s → %s\n", key, value));
                displayArea.append(hashTable.getLastOperationStats() + "\n");
            }
        }

        statusLabel.setText("Добавлено " + added + " случайных элементов");
        displayArea.append("\n");
        if (iteratorPanel != null) {
            iteratorPanel.setHashTable(hashTable);
        }
        updateDisplay();
    }

    private void updateDisplay() {
        if (hashTable != null) {
            sizeLabel.setText(String.valueOf(hashTable.size()));
            loadFactorLabel.setText(String.format("%.4f", hashTable.getLoadFactor()));
            updateStatsTable();
        }
    }

    private void updateStatsTable() {
        if (statsTableModel == null || hashTable == null) return;

        statsTableModel.setRowCount(0);

        statsTableModel.addRow(new Object[]{"Размер таблицы (ёмкость)", hashTable.capacity()});
        statsTableModel.addRow(new Object[]{"Количество элементов", hashTable.size()});
        statsTableModel.addRow(new Object[]{"Коэффициент заполнения α", String.format("%.6f", hashTable.getLoadFactor())});
        statsTableModel.addRow(new Object[]{"Тип ключа", "String (A-Z, заглавные)"});
        statsTableModel.addRow(new Object[]{"Преобразование ключей", "Конкатенация 5-битных образов"});
        statsTableModel.addRow(new Object[]{"Метод хеширования", "Мультипликативный (A≈0.618)"});
        statsTableModel.addRow(new Object[]{"Разрешение коллизий", "Квадратичное зондирование (c₁=1, c₂=1)"});
        statsTableModel.addRow(new Object[]{"Макс. длина ключа", KeyTransformer.getMaxKeyLength()});

        List<String> keys = hashTable.getAllKeys();
        if (!keys.isEmpty()) {
            StringBuilder keysStr = new StringBuilder();
            for (int i = 0; i < Math.min(keys.size(), 15); i++) {
                if (i > 0) keysStr.append(", ");
                keysStr.append(keys.get(i));
            }
            if (keys.size() > 15) keysStr.append(", ...");
            statsTableModel.addRow(new Object[]{"Ключи в таблице", keysStr.toString()});
        }
    }

    private void showError(String message) {
        statusLabel.setText("Ошибка: " + message);
        displayArea.append("✗ " + message + "\n\n");
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}