package com.marvel.exceptions;

public class IncorrectFieldException extends RuntimeException {

    private final String field;

    public IncorrectFieldException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }

}
