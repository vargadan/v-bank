package com.dani.vbank.model.primitive;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.Objects;

public class Username implements Serializable {

    //Username shall consists only word characters and be at least 3 at most 30 chars long
    public static final String USERNAME_PATTERN = "^\\w{3,30}?$";

    private final String value;

    public Username(String username) {
        if (username == null || username.trim().length() == 0) {
            //it cannot be null
            throw new ValidationException("Username is required");
        } else if (username.trim().length() < 3) {
            //it should be 11 long
            throw new ValidationException("Username should be at least 3 characters long");
        } else if (username.trim().length() > 30) {
            //it should be 11 long
            throw new ValidationException("Username should be no longer than 30 characters");
        } else if (!username.matches(USERNAME_PATTERN)) {
            //it has to match patter
            throw new ValidationException("Username is in invalid format");
        }
        this.value = username.trim();
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
