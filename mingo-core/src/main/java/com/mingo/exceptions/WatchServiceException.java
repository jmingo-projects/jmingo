package com.mingo.exceptions;

/**
 * Created by dmgcodevil on 22.05.2014.
 */
public class WatchServiceException extends RuntimeException {

    public WatchServiceException() {
    }

    public WatchServiceException(String message) {
        super(message);
    }

    public WatchServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WatchServiceException(Throwable cause) {
        super(cause);
    }
}
