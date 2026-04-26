package com;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Панель для демонстрации работы итератора хеш-таблицы
 */
public class IteratorDemoPanel extends JPanel {

    private HashTable<Double, String> hashTable;
    private JTable iteratorTable;
    private DefaultTableModel tableModel;
    private JTextArea iteratorOutput;
    private JButton refreshButton;
    private JButton forwardButton;
    private JButton resetButton;
    private JLabel currentElementLabel;
    private JLabel iteratorStatusLabel;

    private java.util.Iterator<HashTable.Entry<Double, String>> currentIterator;
    private HashTable.Entry<Double, String> currentElement;

    public IteratorDemoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        hashTable = new HashTable<>(50);

        initComponents();
        setupIterator();
    }

    private void initComponents() {
        // Верхняя панель - элементы управления
        JPanel controlPanel = new JPanel(new BorderLayout(10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление итератором"));

        // Кнопки управления итератором
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        refreshButton = new JButton("Обновить итератор (begin())");
        forwardButton = new JButton("Переход к следующему (++)");
        resetButton = new JButton("Сбросить итератор");

        refreshButton.addActionListener(e -> resetIterator());
        forwardButton.addActionListener(e -> nextElement());
        resetButton.addActionListener(e -> resetIterator());

        buttonPanel.add(refreshButton);
        buttonPanel.add(forwardButton);
        buttonPanel.add(resetButton);

        // Панель статуса итератора
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        currentElementLabel = new JLabel("Текущий элемент: (нет)");
        iteratorStatusLabel = new JLabel("Состояние итератора: не установлен");

        statusPanel.add(currentElementLabel);
        statusPanel.add(iteratorStatusLabel);

        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        controlPanel.add(statusPanel, BorderLayout.CENTER);

        // Центральная панель - таблица с данными
        JPanel dataPanel = new JPanel(new BorderLayout(5, 5));
        dataPanel.setBorder(BorderFactory.createTitledBorder("Данные в хеш-таблице"));

        String[] columns = {"№", "Ключ (k)", "Значение (data)", "Хеш-индекс"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        iteratorTable = new JTable(tableModel);
        iteratorTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        iteratorTable.setRowHeight(22);

        JScrollPane tableScroll = new JScrollPane(iteratorTable);
        tableScroll.setPreferredSize(new Dimension(500, 200));
        dataPanel.add(tableScroll, BorderLayout.CENTER);

        // Панель управления данными
        JPanel dataControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dataControlPanel.setBorder(BorderFactory.createTitledBorder("Управление данными"));

        JTextField keyField = new JTextField(15);
        JTextField valueField = new JTextField(15);
        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить по ключу");
        JButton clearButton = new JButton("Очистить таблицу");
        JButton loadSampleButton = new JButton("Загрузить образец (10 элементов)");

        addButton.addActionListener(e -> {
            try {
                double key = Double.parseDouble(keyField.getText().trim());
                String value = valueField.getText().trim();
                if (hashTable.insert(key, value)) {
                    updateTableDisplay();
                    resetIterator();
                    JOptionPane.showMessageDialog(this,
                            "Элемент добавлен: " + key + " → " + value,
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Элемент с таким ключом уже существует",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Некорректный формат ключа \n Формат: число.число",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
            keyField.setText("");
            valueField.setText("");
        });

        deleteButton.addActionListener(e -> {
            try {
                double key = Double.parseDouble(JOptionPane.showInputDialog(this,
                        "Введите ключ для удаления: \n Формат: число.число ", "Удаление", JOptionPane.QUESTION_MESSAGE));
                if (hashTable.delete(key)) {
                    updateTableDisplay();
                    resetIterator();
                    JOptionPane.showMessageDialog(this,
                            "Элемент удалён", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Элемент не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                // Отмена ввода
            }
        });

        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите очистить таблицу?",
                    "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                hashTable.clear();
                updateTableDisplay();
                resetIterator();
            }
        });

        loadSampleButton.addActionListener(e -> loadSampleData());

        dataControlPanel.add(new JLabel("Ключ:"));
        dataControlPanel.add(keyField);
        dataControlPanel.add(new JLabel("Значение:"));
        dataControlPanel.add(valueField);
        dataControlPanel.add(addButton);
        dataControlPanel.add(deleteButton);
        dataControlPanel.add(clearButton);
        dataControlPanel.add(loadSampleButton);

        dataPanel.add(dataControlPanel, BorderLayout.SOUTH);

        // Нижняя панель - вывод обхода
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Результат обхода коллекции"));
        iteratorOutput = new JTextArea(8, 50);
        iteratorOutput.setEditable(false);
        iteratorOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputPanel.add(new JScrollPane(iteratorOutput), BorderLayout.CENTER);

        JButton traverseButton = new JButton("Выполнить полный обход (foreach)");
        traverseButton.addActionListener(e -> performFullTraversal());
        outputPanel.add(traverseButton, BorderLayout.SOUTH);

        // Сборка
        add(controlPanel, BorderLayout.NORTH);
        add(dataPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.SOUTH);

        updateTableDisplay();
    }

    private void setupIterator() {
        currentIterator = null;
        currentElement = null;
        updateIteratorStatus();
    }

    private void resetIterator() {
        if (hashTable.size() > 0) {
            currentIterator = hashTable.iterator();
            if (currentIterator.hasNext()) {
                currentElement = currentIterator.next();
                updateIteratorStatus();
            } else {
                currentElement = null;
                updateIteratorStatus();
            }
        } else {
            currentIterator = null;
            currentElement = null;
            updateIteratorStatus();
        }
    }

    private void nextElement() {
        if (currentIterator == null) {
            JOptionPane.showMessageDialog(this,
                    "Итератор не инициализирован. Нажмите 'Обновить итератор'.",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentIterator.hasNext()) {
            currentElement = currentIterator.next();
            updateIteratorStatus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Достигнут конец коллекции (итератор равен end())",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
            currentElement = null;
            updateIteratorStatus();
        }
    }

    private void updateIteratorStatus() {
        if (currentElement != null) {
            currentElementLabel.setText(String.format(
                    "Текущий элемент: key = %.4f, value = %s",
                    currentElement.getKey(), currentElement.getValue()));
            iteratorStatusLabel.setText("Состояние итератора: установлен");
            iteratorStatusLabel.setForeground(new Color(46, 204, 113));
        } else if (hashTable.size() > 0) {
            currentElementLabel.setText("Текущий элемент: (конец коллекции - end())");
            iteratorStatusLabel.setText("Состояние итератора: не установлен (end)");
            iteratorStatusLabel.setForeground(Color.ORANGE);
        } else {
            currentElementLabel.setText("Текущий элемент: (нет)");
            iteratorStatusLabel.setText("Состояние итератора: не установлен (коллекция пуста)");
            iteratorStatusLabel.setForeground(Color.RED);
        }
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);

        List<HashTable.Entry<Double, String>> entries = hashTable.getAllEntries();
        int index = 1;

        for (HashTable.Entry<Double, String> entry : entries) {
            // Вычисляем хеш-индекс для отображения
            long transformedKey = KeyTransformer.transform(entry.getKey());
            int hashIndex = (int)(transformedKey % hashTable.capacity());

            tableModel.addRow(new Object[]{
                    index++,
                    String.format("%.4f", entry.getKey()),
                    entry.getValue(),
                    hashIndex
            });
        }
    }

    private void performFullTraversal() {
        if (hashTable.size() == 0) {
            iteratorOutput.setText("Коллекция пуста. Нет элементов для обхода.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========== ПОЛНЫЙ ОБХОД КОЛЛЕКЦИИ (foreach) ==========\n");
        sb.append("Порядок обхода соответствует порядку в массиве таблицы\n");
        sb.append("-----------------------------------------------------------\n");

        int count = 0;
        for (HashTable.Entry<Double, String> entry : hashTable) {
            count++;
            sb.append(String.format("[%3d] key = %.4f → value = %s\n",
                    count, entry.getKey(), entry.getValue()));
        }

        sb.append("-----------------------------------------------------------\n");
        sb.append(String.format("Всего элементов: %d\n", count));
        sb.append("===========================================================\n");

        iteratorOutput.setText(sb.toString());

        // Сброс итератора после обхода
        resetIterator();
    }

    private void loadSampleData() {
        hashTable.clear();

        double[] sampleKeys = {
                10000.0000, 10250.5000, 10500.2500, 10750.7500, 11000.1250,
                11250.3750, 11500.6250, 11750.8750, 12000.0000, 12250.2500,
                12500.5000, 12750.7500, 13000.0000, 13250.2500, 13500.5000
        };

        String[] sampleValues = {
                "Alpha", "Beta", "Gamma", "Delta", "Epsilon",
                "Zeta", "Eta", "Theta", "Iota", "Kappa",
                "Lambda", "Mu", "Nu", "Xi", "Omicron"
        };

        for (int i = 0; i < Math.min(sampleKeys.length, sampleValues.length); i++) {
            hashTable.insert(sampleKeys[i], sampleValues[i]);
        }

        updateTableDisplay();
        resetIterator();

        iteratorOutput.setText("Загружено " + hashTable.size() + " образцов элементов.\n" +
                "Используйте итератор для последовательного доступа.");

        JOptionPane.showMessageDialog(this,
                "Загружено " + hashTable.size() + " образцов элементов",
                "Образец загружен", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Обновить ссылку на хеш-таблицу (используется из основного GUI)
     */
    public void setHashTable(HashTable<Double, String> table) {
        this.hashTable = table;
        updateTableDisplay();
        resetIterator();
    }
}
