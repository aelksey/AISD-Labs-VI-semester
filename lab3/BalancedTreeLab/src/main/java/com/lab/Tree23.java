package com.lab;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.EmptyStackException;

public class Tree23 {

    private static final int NULL_INDEX = -1;

    private static final class TreeLink {
        double maxChild;
        int child;

        TreeLink(double maxChild, int child) {
            this.maxChild = maxChild;
            this.child = child;
        }
    }

    private static final class TreeNode {
        TreeLink[] children;
        int cCount;
        TreeElement elem;
        int prev;
        int next;

        TreeNode() {
            this.children = new TreeLink[3];
            this.cCount = 0;
            this.elem = null;
            this.prev = NULL_INDEX;
            this.next = NULL_INDEX;
        }
    }

    private static final class IntStack {
        private int[] data;
        private int size;

        IntStack(int capacity) {
            this.data = new int[capacity];
            this.size = 0;
        }

        boolean isEmpty() {
            return size == 0;
        }

        int size() {
            return size;
        }

        int get(int index) {
            return data[index];
        }

        int peek() {
            return data[size - 1];
        }

        int pop() {
            if (size == 0) {
                throw new EmptyStackException();
            }
            return data[--size];
        }

        void push(int value) {
            if (size >= data.length) {
                int newCapacity = data.length == 0 ? 4 : data.length * 2;
                int[] newData = new int[newCapacity];
                System.arraycopy(data, 0, newData, 0, size);
                data = newData;
            }
            data[size++] = value;
        }
    }

    private TreeNode[] treeNodes;
    private int treeNodesFirstFreePos;
    private IntStack treeNodesFreePositions;

    public int root;

    private int[] oneElemTreeList;
    private int[] twoElemTreeList;
    private int[] threeElemTreeList;
    private int[] nineElemTreeList;

    public Tree23() {
        this(1);
    }

    public Tree23(int expectedCapacity) {
        initializeTree(expectedCapacity);
    }

    private void initializeTree(int capacity) {
        root = 0;

        oneElemTreeList = new int[]{-1};
        twoElemTreeList = new int[]{-1, -1};
        threeElemTreeList = new int[]{-1, -1, -1};
        nineElemTreeList = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1};

        treeNodes = new TreeNode[capacity];
        for (int i = 0; i < capacity; i++) {
            treeNodes[i] = new TreeNode();
        }
        treeNodesFirstFreePos = 1;
        treeNodesFreePositions = new IntStack(0);
    }

    public boolean isLeaf(int t) {
        return t != NULL_INDEX && treeNodes[t].cCount == 0;
    }

    public boolean isEmpty(int t) {
        return isLeaf(t) && treeNodes[t].elem == null;
    }

    public int getTreeNodeChildCount(int idx) {
        if (idx == NULL_INDEX || idx >= treeNodes.length) return 0;
        return treeNodes[idx].cCount;
    }

    public int getTreeNodeChild(int idx, int childPos) {
        if (idx == NULL_INDEX || idx >= treeNodes.length) return NULL_INDEX;
        TreeNode node = treeNodes[idx];
        if (childPos < 0 || childPos >= node.cCount) return NULL_INDEX;
        return node.children[childPos].child;
    }

    public TreeElement getValue(int t) {
        return treeNodes[t].elem;
    }

    public void changeValue(int t, TreeElement e) {
        if (isLeaf(t) && treeNodes[t].elem.Equal(e)) {
            treeNodes[t].elem = e;
        }
    }

    public void changeValueUnsafe(int t, TreeElement e) {
        if (isLeaf(t)) {
            treeNodes[t].elem = e;
        }
    }

    private int newNode() {
        if (!treeNodesFreePositions.isEmpty()) {
            int node = treeNodesFreePositions.pop();
            treeNodes[node].cCount = 0;
            treeNodes[node].elem = null;
            treeNodes[node].next = NULL_INDEX;
            treeNodes[node].prev = NULL_INDEX;
            return node;
        }

        if (treeNodesFirstFreePos >= treeNodes.length) {
            int appendSize = (int) (treeNodes.length * 1.25);
            if (treeNodes.length < 1000) {
                appendSize = treeNodes.length * 2;
            }
            treeNodes = Arrays.copyOf(treeNodes, treeNodes.length + appendSize);
            for (int i = treeNodesFirstFreePos; i < treeNodes.length; i++) {
                treeNodes[i] = new TreeNode();
            }
        }

        treeNodesFirstFreePos++;
        return treeNodesFirstFreePos - 1;
    }

    private void recycleNode(int n) {
        treeNodes[n].cCount = 0;
        treeNodes[n].elem = null;
        treeNodes[n].next = NULL_INDEX;
        treeNodes[n].prev = NULL_INDEX;
        treeNodesFreePositions.push(n);
    }

    private int newLeaf(TreeElement elem, int prev, int next) {
        int n = newNode();
        treeNodes[n].cCount = 0;
        treeNodes[n].elem = elem;
        treeNodes[n].prev = prev;
        treeNodes[n].next = next;
        return n;
    }

    public double max(int t) {
        if (isLeaf(t)) {
            return treeNodes[t].elem.ExtractValue();
        }
        return treeNodes[t].children[treeNodes[t].cCount - 1].maxChild;
    }

    private int nodeFromChildrenList(int[] children, int startIndex, int endIndex) {
        int t = newNode();
        treeNodes[t].cCount = endIndex - startIndex;

        for (int i = startIndex; i < endIndex; i++) {
            int c = children[i];
            treeNodes[t].children[i - startIndex] = new TreeLink(max(c), c);
        }
        return t;
    }

    private int[] multipleNodesFromChildrenList(int[] children, int cLen) {
        switch (cLen <= 3 ? 1 : cLen <= 6 ? 2 : cLen <= 9 ? 3 : 0) {
            case 1:
                oneElemTreeList[0] = nodeFromChildrenList(children, 0, cLen);
                return oneElemTreeList;
            case 2:
                twoElemTreeList[0] = nodeFromChildrenList(children, 0, cLen / 2);
                twoElemTreeList[1] = nodeFromChildrenList(children, cLen / 2, cLen);
                return twoElemTreeList;
            case 3:
                threeElemTreeList[0] = nodeFromChildrenList(children, 0, cLen / 3);
                threeElemTreeList[1] = nodeFromChildrenList(children, cLen / 3, 2 * cLen / 3);
                threeElemTreeList[2] = nodeFromChildrenList(children, 2 * cLen / 3, cLen);
                return threeElemTreeList;
        }
        System.out.println("SHOULD NOT GET HERE");
        return null;
    }

    private int insertInto(int t, TreeElement elem) {
        double v = elem.ExtractValue();
        for (int i = 0; i < treeNodes[t].cCount; i++) {
            if (v < treeNodes[t].children[i].maxChild) {
                return i;
            }
        }
        return treeNodes[t].cCount - 1;
    }

    private int distributeTwoChildren(int c1, int c2) {
        int n = newNode();
        treeNodes[n].cCount = 2;

        treeNodes[n].children[0] = new TreeLink(max(c1), c1);
        treeNodes[n].children[1] = new TreeLink(max(c2), c2);

        boolean c1IsLeaf = isLeaf(c1);
        boolean c2IsLeaf = isLeaf(c2);

        if (c1IsLeaf && c2IsLeaf) {
            treeNodes[c1].next = c2;
            treeNodes[c2].prev = c1;
        }

        return n;
    }

    private int distributeFourChildren(int c1, int c2, int c3, int c4) {
        int child1 = distributeTwoChildren(c1, c2);
        int child2 = distributeTwoChildren(c3, c4);
        return distributeTwoChildren(child1, child2);
    }

    private int[] insertRec(int t, TreeElement elem) {
        if (isLeaf(t)) {
            if (treeNodes[t].elem.ExtractValue() < elem.ExtractValue()) {
                int leaf = newLeaf(elem, t, treeNodes[t].next);
                treeNodes[t].next = leaf;
                treeNodes[treeNodes[leaf].next].prev = leaf;

                twoElemTreeList[0] = t;
                twoElemTreeList[1] = leaf;
            } else {
                int leaf = newLeaf(elem, treeNodes[t].prev, t);
                treeNodes[t].prev = leaf;
                treeNodes[treeNodes[leaf].prev].next = leaf;

                twoElemTreeList[0] = leaf;
                twoElemTreeList[1] = t;
            }
            return twoElemTreeList;
        }

        int subTree = insertInto(t, elem);
        int[] newChildren = insertRec(treeNodes[t].children[subTree].child, elem);

        if (newChildren.length == 1) {
            treeNodes[t].children[subTree].maxChild = max(newChildren[0]);
            treeNodes[t].children[subTree].child = newChildren[0];

            oneElemTreeList[0] = t;
            return oneElemTreeList;
        }

        if (treeNodes[t].cCount == 2) {
            treeNodes[t].children[subTree].maxChild = max(newChildren[0]);
            treeNodes[t].children[subTree].child = newChildren[0];

            if (subTree == 0) {
                TreeLink tmpTreeNode = treeNodes[t].children[1];
                treeNodes[t].children[1] = new TreeLink(max(newChildren[1]), newChildren[1]);
                treeNodes[t].children[2] = tmpTreeNode;
            } else {
                treeNodes[t].children[2] = new TreeLink(max(newChildren[1]), newChildren[1]);
            }
            treeNodes[t].cCount = 3;

            oneElemTreeList[0] = t;
            return oneElemTreeList;
        }

        int tmpChild0 = newChildren[0];
        int tmpChild1 = newChildren[1];

        switch (subTree) {
            case 0:
                twoElemTreeList[0] = distributeTwoChildren(tmpChild0, tmpChild1);
                twoElemTreeList[1] = distributeTwoChildren(
                        treeNodes[t].children[1].child,
                        treeNodes[t].children[2].child);
                break;
            case 1:
                twoElemTreeList[0] = distributeTwoChildren(
                        treeNodes[t].children[0].child, tmpChild0);
                twoElemTreeList[1] = distributeTwoChildren(
                        tmpChild1,
                        treeNodes[t].children[2].child);
                break;
            case 2:
                twoElemTreeList[0] = distributeTwoChildren(
                        treeNodes[t].children[0].child,
                        treeNodes[t].children[1].child);
                twoElemTreeList[1] = distributeTwoChildren(tmpChild0, tmpChild1);
                break;
        }

        recycleNode(t);
        return twoElemTreeList;
    }

    public void insert(TreeElement elem) {
        // Запрет дубликатов
        if (!isEmpty(root) && find(elem) != NULL_INDEX) {
            return;
        }

        if (isEmpty(root)) {
            int l = newLeaf(elem, NULL_INDEX, NULL_INDEX);
            treeNodes[l].prev = l;
            treeNodes[l].next = l;
            recycleNode(root);
            root = l;
            return;
        }

        if (isLeaf(root)) {
            int l = newLeaf(elem, NULL_INDEX, NULL_INDEX);

            if (treeNodes[l].elem.ExtractValue() < treeNodes[root].elem.ExtractValue()) {
                treeNodes[l].prev = treeNodes[root].prev;
                treeNodes[treeNodes[l].prev].next = l;
                treeNodes[l].next = root;
                treeNodes[root].prev = l;
                root = distributeTwoChildren(l, root);
            } else {
                treeNodes[l].prev = root;
                treeNodes[l].next = treeNodes[root].next;
                treeNodes[treeNodes[l].next].prev = l;
                treeNodes[root].next = l;
                root = distributeTwoChildren(root, l);
            }
            return;
        }

        int subTree = insertInto(root, elem);
        int[] newChildren = insertRec(treeNodes[root].children[subTree].child, elem);

        if (newChildren.length == 1) {
            treeNodes[root].children[subTree].maxChild = max(newChildren[0]);
            treeNodes[root].children[subTree].child = newChildren[0];
            return;
        }

        if (treeNodes[root].cCount == 2) {
            treeNodes[root].children[subTree].maxChild = max(newChildren[0]);
            treeNodes[root].children[subTree].child = newChildren[0];
            treeNodes[root].cCount = 3;

            if (subTree == 0) {
                TreeLink tmpChild = treeNodes[root].children[1];
                treeNodes[root].children[1] = new TreeLink(max(newChildren[1]), newChildren[1]);
                treeNodes[root].children[2] = tmpChild;
            } else {
                treeNodes[root].children[2] = new TreeLink(max(newChildren[1]), newChildren[1]);
            }
            return;
        }

        int oldRoot = root;
        switch (subTree) {
            case 0:
                root = distributeFourChildren(
                        newChildren[0], newChildren[1],
                        treeNodes[oldRoot].children[1].child,
                        treeNodes[oldRoot].children[2].child);
                break;
            case 1:
                root = distributeFourChildren(
                        treeNodes[oldRoot].children[0].child,
                        newChildren[0], newChildren[1],
                        treeNodes[oldRoot].children[2].child);
                break;
            case 2:
                root = distributeFourChildren(
                        treeNodes[oldRoot].children[0].child,
                        treeNodes[oldRoot].children[1].child,
                        newChildren[0], newChildren[1]);
                break;
        }
        recycleNode(oldRoot);
    }

    private int deleteFrom(int t, double v) {
        for (int i = 0; i < treeNodes[t].cCount; i++) {
            if (v <= treeNodes[t].children[i].maxChild) {
                return i;
            }
        }
        return NULL_INDEX;
    }

    private int[] deleteRec(int t, TreeElement elem) {
        boolean allLeaves = true;
        int leafCount = 0;
        boolean foundLeaf = false;

        for (int i = 0; i < treeNodes[t].cCount; i++) {
            TreeLink c = treeNodes[t].children[i];
            boolean isLeafNode = isLeaf(c.child);
            allLeaves = allLeaves && isLeafNode;
            if (isLeafNode && (foundLeaf || !elem.Equal(treeNodes[c.child].elem))) {
                leafCount++;
            } else {
                foundLeaf = true;
            }
        }

        if (allLeaves) {
            int[] newChildren;
            switch (leafCount) {
                case 1:
                    newChildren = new int[1];
                    break;
                case 2:
                    newChildren = new int[2];
                    break;
                case 3:
                    newChildren = new int[3];
                    break;
                default:
                    newChildren = new int[0];
            }

            int index = 0;
            foundLeaf = false;
            for (int i = 0; i < treeNodes[t].cCount; i++) {
                TreeLink c = treeNodes[t].children[i];
                if (foundLeaf || !elem.Equal(treeNodes[c.child].elem)) {
                    newChildren[index++] = c.child;
                } else {
                    foundLeaf = true;
                    treeNodes[treeNodes[c.child].prev].next = treeNodes[c.child].next;
                    treeNodes[treeNodes[c.child].next].prev = treeNodes[c.child].prev;
                    recycleNode(c.child);
                }
            }

            return newChildren;
        }

        int deleteFrom = deleteFrom(t, elem.ExtractValue());
        if (deleteFrom == NULL_INDEX) {
            switch (treeNodes[t].cCount) {
                case 2:
                    twoElemTreeList[0] = treeNodes[t].children[0].child;
                    twoElemTreeList[1] = treeNodes[t].children[1].child;
                    return twoElemTreeList;
                case 3:
                    threeElemTreeList[0] = treeNodes[t].children[0].child;
                    threeElemTreeList[1] = treeNodes[t].children[1].child;
                    threeElemTreeList[2] = treeNodes[t].children[2].child;
                    return threeElemTreeList;
            }
            return new int[0];
        }

        int[] children = deleteRec(treeNodes[t].children[deleteFrom].child, elem);

        int oGCCount = 0;
        for (int i = 0; i < treeNodes[t].cCount; i++) {
            if (i != deleteFrom) {
                oGCCount += treeNodes[treeNodes[t].children[i].child].cCount;
            }
        }

        int index = 0;
        for (int i = 0; i < treeNodes[t].cCount; i++) {
            TreeLink c = treeNodes[t].children[i];
            if (i != deleteFrom) {
                for (int j = 0; j < treeNodes[c.child].cCount; j++) {
                    nineElemTreeList[index++] = treeNodes[c.child].children[j].child;
                }
                recycleNode(c.child);
            } else {
                for (int c2 : children) {
                    nineElemTreeList[index++] = c2;
                }
            }
        }

        return multipleNodesFromChildrenList(nineElemTreeList, oGCCount + children.length);
    }

    public void delete(TreeElement elem) {
        if (isEmpty(root)) {
            return;
        }

        if (isLeaf(root) && elem.Equal(treeNodes[root].elem)) {
            treeNodes[root].next = NULL_INDEX;
            treeNodes[root].prev = NULL_INDEX;
            treeNodes[root].elem = null;
            return;
        }

        int[] children = deleteRec(root, elem);

        int oldRoot = root;
        if (children.length == 1) {
            root = children[0];
        } else {
            root = nodeFromChildrenList(children, 0, children.length);
        }
        recycleNode(oldRoot);
    }

    private int findRec(int t, TreeElement elem) {
        if (isLeaf(t)) {
            if (elem.Equal(treeNodes[t].elem)) {
                return t;
            }
            return NULL_INDEX;
        }

        int subTree = deleteFrom(t, elem.ExtractValue());
        if (subTree == NULL_INDEX) {
            return NULL_INDEX;
        }

        return findRec(treeNodes[t].children[subTree].child, elem);
    }

    public int find(TreeElement elem) {
        if (isEmpty(root)) {
            return NULL_INDEX;
        }
        return findRec(root, elem);
    }

    private int findFirstLargerLeafRec(int t, double v) {
        if (isLeaf(t)) {
            if (v <= treeNodes[t].elem.ExtractValue()) {
                return t;
            }
            return NULL_INDEX;
        }

        int subTree = deleteFrom(t, v);
        if (subTree == NULL_INDEX) {
            return NULL_INDEX;
        }

        return findFirstLargerLeafRec(treeNodes[t].children[subTree].child, v);
    }

    public int findFirstLargerLeaf(double v) {
        if (isEmpty(root)) {
            return NULL_INDEX;
        }
        return findFirstLargerLeafRec(root, v);
    }

    public int previous(int t) {
        if (isEmpty(t)) {
            return NULL_INDEX;
        }
        if (isLeaf(t)) {
            return treeNodes[t].prev;
        }
        return NULL_INDEX;
    }

    public int next(int t) {
        if (isEmpty(t)) {
            return NULL_INDEX;
        }
        if (isLeaf(t)) {
            return treeNodes[t].next;
        }
        return NULL_INDEX;
    }

    private int[] minmaxDepth(int t) {
        if (isEmpty(t)) {
            return new int[]{0, 0};
        }
        if (isLeaf(t)) {
            return new int[]{1, 1};
        }
        int depthMin = -1;
        int depthMax = -1;

        for (int i = 0; i < treeNodes[t].cCount; i++) {
            TreeLink c = treeNodes[t].children[i];
            int[] depths = minmaxDepth(c.child);
            if (depthMin == -1 || depths[0] < depthMin) {
                depthMin = depths[0] + 1;
            }
            if (depthMax == -1 || depths[1] > depthMax) {
                depthMax = depths[1] + 1;
            }
        }
        return new int[]{depthMin, depthMax};
    }

    public int[] depths() {
        return minmaxDepth(root);
    }

    private int getSmallestLeafRec(int t) {
        if (isLeaf(t)) {
            return t;
        }
        return getSmallestLeafRec(treeNodes[t].children[0].child);
    }

    public int getSmallestLeaf() {
        if (isEmpty(root)) {
            return NULL_INDEX;
        }
        return getSmallestLeafRec(root);
    }

    public int getLargestLeaf() {
        int l = getSmallestLeaf();
        if (l == NULL_INDEX) {
            return NULL_INDEX;
        }
        return previous(l);
    }

    private boolean checkLinkedList(int startNode, int currentNode) {
        int visited = 0;
        int maxIterations = treeNodes.length * 2 + 10;

        while (visited < maxIterations) {
            int nextNode = treeNodes[currentNode].next;

            if (nextNode == NULL_INDEX) {
                return visited == 0;
            }

            boolean linkCheck = treeNodes[nextNode].prev == currentNode;

            if (startNode == nextNode) {
                return linkCheck;
            }

            boolean increasing = treeNodes[nextNode].elem.ExtractValue() >=
                    treeNodes[currentNode].elem.ExtractValue();

            if (!linkCheck || !increasing) {
                return false;
            }

            currentNode = nextNode;
            visited++;
        }

        return false;
    }

    private boolean leafListInvariant() {
        if (isEmpty(root)) {
            return true;
        }
        int startNode = getSmallestLeaf();
        return checkLinkedList(startNode, startNode);
    }

    private void preallocatedMemoryCheckRec(boolean[] s, int t) {
        if (t == NULL_INDEX) {
            return;
        }
        s[t] = true;
        for (int i = 0; i < treeNodes[t].cCount; i++) {
            preallocatedMemoryCheckRec(s, treeNodes[t].children[i].child);
        }
    }

    private void cachedMemoryCheck(boolean[] s) {
        for (int i = 0; i < treeNodesFreePositions.size(); i++) {
            s[treeNodesFreePositions.get(i)] = true;
        }
    }

    private boolean memoryCheck() {
        if (treeNodesFirstFreePos == 0) {
            return true;
        }
        boolean[] s = new boolean[treeNodesFirstFreePos];
        preallocatedMemoryCheckRec(s, root);
        cachedMemoryCheck(s);

        for (int i = 0; i < treeNodesFirstFreePos; i++) {
            if (!s[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean invariant() {
        int[] depths = depths();
        boolean linkedListCorrect = leafListInvariant();
        return depths[0] == depths[1] && linkedListCorrect;
    }

    private void pprint(int t, int indentation) {
        if (isEmpty(t)) {
            return;
        }

        if (isLeaf(t)) {
            if (indentation != 0) {
                System.out.print("  ");
            }
            for (int i = 0; i < indentation - 1; i++) {
                System.out.print("|  ");
            }
            System.out.print("|");
            TreeNode node = treeNodes[t];
            double prevVal = node.prev != NULL_INDEX ?
                    treeNodes[node.prev].elem.ExtractValue() : -1;
            double nextVal = node.next != NULL_INDEX ?
                    treeNodes[node.next].elem.ExtractValue() : -1;
            System.out.printf("--(prev: %.2f. value: %.2f. next: %.2f)\n",
                    prevVal, node.elem.ExtractValue(), nextVal);
            return;
        }

        for (int i = 0; i < treeNodes[t].cCount; i++) {
            TreeLink c = treeNodes[t].children[i];
            if (indentation != 0) {
                System.out.print("  ");
            }
            for (int j = 0; j < indentation - 1; j++) {
                System.out.print("|  ");
            }
            if (indentation != 0) {
                System.out.print("|");
            }
            System.out.printf("--%.0f\n", c.maxChild);
            pprint(c.child, indentation + 1);
        }
    }

    public void prettyPrint() {
        pprint(root, 0);
        System.out.println();
    }

    public class ForwardIterator {
        private static class StackEntry {
            int node;
            int state;

            StackEntry(int node, int state) {
                this.node = node;
                this.state = state;
            }
        }

        private ArrayDeque<StackEntry> stack;
        private double currentValue;
        private boolean isLeafValue;
        private boolean valid;

        public ForwardIterator() {
            stack = new ArrayDeque<>();
            valid = false;
        }

        private void pushLeft(int node) {
            while (node != NULL_INDEX && !isEmpty(node)) {
                stack.push(new StackEntry(node, 0));
                if (isLeaf(node)) {
                    break;
                }
                int childCount = treeNodes[node].cCount;
                if (childCount >= 1) {
                    node = treeNodes[node].children[0].child;
                } else {
                    break;
                }
            }
        }

        public void begin() {
            stack.clear();
            if (isEmpty(root)) {
                valid = false;
                return;
            }
            stack.push(new StackEntry(root, 0));
            advanceToNext();
        }

        public void end() {
            stack.clear();
            if (isEmpty(root)) {
                valid = false;
                return;
            }
            ArrayDeque<Double> values = new ArrayDeque<>();
            begin();
            while (valid) {
                values.addLast(currentValue);
                advanceToNext();
            }
            if (!values.isEmpty()) {
                Double last = values.getLast();
                currentValue = last;
                valid = true;
                isLeafValue = false;
            }
        }

        private void advanceToNext() {
            while (!stack.isEmpty()) {
                StackEntry entry = stack.pop();
                int node = entry.node;

                if (node == NULL_INDEX || isEmpty(node)) {
                    continue;
                }

                if (isLeaf(node)) {
                    currentValue = treeNodes[node].elem.ExtractValue();
                    isLeafValue = true;
                    valid = true;
                    return;
                }

                int childCount = treeNodes[node].cCount;

                if (childCount == 2) {
                    if (entry.state == 0) {
                        currentValue = treeNodes[node].children[0].maxChild;
                        isLeafValue = false;
                        stack.push(new StackEntry(node, 1));
                        int leftChild = treeNodes[node].children[0].child;
                        if (leftChild != NULL_INDEX) {
                            stack.push(new StackEntry(leftChild, 0));
                        }
                        valid = true;
                        return;
                    } else {
                        int rightChild = treeNodes[node].children[1].child;
                        if (rightChild != NULL_INDEX) {
                            stack.push(new StackEntry(rightChild, 0));
                        }
                    }
                } else if (childCount == 3) {
                    if (entry.state == 0) {
                        currentValue = treeNodes[node].children[0].maxChild;
                        isLeafValue = false;
                        stack.push(new StackEntry(node, 1));
                        int leftChild = treeNodes[node].children[0].child;
                        if (leftChild != NULL_INDEX) {
                            stack.push(new StackEntry(leftChild, 0));
                        }
                        valid = true;
                        return;
                    } else if (entry.state == 1) {
                        currentValue = treeNodes[node].children[1].maxChild;
                        isLeafValue = false;
                        stack.push(new StackEntry(node, 2));
                        int midChild = treeNodes[node].children[1].child;
                        if (midChild != NULL_INDEX) {
                            stack.push(new StackEntry(midChild, 0));
                        }
                        valid = true;
                        return;
                    } else {
                        int rightChild = treeNodes[node].children[2].child;
                        if (rightChild != NULL_INDEX) {
                            stack.push(new StackEntry(rightChild, 0));
                        }
                    }
                }
            }
            valid = false;
        }

        public boolean gotoByValue(double value) {
            begin();
            while (valid) {
                if (Math.abs(currentValue - value) < 0.0001) {
                    return true;
                }
                if (currentValue > value) {
                    break;
                }
                next();
            }
            return false;
        }

        public boolean gotoByKey(int keyIndex) {
            begin();
            int count = 0;
            while (valid && count < keyIndex) {
                next();
                count++;
            }
            return valid;
        }

        public boolean hasNext() {
            return valid;
        }

        public TreeElement get() {
            if (!valid) {
                return null;
            }
            if (isLeafValue) {
                int leafNode = findLeafWithValue(currentValue);
                if (leafNode != NULL_INDEX) {
                    return treeNodes[leafNode].elem;
                }
            }
            return null;
        }

        private int findLeafWithValue(double value) {
            if (isEmpty(root)) {
                return NULL_INDEX;
            }
            return findLeafWithValueRec(root, value);
        }

        private int findLeafWithValueRec(int node, double value) {
            if (isLeaf(node)) {
                if (Math.abs(treeNodes[node].elem.ExtractValue() - value) < 0.0001) {
                    return node;
                }
                return NULL_INDEX;
            }
            int subTree = deleteFrom(node, value);
            if (subTree == NULL_INDEX) {
                return NULL_INDEX;
            }
            return findLeafWithValueRec(treeNodes[node].children[subTree].child, value);
        }

        public int getCurrentNode() {
            if (!valid) {
                return NULL_INDEX;
            }
            if (isLeafValue) {
                return findLeafWithValue(currentValue);
            }
            return getCurrentKeyNode();
        }

        public int getCurrentKeyNode() {
            if (!valid || isLeafValue) {
                return NULL_INDEX;
            }
            double keyValue = currentValue;
            return findKeyNodeRec(root, keyValue);
        }

        private int findKeyNodeRec(int node, double keyValue) {
            if (node == NULL_INDEX || isEmpty(node)) {
                return NULL_INDEX;
            }
            if (isLeaf(node)) {
                return NULL_INDEX;
            }
            int childCount = treeNodes[node].cCount;
            for (int i = 0; i < childCount - 1; i++) {
                double maxChild = treeNodes[node].children[i].maxChild;
                if (Math.abs(maxChild - keyValue) < 0.0001) {
                    return node;
                }
            }
            int subTree = deleteFrom(node, keyValue);
            if (subTree == NULL_INDEX) {
                return NULL_INDEX;
            }
            return findKeyNodeRec(treeNodes[node].children[subTree].child, keyValue);
        }

        public double getCurrentKey() {
            return valid ? currentValue : 0;
        }

        public boolean isCurrentLeaf() {
            return valid && isLeafValue;
        }

        public boolean isValid() {
            return valid;
        }

        public void next() {
            if (!valid) {
                return;
            }
            advanceToNext();
        }

        public void previous() {
            if (!valid || isEmpty(root)) {
                return;
            }
            
            double targetValue = currentValue;
            ArrayDeque<Double> values = new ArrayDeque<>();
            
            begin();
            while (valid) {
                if (Math.abs(currentValue - targetValue) < 0.0001) {
                    break;
                }
                values.addLast(currentValue);
                advanceToNext();
            }
            
            if (!values.isEmpty()) {
                Double prevValue = values.getLast();
                gotoByValue(prevValue);
            } else {
                valid = false;
            }
        }

        public void deleteCurrent() {
            if (!valid) {
                return;
            }
            if (isLeafValue) {
                int leafNode = findLeafWithValue(currentValue);
                if (leafNode != NULL_INDEX) {
                    TreeElement elem = treeNodes[leafNode].elem;
                    Tree23.this.delete(elem);
                    begin();
                    return;
                }
            }
            valid = false;
        }
    }

    public ForwardIterator iterator() {
        return new ForwardIterator();
    }
}
