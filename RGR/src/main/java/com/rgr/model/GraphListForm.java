package com.rgr.model;

import java.util.*;

public class GraphListForm<V, E> extends GraphForm<V, E> {
    private static class Node<E> {
        E edge;
        int target;
        Node(E edge, int target) { this.edge = edge; this.target = target; }
    }
    private List<LinkedList<Node<E>>> adjList = new ArrayList<>();
    private boolean directed;

    public GraphListForm(boolean directed) {
        this.directed = directed;
    }

    @Override
    public boolean insertVertex(int index) {
        int size = adjList.size();
        if (index < 0 || index > size) return false;
        adjList.add(index, new LinkedList<>());
        size++;
        // Update indices in edges pointing to vertices with index >= new index
        for (int i = 0; i < size; i++) {
            if (i == index) continue;
            LinkedList<Node<E>> list = adjList.get(i);
            for (Node<E> node : list) {
                if (node.target >= index) node.target++;
            }
        }
        return true;
    }

    @Override
    public boolean deleteVertex(int index) {
        int size = adjList.size();
        if (index < 0 || index >= size) return false;
        adjList.remove(index);
        size--;
        // Update indices in edges that referenced this vertex
        for (int i = 0; i < size; i++) {
            LinkedList<Node<E>> list = adjList.get(i);
            Iterator<Node<E>> it = list.iterator();
            while (it.hasNext()) {
                Node<E> node = it.next();
                if (node.target == index) it.remove();
                else if (node.target > index) node.target--;
            }
        }
        return true;
    }

    @Override
    public boolean insertEdge(int v1, int v2, E edge) {
        int size = adjList.size();
        if (v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return false;
        if (!directed) {
            adjList.get(v1).add(new Node<>(edge, v2));
            adjList.get(v2).add(new Node<>(edge, v1));
        } else {
            adjList.get(v1).add(new Node<>(edge, v2));
        }
        return true;
    }

    @Override
    public boolean deleteEdge(int v1, int v2, E edge) {
        int size = adjList.size();
        if (v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return false;
        LinkedList<Node<E>> list = adjList.get(v1);
        Iterator<Node<E>> it = list.iterator();
        while (it.hasNext()) {
            Node<E> node = it.next();
            if (node.target == v2 && node.edge.equals(edge)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public int deleteEdgesFromVertex(int index, boolean directed) {
        int size = adjList.size();
        if (index < 0 || index >= size) return 0;
        int deleted = 0;
        deleted += adjList.get(index).size();
        adjList.get(index).clear();
        if (directed) {
            for (int i = 0; i < size; i++) {
                if (i == index) continue;
                LinkedList<Node<E>> list = adjList.get(i);
                Iterator<Node<E>> it = list.iterator();
                while (it.hasNext()) {
                    Node<E> node = it.next();
                    if (node.target == index) {
                        it.remove();
                        deleted++;
                    }
                }
            }
        }
        return deleted;
    }

    @Override
    public boolean hasEdge(int v1, int v2) {
        if (v1 < 0 || v2 < 0 || v1 >= adjList.size() || v2 >= adjList.size()) return false;
        for (Node<E> node : adjList.get(v1)) {
            if (node.target == v2) return true;
        }
        if (!directed) {
            for (Node<E> node : adjList.get(v2)) {
                if (node.target == v1) return true;
            }
        }
        return false;
    }

    @Override
    public List<E> getEdges(int v1, int v2) {
        List<E> result = new ArrayList<>();
        if (v1 < 0 || v2 < 0 || v1 >= adjList.size() || v2 >= adjList.size()) return result;
        for (Node<E> node : adjList.get(v1)) {
            if (node.target == v2) result.add(node.edge);
        }
        return result;
    }

    @Override
    public int vertexCount() {
        return adjList.size();
    }
}