package com.rgr.tasks;

import com.rgr.model.*;

import java.util.*;

public class EulerCycleTask<V extends Vertex<K, D>, K, D, W, ED> {
    private Graph<V, K, D, W, ED> graph;
    private List<Edge<V, W, ED>> cycle;
    private boolean valid;

    public EulerCycleTask(Graph<V, K, D, W, ED> g) {
        set(g);
    }

    public EulerCycleTask(EulerCycleTask<V, K, D, W, ED> other) {
        this.graph = new Graph<>(other.graph);
        this.cycle = new ArrayList<>(other.cycle);
        this.valid = other.valid;
    }

    public void set(Graph<V, K, D, W, ED> g) {
        this.graph = g;
        restart();
    }

    public void restart() {
        valid = false;
        cycle = new ArrayList<>();
        if (graph.isDirected()) return;
        // Check all vertices even degree and connectivity
        for (V v : graph.getVertices()) {
            int degree = 0;
            Graph<V, K, D, W, ED>.OutgoingEdgeIterator it = graph.new OutgoingEdgeIterator(v);
            while (it.hasNext()) { it.next(); degree++; }
            if (degree % 2 != 0) return;
        }
        // Check connectivity (simple DFS)
        if (graph.V() == 0) return;
        Set<V> visited = new HashSet<>();
        dfs(graph.getVertex(0), visited);
        if (visited.size() != graph.V()) return;
        // Build Eulerian cycle using Hierholzer's algorithm (two-pass)
        Map<Edge<V, W, ED>, Boolean> used = new HashMap<>();
        for (V v : graph.getVertices()) {
            Graph<V, K, D, W, ED>.OutgoingEdgeIterator it = graph.new OutgoingEdgeIterator(v);
            while (it.hasNext()) used.put(it.next(), false);
        }
        Deque<V> stack = new ArrayDeque<>();
        List<Edge<V, W, ED>> cycleEdges = new ArrayList<>();
        stack.push(graph.getVertex(0));
        while (!stack.isEmpty()) {
            V u = stack.peek();
            Graph<V, K, D, W, ED>.OutgoingEdgeIterator it = graph.new OutgoingEdgeIterator(u);
            Edge<V, W, ED> e = null;
            while (it.hasNext()) {
                Edge<V, W, ED> cand = it.next();
                if (!used.get(cand)) {
                    e = cand;
                    break;
                }
            }
            if (e != null) {
                used.put(e, true);
                V next = e.getTarget() == u ? e.getSource() : e.getTarget(); // undirected
                stack.push(next);
            } else {
                stack.pop();
                if (!stack.isEmpty()) {
                    V prev = stack.peek();
                    Edge<V, W, ED> backEdge = null;
                    Graph<V, K, D, W, ED>.OutgoingEdgeIterator backIt = graph.new OutgoingEdgeIterator(prev);
                    while (backIt.hasNext()) {
                        Edge<V, W, ED> cand = backIt.next();
                        if ((cand.getSource() == prev && cand.getTarget() == u) ||
                            (cand.getTarget() == prev && cand.getSource() == u)) {
                            backEdge = cand;
                            break;
                        }
                    }
                    if (backEdge != null) cycleEdges.add(backEdge);
                }
            }
        }
        if (cycleEdges.size() == graph.E()) {
            valid = true;
            cycle = cycleEdges;
        }
    }

    private void dfs(V v, Set<V> visited) {
        visited.add(v);
        Graph<V, K, D, W, ED>.OutgoingEdgeIterator it = graph.new OutgoingEdgeIterator(v);
        while (it.hasNext()) {
            Edge<V, W, ED> e = it.next();
            V neighbor = e.getTarget() == v ? e.getSource() : e.getTarget();
            if (!visited.contains(neighbor)) dfs(neighbor, visited);
        }
    }

    public List<Edge<V, W, ED>> result() {
        return Collections.unmodifiableList(cycle);
    }

    public boolean isValid() { return valid; }
}