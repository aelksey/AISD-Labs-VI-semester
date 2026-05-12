package com.rgr.tasks;

import com.rgr.model.*;

import java.util.*;

public class EulerCycleTask<V extends Vertex<K, D>, K, D, W, ED> {
    private Graph<V, K, D, W, ED> graph;
    private List<Object> cycle;
    private boolean valid;

    public static class CycleEdge<V, W, ED> {
        public final Edge<V, W, ED> edge;
        public final boolean isReverse;

        public CycleEdge(Edge<V, W, ED> edge, boolean isReverse) {
            this.edge = edge;
            this.isReverse = isReverse;
        }
    }

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

    @SuppressWarnings("unchecked")
    public void restart() {
        valid = false;
        cycle = new ArrayList<>();
        if (graph.isDirected()) return;
        for (V v : graph.getVertices()) {
            int degree = 0;
            Graph<V, K, D, W, ED>.OutgoingEdgeIterator it = graph.new OutgoingEdgeIterator(v);
            while (it.hasNext()) { it.next(); degree++; }
            if (degree % 2 != 0) return;
        }
        if (graph.V() == 0) return;
        Set<V> visited = new HashSet<>();
        dfs(graph.getVertex(0), visited);
        if (visited.size() != graph.V()) return;

        Map<Edge<V, W, ED>, Boolean> used = new HashMap<>();
        for (V v : graph.getVertices()) {
            Graph<V, K, D, W, ED>.OutgoingEdgeIterator it = graph.new OutgoingEdgeIterator(v);
            while (it.hasNext()) used.put(it.next(), false);
        }

        Deque<V> stack = new ArrayDeque<>();
        List<Edge<V, W, ED>> forwardEdges = new ArrayList<>();
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
                V next = e.getTarget() == u ? e.getSource() : e.getTarget();
                forwardEdges.add(e);
                stack.push(next);
            } else {
                stack.pop();
            }
        }

        if (forwardEdges.size() == graph.E()) {
            valid = true;
            List<CycleEdge<V, W, ED>> twoPassCycle = new ArrayList<>();
            for (Edge<V, W, ED> e : forwardEdges) {
                twoPassCycle.add(new CycleEdge<>(e, false));
            }
            for (int i = forwardEdges.size() - 1; i >= 0; i--) {
                twoPassCycle.add(new CycleEdge<>(forwardEdges.get(i), true));
            }
            cycle = (List<Object>) (List<?>) twoPassCycle;
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

    public List<CycleEdge<V, W, ED>> result() {
        List<CycleEdge<V, W, ED>> typedCycle = new ArrayList<>();
        for (Object obj : cycle) {
            typedCycle.add((CycleEdge<V, W, ED>) obj);
        }
        return typedCycle;
    }

    public boolean isValid() { return valid; }
}