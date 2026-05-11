package com.rgr.ui;

import com.rgr.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {
    private Graph<Vertex<String, Integer>, String, Integer, Integer, Integer> graph;
    private Map<Vertex<String, Integer>, Point> vertexPositions = new HashMap<>();

    private static final int VERTEX_RADIUS = 20;
    private static final int VERTEX_HIT_RADIUS = 28;
    private static final int EDGE_HIT_DIST = 16;
    private static final int LOOP_OFFSET = 20;

    // Pan & Zoom
    private double translateX = 0, translateY = 0;
    private double scale = 1.0;
    private int mouseX, mouseY;
    private boolean dragging = false;

    // Hover elements
    private Vertex<String, Integer> hoverVertex = null;
    private Edge<Vertex<String, Integer>, Integer, Integer> hoverEdge = null;

    public GraphPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setupMouseListeners();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                    dragging = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (graph == null) return;
                Point2D p = transformScreenToWorld(e.getX(), e.getY());

                // Check vertex
                for (var entry : vertexPositions.entrySet()) {
                    Vertex<String, Integer> v = entry.getKey();
                    Point pos = entry.getValue();
                    Point2D screenPos = transformWorldToScreen(pos.x, pos.y);
                    double dx = screenPos.getX() - p.getX();
                    double dy = screenPos.getY() - p.getY();
                    if (dx * dx + dy * dy <= VERTEX_HIT_RADIUS * VERTEX_HIT_RADIUS) {
                        int idx = graph.getIndex(v);
                        String msg = "id=" + v.getId() + ", index=" + idx + ", name=" + v.getName();
                        if (v.isDataSet()) msg += ", Данные: " + v.getData();
                        JOptionPane.showMessageDialog(GraphPanel.this, msg,
                                "Информация о вершине", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

                // Check edges (including loops)
                for (int i = 0; i < graph.V(); i++) {
                    Vertex<String, Integer> v1 = graph.getVertex(i);
                    Point p1 = vertexPositions.get(v1);
                    if (p1 == null) continue;
                    Point2D sp1 = transformWorldToScreen(p1.x, p1.y);
                    for (int j = 0; j < graph.V(); j++) {
                        List<Edge<Vertex<String, Integer>, Integer, Integer>> edges = graph.getEdges(i, j);
                        if (edges.isEmpty()) continue;

                        if (i == j) { // loop
                            int loopX = p1.x + VERTEX_RADIUS;
                            int loopY = p1.y - VERTEX_RADIUS - LOOP_OFFSET / 2;
                            Point2D spLoop = transformWorldToScreen(loopX, loopY);
                            double dx = spLoop.getX() - p.getX();
                            double dy = spLoop.getY() - p.getY();
                            if (Math.hypot(dx, dy) < VERTEX_RADIUS + LOOP_OFFSET / 2) {
                                Edge<?, ?, ?> eObj = edges.get(0);
                                String msg = "Петля: id=" + eObj.getId() + ", vertex index=" + graph.getIndex(v1);
                                if (eObj.isWeightSet()) msg += ", вес=" + eObj.getWeight();
                                JOptionPane.showMessageDialog(GraphPanel.this, msg,
                                        "Информация о ребре", JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }
                            continue;
                        }

                        Vertex<String, Integer> v2 = graph.getVertex(j);
                        Point p2 = vertexPositions.get(v2);
                        if (p2 == null) continue;
                        Point2D sp2 = transformWorldToScreen(p2.x, p2.y);
                        double dist = distanceToSegment(p.getX(), p.getY(),
                                sp1.getX(), sp1.getY(), sp2.getX(), sp2.getY());
                        if (dist < EDGE_HIT_DIST) {
                            Edge<?, ?, ?> eObj = edges.get(0);
                            String dir = graph.isDirected() ? " -> " : " -- ";
                            String msg = "Edge id=" + eObj.getId() + ": " + v1.getName() + dir + v2.getName();
                            if (eObj.isWeightSet()) msg += ", вес=" + eObj.getWeight();
                            JOptionPane.showMessageDialog(GraphPanel.this, msg,
                                    "Информация о ребре", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int dx = e.getX() - mouseX;
                    int dy = e.getY() - mouseY;
                    translateX += dx;
                    translateY += dy;
                    mouseX = e.getX();
                    mouseY = e.getY();
                    repaint();
                } else {
                    updateHover(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e);
            }
        });

        addMouseWheelListener(e -> {
            double factor = 1.1;
            if (e.getPreciseWheelRotation() < 0) scale *= factor;
            else scale /= factor;
            scale = Math.max(0.1, Math.min(10.0, scale));
            repaint();
        });
    }

    private void updateHover(MouseEvent e) {
        if (graph == null) {
            setCursor(Cursor.getDefaultCursor());
            hoverVertex = null;
            hoverEdge = null;
            repaint();
            return;
        }

        Point2D p = transformScreenToWorld(e.getX(), e.getY());
        Vertex<String, Integer> newHoverVertex = null;
        Edge<Vertex<String, Integer>, Integer, Integer> newHoverEdge = null;

        // Check vertices
        for (var entry : vertexPositions.entrySet()) {
            Vertex<String, Integer> v = entry.getKey();
            Point pos = entry.getValue();
            Point2D screenPos = transformWorldToScreen(pos.x, pos.y);
            double dx = screenPos.getX() - p.getX();
            double dy = screenPos.getY() - p.getY();
            if (dx * dx + dy * dy <= VERTEX_HIT_RADIUS * VERTEX_HIT_RADIUS) {
                newHoverVertex = v;
                break;
            }
        }

        // If no vertex, check edges (including loops)
        if (newHoverVertex == null) {
            for (int i = 0; i < graph.V(); i++) {
                Vertex<String, Integer> v1 = graph.getVertex(i);
                Point p1 = vertexPositions.get(v1);
                if (p1 == null) continue;
                Point2D sp1 = transformWorldToScreen(p1.x, p1.y);
                for (int j = 0; j < graph.V(); j++) {
                    List<Edge<Vertex<String, Integer>, Integer, Integer>> edges = graph.getEdges(i, j);
                    if (edges.isEmpty()) continue;

                    if (i == j) { // loop
                        int loopX = p1.x + VERTEX_RADIUS;
                        int loopY = p1.y - VERTEX_RADIUS - LOOP_OFFSET / 2;
                        Point2D spLoop = transformWorldToScreen(loopX, loopY);
                        double dx = spLoop.getX() - p.getX();
                        double dy = spLoop.getY() - p.getY();
                        if (Math.hypot(dx, dy) < VERTEX_RADIUS + LOOP_OFFSET / 2) {
                            newHoverEdge = edges.get(0);
                            break;
                        }
                        continue;
                    }

                    Vertex<String, Integer> v2 = graph.getVertex(j);
                    Point p2 = vertexPositions.get(v2);
                    if (p2 == null) continue;
                    Point2D sp2 = transformWorldToScreen(p2.x, p2.y);
                    double dist = distanceToSegment(p.getX(), p.getY(),
                            sp1.getX(), sp1.getY(), sp2.getX(), sp2.getY());
                    if (dist < EDGE_HIT_DIST) {
                        newHoverEdge = edges.get(0);
                        break;
                    }
                }
                if (newHoverEdge != null) break;
            }
        }

        boolean changed = (hoverVertex != newHoverVertex) || (hoverEdge != newHoverEdge);
        hoverVertex = newHoverVertex;
        hoverEdge = newHoverEdge;

        if (hoverVertex != null || hoverEdge != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
        if (changed) repaint();
    }

    public void setGraph(Graph<Vertex<String, Integer>, String, Integer, Integer, Integer> g) {
        this.graph = g;
        Map<Vertex<String, Integer>, Point> oldPositions = vertexPositions;
        vertexPositions = new HashMap<>();
        
        int w = getWidth();
        int h = getHeight();
        if (w <= 0) w = 800;
        if (h <= 0) h = 600;
        int margin = 60;
        Random rand = new Random();
        
        for (int i = 0; i < graph.V(); i++) {
            Vertex<String, Integer> v = graph.getVertex(i);
            Point oldPos = oldPositions.get(v);
            if (oldPos != null) {
                vertexPositions.put(v, oldPos);
            } else {
                int x = margin + rand.nextInt(w - 2 * margin);
                int y = margin + rand.nextInt(h - 2 * margin);
                vertexPositions.put(v, new Point(x, y));
            }
        }
        repaint();
    }

    

    public void resetView() {
        translateX = 0;
        translateY = 0;
        scale = 1.0;
        repaint();
    }

    private double distanceToSegment(double x, double y, double x1, double y1, double x2, double y2) {
        double ax = x2 - x1;
        double ay = y2 - y1;
        double len2 = ax * ax + ay * ay;
        if (len2 == 0) return Math.hypot(x - x1, y - y1);
        double t = ((x - x1) * ax + (y - y1) * ay) / len2;
        t = Math.max(0, Math.min(1, t));
        double projX = x1 + t * ax;
        double projY = y1 + t * ay;
        return Math.hypot(x - projX, y - projY);
    }

    private Point2D transformScreenToWorld(int screenX, int screenY) {
        return new Point2D.Double((screenX - translateX) / scale, (screenY - translateY) / scale);
    }

    private Point2D transformWorldToScreen(double worldX, double worldY) {
        return new Point2D.Double(worldX * scale + translateX, worldY * scale + translateY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform old = g2.getTransform();
        g2.translate(translateX, translateY);
        g2.scale(scale, scale);

        if (graph == null) {
            g2.setTransform(old);
            return;
        }

        // Draw edges (including multiple edges and loops)
        for (int i = 0; i < graph.V(); i++) {
            Vertex<String, Integer> v1 = graph.getVertex(i);
            Point p1 = vertexPositions.get(v1);
            if (p1 == null) continue;
            for (int j = 0; j < graph.V(); j++) {
                List<Edge<Vertex<String, Integer>, Integer, Integer>> edges = graph.getEdges(i, j);
                if (edges.isEmpty()) continue;

                boolean isLoop = (i == j);
                int edgeIdx = 0;
                for (Edge<Vertex<String, Integer>, Integer, Integer> e : edges) {
                    boolean hover = (e == hoverEdge);
                    if (isLoop) {
                        drawLoop(g2, p1, e, hover);
                    } else {
                        Vertex<String, Integer> v2 = graph.getVertex(j);
                        Point p2 = vertexPositions.get(v2);
                        if (p2 == null) continue;
                        double offset = 0;
                        if (edges.size() > 1) {
                            offset = (edgeIdx - (edges.size() - 1) / 2.0) * 6;
                        }
                        drawEdge(g2, p1, p2, e, offset, hover);
                    }
                    edgeIdx++;
                }
            }
        }

        // Draw vertices
        for (var entry : vertexPositions.entrySet()) {
            Vertex<String, Integer> v = entry.getKey();
            Point p = entry.getValue();
            if (v == hoverVertex) {
                g2.setColor(new Color(255, 200, 150)); // highlight
            } else {
                g2.setColor(Color.LIGHT_GRAY);
            }
            g2.fillOval(p.x - VERTEX_RADIUS, p.y - VERTEX_RADIUS, 2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - VERTEX_RADIUS, p.y - VERTEX_RADIUS, 2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);
            String name = v.getName();
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(name);
            g2.drawString(name, p.x - sw / 2, p.y + fm.getAscent() / 2 - 2);
        }

        g2.setTransform(old);
    }

    private void drawEdge(Graphics2D g2, Point p1, Point p2,
                          Edge<Vertex<String, Integer>, Integer, Integer> e,
                          double offset, boolean hover) {
        // Set color and stroke
        if (hover) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(1));
        }

        int x1 = p1.x, y1 = p1.y, x2 = p2.x, y2 = p2.y;
        if (offset != 0) {
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double len = Math.hypot(dx, dy);
            if (len > 0) {
                double perpX = -dy / len * offset;
                double perpY = dx / len * offset;
                x1 = (int)(p1.x + perpX);
                y1 = (int)(p1.y + perpY);
                x2 = (int)(p2.x + perpX);
                y2 = (int)(p2.y + perpY);
            }
        }

        g2.drawLine(x1, y1, x2, y2);
        if (graph.isDirected()) drawArrow(g2, new Point(x1, y1), new Point(x2, y2), hover);

        if (e.isWeightSet()) {
            int mx = (x1 + x2) / 2;
            int my = (y1 + y2) / 2;
            g2.setColor(Color.BLUE);
            g2.drawString(Integer.toString(e.getWeight()), mx, my);
        }

        // Reset stroke
        g2.setStroke(new BasicStroke(1));
    }

    private void drawLoop(Graphics2D g2, Point p,
                          Edge<Vertex<String, Integer>, Integer, Integer> e,
                          boolean hover) {
        int loopRad = VERTEX_RADIUS;
        int loopX = p.x + VERTEX_RADIUS;
        int loopY = p.y - VERTEX_RADIUS - LOOP_OFFSET;

        if (hover) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(1));
        }

        g2.drawOval(loopX, loopY, loopRad, loopRad);
        if (graph.isDirected()) {
            double angle = Math.PI / 4;
            int arrowX = (int)(loopX + loopRad * Math.cos(angle));
            int arrowY = (int)(loopY + loopRad * Math.sin(angle));
            g2.fillPolygon(new int[]{arrowX, arrowX - 5, arrowX - 5},
                           new int[]{arrowY, arrowY - 4, arrowY + 4}, 3);
        }
        if (e.isWeightSet()) {
            g2.setColor(Color.BLUE);
            g2.drawString(Integer.toString(e.getWeight()), loopX + loopRad/2, loopY + loopRad/2);
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void drawArrow(Graphics2D g2, Point from, Point to, boolean hover) {
        int arrowSize = 8;
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int xArrow = (int) (to.x - VERTEX_RADIUS * Math.cos(angle));
        int yArrow = (int) (to.y - VERTEX_RADIUS * Math.sin(angle));
        int x1 = (int) (xArrow - arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (yArrow - arrowSize * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (xArrow - arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (yArrow - arrowSize * Math.sin(angle + Math.PI / 6));
        g2.fillPolygon(new int[]{xArrow, x1, x2}, new int[]{yArrow, y1, y2}, 3);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (graph != null && vertexPositions.isEmpty()) {
            Map<Vertex<String, Integer>, Point> oldPositions = vertexPositions;
            vertexPositions = new HashMap<>();
            int w = getWidth();
            int h = getHeight();
            if (w <= 0) w = 800;
            if (h <= 0) h = 600;
            int margin = 60;
            Random rand = new Random();
            for (int i = 0; i < graph.V(); i++) {
                Vertex<String, Integer> v = graph.getVertex(i);
                int px = margin + rand.nextInt(w - 2 * margin);
                int py = margin + rand.nextInt(h - 2 * margin);
                vertexPositions.put(v, new Point(px, py));
            }
        }
    }
}