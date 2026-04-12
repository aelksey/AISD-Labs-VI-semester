package com.lab;

public class Element implements TreeElement {
    private final int E;

    public Element(int E) {
        this.E = E;
    }

    @Override
    public double ExtractValue() {
        return (double) E;
    }

    @Override
    public boolean Equal(TreeElement e) {
        return e instanceof Element other && this.E == other.E;
    }

    public int getE() {
        return E;
    }

    @Override
    public String toString() {
        return "Element{E=" + E + "}";
    }
}
