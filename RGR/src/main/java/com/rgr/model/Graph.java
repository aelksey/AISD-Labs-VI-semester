package com.rgr.model;

import java.util.*;

public class Graph<V extends Vertex<K, D>, K, D, W, ED> {
    private List<V> vertices = new ArrayList<>();
    private GraphForm<V, Edge<V, W, ED>> form;
    private boolean directed;
    private boolean dense;
    private int edgeCount = 0;

    // Constructors
    public Graph() {
        this.directed = false;
        this.dense = false;
        this.form = new GraphListForm<>(false);
    }

    public Graph(int vertexCount, boolean directed, boolean dense) {
        this.directed = directed;
        this.dense = dense;
        if (dense) form = new GraphMatrixForm<>(directed);
        else form = new GraphListForm<>(directed);
        for (int i = 0; i < vertexCount; i++) {
            form.insertVertex(i);
            V v = (V) new Vertex<K, D>();
            K name = (K) Integer.toString(i);
            v.setName(name);
            vertices.add(v);
        }
        this.edgeCount = 0;
    }

    public Graph(int vertexCount, boolean directed, boolean dense, boolean eulerian) {
        this(vertexCount, directed, dense);
        if (eulerian) {
            List<Integer> order = new ArrayList<>();
            for (int i = 0; i < vertexCount; i++) order.add(i);
            Collections.shuffle(order);
            for (int i = 0; i < vertexCount; i++) {
                int v1 = order.get(i);
                int v2 = order.get((i + 1) % vertexCount);
                Edge<V, W, ED> e = new Edge<>(vertices.get(v1), vertices.get(v2));
                if (form.insertEdge(v1, v2, e)) {
                    this.edgeCount++;
                }
            }
        }
    }

    public Graph(int vertexCount, int edgeCount, boolean directed, boolean dense, boolean eulerian) {
        this(vertexCount, directed, dense);
        if (eulerian) {
            List<Integer> order = new ArrayList<>();
            for (int i = 0; i < vertexCount; i++) order.add(i);
            Collections.shuffle(order);
            for (int i = 0; i < vertexCount; i++) {
                int v1 = order.get(i);
                int v2 = order.get((i + 1) % vertexCount);
                Edge<V, W, ED> e = new Edge<>(vertices.get(v1), vertices.get(v2));
                if (form.insertEdge(v1, v2, e)) {
                    this.edgeCount++;
                }
            }
        } else {
            if (edgeCount <= 0) return;
            Random rand = new Random();
            int maxEdges = directed ? vertexCount * (vertexCount - 1) : vertexCount * (vertexCount - 1) / 2;
            int actualEdgeCount = Math.min(edgeCount, maxEdges);
            while (this.edgeCount < actualEdgeCount) {
                int v1 = rand.nextInt(vertexCount);
                int v2 = rand.nextInt(vertexCount);
                if (v1 == v2 || hasEdge(v1, v2)) continue;
                Edge<V, W, ED> e = new Edge<>(vertices.get(v1), vertices.get(v2));
                if (form.insertEdge(v1, v2, e)) {
                    this.edgeCount++;
                }
            }
        }
    }

    public Graph(int vertexCount, int edgeCount, boolean directed, boolean dense) {
        this(vertexCount, directed, dense);
        if (edgeCount <= 0) return;
        Random rand = new Random();
        int maxEdges = directed ? vertexCount * (vertexCount - 1) : vertexCount * (vertexCount - 1) / 2;
        edgeCount = Math.min(edgeCount, maxEdges);
        while (this.edgeCount < edgeCount) {
            int v1 = rand.nextInt(vertexCount);
            int v2 = rand.nextInt(vertexCount);
            if (v1 == v2 || hasEdge(v1, v2)) continue;
            Edge<V, W, ED> e = new Edge<>(vertices.get(v1), vertices.get(v2));
            if (form.insertEdge(v1, v2, e)) {
                this.edgeCount++;
            }
        }
    }

    // Copy constructor
    public Graph(Graph<V, K, D, W, ED> other) {
        this.directed = other.directed;
        this.dense = other.dense;
        this.edgeCount = other.edgeCount;
        this.vertices = new ArrayList<>(other.vertices);
        if (dense) this.form = new GraphMatrixForm<>(directed);
        else this.form = new GraphListForm<>(directed);
        // Copy edges
        for (int i = 0; i < vertices.size(); i++) {
            form.insertVertex(i);
        }
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (other.hasEdge(i, j)) {
                    Edge<V, W, ED> e = other.getEdge(i, j);
                    Edge<V, W, ED> newE = new Edge<>(vertices.get(i), vertices.get(j), e.getWeight(), e.getData());
                    form.insertEdge(i, j, newE);
                }
            }
        }
    }

    // Basic operations
    public int V() { return vertices.size(); }
    public int E() { return edgeCount; }
    public boolean isDirected() { return directed; }
    public boolean isDense() { return dense; }

    public double K() {
        int max = vertices.size() * (vertices.size() - 1);
        if (!directed) max /= 2;
        if (max == 0) return -1;
        return (double) edgeCount / max;
    }

    public void toListGraph() {
        if (!dense) return;
        GraphForm<V, Edge<V, W, ED>> newForm = new GraphListForm<>(directed);
        for (int i = 0; i < vertices.size(); i++) newForm.insertVertex(i);
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (hasEdge(i, j)) {
                    Edge<V, W, ED> e = getEdge(i, j);
                    newForm.insertEdge(i, j, e);
                }
            }
        }
        this.form = newForm;
        this.dense = false;
    }

    public void toMatrixGraph() {
        if (dense) return;
        GraphForm<V, Edge<V, W, ED>> newForm = new GraphMatrixForm<>(directed);
        for (int i = 0; i < vertices.size(); i++) newForm.insertVertex(i);
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (hasEdge(i, j)) {
                    Edge<V, W, ED> e = getEdge(i, j);
                    newForm.insertEdge(i, j, e);
                }
            }
        }
        this.form = newForm;
        this.dense = true;
    }

    public V insertVertex() {
        V v = (V) new Vertex<K, D>();
        v.setName((K) Integer.toString(vertices.size()));
        form.insertVertex(vertices.size());
        vertices.add(v);
        return v;
    }

    public boolean deleteVertex(V v) {
        int index = getIndex(v);
        edgeCount -= form.deleteEdgesFromVertex(index, directed);
        if (form.deleteVertex(index)) {
            vertices.remove(index);
            return true;
        }
        return false;
    }

    public boolean deleteVertex(int index) {
        if (index < 0 || index >= vertices.size()) return false;
        deleteVertex(vertices.get(index));
        return true;
    }

    public boolean deleteVertexById(int id) {
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).getId() == id) {
                deleteVertex(i);
                return true;
            }
        }
        return false;
    }

    public Edge<V, W, ED> insertEdge(V v1, V v2) {
        int i1 = getIndex(v1);
        int i2 = getIndex(v2);
        Edge<V, W, ED> e = new Edge<>(v1, v2);
        form.insertEdge(i1, i2, e);
        edgeCount++;
        return e;
    }

    public boolean deleteEdge(V v1, V v2) {
        int i1 = getIndex(v1);
        int i2 = getIndex(v2);
        List<Edge<V, W, ED>> edges = form.getEdges(i1, i2);
        if (!edges.isEmpty()) {
            Edge<V, W, ED> e = edges.get(0);
            if (form.deleteEdge(i1, i2, e)) {
                edgeCount--;
                return true;
            }
        }
        return false;
    }

    public boolean deleteEdgeById(int id) {
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                List<Edge<V, W, ED>> edges = form.getEdges(i, j);
                for (Edge<V, W, ED> e : edges) {
                    if (e.getId() == id) {
                        form.deleteEdge(i, j, e);
                        edgeCount--;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasEdge(int i1, int i2) {
        return !form.getEdges(i1, i2).isEmpty();
    }

    public Edge<V, W, ED> getEdge(V v1, V v2) {
        return getEdge(getIndex(v1), getIndex(v2));
    }

    public Edge<V, W, ED> getEdge(int i1, int i2) {
        List<Edge<V, W, ED>> edges = form.getEdges(i1, i2);
        if (edges.isEmpty()) throw new IllegalArgumentException("No edge");
        return edges.get(0);
    }

    public List<Edge<V, W, ED>> getEdges(int i, int j) {
        return form.getEdges(i, j);
    }

    public V getVertex(int index) {
        if (index < 0 || index >= vertices.size()) throw new IndexOutOfBoundsException();
        return vertices.get(index);
    }

    public int getIndex(V v) {
        int idx = vertices.indexOf(v);
        if (idx == -1) throw new IllegalArgumentException("Vertex not in graph");
        return idx;
    }

    // Helper methods for GUI
    public List<V> getVertices() { return Collections.unmodifiableList(vertices); }

    // ---------- Iterators ----------
    public class VertexIterator implements Iterator<V> {
        private int cursor = 0;
        private int lastRet = -1;
        private int modCount = vertices.size();

        @Override
        public boolean hasNext() {
            return cursor < vertices.size();
        }

        @Override
        public V next() {
            if (!hasNext()) throw new NoSuchElementException();
            lastRet = cursor;
            return vertices.get(cursor++);
        }

        @Override
        public void remove() {
            if (lastRet < 0) throw new IllegalStateException();
            deleteVertex(lastRet);
            if (lastRet < cursor) cursor--;
            lastRet = -1;
            modCount++;
        }

        public void reset() { cursor = 0; lastRet = -1; }
        public boolean isEnd() { return cursor >= vertices.size(); }
    }

    public class EdgeIterator implements Iterator<Edge<V, W, ED>> {
        private int row = 0, col = 0;
        private int lastRow = -1, lastCol = -1;

        public EdgeIterator() {
            advanceToNextEdge();
        }

        private void advanceToNextEdge() {
            while (row < vertices.size()) {
                while (col < vertices.size()) {
                    if (hasEdge(row, col)) {
                        return;
                    }
                    col++;
                }
                row++;
                col = 0;
            }
        }

        @Override
        public boolean hasNext() {
            return row < vertices.size();
        }

        @Override
        public Edge<V, W, ED> next() {
            if (!hasNext()) throw new NoSuchElementException();
            Edge<V, W, ED> e = getEdge(row, col);
            lastRow = row; lastCol = col;
            col++;
            advanceToNextEdge();
            return e;
        }

        @Override
        public void remove() {
            if (lastRow < 0) throw new IllegalStateException();
            deleteEdge(getVertex(lastRow), getVertex(lastCol));
            // adjust current position
            if (lastRow < row) {
                // already passed
            } else if (lastRow == row && lastCol < col) {
                col--;
            }
            lastRow = -1; lastCol = -1;
        }
    }

    public class OutgoingEdgeIterator implements Iterator<Edge<V, W, ED>> {
        private int sourceIdx;
        private int curCol = 0;
        private int lastCol = -1;

        public OutgoingEdgeIterator(V source) {
            this.sourceIdx = getIndex(source);
            advanceToNext();
        }

        private void advanceToNext() {
            while (curCol < vertices.size()) {
                if (hasEdge(sourceIdx, curCol)) return;
                curCol++;
            }
        }

        @Override
        public boolean hasNext() {
            return curCol < vertices.size();
        }

        @Override
        public Edge<V, W, ED> next() {
            if (!hasNext()) throw new NoSuchElementException();
            Edge<V, W, ED> e = getEdge(sourceIdx, curCol);
            lastCol = curCol;
            curCol++;
            advanceToNext();
            return e;
        }

        @Override
        public void remove() {
            if (lastCol < 0) throw new IllegalStateException();
            deleteEdge(vertices.get(sourceIdx), vertices.get(lastCol));
            if (lastCol < curCol) curCol--;
            lastCol = -1;
        }
    }
}