package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.domain.exception.DomainException;
import io.garrimanasaryan.meetingscheduler.domain.exception.ValidationException;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.exception.DatabaseException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

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

    // @ExceptionHandler(RuntimeException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public Map<String, String> handleValidationExceptions(RuntimeException ex){
    //     return Map.of("error", ex.getMessage());
    // }

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(DomainException ex){
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDb(DatabaseException ex){
        return Map.of("error", NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExc(ValidationException ex){
        return Map.of("error", NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
    }

}
