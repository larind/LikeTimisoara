package com.example.larisa.liketimisoara.db;

/**
 * Custom database exception.
 */

public class DBException extends Exception {

    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public DBException() {
        super();
    }


    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public DBException(String detailMessage) {
        super(detailMessage);
    }


    /**
     * Constructs a new {@code Exception} with the current stack trace, the
     * specified detail message and the specified cause.
     *
     * @param detailMessage the detail message for this exception.
     * @param throwable
     */
    public DBException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }


    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified cause.
     *
     * @param throwable the cause of this exception.
     */
    public DBException(Throwable throwable) {
        super(throwable);
    }
}
