package com.mingo.exceptions;


public class IdGenerationException extends RuntimeException {

    /**
     * Default constructor.
     */
    public IdGenerationException() {
    }

    /**
     * Constructor with parameters.
     *
     * @param message message
     */
    public IdGenerationException(String message) {
        super(message);
    }

    /**
     * Constructor with parameters.
     *
     * @param cause cause
     */
    public IdGenerationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with parameters.
     *
     * @param message message
     * @param cause   cause
     */
    public IdGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}