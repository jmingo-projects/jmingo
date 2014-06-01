package com.mingo.exceptions;


public class WatchServiceException extends RuntimeException {

    /**
     * Default constructor.
     */
    public WatchServiceException() {
    }

    /**
     * Constructor with parameters.
     *
     * @param message message
     */
    public WatchServiceException(String message) {
        super(message);
    }

    /**
     * Constructor with parameters.
     *
     * @param cause cause
     */
    public WatchServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with parameters.
     *
     * @param message message
     * @param cause   cause
     */
    public WatchServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}