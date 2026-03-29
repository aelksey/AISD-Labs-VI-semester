#include <QApplication>
#include <QMainWindow>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QPushButton>
#include <QTextEdit>
#include <QLineEdit>
#include <QLabel>
#include <QGroupBox>
#include <QSpinBox>
#include <QMessageBox>
#include <QFont>
#include <QPainter>
#include <QWidget>
#include <QResizeEvent>
#include <QSplitter>
#include <QScrollArea>
#include <QTimer>
#include <QTime>
#include <QScrollBar>
#include <QPolygon>
#include <cmath>
#include <vector>
#include <random>
#include <algorithm>
#include <set>
#include <queue>
#include <sstream>
#include <functional>
#include <map>

#include "../include/bst.hpp"

// Custom widget for tree visualization with zoom and pan support
class TreeWidget : public QWidget {
    Q_OBJECT

private:
    BST<int, int>* tree;
    BST<int, int>::Iterator* currentIterator;  // Указатель на текущий итератор
    int nodeRadius;
    int verticalSpacing;
    int minHorizontalOffset;
    double m_scale;
    int translateX;
    int translateY;
    QPoint lastPanPos;
    bool panning;
    
    struct NodePosition {
        int x, y;
        int key;
        int data;
        bool isCurrent;  // Флаг для текущего элемента итератора
    };
    
    std::vector<NodePosition> m_positions;  // Сохраняем позиции для перерисовки
    
    // Рекурсивный расчет позиций узлов
    void calculatePositions(const BST<int, int>::VisualNode* node, 
                           std::vector<NodePosition>& positions, 
                           int x, int y, int level, int offset) {
        if (node == nullptr) return;
        
        NodePosition pos;
        pos.x = x;
        pos.y = y;
        pos.key = node->key;
        pos.data = node->data;
        pos.isCurrent = false;
        
        // Проверяем, является ли этот узел текущим для итератора
        if (currentIterator && *currentIterator != tree->end()) {
            try {
                if (currentIterator->getKey() == node->key) {
                    pos.isCurrent = true;
                }
            } catch (...) {}
        }
        
        positions.push_back(pos);
        
        int levelMultiplier = std::max(1, level + 1);
        int childOffset = offset / 2;
        
        if (childOffset < minHorizontalOffset * levelMultiplier) {
            childOffset = minHorizontalOffset * levelMultiplier;
        }
        
        calculatePositions(node->left, positions, x - childOffset, 
                          y + verticalSpacing, level + 1, childOffset);
        calculatePositions(node->right, positions, x + childOffset, 
                          y + verticalSpacing, level + 1, childOffset);
    }
    
    // Отрисовка стрелки, указывающей на узел
    void drawArrow(QPainter& painter, int x, int y, int radius) {
        // Сохраняем текущее состояние
        painter.save();
        
        // Создаем кисть для стрелки
        QPen arrowPen(Qt::red, 3);
        arrowPen.setCapStyle(Qt::RoundCap);
        arrowPen.setJoinStyle(Qt::RoundJoin);
        painter.setPen(arrowPen);
        painter.setBrush(QBrush(Qt::red));
        
        // Рассчитываем позицию стрелки (над узлом)
        int arrowX = x;
        int arrowY = y - radius - 15 * m_scale;
        
        // Размер стрелки зависит от масштаба
        int arrowSize = 10 * m_scale;
        if (arrowSize < 8) arrowSize = 8;
        if (arrowSize > 20) arrowSize = 20;
        
        // Рисуем линию от стрелки к узлу
        painter.drawLine(arrowX, arrowY + arrowSize, arrowX, y - radius);
        
        // Рисуем треугольник (стрелку)
        QPolygon arrow;
        arrow << QPoint(arrowX, arrowY)
              << QPoint(arrowX - arrowSize/2, arrowY + arrowSize)
              << QPoint(arrowX + arrowSize/2, arrowY + arrowSize);
        painter.drawPolygon(arrow);
        
        // Добавляем пульсирующий эффект (светящийся круг)
        painter.setPen(QPen(Qt::red, 2));
        painter.setBrush(QBrush(Qt::NoBrush));
        for (int i = 1; i <= 2; i++) {
            int glowRadius = radius + i * 3 * m_scale;
            painter.drawEllipse(x - glowRadius, y - glowRadius,
                               glowRadius * 2, glowRadius * 2);
        }
        
        painter.restore();
    }
    
    void drawTree(QPainter& painter, const BST<int, int>::VisualNode* node, 
                  int x, int y, int offset, int level) {
        if (node == nullptr) return;
        
        // Применяем масштаб и трансляцию
        int scaledX = (x + translateX) * m_scale;
        int scaledY = (y + translateY) * m_scale;
        int scaledRadius = nodeRadius * m_scale;
        
        if (scaledRadius < 8) scaledRadius = 8;
        
        // Проверяем, является ли этот узел текущим
        bool isCurrent = false;
        if (currentIterator && *currentIterator != tree->end()) {
            try {
                if (currentIterator->getKey() == node->key) {
                    isCurrent = true;
                }
            } catch (...) {}
        }
        
        // Размер шрифта в зависимости от масштаба
        int fontSize = std::max(8, int(12 * m_scale));
        QFont font = painter.font();
        font.setPointSize(fontSize);
        painter.setFont(font);
        
        // Выбор цвета узла
        if (isCurrent) {
            painter.setBrush(QBrush(QColor(255, 200, 200)));  // Светло-красный для текущего
            painter.setPen(QPen(Qt::red, 3));
        } else {
            painter.setBrush(QBrush(Qt::lightGray));
            painter.setPen(QPen(Qt::black, 2));
        }
        
        // Отрисовка узла
        painter.drawEllipse(scaledX - scaledRadius, scaledY - scaledRadius,
                           scaledRadius * 2, scaledRadius * 2);
        
        // Отображение ключа и данных
        QString nodeText = QString("%1\n%2").arg(node->key).arg(node->data);
        painter.setPen(QPen(Qt::black));
        
        QRect textRect(scaledX - scaledRadius, scaledY - scaledRadius,
                       scaledRadius * 2, scaledRadius * 2);
        painter.drawText(textRect, Qt::AlignCenter, nodeText);
        
        // Отрисовка стрелки для текущего узла
        if (isCurrent) {
            drawArrow(painter, scaledX, scaledY, scaledRadius);
        }
        
        int levelMultiplier = std::max(1, level + 1);
        int childOffset = offset / 2;
        
        if (childOffset < minHorizontalOffset * levelMultiplier) {
            childOffset = minHorizontalOffset * levelMultiplier;
        }
        
        if (node->left) {
            int childX = (x - childOffset + translateX) * m_scale;
            int childY = (y + verticalSpacing + translateY) * m_scale;
            painter.setPen(QPen(Qt::black, 1.5));
            painter.drawLine(scaledX, scaledY + scaledRadius, 
                           childX, childY - scaledRadius);
            drawTree(painter, node->left, x - childOffset, 
                    y + verticalSpacing, childOffset, level + 1);
        }
        
        if (node->right) {
            int childX = (x + childOffset + translateX) * m_scale;
            int childY = (y + verticalSpacing + translateY) * m_scale;
            painter.setPen(QPen(Qt::black, 1.5));
            painter.drawLine(scaledX, scaledY + scaledRadius, 
                           childX, childY - scaledRadius);
            drawTree(painter, node->right, x + childOffset, 
                    y + verticalSpacing, childOffset, level + 1);
        }
    }
    
protected:
    void paintEvent(QPaintEvent* /*event*/) override {
        QPainter painter(this);
        painter.setRenderHint(QPainter::Antialiasing);
        painter.fillRect(rect(), Qt::white);
        
        if (tree == nullptr || tree->empty()) {
            painter.drawText(rect(), Qt::AlignCenter, 
                           "Tree is empty\n\n"
                           "Use controls to generate or insert nodes\n"
                           "Mouse wheel to zoom\n"
                           "Drag to pan");
            return;
        }
        
        BST<int, int>::VisualNode* visualRoot = tree->getVisualRoot();
        if (visualRoot == nullptr) {
            painter.drawText(rect(), Qt::AlignCenter, "Tree is empty");
            return;
        }
        
        int startX = width() / 2 / m_scale - translateX;
        int startY = 80 / m_scale - translateY;
        int initialOffset = width() / 3 / m_scale;
        
        drawTree(painter, visualRoot, startX, startY, initialOffset, 0);
        
        delete visualRoot;
    }
    
    void wheelEvent(QWheelEvent* event) override {
        double zoomFactor = 1.1;
        if (event->angleDelta().y() > 0) {
            m_scale *= zoomFactor;
        } else {
            m_scale /= zoomFactor;
        }
        if (m_scale < 0.3) m_scale = 0.3;
        if (m_scale > 3.0) m_scale = 3.0;
        update();
    }
    
    void mousePressEvent(QMouseEvent* event) override {
        if (event->button() == Qt::MiddleButton || event->button() == Qt::LeftButton) {
            panning = true;
            lastPanPos = event->pos();
            setCursor(Qt::ClosedHandCursor);
        }
    }
    
    void mouseMoveEvent(QMouseEvent* event) override {
        if (panning) {
            QPoint delta = event->pos() - lastPanPos;
            translateX += delta.x() / m_scale;
            translateY += delta.y() / m_scale;
            lastPanPos = event->pos();
            update();
        }
    }
    
    void mouseReleaseEvent(QMouseEvent* event) override {
        if (event->button() == Qt::MiddleButton || event->button() == Qt::LeftButton) {
            panning = false;
            setCursor(Qt::ArrowCursor);
        }
    }
    
public:
    TreeWidget(QWidget* parent = nullptr) 
        : QWidget(parent), tree(nullptr), currentIterator(nullptr),
          nodeRadius(30), verticalSpacing(100), minHorizontalOffset(70),
          m_scale(1.0), translateX(0), translateY(0), panning(false) {
        setMinimumSize(600, 400);
        setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
        setMouseTracking(true);
        
        setStyleSheet("TreeWidget { background-color: white; }");
    }
    
    void setTree(BST<int, int>* t) {
        tree = t;
        resetView();
        update();
    }
    
    void setIterator(BST<int, int>::Iterator* iter) {
        currentIterator = iter;
        update();  // Перерисовываем для отображения стрелки
    }
    
    void resetView() {
        m_scale = 1.0;
        translateX = 0;
        translateY = 0;
        update();
    }
    
    void zoomIn() {
        m_scale *= 1.2;
        if (m_scale > 3.0) m_scale = 3.0;
        update();
    }
    
    void zoomOut() {
        m_scale /= 1.2;
        if (m_scale < 0.3) m_scale = 0.3;
        update();
    }
    
    void fitToView() {
        m_scale = 1.0;
        translateX = 0;
        translateY = 0;
        update();
    }
    
    double getScale() const {
        return m_scale;
    }
    
    void resizeEvent(QResizeEvent* event) override {
        QWidget::resizeEvent(event);
        update();
    }
    
    void setVerticalSpacing(int spacing) {
        verticalSpacing = spacing;
        update();
    }
    
    void setNodeRadius(int radius) {
        nodeRadius = radius;
        update();
    }
    
    void setMinHorizontalOffset(int offset) {
        minHorizontalOffset = offset;
        update();
    }
};

// MainWindow class definition
class MainWindow : public QMainWindow {
    Q_OBJECT

private:
    BST<int, int> tree;
    BST<int, int>::Iterator currentIterator;
    TreeWidget* treeWidget;
    QTextEdit* logWindow;
    QLineEdit* keyInput;
    QLineEdit* dataInput;
    QSpinBox* depthSpinBox;
    QLabel* iteratorLabel;
    QLabel* treeInfoLabel;
    QLabel* iteratorValueLabel;
    
    QSpinBox* verticalSpacingSpin;
    QSpinBox* nodeRadiusSpin;
    QSpinBox* horizontalOffsetSpin;
    QLabel* scaleLabel;
    
    void logMessage(const QString& msg) {
        QString timestamp = QTime::currentTime().toString("hh:mm:ss.zzz");
        logWindow->append(QString("[%1] %2").arg(timestamp).arg(msg));
        if (logWindow->verticalScrollBar()) {
            logWindow->verticalScrollBar()->setValue(logWindow->verticalScrollBar()->maximum());
        }
    }
    
    void generateTreeWithDepth(int depth, bool randomOrder) {
        tree.clear();
        
        if (depth <= 0) return;
        
        int maxNodes = (1 << depth) - 1;
        std::vector<int> keys;
        
        std::function<void(int, int, int, int)> fillLevelOrder = 
            [&](int index, int minVal, int maxVal, int currentDepth) {
            if (currentDepth > depth) return;
            if (index >= maxNodes) return;
            
            int key = (minVal + maxVal) / 2;
            keys.push_back(key);
            
            fillLevelOrder(index * 2 + 1, minVal, key - 1, currentDepth + 1);
            fillLevelOrder(index * 2 + 2, key + 1, maxVal, currentDepth + 1);
        };
        
        fillLevelOrder(0, 1, 10000, 1);
        
        if (randomOrder) {
            std::random_device rd;
            std::mt19937 gen(rd());
            std::shuffle(keys.begin(), keys.end(), gen);
        }
        
        for (int key : keys) {
            tree.insert(key, key * 10);
        }
        
        // Сбрасываем итератор
        currentIterator = tree.end();
        
        logMessage(QString("Generated %1 with %2 nodes, depth=%3, height=%4")
            .arg(randomOrder ? "random tree" : "perfect tree")
            .arg(tree.size())
            .arg(depth)
            .arg(tree.height()));
        
        updateDisplay();
    }
    
    void updateDisplay() {
        treeWidget->setTree(&tree);
        updateTreeInfo();
        updateIteratorLabel();
        treeWidget->setIterator(&currentIterator);
    }
    
    void updateTreeInfo() {
        QString info = QString("Size: %1 | Height: %2 | Balanced: %3")
            .arg(tree.size())
            .arg(tree.height())
            .arg(tree.isBalanced() ? "Yes" : "No");
        treeInfoLabel->setText(info);
    }
    
    void updateIteratorLabel() {
        try {
            if (currentIterator != tree.end()) {
                QString info = QString("Iterator: key=%1, data=%2")
                    .arg(currentIterator.getKey())
                    .arg(*currentIterator);
                iteratorLabel->setText(info);
                iteratorValueLabel->setText(QString("📍 Points to: %1 → %2")
                    .arg(currentIterator.getKey())
                    .arg(*currentIterator));
                iteratorValueLabel->setStyleSheet("QLabel { color: #d32f2f; font-weight: bold; background-color: #ffebee; padding: 5px; border: 1px solid #ffcdd2; border-radius: 5px; }");
            } else {
                iteratorLabel->setText("Iterator: end (not set)");
                iteratorValueLabel->setText("🔴 Iterator not pointing to any node");
                iteratorValueLabel->setStyleSheet("QLabel { color: #666; background-color: #f5f5f5; padding: 5px; border: 1px solid #ddd; border-radius: 5px; }");
            }
        } catch (const std::exception& e) {
            iteratorLabel->setText("Iterator: invalid");
            iteratorValueLabel->setText("⚠️ Iterator is invalid");
            iteratorValueLabel->setStyleSheet("QLabel { color: #f57c00; background-color: #fff3e0; padding: 5px; border: 1px solid #ffe0b2; border-radius: 5px; }");
        }
    }
    
    void updateScaleLabel() {
        scaleLabel->setText(QString("Zoom: %1%").arg(int(treeWidget->getScale() * 100)));
    }
    
private slots:
    void onInsert();
    void onRemove();
    void onSearch();
    void onGenerateRandom();
    void onGeneratePerfect();
    void onGenerateDegenerate();
    void onClear();
    void onPrintTree();
    void onIteratorBegin();
    void onIteratorEnd();
    void onIteratorIncrement();
    void onIteratorDecrement();
    void onIteratorGet();
    void onZoomIn();
    void onZoomOut();
    void onFitToView();
    void onResetView();
    
    void onVerticalSpacingChanged(int value) {
        treeWidget->setVerticalSpacing(value);
        logMessage(QString("Vertical spacing changed to %1 px").arg(value));
    }
    
    void onNodeRadiusChanged(int value) {
        treeWidget->setNodeRadius(value);
        logMessage(QString("Node radius changed to %1 px").arg(value));
    }
    
    void onHorizontalOffsetChanged(int value) {
        treeWidget->setMinHorizontalOffset(value);
        logMessage(QString("Horizontal offset changed to %1 px").arg(value));
    }

public:
    MainWindow(QWidget* parent = nullptr);
};

// Implementation of MainWindow methods
MainWindow::MainWindow(QWidget* parent) : QMainWindow(parent) {
    setWindowTitle("Программа визуализации BST");
    setMinimumSize(1200, 800);
    
    // Центральный виджет с разделителями
    QWidget* centralWidget = new QWidget(this);
    setCentralWidget(centralWidget);
    
    QHBoxLayout* mainLayout = new QHBoxLayout(centralWidget);
    mainLayout->setContentsMargins(5, 5, 5, 5);
    mainLayout->setSpacing(5);
    
    // Создаем горизонтальный сплиттер для левой панели и правой области
    QSplitter* mainSplitter = new QSplitter(Qt::Horizontal);
    mainLayout->addWidget(mainSplitter);
    
    // Левая панель управления
    QWidget* leftPanel = new QWidget();
    leftPanel->setMinimumWidth(450);
    leftPanel->setMaximumWidth(550);
    QVBoxLayout* leftLayout = new QVBoxLayout(leftPanel);
    leftLayout->setSpacing(8);
    
    // Tree operations group
    QGroupBox* opsGroup = new QGroupBox("Операции над деревом");
    QVBoxLayout* opsLayout = new QVBoxLayout();
    opsLayout->setSpacing(5);
    
    QHBoxLayout* inputLayout = new QHBoxLayout();
    inputLayout->addWidget(new QLabel("Key:"));
    keyInput = new QLineEdit();
    keyInput->setFixedHeight(30);
    inputLayout->addWidget(keyInput);
    inputLayout->addWidget(new QLabel("Data:"));
    dataInput = new QLineEdit();
    dataInput->setFixedHeight(30);
    inputLayout->addWidget(dataInput);
    opsLayout->addLayout(inputLayout);
    
    QPushButton* insertBtn = new QPushButton("Insert");
    insertBtn->setFixedHeight(35);
    connect(insertBtn, &QPushButton::clicked, this, &MainWindow::onInsert);
    opsLayout->addWidget(insertBtn);
    
    QPushButton* removeBtn = new QPushButton("Remove");
    removeBtn->setFixedHeight(35);
    connect(removeBtn, &QPushButton::clicked, this, &MainWindow::onRemove);
    opsLayout->addWidget(removeBtn);
    
    QPushButton* searchBtn = new QPushButton("Search");
    searchBtn->setFixedHeight(35);
    connect(searchBtn, &QPushButton::clicked, this, &MainWindow::onSearch);
    opsLayout->addWidget(searchBtn);
    
    opsGroup->setLayout(opsLayout);
    leftLayout->addWidget(opsGroup);
    
    // Tree generation group
    QGroupBox* genGroup = new QGroupBox("Генерация дерева");
    QVBoxLayout* genLayout = new QVBoxLayout();
    genLayout->setSpacing(5);
    
    QHBoxLayout* depthLayout = new QHBoxLayout();
    depthLayout->addWidget(new QLabel("Размер:"));
    depthSpinBox = new QSpinBox();
    depthSpinBox->setRange(1, 10);
    depthSpinBox->setValue(4);
    depthSpinBox->setFixedHeight(30);
    depthLayout->addWidget(depthSpinBox);
    depthLayout->addWidget(new QLabel("(max 10)"));
    depthLayout->addStretch();
    genLayout->addLayout(depthLayout);
    
    QPushButton* randomBtn = new QPushButton("Создать случайное дерево");
    randomBtn->setFixedHeight(35);
    randomBtn->setStyleSheet("QPushButton { background-color: #4CAF50; color: white; font-weight: bold; }");
    connect(randomBtn, &QPushButton::clicked, this, &MainWindow::onGenerateRandom);
    genLayout->addWidget(randomBtn);
    
    QPushButton* perfectBtn = new QPushButton("Создать сбалансированное дерево");
    perfectBtn->setFixedHeight(35);
    connect(perfectBtn, &QPushButton::clicked, this, &MainWindow::onGeneratePerfect);
    genLayout->addWidget(perfectBtn);
    
    QPushButton* degenerateBtn = new QPushButton("Создать вырожденное дерево");
    degenerateBtn->setFixedHeight(35);
    connect(degenerateBtn, &QPushButton::clicked, this, &MainWindow::onGenerateDegenerate);
    genLayout->addWidget(degenerateBtn);
    
    QPushButton* clearBtn = new QPushButton("Clear Tree");
    clearBtn->setFixedHeight(35);
    connect(clearBtn, &QPushButton::clicked, this, &MainWindow::onClear);
    genLayout->addWidget(clearBtn);
    
    genGroup->setLayout(genLayout);
    leftLayout->addWidget(genGroup);
    
    // Visualization settings group
    QGroupBox* vizGroup = new QGroupBox("Настройки отображения");
    QVBoxLayout* vizLayout = new QVBoxLayout();
    vizLayout->setSpacing(5);
    
    QHBoxLayout* verticalLayout = new QHBoxLayout();
    verticalLayout->addWidget(new QLabel("Vertical:"));
    verticalSpacingSpin = new QSpinBox();
    verticalSpacingSpin->setRange(50, 200);
    verticalSpacingSpin->setValue(100);
    verticalSpacingSpin->setSuffix(" px");
    verticalSpacingSpin->setFixedHeight(30);
    connect(verticalSpacingSpin, QOverload<int>::of(&QSpinBox::valueChanged), 
            this, &MainWindow::onVerticalSpacingChanged);
    verticalLayout->addWidget(verticalSpacingSpin);
    vizLayout->addLayout(verticalLayout);
    
    QHBoxLayout* radiusLayout = new QHBoxLayout();
    radiusLayout->addWidget(new QLabel("Радиус ноды:"));
    nodeRadiusSpin = new QSpinBox();
    nodeRadiusSpin->setRange(20, 50);
    nodeRadiusSpin->setValue(30);
    nodeRadiusSpin->setSuffix(" px");
    nodeRadiusSpin->setFixedHeight(30);
    connect(nodeRadiusSpin, QOverload<int>::of(&QSpinBox::valueChanged), 
            this, &MainWindow::onNodeRadiusChanged);
    radiusLayout->addWidget(nodeRadiusSpin);
    vizLayout->addLayout(radiusLayout);
    
    QHBoxLayout* offsetLayout = new QHBoxLayout();
    offsetLayout->addWidget(new QLabel("Отступ Horizontal:"));
    horizontalOffsetSpin = new QSpinBox();
    horizontalOffsetSpin->setRange(40, 150);
    horizontalOffsetSpin->setValue(70);
    horizontalOffsetSpin->setSuffix(" px");
    horizontalOffsetSpin->setFixedHeight(30);
    connect(horizontalOffsetSpin, QOverload<int>::of(&QSpinBox::valueChanged), 
            this, &MainWindow::onHorizontalOffsetChanged);
    offsetLayout->addWidget(horizontalOffsetSpin);
    vizLayout->addLayout(offsetLayout);
    
    // Zoom controls
    QHBoxLayout* zoomLayout = new QHBoxLayout();
    QPushButton* zoomInBtn = new QPushButton("Zoom In (+)");
    zoomInBtn->setFixedHeight(30);
    connect(zoomInBtn, &QPushButton::clicked, this, &MainWindow::onZoomIn);
    zoomLayout->addWidget(zoomInBtn);
    
    QPushButton* zoomOutBtn = new QPushButton("Zoom Out (-)");
    zoomOutBtn->setFixedHeight(30);
    connect(zoomOutBtn, &QPushButton::clicked, this, &MainWindow::onZoomOut);
    zoomLayout->addWidget(zoomOutBtn);
    
    QPushButton* fitBtn = new QPushButton("Fit to View");
    fitBtn->setFixedHeight(30);
    connect(fitBtn, &QPushButton::clicked, this, &MainWindow::onFitToView);
    zoomLayout->addWidget(fitBtn);
    
    QPushButton* resetViewBtn = new QPushButton("Reset View");
    resetViewBtn->setFixedHeight(30);
    connect(resetViewBtn, &QPushButton::clicked, this, &MainWindow::onResetView);
    zoomLayout->addWidget(resetViewBtn);
    
    scaleLabel = new QLabel("Zoom: 100%");
    scaleLabel->setAlignment(Qt::AlignCenter);
    scaleLabel->setFixedHeight(30);
    zoomLayout->addWidget(scaleLabel);
    
    vizLayout->addLayout(zoomLayout);
    
    vizGroup->setLayout(vizLayout);
    leftLayout->addWidget(vizGroup);
    
    // Iterator group with enhanced display
    QGroupBox* iterGroup = new QGroupBox("Операции над итератором");
    QVBoxLayout* iterLayout = new QVBoxLayout();
    iterLayout->setSpacing(5);
    
    iteratorLabel = new QLabel("Iterator: not set");
    iteratorLabel->setWordWrap(true);
    iteratorLabel->setFixedHeight(40);
    iteratorLabel->setStyleSheet("QLabel { background-color: #f0f0f0; padding: 5px; border: 1px solid #ccc; font-family: monospace; }");
    iterLayout->addWidget(iteratorLabel);
    
    iteratorValueLabel = new QLabel("🔴 Iterator not pointing to any node");
    iteratorValueLabel->setWordWrap(true);
    iteratorValueLabel->setFixedHeight(50);
    iteratorValueLabel->setAlignment(Qt::AlignCenter);
    iteratorValueLabel->setStyleSheet("QLabel { background-color: #f5f5f5; padding: 8px; border: 1px solid #ddd; border-radius: 5px; font-weight: bold; }");
    iterLayout->addWidget(iteratorValueLabel);
    
    QGridLayout* iterButtonsLayout = new QGridLayout();
    iterButtonsLayout->setSpacing(5);
    
    QPushButton* iterBeginBtn = new QPushButton("begin() ←");
    iterBeginBtn->setFixedHeight(35);
    iterBeginBtn->setToolTip("Move iterator to the smallest key (first element)");
    connect(iterBeginBtn, &QPushButton::clicked, this, &MainWindow::onIteratorBegin);
    iterButtonsLayout->addWidget(iterBeginBtn, 0, 0);
    
    QPushButton* iterEndBtn = new QPushButton("end() →");
    iterEndBtn->setFixedHeight(35);
    iterEndBtn->setToolTip("Move iterator to end (not pointing to any element)");
    connect(iterEndBtn, &QPushButton::clicked, this, &MainWindow::onIteratorEnd);
    iterButtonsLayout->addWidget(iterEndBtn, 0, 1);
    
    QPushButton* iterIncBtn = new QPushButton("++ (next)");
    iterIncBtn->setFixedHeight(35);
    iterIncBtn->setToolTip("Move iterator to the next element (inorder successor)");
    connect(iterIncBtn, &QPushButton::clicked, this, &MainWindow::onIteratorIncrement);
    iterButtonsLayout->addWidget(iterIncBtn, 1, 0);
    
    QPushButton* iterDecBtn = new QPushButton("-- (previous)");
    iterDecBtn->setFixedHeight(35);
    iterDecBtn->setToolTip("Move iterator to the previous element (inorder predecessor)");
    connect(iterDecBtn, &QPushButton::clicked, this, &MainWindow::onIteratorDecrement);
    iterButtonsLayout->addWidget(iterDecBtn, 1, 1);
    
    QPushButton* iterGetBtn = new QPushButton("Get current value");
    iterGetBtn->setFixedHeight(35);
    iterGetBtn->setToolTip("Display the current iterator's key and data");
    connect(iterGetBtn, &QPushButton::clicked, this, &MainWindow::onIteratorGet);
    iterButtonsLayout->addWidget(iterGetBtn, 2, 0, 1, 2);
    
    iterLayout->addLayout(iterButtonsLayout);
    iterGroup->setLayout(iterLayout);
    leftLayout->addWidget(iterGroup);
    
    // Tree info display
    QGroupBox* infoGroup = new QGroupBox("Информация о дереве");
    QVBoxLayout* infoLayout = new QVBoxLayout();
    treeInfoLabel = new QLabel("Size: 0 | Height: 0 | Balanced: Yes");
    treeInfoLabel->setStyleSheet("QLabel { font-family: monospace; font-size: 12px; padding: 5px; background-color: #e8f5e9; border: 1px solid #c8e6c9; border-radius: 3px; }");
    infoLayout->addWidget(treeInfoLabel);
    infoGroup->setLayout(infoLayout);
    leftLayout->addWidget(infoGroup);
    
    leftLayout->addStretch();
    
    // Правая область с вертикальным сплиттером
    QWidget* rightArea = new QWidget();
    QVBoxLayout* rightLayout = new QVBoxLayout(rightArea);
    rightLayout->setContentsMargins(0, 0, 0, 0);
    rightLayout->setSpacing(5);
    
    // Создаем вертикальный сплиттер для дерева и лога
    QSplitter* vertSplitter = new QSplitter(Qt::Vertical);
    rightLayout->addWidget(vertSplitter);
    
    // Виджет для дерева с прокруткой
    QScrollArea* treeScrollArea = new QScrollArea();
    treeScrollArea->setWidgetResizable(true);
    treeScrollArea->setHorizontalScrollBarPolicy(Qt::ScrollBarAsNeeded);
    treeScrollArea->setVerticalScrollBarPolicy(Qt::ScrollBarAsNeeded);
    treeScrollArea->setStyleSheet("QScrollArea { border: 1px solid #ccc; background-color: white; }");
    
    treeWidget = new TreeWidget();
    treeScrollArea->setWidget(treeWidget);
    vertSplitter->addWidget(treeScrollArea);
    
    // Лог-окно
    QGroupBox* logGroup = new QGroupBox("Event Log");
    QVBoxLayout* logLayout = new QVBoxLayout();
    logWindow = new QTextEdit();
    logWindow->setReadOnly(true);
    logWindow->setFont(QFont("Monospace", 9));
    logWindow->setMinimumHeight(150);
    logWindow->setStyleSheet("QTextEdit { background-color: #fafafa; }");
    logLayout->addWidget(logWindow);
    logGroup->setLayout(logLayout);
    vertSplitter->addWidget(logGroup);
    
    // Устанавливаем начальное соотношение размеров (70% дерево, 30% лог)
    vertSplitter->setSizes({600, 250});
    
    // Добавляем правую область в сплиттер
    mainSplitter->addWidget(leftPanel);
    mainSplitter->addWidget(rightArea);
    mainSplitter->setSizes({500, 800});
    
    // Применяем стиль
    setStyleSheet(R"(
        QGroupBox {
            font-weight: bold;
            border: 1px solid #ccc;
            border-radius: 5px;
            margin-top: 1ex;
            padding-top: 5px;
        }
        QGroupBox::title {
            subcontrol-origin: margin;
            left: 10px;
            padding: 0 5px 0 5px;
        }
        QPushButton {
            background-color: #e0e0e0;
            border: 1px solid #aaa;
            border-radius: 3px;
            padding: 5px;
        }
        QPushButton:hover {
            background-color: #d0d0d0;
        }
        QPushButton:pressed {
            background-color: #c0c0c0;
        }
        QLineEdit, QSpinBox {
            border: 1px solid #aaa;
            border-radius: 3px;
            padding: 3px;
        }
    )");
    
    logMessage("Application started");
    logMessage("Use mouse wheel to zoom, drag to pan the tree view");
    logMessage("Node circles display key and data values");
    logMessage("RED arrow indicates the element pointed by iterator");
    logMessage("Use iterator buttons to navigate through the tree");
    
    updateDisplay();
    
    keyInput->setText("50");
    dataInput->setText("500");
    
    // Сбрасываем итератор
    currentIterator = tree.end();
    treeWidget->setIterator(&currentIterator);
}

void MainWindow::onInsert() {
    bool ok;
    int key = keyInput->text().toInt(&ok);
    if (!ok) {
        QMessageBox::warning(this, "Error", "Invalid key");
        logMessage("ERROR: Invalid key value");
        return;
    }
    
    int data = dataInput->text().toInt(&ok);
    if (!ok) {
        data = key * 10;
    }
    
    if (tree.insert(key, data)) {
        logMessage(QString("INSERTED: key=%1, data=%2").arg(key).arg(data));
        // Сбрасываем итератор после вставки
        currentIterator = tree.end();
    } else {
        logMessage(QString("FAILED: key=%1 already exists").arg(key));
        QMessageBox::warning(this, "Error", "Key already exists");
    }
    updateDisplay();
}

void MainWindow::onRemove() {
    bool ok;
    int key = keyInput->text().toInt(&ok);
    if (!ok) {
        QMessageBox::warning(this, "Error", "Invalid key");
        logMessage("ERROR: Invalid key value");
        return;
    }
    
    // Проверяем, существует ли элемент в дереве
    if (!tree.contains(key)) {
        logMessage(QString("FAILED: key=%1 not found").arg(key));
        QMessageBox::warning(this, "Error", "Key not found");
        return;
    }
    
    // Проверяем, указывает ли текущий итератор на удаляемый ключ
    bool iteratorPointsToKey = false;
    
    try {
        if (currentIterator != tree.end() && currentIterator.getKey() == key) {
            iteratorPointsToKey = true;
            logMessage(QString("Iterator currently points to key=%1").arg(key));
        }
    } catch (const std::exception& e) {
        // Итератор невалиден, игнорируем
        logMessage(QString("Iterator is invalid: %1").arg(e.what()));
        iteratorPointsToKey = false;
    }
    
    if (iteratorPointsToKey) {
        // Удаляем с перемещением итератора
        logMessage(QString("REMOVING: key=%1 (iterator will advance)").arg(key));
        
        try {
            int oldKey = currentIterator.getKey();
            currentIterator = tree.remove(currentIterator);
            
            if (currentIterator != tree.end()) {
                logMessage(QString("REMOVED: key=%1, iterator now points to key=%2")
                    .arg(oldKey).arg(currentIterator.getKey()));
            } else {
                logMessage(QString("REMOVED: key=%1, iterator now at end()").arg(oldKey));
            }
        } catch (const std::exception& e) {
            logMessage(QString("ERROR during removal: %1").arg(e.what()));
            QMessageBox::warning(this, "Error", QString("Failed to remove: %1").arg(e.what()));
            updateDisplay();
            return;
        }
    } else {
        // Обычное удаление
        logMessage(QString("REMOVED: key=%1 (iterator unchanged)").arg(key));
        tree.remove(key);
        
        // Проверяем, не стал ли итератор невалидным
        try {
            if (currentIterator != tree.end()) {
                currentIterator.getKey(); // Проверяем валидность
                logMessage("Iterator remains valid");
            }
        } catch (const std::exception&) {
            logMessage("Iterator became invalid, resetting to end()");
            currentIterator = tree.end();
        }
    }
    
    updateDisplay();
}

void MainWindow::onSearch() {
    bool ok;
    int key = keyInput->text().toInt(&ok);
    if (!ok) {
        QMessageBox::warning(this, "Error", "Invalid key");
        logMessage("ERROR: Invalid key value");
        return;
    }
    
    try {
        int data = tree.getItem(key);
        logMessage(QString("FOUND: key=%1, data=%2").arg(key).arg(data));
        QMessageBox::information(this, "Search Result", 
            QString("Found: key=%1, data=%2").arg(key).arg(data));
    } catch (const std::out_of_range&) {
        logMessage(QString("NOT FOUND: key=%1").arg(key));
        QMessageBox::information(this, "Search Result", "Key not found");
    }
    updateDisplay();
}

void MainWindow::onGenerateRandom() {
    int depth = depthSpinBox->value();
    generateTreeWithDepth(depth, true);
}

void MainWindow::onGeneratePerfect() {
    int depth = depthSpinBox->value();
    generateTreeWithDepth(depth, false);
}

void MainWindow::onGenerateDegenerate() {
    int depth = depthSpinBox->value();
    tree.clear();
    
    int maxNodes = (1 << depth) - 1;
    for (int i = 1; i <= maxNodes; ++i) {
        tree.insert(i, i * 10);
    }
    
    currentIterator = tree.end();
    
    logMessage(QString("Generated DEGENERATE tree with %1 nodes, depth=%2, height=%3")
        .arg(tree.size())
        .arg(depth)
        .arg(tree.height()));
    updateDisplay();
}

void MainWindow::onClear() {
    tree.clear();
    currentIterator = tree.end();
    logMessage("Tree cleared");
    updateDisplay();
}

void MainWindow::onPrintTree() {
    logMessage("Printing tree structure to console:");
    tree.printTree();
    updateDisplay();
}

void MainWindow::onIteratorBegin() {
    if (!tree.empty()) {
        currentIterator = tree.begin();
        logMessage(QString("Iterator set to begin() -> key=%1, data=%2")
            .arg(currentIterator.getKey())
            .arg(*currentIterator));
    } else {
        logMessage("Cannot set iterator to begin(): tree is empty");
        QMessageBox::warning(this, "Warning", "Tree is empty");
    }
    updateDisplay();
}

void MainWindow::onIteratorEnd() {
    currentIterator = tree.end();
    logMessage("Iterator set to end()");
    updateDisplay();
}

void MainWindow::onIteratorIncrement() {
    if (currentIterator != tree.end()) {
        ++currentIterator;
        if (currentIterator != tree.end()) {
            logMessage(QString("Iterator incremented -> key=%1, data=%2")
                .arg(currentIterator.getKey())
                .arg(*currentIterator));
        } else {
            logMessage("Iterator incremented -> reached end()");
        }
    } else {
        logMessage("Cannot increment: iterator at end");
        QMessageBox::information(this, "Info", "Iterator is at end, cannot increment");
    }
    updateDisplay();
}

void MainWindow::onIteratorDecrement() {
    if (currentIterator != tree.end()) {
        --currentIterator;
        if (currentIterator != tree.end()) {
            logMessage(QString("Iterator decremented -> key=%1, data=%2")
                .arg(currentIterator.getKey())
                .arg(*currentIterator));
        } else {
            logMessage("Iterator decremented -> reached end()");
        }
    } else {
        logMessage("Cannot decrement: iterator at end");
        QMessageBox::information(this, "Info", "Iterator is at end, cannot decrement");
    }
    updateDisplay();
}

void MainWindow::onIteratorGet() {
    try {
        logMessage(QString("Current iterator: key=%1, data=%2")
            .arg(currentIterator.getKey())
            .arg(*currentIterator));
        QMessageBox::information(this, "Iterator Info", 
            QString("Iterator points to:\nKey: %1\nData: %2")
            .arg(currentIterator.getKey())
            .arg(*currentIterator));
    } catch (const std::out_of_range&) {
        logMessage("Iterator not dereferenceable (at end)");
        QMessageBox::information(this, "Iterator Info", "Iterator is at end (not pointing to any element)");
    }
    updateDisplay();
}

void MainWindow::onZoomIn() {
    treeWidget->zoomIn();
    updateScaleLabel();
    logMessage(QString("Zoom in: %1%").arg(int(treeWidget->getScale() * 100)));
}

void MainWindow::onZoomOut() {
    treeWidget->zoomOut();
    updateScaleLabel();
    logMessage(QString("Zoom out: %1%").arg(int(treeWidget->getScale() * 100)));
}

void MainWindow::onFitToView() {
    treeWidget->fitToView();
    updateScaleLabel();
    logMessage("Fit to view");
}

void MainWindow::onResetView() {
    treeWidget->resetView();
    updateScaleLabel();
    logMessage("View reset");
}

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);
    
    // Устанавливаем стиль приложения
    app.setStyle("Fusion");
    
    MainWindow window;
    window.show();
    
    return app.exec();
}

#include "main_gui.moc"