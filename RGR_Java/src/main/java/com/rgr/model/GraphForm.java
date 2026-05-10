package com.rgr.model;

import java.util.List;

public abstract class GraphForm<V, E> {
    public abstract boolean insertVertex(int index);
    public abstract boolean deleteVertex(int index);
    public abstract boolean insertEdge(int v1, int v2, E edge);
    public abstract boolean deleteEdge(int v1, int v2, E edge);
    public abstract int deleteEdgesFromVertex(int index, boolean directed);
    public abstract boolean hasEdge(int v1, int v2);
    public abstract List<E> getEdges(int v1, int v2);
    public abstract int vertexCount();
}