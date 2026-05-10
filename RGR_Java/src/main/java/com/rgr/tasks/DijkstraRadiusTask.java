package com.rgr.tasks;

import com.rgr.model.*;

import java.util.*;

public class DijkstraRadiusTask<V extends Vertex<K, D>, K, D, W, ED> {
    private Graph<V, K, D, W, ED> graph;
    private V center;
    private int radius;
    private List<V> radiusPath;
    private boolean valid;

    public DijkstraRadiusTask(Graph<V, K, D, W, ED> g) {
        set(g);
    }

    public DijkstraRadiusTask(DijkstraRadiusTask<V, K, D, W, ED> other) {
        this.graph = new Graph<>(other.graph);
        this.center = other.center;
        this.radius = other.radius;
        this.radiusPath = new ArrayList<>(other.radiusPath);
        this.valid = other.valid;
    }

    public void set(Graph<V, K, D, W, ED> g) {
        this.graph = g;
        restart();
    }

    public void restart() {
        valid = false;
        if (!graph.isDirected()) return; // задача для орграфа
        int n = graph.V();
        if (n == 0) return;

        int[][] dist = new int[n][n];
        int INF = Integer.MAX_VALUE / 2;
        for (int i = 0; i < n; i++) Arrays.fill(dist[i], INF);
        for (int i = 0; i < n; i++) dist[i][i] = 0;
        // Fill edges
        for (int i = 0; i < n; i++) {
            V u = graph.getVertex(i);
            Graph<V, K, D, W, ED>.OutgoingEdgeIterator it = graph.new OutgoingEdgeIterator(u);
            while (it.hasNext()) {
                Edge<V, W, ED> e = it.next();
                int j = graph.getIndex(e.getTarget());
                if (e.isWeightSet()) {
                    int w = (Integer) e.getWeight(); // assuming weights are integers
                    dist[i][j] = Math.min(dist[i][j], w);
                }
            }
        }

        // Dijkstra from each vertex to compute shortest paths
        int[] eccentricity = new int[n];
        for (int s = 0; s < n; s++) {
            int[] d = new int[n];
            Arrays.fill(d, INF);
            d[s] = 0;
            boolean[] used = new boolean[n];
            for (int it = 0; it < n; it++) {
                int u = -1;
                for (int i = 0; i < n; i++) {
                    if (!used[i] && (u == -1 || d[i] < d[u])) u = i;
                }
                if (u == -1 || d[u] == INF) break;
                used[u] = true;
                for (int v = 0; v < n; v++) {
                    if (dist[u][v] < INF && d[v] > d[u] + dist[u][v]) {
                        d[v] = d[u] + dist[u][v];
                    }
                }
            }
            int maxDist = 0;
            for (int i = 0; i < n; i++) {
                if (i != s && d[i] < INF) maxDist = Math.max(maxDist, d[i]);
            }
            eccentricity[s] = maxDist;
        }

        // Find vertex with minimum eccentricity (center)
        int minEcc = INF;
        int centerIdx = -1;
        for (int i = 0; i < n; i++) {
            if (eccentricity[i] > 0 && eccentricity[i] < minEcc) {
                minEcc = eccentricity[i];
                centerIdx = i;
            }
        }
        if (centerIdx == -1) return;
        radius = minEcc;
        center = graph.getVertex(centerIdx);

        // Build path from center to farthest vertex (the one that achieved eccentricity)
        int farthestIdx = -1;
        for (int i = 0; i < n; i++) {
            if (i != centerIdx && dist[centerIdx][i] < INF && 
                (farthestIdx == -1 || dist[centerIdx][i] > dist[centerIdx][farthestIdx])) {
                farthestIdx = i;
            }
        }
        if (farthestIdx != -1) {
            radiusPath = reconstructPath(centerIdx, farthestIdx, dist);
        } else {
            radiusPath = new ArrayList<>();
        }
        valid = true;
    }

    private List<V> reconstructPath(int src, int dst, int[][] dist) {
        // simple reconstruction using parent array from Dijkstra (re-run)
        int n = graph.V();
        int[] d = new int[n];
        int[] parent = new int[n];
        Arrays.fill(d, Integer.MAX_VALUE / 2);
        Arrays.fill(parent, -1);
        d[src] = 0;
        boolean[] used = new boolean[n];
        for (int it = 0; it < n; it++) {
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!used[i] && (u == -1 || d[i] < d[u])) u = i;
            }
            if (u == -1 || d[u] == Integer.MAX_VALUE / 2) break;
            used[u] = true;
            for (int v = 0; v < n; v++) {
                if (graph.hasEdge(u, v)) {
                    int w = (Integer) graph.getEdge(u, v).getWeight();
                    if (d[v] > d[u] + w) {
                        d[v] = d[u] + w;
                        parent[v] = u;
                    }
                }
            }
        }
        List<V> path = new ArrayList<>();
        int cur = dst;
        while (cur != -1) {
            path.add(0, graph.getVertex(cur));
            cur = parent[cur];
        }
        return path;
    }

    public V getCenter() { return center; }
    public int getRadius() { return radius; }
    public List<V> getRadiusPath() { return Collections.unmodifiableList(radiusPath); }
    public boolean isValid() { return valid; }
    public Object result() { return this; } // or return string representation
}