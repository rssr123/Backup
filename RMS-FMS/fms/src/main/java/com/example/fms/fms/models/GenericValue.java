package com.example.fms.fms.models;

public class GenericValue<T> {
    private T value;

    public GenericValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

