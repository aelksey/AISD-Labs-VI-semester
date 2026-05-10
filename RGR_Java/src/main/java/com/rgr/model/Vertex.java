package com.rgr.model;

public class Vertex<K, D> {
    private K name;
    private D data;
    private boolean nameSet;
    private boolean dataSet;

    public Vertex() {
        this.nameSet = false;
        this.dataSet = false;
    }

    public Vertex(K name, D data) {
        this.name = name;
        this.data = data;
        this.nameSet = true;
        this.dataSet = true;
    }

    public void setName(K name) {
        this.name = name;
        this.nameSet = true;
    }

    public void setData(D data) {
        this.data = data;
        this.dataSet = true;
    }

    public K getName() {
        return name;
    }

    public D getData() {
        return data;
    }

    public boolean isNameSet() {
        return nameSet;
    }

    public boolean isDataSet() {
        return dataSet;
    }
}