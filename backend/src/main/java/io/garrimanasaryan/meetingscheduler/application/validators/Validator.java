package io.garrimanasaryan.meetingscheduler.application.validators;

public interface Validator<T> {
    void validate(T t);
}
