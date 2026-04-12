package com.lab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import javax.swing.border.*;

public class Tree23GUI extends JPanel {
    private Tree23 tree;
    private List<TreeNode> treeLayout;
    private double zoom = 1.0;
    private double panX = 0, panY = 0;
    private Point lastDragPoint;
    private double horizontalSpacing = 100;
    private double verticalSpacing = 80;
    private int nodeRadius = 25;
    private int fontSize = 12;

    public Tree23GUI() {
        tree = new Tree23();
        treeLayout = new ArrayList<>();
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Mouse listeners for panning and zooming
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    panX += (e.getX() - lastDragPoint.getX()) / zoom;
                    panY += (e.getY() - lastDragPoint.getY()) / zoom;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
                setCursor(Cursor.getDefaultCursor());
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        addMouseWheelListener(e -> {
            double scaleFactor = 1.1;
            Point mousePoint = e.getPoint();
            Point2D beforeZoom = screenToTree(mousePoint);

            if (e.getWheelRotation() < 0) {
                zoom *= scaleFactor;
            } else {
                zoom /= scaleFactor;
            }

            // Limit zoom
            zoom = Math.max(0.1, Math.min(5.0, zoom));

            Point2D afterZoom = screenToTree(mousePoint);
            panX += afterZoom.getX() - beforeZoom.getX();
            panY += afterZoom.getY() - beforeZoom.getY();
            repaint();
        });
    }

    /**
     * Main visualization method - takes a Tree23 object and visualizes it
     *
     * @param treeToVisualize The 2-3 tree to visualize
     */
    public void visualizeTree(Tree23 treeToVisualize) {
        if (treeToVisualize == null) {
            this.tree = new Tree23();
        } else {
            this.tree = treeToVisualize;
        }
        updateTree();
    }

    public void setTree(Tree23 newTree) {
        visualizeTree(newTree);
    }

    public Tree23 getTree() {
        return tree;
    }

    public void setFontSize(int size) {
        this.fontSize = Math.max(8, Math.min(24, size));
        repaint();
    }

    private Point2D screenToTree(Point screenPoint) {
        double x = (screenPoint.getX() - getWidth() / 2) / zoom + panX;
        double y = (screenPoint.getY() - getHeight() / 2) / zoom + panY;
        return new Point2D.Double(x, y);
    }

    private Point2D treeToScreen(double x, double y) {
        double screenX = (x - panX) * zoom + getWidth() / 2;
        double screenY = (y - panY) * zoom + getHeight() / 2;
        return new Point2D.Double(screenX, screenY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (treeLayout.isEmpty()) {
            drawEmptyMessage(g2d);
            return;
        }

        // Draw edges first
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        for (TreeNode node : treeLayout) {
            for (TreeNode child : node.children) {
                Point2D from = treeToScreen(node.x, node.y);
                Point2D to = treeToScreen(child.x, child.y);
                g2d.draw(new Line2D.Double(from, to));
            }
        }

        // Draw nodes
        for (TreeNode node : treeLayout) {
            Point2D pos = treeToScreen(node.x, node.y);
            int x = (int) (pos.getX() - nodeRadius);
            int y = (int) (pos.getY() - nodeRadius);
            int diameter = nodeRadius * 2;

            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(x + 3, y + 3, diameter, diameter);

            // Draw node background
            GradientPaint gradient;
            if (node.isLeaf) {
                gradient = new GradientPaint(x, y, new Color(100, 149, 237),
                        x + diameter, y + diameter, new Color(70, 130, 180));
            } else {
                gradient = new GradientPaint(x, y, new Color(50, 205, 50),
                        x + diameter, y + diameter, new Color(34, 139, 34));
            }
            g2d.setPaint(gradient);
            g2d.fillOval(x, y, diameter, diameter);

            // Draw border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, diameter, diameter);

            // Draw text (keys for internal nodes) - all text in white
            g2d.setColor(Color.WHITE);
            int calculatedFontSize = Math.max(8, Math.min(24, fontSize));
            g2d.setFont(new Font("Arial", Font.BOLD, calculatedFontSize));

            String text = node.value;
            if (!node.isLeaf && node.keys != null && !node.keys.isEmpty()) {
                // Display keys for internal nodes
                text = String.join(", ", node.keys);
            }

            FontMetrics fm = g2d.getFontMetrics();
            Rectangle2D textBounds = fm.getStringBounds(text, g2d);
            int textX = (int) (pos.getX() - textBounds.getWidth() / 2);
            int textY = (int) (pos.getY() + fm.getAscent() / 2);
            g2d.drawString(text, textX, textY);
        }
    }

    private void drawEmptyMessage(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.ITALIC, 16));
        String message = "🌳 Tree is empty. Use controls to add elements.";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(message, x, y);
    }

    private void updateTree() {
        treeLayout.clear();

        if (tree == null || tree.isEmpty(tree.root) || tree.root == -1) {
            repaint();
            return;
        }

        try {
            // Collect all nodes by traversing the tree
            Map<Integer, TreeNode> nodeMap = new HashMap<>();
            buildTreeStructure(tree.root, nodeMap, null);

            // Calculate positions
            if (!nodeMap.isEmpty()) {
                calculatePositions(nodeMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        repaint();
    }

    private void buildTreeStructure(int nodeIdx, Map<Integer, TreeNode> nodeMap, TreeNode parent) {
        if (nodeIdx == -1) return;

        TreeNode node = nodeMap.get(nodeIdx);
        if (node == null) {
            boolean isLeaf = tree.isLeaf(nodeIdx);
            String value = "";
            List<String> keys = new ArrayList<>();

            if (isLeaf) {
                TreeElement elem = tree.getValue(nodeIdx);
                if (elem != null) {
                    value = String.format("%.0f", elem.ExtractValue());
                } else {
                    value = "?";
                }
            } else {
                // Extract keys from internal node
                int childCount = tree.getTreeNodeChildCount(nodeIdx);
                for (int i = 0; i < childCount - 1; i++) {
                    int childIdx = tree.getTreeNodeChild(nodeIdx, i);
                    if (childIdx != -1) {
                        double maxValue = tree.max(childIdx);
                        keys.add(String.format("%.0f", maxValue));
                    }
                }
                // For display, show the max values that separate children
                value = String.join(", ", keys);
            }

            node = new TreeNode(value, isLeaf);
            node.keys = keys;
            node.index = nodeIdx;
            nodeMap.put(nodeIdx, node);
        }

        if (parent != null && !parent.children.contains(node)) {
            parent.children.add(node);
        }

        if (!tree.isLeaf(nodeIdx)) {
            int childCount = tree.getTreeNodeChildCount(nodeIdx);
            for (int i = 0; i < childCount; i++) {
                int childIdx = tree.getTreeNodeChild(nodeIdx, i);
                if (childIdx != -1) {
                    buildTreeStructure(childIdx, nodeMap, node);
                }
            }
        }
    }

    private void calculatePositions(Map<Integer, TreeNode> nodeMap) {
        // Find root (node without parent)
        TreeNode rootNode = null;
        for (TreeNode node : nodeMap.values()) {
            boolean hasParent = false;
            for (TreeNode other : nodeMap.values()) {
                if (other.children.contains(node)) {
                    hasParent = true;
                    break;
                }
            }
            if (!hasParent) {
                rootNode = node;
                break;
            }
        }

        if (rootNode != null) {
            calculateNodePositions(rootNode, 0, 0);
            // Add all nodes to layout
            for (TreeNode node : nodeMap.values()) {
                if (!treeLayout.contains(node)) {
                    treeLayout.add(node);
                }
            }
        }
    }

    private void calculateNodePositions(TreeNode node, int depth, double xOffset) {
        if (node == null) return;

        node.y = depth * verticalSpacing;

        if (node.isLeaf) {
            node.x = xOffset * horizontalSpacing;
            return;
        }

        // Calculate positions for children
        if (!node.children.isEmpty()) {
            double totalWidth = (node.children.size() - 1) * horizontalSpacing;
            double startX = xOffset * horizontalSpacing - totalWidth / 2;

            for (int i = 0; i < node.children.size(); i++) {
                TreeNode child = node.children.get(i);
                calculateNodePositions(child, depth + 1, startX / horizontalSpacing + i);
            }

            // Position internal node at average of children
            double avgX = 0;
            for (TreeNode child : node.children) {
                avgX += child.x;
            }
            node.x = avgX / node.children.size();
        } else {
            node.x = xOffset * horizontalSpacing;
        }
    }

    public void resetView() {
        zoom = 1.0;
        panX = 0;
        panY = 0;
        repaint();
    }

    public void setHorizontalSpacing(int spacing) {
        this.horizontalSpacing = Math.max(30, Math.min(300, spacing));
        updateTree();
    }

    public void setVerticalSpacing(int spacing) {
        this.verticalSpacing = Math.max(30, Math.min(250, spacing));
        updateTree();
    }

    public void setNodeRadius(int radius) {
        this.nodeRadius = Math.max(15, Math.min(60, radius));
        repaint();
    }

    // Inner class to represent tree node for layout
    private static class TreeNode {
        String value;
        boolean isLeaf;
        List<TreeNode> children;
        List<String> keys;
        double x, y;
        int index;

        TreeNode(String value, boolean isLeaf) {
            this.value = value;
            this.isLeaf = isLeaf;
            this.children = new ArrayList<>();
            this.keys = new ArrayList<>();
        }
    }

    // GUI Frame class
    public static class Tree23Frame extends JFrame {
        private Tree23GUI treePanel;
        private JTextField addField, deleteField, sizeField;
        private JTextField horizontalSpacingField, verticalSpacingField, radiusField, textSizeField;
        private JLabel statusLabel;
        private JButton addButton, deleteButton, randomButton, degenerateButton;
        private Tree23 currentTree;

        public Tree23Frame() {
            setTitle("2-3 Tree Visualizer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1400, 800);
            setLayout(new BorderLayout());

            // Initialize current tree
            currentTree = new Tree23();

            // Create tree panel (right side)
            treePanel = new Tree23GUI();
            treePanel.setPreferredSize(new Dimension(1000, 800));
            add(treePanel, BorderLayout.CENTER);

            // Create scrollable control panel (left side)
            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
            controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            controlPanel.setBackground(new Color(45, 45, 45));

            JScrollPane scrollPane = new JScrollPane(controlPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setPreferredSize(new Dimension(450, 800));
            scrollPane.setBackground(new Color(45, 45, 45));
            add(scrollPane, BorderLayout.WEST);

            // Title
            JLabel titleLabel = new JLabel("2-3 Tree Controls");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            controlPanel.add(titleLabel);
            controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Add element panel
            JPanel addPanel = createStyledPanel("Add Element", new Color(60, 60, 60));
            addField = new JTextField(10);
            addField.setFont(new Font("Arial", Font.PLAIN, 14));
            addField.setBackground(Color.WHITE);
            addField.setForeground(Color.BLACK);
            addField.setCaretColor(Color.BLACK);
            addButton = createStyledButton("➕ Add", new Color(46, 204, 113));
            addButton.addActionListener(e -> addElement());
            addPanel.add(addField);
            addPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            addPanel.add(addButton);
            controlPanel.add(addPanel);
            controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Delete element panel
            JPanel deletePanel = createStyledPanel("Delete Element", new Color(60, 60, 60));
            deleteField = new JTextField(10);
            deleteField.setFont(new Font("Arial", Font.PLAIN, 14));
            deleteField.setBackground(Color.WHITE);
            deleteField.setForeground(Color.BLACK);
            deleteField.setCaretColor(Color.BLACK);
            deleteButton = createStyledButton("🗑 Delete", new Color(231, 76, 60));
            deleteButton.addActionListener(e -> deleteElement());
            deletePanel.add(deleteField);
            deletePanel.add(Box.createRigidArea(new Dimension(10, 0)));
            deletePanel.add(deleteButton);
            controlPanel.add(deletePanel);
            controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Generate tree panel
            JPanel generatePanel = createStyledPanel("Generate Tree", new Color(60, 60, 60));
            sizeField = new JTextField(5);
            sizeField.setText("10");
            sizeField.setFont(new Font("Arial", Font.PLAIN, 14));
            sizeField.setBackground(Color.WHITE);
            sizeField.setForeground(Color.BLACK);
            sizeField.setCaretColor(Color.BLACK);
            randomButton = createStyledButton("🎲 Random", new Color(52, 152, 219));
            randomButton.addActionListener(e -> generateRandomTree());

            JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            sizePanel.setOpaque(false);
            JLabel sizeLabel = new JLabel("Size:");
            sizeLabel.setForeground(Color.WHITE);
            sizePanel.add(sizeLabel);
            sizePanel.add(sizeField);
            generatePanel.add(sizePanel);
            generatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.add(randomButton);
            generatePanel.add(buttonPanel);
            controlPanel.add(generatePanel);
            controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // View options panel - FIXED LAYOUT
            JPanel viewPanel = createStyledPanel("View Options", new Color(60, 60, 60));
            viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));

            // Horizontal Spacing
            JPanel hPanel = new JPanel(new BorderLayout(10, 5));
            hPanel.setOpaque(false);
            JLabel hLabel = new JLabel("Horizontal Spacing (30-300):");
            hLabel.setForeground(Color.WHITE);
            horizontalSpacingField = new JTextField();
            horizontalSpacingField.setText(String.valueOf((int) treePanel.horizontalSpacing));
            horizontalSpacingField.setHorizontalAlignment(JTextField.CENTER);
            horizontalSpacingField.setBackground(Color.WHITE);
            horizontalSpacingField.setForeground(Color.BLACK);
            horizontalSpacingField.setCaretColor(Color.BLACK);
            horizontalSpacingField.setFont(new Font("Arial", Font.PLAIN, 14));
            horizontalSpacingField.setPreferredSize(new Dimension(100, 30));
            horizontalSpacingField.setMinimumSize(new Dimension(80, 30));
            horizontalSpacingField.addActionListener(e -> {
                try {
                    int value = Integer.parseInt(horizontalSpacingField.getText());
                    value = Math.max(30, Math.min(300, value));
                    horizontalSpacingField.setText(String.valueOf(value));
                    treePanel.setHorizontalSpacing(value);
                    updateStatus("Horizontal spacing: " + value);
                } catch (NumberFormatException ex) {
                    updateStatus("Invalid number for horizontal spacing");
                    horizontalSpacingField.setText(String.valueOf((int) treePanel.horizontalSpacing));
                }
            });
            hPanel.add(hLabel, BorderLayout.CENTER);
            hPanel.add(horizontalSpacingField, BorderLayout.EAST);
            viewPanel.add(hPanel);
            viewPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Vertical Spacing
            JPanel vPanel = new JPanel(new BorderLayout(10, 5));
            vPanel.setOpaque(false);
            JLabel vLabel = new JLabel("Vertical Spacing (30-250):");
            vLabel.setForeground(Color.WHITE);
            verticalSpacingField = new JTextField();
            verticalSpacingField.setText(String.valueOf((int) treePanel.verticalSpacing));
            verticalSpacingField.setHorizontalAlignment(JTextField.CENTER);
            verticalSpacingField.setBackground(Color.WHITE);
            verticalSpacingField.setForeground(Color.BLACK);
            verticalSpacingField.setCaretColor(Color.BLACK);
            verticalSpacingField.setFont(new Font("Arial", Font.PLAIN, 14));
            verticalSpacingField.setPreferredSize(new Dimension(100, 30));
            verticalSpacingField.setMinimumSize(new Dimension(80, 30));
            verticalSpacingField.addActionListener(e -> {
                try {
                    int value = Integer.parseInt(verticalSpacingField.getText());
                    value = Math.max(30, Math.min(250, value));
                    verticalSpacingField.setText(String.valueOf(value));
                    treePanel.setVerticalSpacing(value);
                    updateStatus("Vertical spacing: " + value);
                } catch (NumberFormatException ex) {
                    updateStatus("Invalid number for vertical spacing");
                    verticalSpacingField.setText(String.valueOf((int) treePanel.verticalSpacing));
                }
            });
            vPanel.add(vLabel, BorderLayout.CENTER);
            vPanel.add(verticalSpacingField, BorderLayout.EAST);
            viewPanel.add(vPanel);
            viewPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Node Radius
            JPanel rPanel = new JPanel(new BorderLayout(10, 5));
            rPanel.setOpaque(false);
            JLabel rLabel = new JLabel("Node Radius (15-60):");
            rLabel.setForeground(Color.WHITE);
            radiusField = new JTextField();
            radiusField.setText(String.valueOf(treePanel.nodeRadius));
            radiusField.setHorizontalAlignment(JTextField.CENTER);
            radiusField.setBackground(Color.WHITE);
            radiusField.setForeground(Color.BLACK);
            radiusField.setCaretColor(Color.BLACK);
            radiusField.setFont(new Font("Arial", Font.PLAIN, 14));
            radiusField.setPreferredSize(new Dimension(100, 30));
            radiusField.setMinimumSize(new Dimension(80, 30));
            radiusField.addActionListener(e -> {
                try {
                    int value = Integer.parseInt(radiusField.getText());
                    value = Math.max(15, Math.min(60, value));
                    radiusField.setText(String.valueOf(value));
                    treePanel.setNodeRadius(value);
                    updateStatus("Node radius: " + value);
                } catch (NumberFormatException ex) {
                    updateStatus("Invalid number for node radius");
                    radiusField.setText(String.valueOf(treePanel.nodeRadius));
                }
            });
            rPanel.add(rLabel, BorderLayout.CENTER);
            rPanel.add(radiusField, BorderLayout.EAST);
            viewPanel.add(rPanel);
            viewPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Text Size
            JPanel tPanel = new JPanel(new BorderLayout(10, 5));
            tPanel.setOpaque(false);
            JLabel tLabel = new JLabel("Text Size (8-24):");
            tLabel.setForeground(Color.WHITE);
            textSizeField = new JTextField();
            textSizeField.setText("12");
            textSizeField.setHorizontalAlignment(JTextField.CENTER);
            textSizeField.setBackground(Color.WHITE);
            textSizeField.setForeground(Color.BLACK);
            textSizeField.setCaretColor(Color.BLACK);
            textSizeField.setFont(new Font("Arial", Font.PLAIN, 14));
            textSizeField.setPreferredSize(new Dimension(100, 30));
            textSizeField.setMinimumSize(new Dimension(80, 30));
            textSizeField.addActionListener(e -> {
                try {
                    int value = Integer.parseInt(textSizeField.getText());
                    value = Math.max(8, Math.min(24, value));
                    textSizeField.setText(String.valueOf(value));
                    treePanel.setFontSize(value);
                    updateStatus("Text size: " + value);
                } catch (NumberFormatException ex) {
                    updateStatus("Invalid number for text size");
                    textSizeField.setText("12");
                }
            });
            tPanel.add(tLabel, BorderLayout.CENTER);
            tPanel.add(textSizeField, BorderLayout.EAST);
            viewPanel.add(tPanel);
            viewPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Reset View Button
            JButton resetViewButton = createStyledButton("🔄 Reset View", new Color(149, 165, 166));
            resetViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            resetViewButton.addActionListener(e -> {
                treePanel.resetView();
                updateStatus("View reset");
            });
            viewPanel.add(resetViewButton);

            controlPanel.add(viewPanel);
            controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Info panel
            JPanel infoPanel = createStyledPanel("Instructions", new Color(60, 60, 60));
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JLabel info1 = createInfoLabel("• Left-click and drag to pan");
            JLabel info2 = createInfoLabel("• Mouse wheel to zoom in/out");
            JLabel info3 = createInfoLabel("• Press 'R' to reset view");
            JLabel info4 = createInfoLabel("• Green: Internal nodes (show keys)");
            JLabel info5 = createInfoLabel("• Blue: Leaf nodes (show values)");
            infoPanel.add(info1);
            infoPanel.add(info2);
            infoPanel.add(info3);
            infoPanel.add(info4);
            infoPanel.add(info5);
            controlPanel.add(infoPanel);

            controlPanel.add(Box.createVerticalGlue());

            // Status label
            statusLabel = new JLabel("✓ Ready");
            statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            controlPanel.add(statusLabel);

            // Initial visualization
            treePanel.visualizeTree(currentTree);

            // Keyboard shortcuts
            InputMap inputMap = treePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = treePanel.getActionMap();

            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "zoomIn");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), "zoomIn");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zoomOut");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "resetView");

            actionMap.put("zoomIn", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    treePanel.zoom *= 1.1;
                    treePanel.zoom = Math.min(5.0, treePanel.zoom);
                    treePanel.repaint();
                    updateStatus("Zoomed in");
                }
            });

            actionMap.put("zoomOut", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    treePanel.zoom /= 1.1;
                    treePanel.zoom = Math.max(0.1, treePanel.zoom);
                    treePanel.repaint();
                    updateStatus("Zoomed out");
                }
            });

            actionMap.put("resetView", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    treePanel.resetView();
                    updateStatus("View reset");
                }
            });

            // Enable/disable buttons during operations
            setButtonsEnabled(true);
        }

        private JLabel createInfoLabel(String text) {
            JLabel label = new JLabel(text);
            label.setForeground(Color.WHITE);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            return label;
        }

        private JPanel createStyledPanel(String title, Color bgColor) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                            title,
                            TitledBorder.LEFT,
                            TitledBorder.TOP,
                            new Font("Arial", Font.BOLD, 14),
                            Color.WHITE
                    ),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            panel.setBackground(bgColor);
            panel.setMaximumSize(new Dimension(420, 300));
            return panel;
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(color);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }

        private void setButtonsEnabled(boolean enabled) {
            addButton.setEnabled(enabled);
            deleteButton.setEnabled(enabled);
            randomButton.setEnabled(enabled);
            addField.setEnabled(enabled);
            deleteField.setEnabled(enabled);
            sizeField.setEnabled(enabled);
        }

        private void updateStatus(String message) {
            statusLabel.setText("✓ " + message);
            // Reset status after 2 seconds
            Timer timer = new Timer(2000, e -> statusLabel.setText("✓ Ready"));
            timer.setRepeats(false);
            timer.start();
        }

        private void refreshVisualization() {
            treePanel.visualizeTree(currentTree);
        }

        private void addElement() {
            try {
                double value = Double.parseDouble(addField.getText());
                TreeElement elem = new TreeElement() {
                    @Override
                    public double ExtractValue() {
                        return value;
                    }

                    @Override
                    public boolean Equal(TreeElement other) {
                        return Math.abs(this.ExtractValue() - other.ExtractValue()) < 0.0001;
                    }
                };

                setButtonsEnabled(false);
                SwingUtilities.invokeLater(() -> {
                    currentTree.insert(elem);
                    refreshVisualization();
                    setButtonsEnabled(true);
                    updateStatus("Added element: " + value);
                    addField.setText("");
                });
            } catch (NumberFormatException ex) {
                updateStatus("Error: Invalid number format");
            }
        }

        private void deleteElement() {
            try {
                double value = Double.parseDouble(deleteField.getText());
                TreeElement elem = new TreeElement() {
                    @Override
                    public double ExtractValue() {
                        return value;
                    }

                    @Override
                    public boolean Equal(TreeElement other) {
                        return Math.abs(this.ExtractValue() - other.ExtractValue()) < 0.0001;
                    }
                };

                setButtonsEnabled(false);
                SwingUtilities.invokeLater(() -> {
                    currentTree.delete(elem);
                    refreshVisualization();
                    setButtonsEnabled(true);
                    updateStatus("Deleted element: " + value);
                    deleteField.setText("");
                });
            } catch (NumberFormatException ex) {
                updateStatus("Error: Invalid number format");
            }
        }

        private void generateRandomTree() {
            int size = Integer.parseInt(sizeField.getText());

            if (size <= 0 || size > 1000) {
                updateStatus("Error: Size must be between 1 and 1000");
                return;
            }

            setButtonsEnabled(false);

            currentTree = RandomGenerator.generateRandomTree(size);
            refreshVisualization();
            setButtonsEnabled(true);
            updateStatus("Generated random tree with " + size + " elements");
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Tree23Frame frame = new Tree23Frame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }
    }
}