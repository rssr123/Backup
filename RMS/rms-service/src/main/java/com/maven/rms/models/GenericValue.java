package com.maven.rms.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericValue<T> {
    private T value;

    public GenericValue(T value) {
        this.value = value;
    }

    // public T getValue() {
    //     return value;
    // }

    // public void setValue(T value) {
    //     this.value = value;
    // }
}

