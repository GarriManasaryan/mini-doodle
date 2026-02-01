package io.garrimanasaryan.meetingscheduler.domain.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}