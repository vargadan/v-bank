package com.dani.vbank.model.primitive;

import java.io.Serializable;
import java.util.Objects;

public class Username implements Serializable {

    private final String value;

    public Username(String username) {
        this.value = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username that = (Username) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public String getValue() {
        return value;
    }
}
