package com;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

/**
 * Панель для отображения графиков трудоёмкости операций
 */
public class PerformanceChartPanel extends JPanel {

    private PerformanceTest.TestResult testResult;
    private ChartType currentChartType;
    private String[] chartOptions = {
            "Сравнение операций (эксперимент)",
            "Поиск: эксперимент vs теория",
            "Неуспешный поиск: эксперимент vs теория",
            "Все операции с теоретическими кривыми"
    };

    public enum ChartType {
        OPERATIONS_COMPARISON,
        SEARCH_THEORETICAL,
        UNSUCCESS_THEORETICAL,
        ALL_WITH_THEORY
    }

    // Цвета для графиков
    private static final Color COLOR_INSERT = new Color(52, 152, 219);
    private static final Color COLOR_SEARCH = new Color(46, 204, 113);
    private static final Color COLOR_DELETE = new Color(231, 76, 60);
    private static final Color COLOR_THEORY_SUCCESS = new Color(155, 89, 182);
    private static final Color COLOR_THEORY_UNSUCCESS = new Color(241, 196, 15);
    private static final Color COLOR_UNSUCCESSFUL = new Color(230, 126, 34);

    public PerformanceChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 500));
        this.currentChartType = ChartType.OPERATIONS_COMPARISON;
    }

    public void setTestResult(PerformanceTest.TestResult result) {
        this.testResult = result;
        repaint();
    }

    public void setChartType(ChartType type) {
        this.currentChartType = type;
        repaint();
    }

    public ChartType getChartType() {
        return currentChartType;
    }

    public String[] getChartOptions() {
        return chartOptions;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (testResult == null || testResult.getEntries().isEmpty()) {
            drawNoDataMessage(g2d);
            return;
        }

        drawChart(g2d);
    }

    private void drawNoDataMessage(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String message = "Выполните тестирование для отображения графиков";
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(message, x, y);
    }

    private void drawChart(Graphics2D g2d) {
        int padding = 60;
        int labelPadding = 20;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding - labelPadding;

        double[] loadFactors = testResult.getLoadFactors();

        // Находим максимальное значение для масштабирования
        double maxValue = getMaxValue();

        // Рисуем оси
        drawAxes(g2d, padding, labelPadding, width, height, maxValue, loadFactors);

        // Рисуем сетку
        drawGrid(g2d, padding, labelPadding, width, height, maxValue);

        // Рисуем графики в зависимости от типа
        switch (currentChartType) {
            case OPERATIONS_COMPARISON:
                drawOperationsComparison(g2d, padding, labelPadding, width, height, maxValue);
                break;
            case SEARCH_THEORETICAL:
                drawSearchTheoretical(g2d, padding, labelPadding, width, height, maxValue);
                break;
            case UNSUCCESS_THEORETICAL:
                drawUnsuccessTheoretical(g2d, padding, labelPadding, width, height, maxValue);
                break;
            case ALL_WITH_THEORY:
                drawAllWithTheory(g2d, padding, labelPadding, width, height, maxValue);
                break;
        }

        // Рисуем легенду
        drawLegend(g2d, padding, labelPadding, width, height);
    }

    private double getMaxValue() {
        double max = 0;
        List<PerformanceTest.ResultEntry> entries = testResult.getEntries();

        for (PerformanceTest.ResultEntry e : entries) {
            switch (currentChartType) {
                case OPERATIONS_COMPARISON:
                    max = Math.max(max, e.insertProbes);
                    max = Math.max(max, e.searchProbes);
                    max = Math.max(max, e.deleteProbes);
                    break;
                case SEARCH_THEORETICAL:
                    max = Math.max(max, e.searchProbes);
                    max = Math.max(max, e.theoreticalSearchSuccess);
                    break;
                case UNSUCCESS_THEORETICAL:
                    max = Math.max(max, e.unsuccessfulSearchProbes);
                    max = Math.max(max, e.theoreticalSearchUnsuccess);
                    break;
                case ALL_WITH_THEORY:
                    max = Math.max(max, e.insertProbes);
                    max = Math.max(max, e.searchProbes);
                    max = Math.max(max, e.deleteProbes);
                    max = Math.max(max, e.theoreticalSearchSuccess);
                    max = Math.max(max, e.theoreticalSearchUnsuccess);
                    break;
            }
        }

        // Добавляем небольшой запас
        return max * 1.1;
    }

    private void drawAxes(Graphics2D g2d, int padding, int labelPadding,
                          int width, int height, double maxValue, double[] loadFactors) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        // Y-ось
        g2d.drawLine(padding, padding, padding, padding + height);
        // X-ось
        g2d.drawLine(padding, padding + height, padding + width, padding + height);

        // Метки Y-оси
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2d.getFontMetrics();

        for (int i = 0; i <= 5; i++) {
            int y = padding + height - (i * height / 5);
            double value = maxValue * i / 5;
            String label = String.format("%.1f", value);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, padding - labelWidth - 5, y + 5);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawLine(padding, y, padding + width, y);
            g2d.setColor(Color.BLACK);
        }

        // Метки X-оси
        for (int i = 0; i < loadFactors.length; i++) {
            int x = padding + (i * width / (loadFactors.length - 1));
            String label = String.format("%.2f", loadFactors[i]);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, x - labelWidth / 2, padding + height + 15);
        }

        // Подписи осей
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Коэффициент заполнения α", padding + width / 2 - 100, padding + height + 40);

        // Повёрнутая подпись Y-оси
        Graphics2D g2dRotated = (Graphics2D) g2d.create();
        g2dRotated.rotate(-Math.PI / 2);
        g2dRotated.drawString("Среднее число зондирований", -padding - height / 2, padding - 35);
        g2dRotated.dispose();
    }

    private void drawGrid(Graphics2D g2d, int padding, int labelPadding,
                          int width, int height, double maxValue) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(0.5f));

        for (int i = 0; i <= 5; i++) {
            int y = padding + height - (i * height / 5);
            g2d.drawLine(padding, y, padding + width, y);
        }
    }

    private void drawOperationsComparison(Graphics2D g2d, int padding, int labelPadding,
                                          int width, int height, double maxValue) {
        List<PerformanceTest.ResultEntry> entries = testResult.getEntries();
        int points = entries.size();

        // Вставка
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.insertProbes, COLOR_INSERT);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.insertProbes, COLOR_INSERT);

        // Поиск
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.searchProbes, COLOR_SEARCH);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.searchProbes, COLOR_SEARCH);

        // Удаление
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.deleteProbes, COLOR_DELETE);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.deleteProbes, COLOR_DELETE);
    }

    private void drawSearchTheoretical(Graphics2D g2d, int padding, int labelPadding,
                                       int width, int height, double maxValue) {
        List<PerformanceTest.ResultEntry> entries = testResult.getEntries();

        // Экспериментальный поиск
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.searchProbes, COLOR_SEARCH);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.searchProbes, COLOR_SEARCH);

        // Теоретическая кривая
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.theoreticalSearchSuccess, COLOR_THEORY_SUCCESS);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.theoreticalSearchSuccess, COLOR_THEORY_SUCCESS);
    }

    private void drawUnsuccessTheoretical(Graphics2D g2d, int padding, int labelPadding,
                                          int width, int height, double maxValue) {
        List<PerformanceTest.ResultEntry> entries = testResult.getEntries();

        // Экспериментальный неуспешный поиск
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.unsuccessfulSearchProbes, COLOR_UNSUCCESSFUL);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.unsuccessfulSearchProbes, COLOR_UNSUCCESSFUL);

        // Теоретическая кривая
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.theoreticalSearchUnsuccess, COLOR_THEORY_UNSUCCESS);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.theoreticalSearchUnsuccess, COLOR_THEORY_UNSUCCESS);
    }

    private void drawAllWithTheory(Graphics2D g2d, int padding, int labelPadding,
                                   int width, int height, double maxValue) {
        List<PerformanceTest.ResultEntry> entries = testResult.getEntries();

        // Экспериментальные данные
        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.insertProbes, COLOR_INSERT);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.insertProbes, COLOR_INSERT);

        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.searchProbes, COLOR_SEARCH);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.searchProbes, COLOR_SEARCH);

        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.deleteProbes, COLOR_DELETE);
        drawPoints(g2d, entries, padding, width, height, maxValue,
                e -> e.deleteProbes, COLOR_DELETE);

        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.theoreticalSearchSuccess, COLOR_THEORY_SUCCESS);

        drawLine(g2d, entries, padding, width, height, maxValue,
                e -> e.theoreticalSearchUnsuccess, COLOR_THEORY_UNSUCCESS);
    }

    private void drawLine(Graphics2D g2d, List<PerformanceTest.ResultEntry> entries,
                          int padding, int width, int height, double maxValue,
                          java.util.function.ToDoubleFunction<PerformanceTest.ResultEntry> valueExtractor,
                          Color color) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));

        for (int i = 0; i < entries.size() - 1; i++) {
            int x1 = padding + (i * width / (entries.size() - 1));
            int y1 = padding + height - (int)((valueExtractor.applyAsDouble(entries.get(i)) / maxValue) * height);
            int x2 = padding + ((i + 1) * width / (entries.size() - 1));
            int y2 = padding + height - (int)((valueExtractor.applyAsDouble(entries.get(i + 1)) / maxValue) * height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawPoints(Graphics2D g2d, List<PerformanceTest.ResultEntry> entries,
                            int padding, int width, int height, double maxValue,
                            java.util.function.ToDoubleFunction<PerformanceTest.ResultEntry> valueExtractor,
                            Color color) {
        g2d.setColor(color);
        int pointSize = 6;

        for (int i = 0; i < entries.size(); i++) {
            int x = padding + (i * width / (entries.size() - 1));
            int y = padding + height - (int)((valueExtractor.applyAsDouble(entries.get(i)) / maxValue) * height);
            g2d.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
        }
    }

    private void drawLegend(Graphics2D g2d, int padding, int labelPadding,
                            int width, int height) {
        int legendX = padding + width - 180;
        int legendY = padding + 10;
        int itemHeight = 20;

        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRect(legendX - 5, legendY - 5, 180, getLegendHeight() + 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX - 5, legendY - 5, 180, getLegendHeight() + 10);

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        int y = legendY;

        switch (currentChartType) {
            case OPERATIONS_COMPARISON:
                drawLegendItem(g2d, legendX, y, COLOR_INSERT, "Вставка (эксп.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_SEARCH, "Поиск (эксп.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_DELETE, "Удаление (эксп.)");
                break;

            case SEARCH_THEORETICAL:
                drawLegendItem(g2d, legendX, y, COLOR_SEARCH, "Поиск (эксп.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_THEORY_SUCCESS, "Поиск (теор.) -ln(1-α)/α");
                break;

            case UNSUCCESS_THEORETICAL:
                drawLegendItem(g2d, legendX, y, COLOR_UNSUCCESSFUL, "Неуспешный поиск (эксп.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_THEORY_UNSUCCESS, "Неуспешный поиск (теор.) 1/(1-α)");
                break;

            case ALL_WITH_THEORY:
                drawLegendItem(g2d, legendX, y, COLOR_INSERT, "Вставка (эксп.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_SEARCH, "Поиск (эксп.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_DELETE, "Удаление (эксп.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_THEORY_SUCCESS, "Поиск (теор.)");
                y += itemHeight;
                drawLegendItem(g2d, legendX, y, COLOR_THEORY_UNSUCCESS, "Неуспешный поиск (теор.)");
                break;
        }
    }

    private void drawLegendItem(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setColor(color);
        g2d.fillRect(x, y + 5, 15, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x + 20, y + 14);
    }

    private int getLegendHeight() {
        switch (currentChartType) {
            case OPERATIONS_COMPARISON: return 60;
            case SEARCH_THEORETICAL: return 40;
            case UNSUCCESS_THEORETICAL: return 40;
            case ALL_WITH_THEORY: return 100;
            default: return 60;
        }
    }
}
