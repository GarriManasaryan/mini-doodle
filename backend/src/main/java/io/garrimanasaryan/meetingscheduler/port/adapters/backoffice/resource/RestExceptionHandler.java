package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.domain.exception.DomainException;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.exception.DatabaseException;
import jakarta.validation.ConstraintViolationException;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.support.incrementer.HsqlMaxValueIncrementer;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBindingErrors(Exception ex){
        return Map.of("error", "Invalid request format");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingParam(MissingServletRequestParameterException ex){
        return Map.of(
                "error", "Missing required request parameter",
                "parameter", ex.getParameterName()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(RuntimeException ex){
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDb(DatabaseException ex){
        return Map.of("error", NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpected(Exception ex){
        // TODO: logging needed here
        return Map.of("error", "Unexpected error");
    }
}
