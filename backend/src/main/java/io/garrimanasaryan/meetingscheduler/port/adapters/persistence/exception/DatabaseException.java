package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}