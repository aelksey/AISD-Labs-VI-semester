# 📝 Ответы на подготовленные вопросы для защиты лабораторной работы

## 9.1 Теоретические вопросы

### 1. Что такое 2-3 дерево? Опишите его свойства.

**Ответ:** 2-3 дерево - это сбалансированное дерево поиска, в котором каждый внутренний узел может иметь 2 или 3 дочерних элемента.

**Основные свойства:**
- **2-узел**: содержит 1 ключ и имеет 2 детей (левое и правое поддеревья)
- **3-узел**: содержит 2 ключа и имеет 3 детей (левое, среднее и правое поддеревья)
- **Все листья находятся на одном уровне** (идеальная сбалансированность)
- **Упорядоченность**: для любого узла все ключи в левом поддереве меньше первого ключа, в правом - больше последнего
- **Динамическая структура**: может расти и уменьшаться без потери свойств

### 2. Чем 2-узел отличается от 3-узла?

**Ответ:**

| Характеристика | 2-узел | 3-узел |
|----------------|--------|--------|
| Количество ключей | 1 ключ | 2 ключа |
| Количество детей | 2 ребенка | 3 ребенка |
| Обозначение | [k1] | [k1, k2] |
| Структура | A < k1 < B | A < k1 < B < k2 < C |
| Использование | Базовый узел | Возникает при вставке |

**Визуальное представление:**
```
2-узел:         3-узел:
   [5]            [3, 7]
  /   \          /  |  \
[2]   [8]      [1] [5] [9]
```

### 3. Почему 2-3 дерево всегда сбалансировано?

**Ответ:** 2-3 дерево всегда сбалансировано благодаря своим инвариантам:

1. **Инвариант высоты**: Все листья находятся на одном уровне
2. **Инвариант роста**: При вставке дерево растет в высоту только тогда, когда корень разбивается на два узла, и новый корень создается над ними
3. **Отсутствие деградации**: В отличие от обычного BST, 2-3 дерево не может выродиться в линейную структуру

**Доказательство:**
- При вставке элементы никогда не добавляются "в глубину" без балансировки
- Если узел переполняется (3 ключа), он разбивается, и ключ поднимается вверх
- Это гарантирует, что все пути от корня до листьев имеют одинаковую длину

### 4. Как выполняется поиск в 2-3 дереве?

**Ответ:** Алгоритм поиска:

```java
public int find(TreeElement elem) {
    if (isEmpty(root)) return -1;
    return findRec(root, elem);
}

private int findRec(int t, TreeElement elem) {
    // Если лист - проверяем значение
    if (isLeaf(t)) {
        if (elem.Equal(treeNodes[t].elem)) return t;
        return -1;
    }
    
    // Находим поддерево для поиска
    int subTree = findSubTree(t, elem.ExtractValue());
    if (subTree == -1) return -1;
    
    // Рекурсивно ищем в поддереве
    return findRec(treeNodes[t].children[subTree].child, elem);
}
```

**Шаги:**
1. Начинаем с корня
2. Сравниваем искомый ключ с ключами в узле
3. Выбираем соответствующее поддерево
4. Повторяем до нахождения или достижения листа
5. В листе проверяем равенство значений

**Сложность:** O(log n) сравнений

### 5. Опишите алгоритм вставки в 2-3 дерево.

**Ответ:** Алгоритм вставки:

**Этап 1: Поиск места вставки**
- Спускаемся от корня к листу, куда нужно вставить элемент

**Этап 2: Вставка в лист**
- Если лист - 2-узел (1 ключ): просто вставляем, превращая в 3-узел
- Если лист - 3-узел (2 ключа): разбиваем на два 2-узла

**Этап 3: Разбиение (если необходимо)**
```
Исходный 3-узел: [a, b, c] (вставка c)
Разбиение: 
  Левый узел: [a]
  Правый узел: [c]
  Средний ключ b поднимается в родителя
```

**Этап 4: Рекурсивный подъем**
- Если родитель становится 3-узлом, разбиение продолжается вверх
- Если разбивается корень, создается новый корень

**Пример вставки числа 2.5:**
```
До: [3, 6]          После:    [3]
    / | \                    /   \
  [1] [4] [7]              [1,2] [4,6]
                               |   /|\
                             2.5 [4][5][7]
```

### 6. Опишите алгоритм удаления из 2-3 дерева.

**Ответ:** Алгоритм удаления:

**Случай 1: Удаление из листа**
- Если после удаления в листе остается ключ - просто удаляем
- Если лист становится пустым - выполняем перебалансировку

**Случай 2: Удаление из внутреннего узла**
- Находим преемника (следующий по величине элемент в листе)
- Заменяем удаляемый элемент преемником
- Удаляем преемника из листа

**Случаи перебалансировки:**

1. **Перераспределение (Rotation)**:
   - Брат может "отдать" один ключ
   - Родительский ключ опускается в пустой узел
   - Ключ брата поднимается в родителя

2. **Слияние (Merge)**:
   - Брат также имеет минимальное количество ключей
   - Объединяем пустой узел с братом
   - Ключ из родителя опускается в объединенный узел
   - Если родитель становится пустым - рекурсивно балансируем

**Пример удаления:**
```
До:    [5]           После:   [5]
      /   \                 /   \
    [3]   [7]             [3]   [9]
    / \   / \             / \   / \
  [2][4][6][8]          [2][4][6][8]
  
Удаляем 7 → поднимаем 8
```

### 7. Какова сложность основных операций? Докажите.

**Ответ:** 

**Сложность:**
- **Search**: O(log n)
- **Insert**: O(log n)
- **Delete**: O(log n)

**Доказательство:**

1. **Высота дерева h ≤ log₂(n+1)**
   - Минимальное количество узлов на глубине h: 2^h
   - Все листья на глубине h, значит n ≥ 2^h
   - Следовательно, h ≤ log₂(n)

2. **Поиск (Search)**: 
   - На каждом уровне выполняем O(1) сравнений
   - Всего уровней h = O(log n)
   - Итого: O(log n)

3. **Вставка (Insert)**:
   - Поиск места: O(log n)
   - Разбиение: O(1) на уровне
   - В худшем случае разбиения до корня: O(log n)
   - Итого: O(log n)

4. **Удаление (Delete)**:
   - Поиск элемента: O(log n)
   - Перебалансировка: O(1) на уровне
   - В худшем случае балансировка до корня: O(log n)
   - Итого: O(log n)

### 8. Сравните 2-3 дерево с AVL деревом.

**Ответ:**

| Характеристика | 2-3 Tree | AVL Tree |
|----------------|----------|----------|
| **Сбалансированность** | Идеальная (все листья на одном уровне) | Строгая (разница высот ≤ 1) |
| **Высота** | log₂(n) ≤ h ≤ log₃(n) | ≈ 1.44 log₂(n) |
| **Поиск** | O(log n) | O(log n) |
| **Вставка** | O(log n) амортизированная | O(log n) с вращениями |
| **Удаление** | O(log n) амортизированная | O(log n) с вращениями |
| **Сложность реализации** | Средняя | Высокая |
| **Память на узел** | Переменная (2-3 ссылки) | Фиксированная (2 ссылки + высота) |
| **Количество вращений** | Разбиения и слияния | Одиночные и двойные вращения |
| **Применение** | Базы данных, файловые системы | В памяти, компиляторы |

**Преимущества 2-3 дерева:**
- Проще реализация балансировки (нет вращений)
- Лучше подходит для дисковых операций
- Гарантированная высота log₂(n)

**Преимущества AVL:**
- Меньше памяти на узел
- Быстрее для операций в памяти
- Более строгая балансировка

### 9. Что такое вырожденное дерево? Почему оно возникает?

**Ответ:** 

**Вырожденное дерево** - это дерево, которое по своей структуре напоминает линейный список (связный список).

**Визуализация:**
```
Обычное BST:          Вырожденное BST:
      5                       1
     / \                       \
    3   7                       2
   / \   \                       \
  2   4   8                       3
                                   \
                                    4
                                     \
                                      5
```

**Почему возникает:**

1. **Вставка в отсортированном порядке**:
   - При вставке элементов в возрастающем или убывающем порядке
   - Каждый новый элемент становится самым правым (или левым) узлом

2. **Отсутствие самобалансировки**:
   - В обычном BST нет механизмов балансировки
   - Дерево "вырождается" в связный список

3. **2-3 дерево не вырождается!**
   - Благодаря самобалансировке, даже при вставке отсортированных данных
   - Дерево остается сбалансированным

**Для 2-3 дерева:**
- При вставке отсортированных данных: [1,2,3,4,5,6,7,8]
- Дерево будет расти в ширину, а не в глубину
- Высота останется O(log n)

### 10. Как связаны 2-3 деревья и B-деревья?

**Ответ:**

**B-дерево** - это обобщение 2-3 дерева на случай, когда узел может содержать от t до 2t ключей.

**Связь:**
- 2-3 дерево является частным случаем B-дерева порядка 3 (t=2)
- B-дерево порядка m: узел может иметь от m до 2m ключей

**Иерархия:**
```
B-дерево порядка 2 (2-3-4 дерево)
    ↓
B-дерево порядка 3 (2-3 дерево)
    ↓
B-дерево порядка m (общий случай)
```

**Общие свойства:**
1. Все листья на одном уровне
2. Самобалансировка
3. O(log n) для основных операций

**Различия:**
| Характеристика | 2-3 Tree | B-Tree |
|----------------|----------|---------|
| Ключей в узле | 1-2 | t до 2t |
| Степень ветвления | 2-3 | t+1 до 2t+1 |
| Применение | Учебное | Реальные БД, файловые системы |

## 9.2 Практические вопросы

### 1. Как реализовать поиск минимального элемента?

**Ответ:**

```java
public int getSmallestLeaf() {
    if (isEmpty(root)) return -1;
    return getSmallestLeafRec(root);
}

private int getSmallestLeafRec(int t) {
    // Если лист - возвращаем его
    if (isLeaf(t)) return t;
    
    // Идем в самый левый дочерний узел
    return getSmallestLeafRec(treeNodes[t].children[0].child);
}

// Альтернативная реализация через связанный список листьев
public int getSmallestLeafFromList() {
    if (isEmpty(root)) return -1;
    return getSmallestLeaf(); // Первый лист в двусвязном списке
}
```

**Временная сложность:** O(log n) - спуск по левым указателям

### 2. Как обойти дерево в порядке возрастания?

**Ответ:**

```java
// Используя связный список листьев (O(n) времени, O(1) памяти)
public void traverseInOrder() {
    int current = getSmallestLeaf();
    while (current != -1) {
        System.out.println(treeNodes[current].elem.ExtractValue());
        current = next(current);
    }
}

// Рекурсивный обход (O(n) времени, O(log n) памяти под стек)
private void inorderTraversal(int t) {
    if (isLeaf(t)) {
        System.out.println(treeNodes[t].elem.ExtractValue());
        return;
    }
    
    // Обходим левое поддерево
    inorderTraversal(treeNodes[t].children[0].child);
    
    // Выводим первый ключ
    System.out.println(treeNodes[t].children[0].maxChild);
    
    // Обходим среднее поддерево (если есть)
    if (treeNodes[t].cCount >= 2) {
        inorderTraversal(treeNodes[t].children[1].child);
        
        // Выводим второй ключ (для 3-узла)
        if (treeNodes[t].cCount == 3) {
            System.out.println(treeNodes[t].children[1].maxChild);
            inorderTraversal(treeNodes[t].children[2].child);
        }
    }
}
```

### 3. Как проверить, что дерево корректно?

**Ответ:**

```java
public boolean invariant() {
    // 1. Проверка высоты (все листья на одном уровне)
    int[] depths = depths();
    if (depths[0] != depths[1]) return false;
    
    // 2. Проверка связного списка листьев
    if (!leafListInvariant()) return false;
    
    // 3. Проверка упорядоченности
    if (!orderingInvariant()) return false;
    
    // 4. Проверка количества детей
    if (!childCountInvariant()) return false;
    
    return true;
}

// Проверка количества детей
private boolean childCountInvariant() {
    return checkChildCount(root);
}

private boolean checkChildCount(int t) {
    if (t == -1) return true;
    
    int childCount = treeNodes[t].cCount;
    if (!isLeaf(t)) {
        // Внутренний узел должен иметь 2 или 3 детей
        if (childCount != 2 && childCount != 3) return false;
        
        // Рекурсивно проверяем детей
        for (int i = 0; i < childCount; i++) {
            if (!checkChildCount(treeNodes[t].children[i].child)) 
                return false;
        }
    }
    return true;
}

// Проверка упорядоченности
private boolean orderingInvariant() {
    double prev = Double.NEGATIVE_INFINITY;
    int current = getSmallestLeaf();
    
    while (current != -1) {
        double currVal = treeNodes[current].elem.ExtractValue();
        if (currVal <= prev) return false;
        prev = currVal;
        current = next(current);
    }
    return true;
}
```

### 4. Как измерить время выполнения операций?

**Ответ:**

```java
public class PerformanceTest {
    public static void measureInsertTime(Tree23 tree, int size) {
        long startTime = System.nanoTime();
        
        for (int i = 0; i < size; i++) {
            tree.insert(createElement(i));
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        System.out.printf("Insert %d elements: %d ns (%.3f ms)%n", 
            size, duration, duration / 1_000_000.0);
    }
    
    public static void measureSearchTime(Tree23 tree, int iterations) {
        // Подготовка: заполняем дерево
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            double val = Math.random() * 10000;
            values.add(val);
            tree.insert(createElement(val));
        }
        
        // Измерение поиска
        long startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            double searchVal = values.get((int)(Math.random() * values.size()));
            tree.find(createElement(searchVal));
        }
        
        long endTime = System.nanoTime();
        long avgTime = (endTime - startTime) / iterations;
        
        System.out.printf("Average search time: %d ns%n", avgTime);
    }
}
```

### 5. Как визуализировать 2-3 дерево?

**Ответ:**

```java
public void visualize() {
    // 1. Собираем все узлы в Map
    Map<Integer, TreeNode> nodeMap = new HashMap<>();
    buildTreeStructure(root, nodeMap, null);
    
    // 2. Вычисляем позиции узлов
    calculatePositions(nodeMap);
    
    // 3. Рисуем дерево
    for (TreeNode node : treeLayout) {
        // Рисуем ребра к детям
        for (TreeNode child : node.children) {
            drawLine(node.x, node.y, child.x, child.y);
        }
        
        // Рисуем узел
        drawNode(node.x, node.y, node.value, node.isLeaf);
    }
}

// Рекурсивный обход для построения структуры
private void buildTreeStructure(int nodeIdx, 
        Map<Integer, TreeNode> nodeMap, TreeNode parent) {
    if (nodeIdx == -1) return;
    
    TreeNode node = nodeMap.get(nodeIdx);
    if (node == null) {
        node = new TreeNode(getNodeValue(nodeIdx), isLeaf(nodeIdx));
        nodeMap.put(nodeIdx, node);
    }
    
    if (parent != null) {
        parent.children.add(node);
    }
    
    // Рекурсивно обрабатываем детей
    if (!isLeaf(nodeIdx)) {
        int childCount = getTreeNodeChildCount(nodeIdx);
        for (int i = 0; i < childCount; i++) {
            int childIdx = getTreeNodeChild(nodeIdx, i);
            buildTreeStructure(childIdx, nodeMap, node);
        }
    }
}
```

### 6. Что такое инварианты и как их проверять?

**Ответ:**

**Инварианты** - это свойства структуры данных, которые всегда должны быть истинными.

**Основные инварианты 2-3 дерева:**

1. **Инвариант упорядоченности**:
```java
private boolean checkOrder(int t) {
    if (isLeaf(t)) return true;
    
    double maxLeft = max(treeNodes[t].children[0].child);
    double minRight = min(treeNodes[t].children[treeNodes[t].cCount - 1].child);
    
    if (maxLeft >= minRight) return false;
    
    // Проверяем всех детей
    for (int i = 0; i < treeNodes[t].cCount; i++) {
        if (!checkOrder(treeNodes[t].children[i].child)) 
            return false;
    }
    return true;
}
```

2. **Инвариант количества детей**:
```java
private boolean checkChildCount(int t) {
    if (t == -1) return true;
    
    if (!isLeaf(t)) {
        int count = treeNodes[t].cCount;
        if (count < 2 || count > 3) return false;
        
        for (int i = 0; i < count; i++) {
            if (!checkChildCount(treeNodes[t].children[i].child))
                return false;
        }
    }
    return true;
}
```

3. **Инвариант высоты**:
```java
private int[] getHeights(int t) {
    if (isLeaf(t)) return new int[]{1, 1};
    
    int minHeight = Integer.MAX_VALUE;
    int maxHeight = Integer.MIN_VALUE;
    
    for (int i = 0; i < treeNodes[t].cCount; i++) {
        int[] childHeights = getHeights(treeNodes[t].children[i].child);
        minHeight = Math.min(minHeight, childHeights[0]);
        maxHeight = Math.max(maxHeight, childHeights[1]);
    }
    
    return new int[]{minHeight + 1, maxHeight + 1};
}
```

## 9.3 Вопросы по коду

### 1. Зачем нужны поля prev и next?

**Ответ:** Поля `prev` и `next` создают двусвязный список всех листьев дерева в порядке возрастания ключей.

**Назначение:**
1. **Быстрый обход**: O(n) для прохода по всем элементам
2. **Поиск следующего/предыдущего элемента**: O(1) время
3. **Упрощение некоторых операций**: например, поиск минимального/максимального элемента

**Пример использования:**
```java
// Обход всех элементов в порядке возрастания
int current = getSmallestLeaf();
while (current != -1) {
    System.out.println(treeNodes[current].elem.ExtractValue());
    current = treeNodes[current].next;
}
```

### 2. Как работает метод max(int t)?

**Ответ:** Метод `max(int t)` возвращает максимальное значение в поддереве с корнем в узле `t`.

**Реализация:**
```java
private double max(int t) {
    // Если узел - лист, возвращаем его значение
    if (isLeaf(t)) {
        return treeNodes[t].elem.ExtractValue();
    }
    
    // Иначе возвращаем maxChild последнего ребенка
    return treeNodes[t].children[treeNodes[t].cCount - 1].maxChild;
}
```

**Использование:**
- При вставке: для обновления максимальных значений на пути к корню
- При разбиении: для установки правильных maxChild в новых узлах
- При поиске: для определения, в какое поддерево идти

### 3. Что делает метод insertRec?

**Ответ:** `insertRec` - рекурсивный метод вставки, который возвращает массив новых детей после вставки.

**Логика работы:**
```java
private int[] insertRec(int t, TreeElement elem) {
    // 1. Если лист - вставляем и возвращаем массив из 1 или 2 узлов
    if (isLeaf(t)) {
        // Создаем новый лист
        // Возвращаем 1 узел, если влезло, или 2 узла при разбиении
    }
    
    // 2. Ищем поддерево для вставки
    int subTree = insertInto(t, elem);
    
    // 3. Рекурсивно вставляем в поддерево
    int[] newChildren = insertRec(treeNodes[t].children[subTree].child, elem);
    
    // 4. Обрабатываем результат:
    //    - Если вернулся 1 узел - просто обновляем ссылку
    //    - Если вернулось 2 узла - нужно вставить их в текущий узел
    //    - Если текущий узел переполняется - разбиваем его
    
    return result; // Массив из 1 или 2 узлов
}
```

**Возвращаемые значения:**
- `int[1]` - вставка не вызвала разбиения
- `int[2]` - узел разбился, нужно поднять ключ в родителя

### 4. Как работает перебалансировка при удалении?

**Ответ:** Перебалансировка при удалении происходит, когда после удаления узел становится пустым (0 ключей).

**Алгоритм перебалансировки:**

1. **Поиск брата для перераспределения**:
```java
private void rebalance(int node, int parent, int childIndex) {
    // Проверяем левого брата
    if (childIndex > 0 && 
        treeNodes[treeNodes[parent].children[childIndex-1].child].cCount > 1) {
        // Перераспределяем с левым братом
        redistributeLeft(node, parent, childIndex);
        return;
    }
    
    // Проверяем правого брата
    if (childIndex < treeNodes[parent].cCount - 1 &&
        treeNodes[treeNodes[parent].children[childIndex+1].child].cCount > 1) {
        // Перераспределяем с правым братом
        redistributeRight(node, parent, childIndex);
        return;
    }
    
    // Иначе - слияние
    if (childIndex > 0) {
        mergeWithLeft(node, parent, childIndex);
    } else {
        mergeWithRight(node, parent, childIndex);
    }
}
```

2. **Перераспределение (Rotation)**:
```java
private void redistributeLeft(int node, int parent, int childIndex) {
    TreeNode leftSibling = treeNodes[treeNodes[parent].children[childIndex-1].child];
    TreeNode currentNode = treeNodes[node];
    
    // Берем максимальный ключ из брата
    TreeElement maxFromSibling = getMaxLeaf(leftSibling);
    
    // Вставляем в текущий узел
    currentNode.insert(maxFromSibling);
    
    // Обновляем родительский ключ
    updateParentKey(parent, childIndex-1);
}
```

3. **Слияние (Merge)**:
```java
private void mergeWithLeft(int node, int parent, int childIndex) {
    TreeNode leftSibling = treeNodes[treeNodes[parent].children[childIndex-1].child];
    TreeNode currentNode = treeNodes[node];
    
    // Объединяем узлы
    leftSibling.merge(currentNode);
    
    // Удаляем ссылку на текущий узел из родителя
    removeChild(parent, childIndex);
    
    // Если родитель стал пустым - рекурсивно балансируем
    if (treeNodes[parent].cCount == 0) {
        rebalance(parent, getParent(parent), getChildIndex(parent));
    }
}
```

### 5. Почему используется рекурсия?

**Ответ:** Рекурсия используется из-за естественной рекурсивной структуры дерева.

**Преимущества рекурсии:**
1. **Естественность**: Дерево по определению рекурсивная структура
2. **Простота кода**: Рекурсивный код короче и понятнее
3. **Удобство обхода**: Легко обходить поддеревья

**Пример рекурсивного обхода:**
```java
// Рекурсия естественно отражает структуру дерева
private void traverse(TreeNode node) {
    if (node == null) return;
    
    traverse(node.left);   // Левое поддерево
    process(node);         // Обработка узла
    traverse(node.right);  // Правое поддерево
}
```

**Недостатки:**
- Риск переполнения стека для очень глубоких деревьев
- Дополнительные затраты на вызовы функций

**Альтернативы:**
- Итеративный обход с использованием стека
- Для 2-3 дерева высота O(log n), поэтому переполнение стека маловероятно

**Итеративная версия поиска:**
```java
public int findIterative(TreeElement elem) {
    int current = root;
    double value = elem.ExtractValue();
    
    while (!isLeaf(current)) {
        int childIndex = findChildIndex(current, value);
        current = treeNodes[current].children[childIndex].child;
    }
    
    if (elem.Equal(treeNodes[current].elem)) return current;
    return -1;
}
```

---

## Дополнительные вопросы

### Как проверить, что дерево является 2-3 деревом?

```java
public boolean is23Tree() {
    // Проверка 1: Все листья на одном уровне
    int[] depths = depths();
    if (depths[0] != depths[1]) return false;
    
    // Проверка 2: Каждый внутренний узел имеет 2 или 3 детей
    if (!checkChildrenCount()) return false;
    
    // Проверка 3: Упорядоченность ключей
    if (!checkOrdering()) return false;
    
    // Проверка 4: Связный список листьев корректен
    if (!checkLeafList()) return false;
    
    return true;
}
```

### Как найти высоту дерева?

```java
public int height() {
    return heightRec(root);
}

private int heightRec(int t) {
    if (isLeaf(t)) return 1;
    
    int maxHeight = 0;
    for (int i = 0; i < treeNodes[t].cCount; i++) {
        maxHeight = Math.max(maxHeight, 
            heightRec(treeNodes[t].children[i].child));
    }
    return maxHeight + 1;
}
```

### Как подсчитать количество узлов?

```java
public int countNodes() {
    return countNodesRec(root);
}

private int countNodesRec(int t) {
    if (t == -1) return 0;
    
    int count = 1; // Текущий узел
    for (int i = 0; i < treeNodes[t].cCount; i++) {
        count += countNodesRec(treeNodes[t].children[i].child);
    }
    return count;
}
```

---

**Готовьтесь к защите, понимая не только теорию, но и умея объяснить практическую реализацию!** 💪