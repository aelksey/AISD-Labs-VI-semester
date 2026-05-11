package com.rgr.ui;

import com.rgr.model.*;
import com.rgr.tasks.*;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MainFrame extends JFrame {
    private Graph<Vertex<String, Integer>, String, Integer, Integer, Integer> graph;
    private GraphPanel graphPanel;
    private JTextArea resultArea;
    private JComboBox<String> formCombo;
    private JCheckBox directedCheck;
    private JCheckBox weightedCheck;  // новый чекбокс
    private JTextField vertexCountField;
    private JTextField edgeCountField;

    public MainFrame() {
        setTitle("РГР: Простой граф - Эйлеров цикл и Радиус орграфа");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initUI();
        createNewGraph(5, 5, false, false, false);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление"));

        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createPanel.add(new JLabel("Вершин:"));
        vertexCountField = new JTextField("5", 3);
        createPanel.add(vertexCountField);
        createPanel.add(new JLabel("Рёбер:"));
        edgeCountField = new JTextField("5", 3);
        createPanel.add(edgeCountField);
        directedCheck = new JCheckBox("Ориентированный");
        createPanel.add(directedCheck);
        weightedCheck = new JCheckBox("Взвешенный");
        createPanel.add(weightedCheck);
        formCombo = new JComboBox<>(new String[]{"L-граф (список)", "M-граф (матрица)"});
        createPanel.add(formCombo);
        
        JButton createBtn = new JButton("Создать");
        createBtn.addActionListener(e -> {
            int v = Integer.parseInt(vertexCountField.getText());
            int eCount = Integer.parseInt(edgeCountField.getText());
            boolean dir = directedCheck.isSelected();
            boolean dense = formCombo.getSelectedIndex() == 1;
            boolean weighted = weightedCheck.isSelected();
            createNewGraph(v, eCount, dir, dense, weighted);
        });
        createPanel.add(createBtn);
        
        JButton randomBtn = new JButton("Случайный");
        randomBtn.addActionListener(e -> {
            try {
                int v = Integer.parseInt(vertexCountField.getText());
                int eCount = Integer.parseInt(edgeCountField.getText());
                boolean dir = directedCheck.isSelected();    // используем состояние чекбокса
                boolean dense = formCombo.getSelectedIndex() == 1;
                boolean weighted = weightedCheck.isSelected();
                createNewGraph(v, eCount, dir, dense, weighted);
            } catch (NumberFormatException ex) {
                resultArea.setText("Ошибка: введите целые числа в поля «Вершин» и «Рёбер»");
            }
        });
        createPanel.add(randomBtn);
        controlPanel.add(createPanel);

        // Панели удаления/добавления вершин и рёбер (без изменений)
        JPanel vertexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vertexPanel.add(new JLabel("Добавить вершину:"));
        JButton addVertexBtn = new JButton("+");
        addVertexBtn.addActionListener(e -> addVertex());
        vertexPanel.add(addVertexBtn);
        vertexPanel.add(new JLabel("Удалить вершину:"));
        JTextField delVertexField = new JTextField("0", 3);
        vertexPanel.add(delVertexField);
        JButton delVertexBtn = new JButton("Удалить");
        delVertexBtn.addActionListener(e -> deleteVertex(Integer.parseInt(delVertexField.getText())));
        vertexPanel.add(delVertexBtn);
        vertexPanel.add(new JLabel("Удалить по id:"));
        JTextField delVertexByIdField = new JTextField("0", 3);
        vertexPanel.add(delVertexByIdField);
        JButton delVertexByIdBtn = new JButton("Удалить");
        delVertexByIdBtn.addActionListener(e -> deleteVertexById(Integer.parseInt(delVertexByIdField.getText())));
        vertexPanel.add(delVertexByIdBtn);
        controlPanel.add(vertexPanel);

        JPanel edgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        edgePanel.add(new JLabel("Ребро (из, в, вес):"));
        JTextField fromField = new JTextField("0", 2);
        JTextField toField = new JTextField("1", 2);
        JTextField weightField = new JTextField("1", 2);
        edgePanel.add(fromField);
        edgePanel.add(toField);
        edgePanel.add(weightField);
        JButton addEdgeBtn = new JButton("Добавить");
        addEdgeBtn.addActionListener(e -> addEdge(Integer.parseInt(fromField.getText()), Integer.parseInt(toField.getText()), Integer.parseInt(weightField.getText())));
        edgePanel.add(addEdgeBtn);
        JButton delEdgeBtn = new JButton("Удалить ребро");
        delEdgeBtn.addActionListener(e -> deleteEdge(Integer.parseInt(fromField.getText()), Integer.parseInt(toField.getText())));
        edgePanel.add(delEdgeBtn);
        edgePanel.add(new JLabel("Удалить ребро по id:"));
        JTextField delEdgeByIdField = new JTextField("0", 3);
        edgePanel.add(delEdgeByIdField);
        JButton delEdgeByIdBtn = new JButton("Удалить");
        delEdgeByIdBtn.addActionListener(e -> deleteEdgeById(Integer.parseInt(delEdgeByIdField.getText())));
        edgePanel.add(delEdgeByIdBtn);
        controlPanel.add(edgePanel);

        JPanel taskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton eulerBtn = new JButton("Эйлеров цикл (неор.)");
        eulerBtn.addActionListener(e -> computeEulerCycle());
        taskPanel.add(eulerBtn);
        JButton radiusBtn = new JButton("Радиус орграфа (Дейкстра)");
        radiusBtn.addActionListener(e -> computeRadius());
        taskPanel.add(radiusBtn);
        JButton convertBtn = new JButton("Преобразовать L<->M");
        convertBtn.addActionListener(e -> toggleForm());
        taskPanel.add(convertBtn);
        JButton resetViewBtn = new JButton("Сбросить вид");
        resetViewBtn.addActionListener(e -> graphPanel.resetView());
        taskPanel.add(resetViewBtn);
        controlPanel.add(taskPanel);

        add(controlPanel, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Результаты"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void createNewGraph(int vertexCount, int edgeCount, boolean directed, boolean dense, boolean weighted) {
        graph = new Graph<>(vertexCount, edgeCount, directed, dense);
        // Если граф взвешенный, назначаем случайные веса всем рёбрам
        if (weighted) {
            Random rand = new Random();
            for (int i = 0; i < graph.V(); i++) {
                for (int j = 0; j < graph.V(); j++) {
                    if (graph.hasEdge(i, j)) {
                        Edge<Vertex<String, Integer>, Integer, Integer> e = graph.getEdge(i, j);
                        if (e != null && !e.isWeightSet()) {
                            e.setWeight(rand.nextInt(9) + 1); // вес от 1 до 9
                        }
                    }
                }
            }
        }
        graphPanel.setGraph(graph);
        resultArea.setText("Создан граф: |V|=" + graph.V() + ", |E|=" + graph.E() +
                ", dir=" + directed + ", dense=" + dense + ", weighted=" + weighted);
    }

    private void addVertex() {
        graph.insertVertex();
        graphPanel.setGraph(graph);
        resultArea.setText("Добавлена вершина. |V|=" + graph.V());
    }

    private void deleteVertex(int idx) {
        if (idx >= 0 && idx < graph.V()) {
            graph.deleteVertex(idx);
            graphPanel.setGraph(graph);
            resultArea.setText("Удалена вершина " + idx);
        } else {
            resultArea.setText("Неверный индекс вершины");
        }
    }

    private void deleteVertexById(int id) {
        if (graph.deleteVertexById(id)) {
            graphPanel.setGraph(graph);
            resultArea.setText("Удалена вершина по id=" + id);
        } else {
            resultArea.setText("Вершина с id=" + id + " не найдена");
        }
    }

    private void addEdge(int from, int to, int weight) {
        if (from >= 0 && from < graph.V() && to >= 0 && to < graph.V()) {
            try {
                Edge<Vertex<String, Integer>, Integer, Integer> e = graph.insertEdge(graph.getVertex(from), graph.getVertex(to));
                if (e != null) {
                    if (weight != 0) {
                        e.setWeight(weight);
                    }
                    graphPanel.repaint();
                    String weightInfo = weight != 0 ? " вес=" + weight : " (без веса)";
                    resultArea.setText("Добавлено ребро " + from + "->" + to + weightInfo);
                } else {
                    resultArea.setText("Ребро уже существует");
                }
            } catch (Exception ex) {
                resultArea.setText("Ошибка: " + ex.getMessage());
            }
        } else {
            resultArea.setText("Неверные индексы вершин");
        }
    }

    private void deleteEdge(int from, int to) {
        if (from >= 0 && from < graph.V() && to >= 0 && to < graph.V()) {
            if (graph.deleteEdge(graph.getVertex(from), graph.getVertex(to))) {
                graphPanel.repaint();
                resultArea.setText("Удалено ребро " + from + "->" + to);
            } else {
                resultArea.setText("Ребро не существует");
            }
        } else {
            resultArea.setText("Неверные индексы");
        }
    }

    private void deleteEdgeById(int id) {
        if (graph.deleteEdgeById(id)) {
            graphPanel.repaint();
            resultArea.setText("Удалено ребро по id=" + id);
        } else {
            resultArea.setText("Ребро с id=" + id + " не найдено");
        }
    }

    private void computeEulerCycle() {
        if (graph.isDirected()) {
            resultArea.setText("Эйлеров цикл определён только для неориентированных графов");
            return;
        }
        Graph<Vertex<String, Integer>, String, Integer, Integer, Integer> copy = new Graph<>(graph);
        EulerCycleTask<Vertex<String, Integer>, String, Integer, Integer, Integer> task = new EulerCycleTask<>(copy);
        if (task.isValid()) {
            var cycle = task.result();
            StringBuilder sb = new StringBuilder("Эйлеров цикл (список рёбер):\n");
            for (var e : cycle) {
                sb.append(e.getSource().getName()).append(" -> ").append(e.getTarget().getName()).append("\n");
            }
            resultArea.setText(sb.toString());
        } else {
            resultArea.setText("Граф не является эйлеровым (не все вершины чётной степени или несвязен)");
        }
    }

    private void computeRadius() {
        if (!graph.isDirected()) {
            resultArea.setText("Радиус определён для ориентированных графов");
            return;
        }
        Graph<Vertex<String, Integer>, String, Integer, Integer, Integer> copy = new Graph<>(graph);
        DijkstraRadiusTask<Vertex<String, Integer>, String, Integer, Integer, Integer> task = new DijkstraRadiusTask<>(copy);
        if (task.isValid()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Центр орграфа: ").append(task.getCenter().getName()).append("\n");
            sb.append("Радиус: ").append(task.getRadius()).append("\n");
            sb.append("Путь, соответствующий радиусу: ");
            for (Vertex<String, Integer> v : task.getRadiusPath()) {
                sb.append(v.getName()).append(" ");
            }
            resultArea.setText(sb.toString());
        } else {
            resultArea.setText("Не удалось вычислить радиус (возможно, граф несвязен или нет вершин)");
        }
    }

    private void toggleForm() {
        if (graph.isDense()) graph.toListGraph();
        else graph.toMatrixGraph();
        graphPanel.setGraph(graph);
        resultArea.setText("Форма представления изменена на " + (graph.isDense() ? "M-граф" : "L-граф"));
    }
}