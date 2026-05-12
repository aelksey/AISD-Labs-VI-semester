package com.rgr.ui;

import com.rgr.model.*;
import com.rgr.tasks.EulerCycleTask;
import com.rgr.tasks.EulerCycleTask.CycleEdge;
import com.rgr.tasks.DijkstraRadiusTask;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.NoSuchElementException;

public class MainFrame extends JFrame {
    private Graph<Vertex<String, Integer>, String, Integer, Integer, Integer> graph;
    private GraphPanel graphPanel;
    private JTextArea resultArea;
    private JComboBox<String> formCombo;
    private JCheckBox directedCheck;
    private JCheckBox weightedCheck;
    private JCheckBox eulerianCheck;
    private JTextField vertexCountField;
    private JTextField edgeCountField;

    private Graph<Vertex<String, Integer>, String, Integer, Integer, Integer>.VertexIterator vertexIter;
    private Graph<Vertex<String, Integer>, String, Integer, Integer, Integer>.EdgeIterator edgeIter;
    private Vertex<String, Integer> currentVertex;
    private Edge<Vertex<String, Integer>, Integer, Integer> currentEdge;
    private JLabel vertexIterLabel;
    private JLabel edgeIterLabel;

    public MainFrame() {
        setTitle("РГР: Простой граф - Эйлеров цикл и Радиус орграфа");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initUI();
        createNewGraph(5, 5, false, false, false, false);
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
        eulerianCheck = new JCheckBox("Эйлеров граф");
        createPanel.add(eulerianCheck);
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
            boolean eulerian = eulerianCheck.isSelected();
            createNewGraph(v, eCount, dir, dense, weighted, eulerian);
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
                boolean eulerian = eulerianCheck.isSelected();
                createNewGraph(v, eCount, dir, dense, weighted, eulerian);
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

        JPanel iterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iterPanel.setBorder(BorderFactory.createTitledBorder("Итераторы"));
        JButton nextVertexBtn = new JButton("Вершина Next");
        nextVertexBtn.addActionListener(e -> nextVertex());
        iterPanel.add(nextVertexBtn);
        JButton resetVertexBtn = new JButton("Сброс");
        resetVertexBtn.addActionListener(e -> resetVertexIter());
        iterPanel.add(resetVertexBtn);
        JButton clearVertexBtn = new JButton("Очистить");
        clearVertexBtn.addActionListener(e -> clearVertexIter());
        iterPanel.add(clearVertexBtn);
        vertexIterLabel = new JLabel("Текущая: none");
        iterPanel.add(vertexIterLabel);

        iterPanel.add(Box.createHorizontalStrut(20));

        JButton nextEdgeBtn = new JButton("Ребро Next");
        nextEdgeBtn.addActionListener(e -> nextEdge());
        iterPanel.add(nextEdgeBtn);
        JButton resetEdgeBtn = new JButton("Сброс");
        resetEdgeBtn.addActionListener(e -> resetEdgeIter());
        iterPanel.add(resetEdgeBtn);
        JButton clearEdgeBtn = new JButton("Очистить");
        clearEdgeBtn.addActionListener(e -> clearEdgeIter());
        iterPanel.add(clearEdgeBtn);
        edgeIterLabel = new JLabel("Текущее: none");
        iterPanel.add(edgeIterLabel);
        controlPanel.add(iterPanel);

        add(controlPanel, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Результаты"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void createNewGraph(int vertexCount, int edgeCount, boolean directed, boolean dense, boolean weighted, boolean eulerian) {
        graph = new Graph<>(vertexCount, edgeCount, directed, dense, eulerian);
        if (weighted) {
            Random rand = new Random();
            for (int i = 0; i < graph.V(); i++) {
                for (int j = 0; j < graph.V(); j++) {
                    if (graph.hasEdge(i, j)) {
                        Edge<Vertex<String, Integer>, Integer, Integer> e = graph.getEdge(i, j);
                        if (e != null && !e.isWeightSet()) {
                            e.setWeight(rand.nextInt(9) + 1);
                        }
                    }
                }
            }
        }
        graphPanel.setGraph(graph);
        resetIterators();
        resultArea.setText("Создан граф: |V|=" + graph.V() + ", |E|=" + graph.E() +
                ", dir=" + directed + ", dense=" + dense + ", weighted=" + weighted +
                ", eulerian=" + eulerian);
    }

    private void addVertex() {
        graph.insertVertex();
        graphPanel.setGraph(graph);
        resultArea.setText("Добавлена вершина. |V|=" + graph.V());
    }

    private void deleteVertex(int idx) {
        if (idx >= 0 && idx < graph.V()) {
            Vertex<String, Integer> v = graph.getVertex(idx);
            if (currentVertex == v) {
                advanceVertexIterator();
            }
            graph.deleteVertex(idx);
            graphPanel.setGraph(graph);
            resultArea.setText("Удалена вершина " + idx);
        } else {
            resultArea.setText("Неверный индекс вершины");
        }
    }

    private void deleteVertexById(int id) {
        Vertex<String, Integer> toDelete = null;
        for (int i = 0; i < graph.V(); i++) {
            if (graph.getVertex(i).getId() == id) {
                toDelete = graph.getVertex(i);
                break;
            }
        }
        if (toDelete != null) {
            if (currentVertex == toDelete) {
                advanceVertexIterator();
            }
            graph.deleteVertexById(id);
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
            if (!graph.hasEdge(from, to)) {
                resultArea.setText("Ребро не существует");
                return;
            }
            Edge<Vertex<String, Integer>, Integer, Integer> edge = graph.getEdge(from, to);
            if (currentEdge == edge) {
                advanceEdgeIterator();
            }
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
        if (id < 0) {
            resultArea.setText("Неверный id ребра");
            return;
        }
        Edge<Vertex<String, Integer>, Integer, Integer> edgeToDelete = null;
        for (int i = 0; i < graph.V(); i++) {
            for (int j = 0; j < graph.V(); j++) {
                List<Edge<Vertex<String, Integer>, Integer, Integer>> edges = graph.getEdges(i, j);
                for (Edge<Vertex<String, Integer>, Integer, Integer> e : edges) {
                    if (e.getId() == id) {
                        edgeToDelete = e;
                        break;
                    }
                }
                if (edgeToDelete != null) break;
            }
            if (edgeToDelete != null) break;
        }
        if (edgeToDelete != null) {
            if (currentEdge == edgeToDelete) {
                advanceEdgeIterator();
            }
            if (graph.deleteEdgeById(id)) {
                graphPanel.repaint();
                resultArea.setText("Удалено ребро по id=" + id);
            } else {
                resultArea.setText("Ребро с id=" + id + " не найдено");
            }
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
            StringBuilder sb = new StringBuilder("Эйлеров цикл (двухпроходной, " + cycle.size() + " рёбер):\n");
            for (var ce : cycle) {
                String direction = ce.isReverse ? " ← " : " → ";
                sb.append(ce.edge.getSource().getName()).append(direction).append(ce.edge.getTarget().getName());
                sb.append(" (").append(ce.isReverse ? "обратный" : "прямой").append(")\n");
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

    private void resetIterators() {
        vertexIter = graph.new VertexIterator();
        edgeIter = graph.new EdgeIterator();
        currentVertex = null;
        currentEdge = null;
        graphPanel.clearIteratorHighlight();
        updateVertexIterLabel();
        updateEdgeIterLabel();
    }

    private void nextVertex() {
        if (graph.V() == 0) {
            resultArea.setText("Граф не содержит вершин");
            return;
        }
        if (currentVertex == null) {
            if (vertexIter.hasNext()) {
                currentVertex = vertexIter.next();
                graphPanel.setIteratorVertex(currentVertex);
                graphPanel.repaint();
                updateVertexIterLabel();
            }
        } else {
            if (vertexIter.hasNext()) {
                currentVertex = vertexIter.next();
                graphPanel.setIteratorVertex(currentVertex);
                graphPanel.repaint();
                updateVertexIterLabel();
            } else {
                resultArea.setText("Конец итератора вершин");
            }
        }
    }

    private void advanceVertexIterator() {
        if (vertexIter != null && vertexIter.hasNext()) {
            currentVertex = vertexIter.next();
            graphPanel.setIteratorVertex(currentVertex);
        } else {
            currentVertex = null;
            graphPanel.clearIteratorHighlight();
        }
        graphPanel.repaint();
        updateVertexIterLabel();
    }

    private void resetVertexIter() {
        vertexIter = graph.new VertexIterator();
        currentVertex = null;
        graphPanel.clearIteratorHighlight();
        graphPanel.repaint();
        updateVertexIterLabel();
        resultArea.setText("Итератор вершин сброшен");
    }

    private void clearVertexIter() {
        currentVertex = null;
        graphPanel.setIteratorVertex(null);
        graphPanel.repaint();
        updateVertexIterLabel();
    }

    private void updateVertexIterLabel() {
        if (currentVertex != null) {
            int idx = graph.getIndex(currentVertex);
            vertexIterLabel.setText("Текущая: " + currentVertex.getName() + " (id=" + currentVertex.getId() + ", idx=" + idx + ")");
        } else {
            vertexIterLabel.setText("Текущая: none");
        }
    }

    private void nextEdge() {
        if (graph.E() == 0) {
            resultArea.setText("Граф не содержит рёбер");
            return;
        }
        if (currentEdge == null) {
            if (edgeIter.hasNext()) {
                currentEdge = edgeIter.next();
                graphPanel.setIteratorEdge(currentEdge);
                graphPanel.repaint();
                updateEdgeIterLabel();
            }
        } else {
            if (edgeIter.hasNext()) {
                currentEdge = edgeIter.next();
                graphPanel.setIteratorEdge(currentEdge);
                graphPanel.repaint();
                updateEdgeIterLabel();
            } else {
                resultArea.setText("Конец итератора рёбер");
            }
        }
    }

    private void advanceEdgeIterator() {
        if (edgeIter != null && edgeIter.hasNext()) {
            currentEdge = edgeIter.next();
            graphPanel.setIteratorEdge(currentEdge);
        } else {
            currentEdge = null;
            graphPanel.clearIteratorHighlight();
        }
        graphPanel.repaint();
        updateEdgeIterLabel();
    }

    private void resetEdgeIter() {
        edgeIter = graph.new EdgeIterator();
        currentEdge = null;
        graphPanel.clearIteratorHighlight();
        graphPanel.repaint();
        updateEdgeIterLabel();
        resultArea.setText("Итератор рёбер сброшен");
    }

    private void clearEdgeIter() {
        currentEdge = null;
        graphPanel.setIteratorEdge(null);
        graphPanel.repaint();
        updateEdgeIterLabel();
    }

    private void updateEdgeIterLabel() {
        if (currentEdge != null) {
            String dir = graph.isDirected() ? " -> " : " -- ";
            String weightInfo = currentEdge.isWeightSet() ? ", вес=" + currentEdge.getWeight() : "";
            edgeIterLabel.setText("Текущее: " + currentEdge.getSource().getName() + dir + currentEdge.getTarget().getName() +
                    " (id=" + currentEdge.getId() + ")" + weightInfo);
        } else {
            edgeIterLabel.setText("Текущее: none");
        }
    }
}