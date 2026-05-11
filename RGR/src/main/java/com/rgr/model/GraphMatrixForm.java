package com.rgr.model;

import java.util.*;

public class GraphMatrixForm<V, E> extends GraphForm<V, E> {
    private List<List<List<E>>> matrix = new ArrayList<>(); // matrix[i][j] = список рёбер
    private boolean directed;

    public GraphMatrixForm(boolean directed) {
        this.directed = directed;
    }

    @Override
    public boolean insertVertex(int index) {
        int size = matrix.size();
        if (index < 0 || index > size) return false;
        // Create new row
        List<List<E>> newRow = new ArrayList<>();
        for (int i = 0; i < size; i++) newRow.add(new ArrayList<>());
        matrix.add(index, newRow);
        size++;
        // Add new column to all rows
        for (int i = 0; i < size; i++) {
            matrix.get(i).add(index, new ArrayList<>());
        }
        return true;
    }

    @Override
    public boolean deleteVertex(int index) {
        int size = matrix.size();
        if (index < 0 || index >= size) return false;
        // Remove all edges incident to this vertex (no need to delete edge objects separately)
        matrix.remove(index);
        size--;
        for (int i = 0; i < size; i++) {
            matrix.get(i).remove(index);
        }
        return true;
    }

    @Override
    public boolean insertEdge(int v1, int v2, E edge) {
        int size = matrix.size();
        if (v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return false;
        matrix.get(v1).get(v2).add(edge);
        return true;
    }

    @Override
    public boolean deleteEdge(int v1, int v2, E edge) {
        int size = matrix.size();
        if (v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return false;
        return matrix.get(v1).get(v2).remove(edge);
    }

    @Override
    public int deleteEdgesFromVertex(int index, boolean directed) {
        int size = matrix.size();
        if (index < 0 || index >= size) return 0;
        int deleted = 0;
        for (int j = 0; j < size; j++) {
            deleted += matrix.get(index).get(j).size();
            matrix.get(index).get(j).clear();
        }
        if (directed) {
            for (int i = 0; i < size; i++) {
                if (i != index) {
                    deleted += matrix.get(i).get(index).size();
                    matrix.get(i).get(index).clear();
                }
            }
        }
        return deleted;
    }

    @Override
    public boolean hasEdge(int v1, int v2) {
        int size = matrix.size();
        if (v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return false;
        return !matrix.get(v1).get(v2).isEmpty();
    }

    @Override
    public List<E> getEdges(int v1, int v2) {
        int size = matrix.size();
        if (v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) return new ArrayList<>();
        return matrix.get(v1).get(v2);
    }

    @Override
    public int vertexCount() {
        return matrix.size();
    }
}