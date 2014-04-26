package com.mingo.exceptions;


public class MingoException extends RuntimeException {

    public MingoException() {
    }

    public MingoException(String message) {
        super(message);
    }

    public MingoException(String message, Throwable cause) {
        super(message, cause);
    }

    public MingoException(Throwable cause) {
        super(cause);
    }
}
