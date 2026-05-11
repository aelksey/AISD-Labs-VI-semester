package com.rgr.model;

public class Edge<V, W, D> {
    private static int nextId = 0;
    private final int id;
    private V source;
    private V target;
    private W weight;
    private D data;
    private boolean weightSet;
    private boolean dataSet;

    public Edge(V source, V target) {
        this.id = nextId++;
        this.source = source;
        this.target = target;
        this.weightSet = false;
        this.dataSet = false;
    }

    public Edge(V source, V target, W weight) {
        this(source, target);
        this.weight = weight;
        this.weightSet = true;
    }

    public Edge(V source, V target, W weight, D data) {
        this(source, target, weight);
        this.data = data;
        this.dataSet = true;
    }

    public int getId() { return id; }

    public V getSource() { return source; }
    public V getTarget() { return target; }

    public void setWeight(W weight) {
        this.weight = weight;
        this.weightSet = true;
    }

    public void setData(D data) {
        this.data = data;
        this.dataSet = true;
    }

    public W getWeight() { return weight; }
    public D getData() { return data; }
    public boolean isWeightSet() { return weightSet; }
    public boolean isDataSet() { return dataSet; }
}