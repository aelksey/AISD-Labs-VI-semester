package com;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Панель для демонстрации работы итератора хеш-таблицы
 * ВАРИАНТ №6: ключ - строка (заглавные латинские буквы A-Z)
 */
public class IteratorDemoPanel extends JPanel {

    private HashTable<String, String> hashTable;
    private JTable iteratorTable;
    private DefaultTableModel tableModel;
    private JTextArea iteratorOutput;
    private JButton refreshButton;
    private JButton forwardButton;
    private JButton resetButton;
    private JButton traverseButton;
    private JLabel currentElementLabel;
    private JLabel iteratorStatusLabel;

    private java.util.Iterator<HashTable.Entry<String, String>> currentIterator;
    private HashTable.Entry<String, String> currentElement;

    // Цвета для UI
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private static final Color COLOR_WARNING = new Color(241, 196, 15);
    private static final Color COLOR_ERROR = new Color(231, 76, 60);
    private static final Color COLOR_INFO = new Color(52, 152, 219);

    public IteratorDemoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Инициализация пустой таблицей (будет установлена позже)
        this.hashTable = null;

        initComponents();
    }

    private void initComponents() {
        // Верхняя панель - элементы управления итератором
        JPanel controlPanel = new JPanel(new BorderLayout(10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_INFO, 2),
                "Управление итератором",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                COLOR_INFO
        ));

        // Кнопки управления итератором
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        refreshButton = new JButton("Обновить итератор (begin())");
        refreshButton.setToolTipText("Устанавливает итератор на первый элемент коллекции");
        forwardButton = new JButton("Переход к следующему (++)");
        forwardButton.setToolTipText("Перемещает итератор к следующему элементу");
        resetButton = new JButton("Сбросить итератор");
        resetButton.setToolTipText("Сбрасывает итератор в начальное состояние");

        // Иконки для кнопок (текстовые)
        refreshButton.setBackground(COLOR_INFO);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        forwardButton.setBackground(COLOR_SUCCESS);
        forwardButton.setForeground(Color.WHITE);
        forwardButton.setFocusPainted(false);

        resetButton.setBackground(COLOR_WARNING);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);

        refreshButton.addActionListener(e -> resetIterator());
        forwardButton.addActionListener(e -> nextElement());
        resetButton.addActionListener(e -> resetIterator());

        buttonPanel.add(refreshButton);
        buttonPanel.add(forwardButton);
        buttonPanel.add(resetButton);

        // Панель статуса итератора
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        currentElementLabel = new JLabel("Текущий элемент: (нет)");
        currentElementLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        iteratorStatusLabel = new JLabel("Состояние итератора: не установлен");
        iteratorStatusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Дополнительная информация о текущем элементе
        JLabel hintLabel = new JLabel("Подсказка: используйте 'Обновить итератор' для начала обхода");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        hintLabel.setForeground(Color.GRAY);

        statusPanel.add(currentElementLabel);
        statusPanel.add(iteratorStatusLabel);
        statusPanel.add(hintLabel);

        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        controlPanel.add(statusPanel, BorderLayout.CENTER);

        // Центральная панель - таблица с данными
        JPanel dataPanel = new JPanel(new BorderLayout(5, 5));
        dataPanel.setBorder(BorderFactory.createTitledBorder(
                "Данные в хеш-таблице (с отображением хеш-индексов)"
        ));

        String[] columns = {"№", "Ключ (k)", "Значение (data)", "k' (преобр.)", "Хеш-индекс"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        iteratorTable = new JTable(tableModel);
        iteratorTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        iteratorTable.setRowHeight(25);
        iteratorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Установка ширины колонок
        iteratorTable.getColumnModel().getColumn(0).setMaxWidth(50);
        iteratorTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        iteratorTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        iteratorTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        iteratorTable.getColumnModel().getColumn(4).setMaxWidth(100);

        JScrollPane tableScroll = new JScrollPane(iteratorTable);
        tableScroll.setPreferredSize(new Dimension(700, 200));
        dataPanel.add(tableScroll, BorderLayout.CENTER);

        // Панель управления данными (для демонстрации работы с коллекцией)
        JPanel dataControlPanel = new JPanel(new GridBagLayout());
        dataControlPanel.setBorder(BorderFactory.createTitledBorder(
                "Управление данными (добавление/удаление элементов)"
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel keyLabel = new JLabel("Ключ (A-Z):");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 11));
        JTextField keyField = new JTextField(15);
        keyField.setToolTipText("Введите строку из заглавных латинских букв (A-Z)");

        JLabel valueLabel = new JLabel("Значение:");
        valueLabel.setFont(new Font("Arial", Font.BOLD, 11));
        JTextField valueField = new JTextField(15);

        JButton addButton = new JButton("➕ Добавить");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        JButton deleteButton = new JButton("🗑 Удалить по ключу");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        JButton clearButton = new JButton("❌ Очистить таблицу");
        clearButton.setBackground(COLOR_WARNING);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);

        JButton loadSampleButton = new JButton("📋 Загрузить образец (15 элементов)");
        loadSampleButton.setBackground(COLOR_INFO);
        loadSampleButton.setForeground(Color.WHITE);
        loadSampleButton.setFocusPainted(false);

        // Информация о допустимых ключах
        JLabel infoLabel = new JLabel(
                "Допустимые символы: A-Z (заглавные латинские буквы). Макс. длина: " +
                        KeyTransformer.getMaxKeyLength() + " символов"
        );
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);

        addButton.addActionListener(e -> {
            if (hashTable == null) {
                showMessageDialog("Хеш-таблица не инициализирована", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String key = keyField.getText().trim().toUpperCase();
                String value = valueField.getText().trim();

                if (key.isEmpty()) {
                    showMessageDialog("Введите ключ (заглавные латинские буквы A-Z)", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (value.isEmpty()) {
                    showMessageDialog("Введите значение", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!KeyTransformer.isValidKey(key)) {
                    showMessageDialog(
                            "Недопустимый ключ: " + key + "\nКлюч может содержать только заглавные латинские буквы A-Z",
                            "Ошибка", JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                if (key.length() > KeyTransformer.getMaxKeyLength()) {
                    showMessageDialog(
                            "Ключ слишком длинный (макс. " + KeyTransformer.getMaxKeyLength() + " символов)",
                            "Ошибка", JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                boolean result = hashTable.insert(key, value);
                if (result) {
                    updateTableDisplay();
                    resetIterator();
                    appendToOutput("✓ Добавлен элемент: " + key + " → " + value + "\n");
                    showMessageDialog("Элемент добавлен: " + key + " → " + value, "Успех", JOptionPane.INFORMATION_MESSAGE);
                    keyField.setText("");
                    valueField.setText("");
                } else {
                    showMessageDialog("Элемент с таким ключом уже существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                showMessageDialog(ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                showMessageDialog("Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            if (hashTable == null) {
                showMessageDialog("Хеш-таблица не инициализирована", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String input = JOptionPane.showInputDialog(this,
                        "Введите ключ для удаления (A-Z):",
                        "Удаление элемента",
                        JOptionPane.QUESTION_MESSAGE);
                if (input != null) {
                    String key = input.trim().toUpperCase();
                    if (hashTable.delete(key)) {
                        updateTableDisplay();
                        resetIterator();
                        appendToOutput("🗑 Удалён элемент: " + key + "\n");
                        showMessageDialog("Элемент удалён: " + key, "Успех", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showMessageDialog("Элемент с ключом '" + key + "' не найден", "Ошибка", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                showMessageDialog("Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            if (hashTable == null) return;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите очистить всю таблицу?",
                    "Подтверждение очистки",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                hashTable.clear();
                updateTableDisplay();
                resetIterator();
                appendToOutput("=== Таблица очищена ===\n");
                showMessageDialog("Таблица очищена", "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadSampleButton.addActionListener(e -> loadSampleData());

        // Расположение элементов
        gbc.gridx = 0; gbc.gridy = 0;
        dataControlPanel.add(keyLabel, gbc);
        gbc.gridx = 1;
        dataControlPanel.add(keyField, gbc);
        gbc.gridx = 2;
        dataControlPanel.add(valueLabel, gbc);
        gbc.gridx = 3;
        dataControlPanel.add(valueField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        dataControlPanel.add(addButton, gbc);
        gbc.gridx = 1;
        dataControlPanel.add(deleteButton, gbc);
        gbc.gridx = 2;
        dataControlPanel.add(clearButton, gbc);
        gbc.gridx = 3;
        dataControlPanel.add(loadSampleButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        dataControlPanel.add(infoLabel, gbc);

        dataPanel.add(dataControlPanel, BorderLayout.SOUTH);

        // Нижняя панель - вывод результатов обхода
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                "Результат обхода коллекции (foreach)"
        ));

        iteratorOutput = new JTextArea(10, 60);
        iteratorOutput.setEditable(false);
        iteratorOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        iteratorOutput.setBackground(new Color(250, 250, 250));
        JScrollPane outputScroll = new JScrollPane(iteratorOutput);
        outputScroll.setPreferredSize(new Dimension(700, 180));

        traverseButton = new JButton("▶ Выполнить полный обход (foreach)");
        traverseButton.setBackground(new Color(155, 89, 182));
        traverseButton.setForeground(Color.WHITE);
        traverseButton.setFocusPainted(false);
        traverseButton.setFont(new Font("Arial", Font.BOLD, 12));
        traverseButton.addActionListener(e -> performFullTraversal());

        JPanel traversePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        traversePanel.add(traverseButton);

        outputPanel.add(outputScroll, BorderLayout.CENTER);
        outputPanel.add(traversePanel, BorderLayout.SOUTH);

        // Сборка главной панели
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(dataPanel);
        splitPane.setBottomComponent(outputPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(300);

        add(controlPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Инициализация итератора
     */
    private void setupIterator() {
        currentIterator = null;
        currentElement = null;
        updateIteratorStatus();
    }

    /**
     * Сброс итератора (begin())
     */
    private void resetIterator() {
        if (hashTable == null) {
            currentIterator = null;
            currentElement = null;
            updateIteratorStatus();
            appendToOutput("⚠ Итератор не может быть инициализирован: хеш-таблица не установлена\n");
            return;
        }

        if (hashTable.size() == 0) {
            currentIterator = null;
            currentElement = null;
            updateIteratorStatus();
            appendToOutput("⚠ Итератор не может быть инициализирован: коллекция пуста\n");
            return;
        }

        currentIterator = hashTable.iterator();
        if (currentIterator.hasNext()) {
            currentElement = currentIterator.next();
            updateIteratorStatus();
            appendToOutput("✓ Итератор установлен на первый элемент (begin())\n");
        } else {
            currentElement = null;
            updateIteratorStatus();
            appendToOutput("⚠ Итератор достиг конца коллекции (end())\n");
        }
    }

    /**
     * Переход к следующему элементу (++iter)
     */
    private void nextElement() {
        if (hashTable == null) {
            showMessageDialog("Хеш-таблица не инициализирована", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentIterator == null) {
            showMessageDialog(
                    "Итератор не инициализирован.\nНажмите 'Обновить итератор (begin())' для начала обхода.",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentIterator.hasNext()) {
            currentElement = currentIterator.next();
            updateIteratorStatus();
            appendToOutput("→ Переход к следующему элементу: " +
                    formatElementString(currentElement) + "\n");
        } else {
            showMessageDialog(
                    "Достигнут конец коллекции (итератор равен end())",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
            currentElement = null;
            updateIteratorStatus();
            appendToOutput("⚠ Достигнут конец коллекции (end())\n");
        }
    }

    /**
     * Обновление статуса итератора в UI
     */
    private void updateIteratorStatus() {
        if (currentElement != null) {
            currentElementLabel.setText(String.format(
                    "Текущий элемент: key = \"%s\", value = \"%s\" | k' = %d",
                    currentElement.getKey(),
                    currentElement.getValue(),
                    KeyTransformer.transform(currentElement.getKey())
            ));
            currentElementLabel.setForeground(COLOR_SUCCESS);
            iteratorStatusLabel.setText("Состояние итератора: установлен");
            iteratorStatusLabel.setForeground(COLOR_SUCCESS);
        } else if (hashTable != null && hashTable.size() > 0) {
            currentElementLabel.setText("Текущий элемент: (конец коллекции - end())");
            currentElementLabel.setForeground(COLOR_WARNING);
            iteratorStatusLabel.setText("Состояние итератора: не установлен (end)");
            iteratorStatusLabel.setForeground(COLOR_WARNING);
        } else {
            currentElementLabel.setText("Текущий элемент: (нет)");
            currentElementLabel.setForeground(COLOR_ERROR);
            iteratorStatusLabel.setText("Состояние итератора: не установлен (коллекция пуста)");
            iteratorStatusLabel.setForeground(COLOR_ERROR);
        }
    }

    /**
     * Обновление отображения таблицы с данными
     */
    private void updateTableDisplay() {
        tableModel.setRowCount(0);

        if (hashTable == null || hashTable.size() == 0) {
            return;
        }

        List<HashTable.Entry<String, String>> entries = hashTable.getAllEntries();
        int index = 1;

        for (HashTable.Entry<String, String> entry : entries) {
            // Вычисляем хеш-индекс для отображения
            long transformedKey = KeyTransformer.transform(entry.getKey());
            int hashIndex = (int)(transformedKey % hashTable.capacity());

            tableModel.addRow(new Object[]{
                    index++,
                    entry.getKey(),
                    entry.getValue(),
                    transformedKey,
                    hashIndex
            });
        }
    }

    /**
     * Полный обход коллекции с помощью foreach
     */
    private void performFullTraversal() {
        if (hashTable == null) {
            iteratorOutput.setText("Хеш-таблица не инициализирована.");
            return;
        }

        if (hashTable.size() == 0) {
            iteratorOutput.setText("Коллекция пуста. Нет элементов для обхода.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(70)).append("\n");
        sb.append("ПОЛНЫЙ ОБХОД КОЛЛЕКЦИИ (с использованием foreach)\n");
        sb.append("=".repeat(70)).append("\n");
        sb.append(String.format("%-4s | %-15s | %-15s | %-12s | %-10s\n",
                "№", "Ключ (k)", "Значение", "k' (преобр.)", "Хеш-индекс"));
        sb.append("-".repeat(70)).append("\n");

        int count = 0;
        for (HashTable.Entry<String, String> entry : hashTable) {
            count++;
            long transformedKey = KeyTransformer.transform(entry.getKey());
            int hashIndex = (int)(transformedKey % hashTable.capacity());
            sb.append(String.format("%-4d | \"%-13s\" | %-15s | %-12d | %-10d\n",
                    count,
                    truncateString(entry.getKey(), 13),
                    truncateString(entry.getValue(), 15),
                    transformedKey,
                    hashIndex));
        }

        sb.append("-".repeat(70)).append("\n");
        sb.append(String.format("Всего элементов: %d\n", count));
        sb.append("=".repeat(70)).append("\n");
        sb.append("\nПримечание: Порядок обхода соответствует порядку\n");
        sb.append("элементов в массиве хеш-таблицы (с пропуском FREE и DELETED).\n");

        iteratorOutput.setText(sb.toString());

        // Сброс итератора после обхода
        resetIterator();
    }

    /**
     * Загрузка образцов данных для демонстрации
     */
    private void loadSampleData() {
        if (hashTable == null) {
            showMessageDialog("Хеш-таблица не инициализирована", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        hashTable.clear();

        // Образцы ключей и значений
        String[] sampleKeys = {
                "APPLE", "BANANA", "CHERRY", "DATE", "ELDERBERRY",
                "FIG", "GRAPE", "HONEYDEW", "KIWI", "LEMON",
                "MANGO", "NECTARINE", "ORANGE", "PAPAYA", "QUINCE",
                "RASPBERRY", "STRAWBERRY", "TANGERINE", "UGLI", "VANILLA",
                "WATERMELON", "XENIAL", "YELLOW", "ZEBRA", "ALPHA"
        };

        String[] sampleValues = {
                "Яблоко", "Банан", "Вишня", "Финик", "Бузина",
                "Инжир", "Виноград", "Дыня", "Киви", "Лимон",
                "Манго", "Нектарин", "Апельсин", "Папайя", "Айва",
                "Малина", "Клубника", "Мандарин", "Угли", "Ваниль",
                "Арбуз", "Ксениал", "Жёлтый", "Зебра", "Альфа"
        };

        int count = Math.min(sampleKeys.length, sampleValues.length);
        for (int i = 0; i < count; i++) {
            hashTable.insert(sampleKeys[i], sampleValues[i]);
        }

        updateTableDisplay();
        resetIterator();

        appendToOutput("📋 Загружено " + hashTable.size() + " образцов элементов.\n");
        appendToOutput("Используйте итератор для последовательного доступа.\n");

        showMessageDialog(
                "Загружено " + hashTable.size() + " образцов элементов",
                "Образец загружен", JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Форматирование элемента для вывода
     */
    private String formatElementString(HashTable.Entry<String, String> entry) {
        if (entry == null) return "(null)";
        return String.format("\"%s\" → \"%s\"", entry.getKey(), entry.getValue());
    }

    /**
     * Усечение строки до заданной длины
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "(null)";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Добавление текста в область вывода с прокруткой вниз
     */
    private void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            iteratorOutput.append(text);
            // Прокрутка вниз
            iteratorOutput.setCaretPosition(iteratorOutput.getDocument().getLength());
        });
    }

    /**
     * Показать диалоговое сообщение
     */
    private void showMessageDialog(String message, String title, int messageType) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, title, messageType);
        });
    }

    /**
     * Обновить ссылку на хеш-таблицу (используется из основного GUI)
     */
    public void setHashTable(HashTable<String, String> table) {
        this.hashTable = table;
        updateTableDisplay();
        setupIterator();
        appendToOutput("✓ Итератор синхронизирован с таблицей (размер: " +
                (table != null ? table.size() : 0) + " элементов)\n");
    }

    /**
     * Получить текущую хеш-таблицу (для синхронизации)
     */
    public HashTable<String, String> getHashTable() {
        return hashTable;
    }

    /**
     * Очистить вывод итератора
     */
    public void clearOutput() {
        iteratorOutput.setText("");
    }
}