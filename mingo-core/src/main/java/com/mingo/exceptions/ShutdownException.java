package com.mingo.exceptions;


public class ShutdownException extends RuntimeException {

    /**
     * Default constructor.
     */
    public ShutdownException() {
    }

    /**
     * Constructor with parameters.
     *
     * @param message message
     */
    public ShutdownException(String message) {
        super(message);
    }

    /**
     * Constructor with parameters.
     *
     * @param cause cause
     */
    public ShutdownException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with parameters.
     *
     * @param message message
     * @param cause   cause
     */
    public ShutdownException(String message, Throwable cause) {
        super(message, cause);
    }
}