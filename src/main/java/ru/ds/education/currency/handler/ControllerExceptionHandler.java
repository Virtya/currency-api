package ru.ds.education.currency.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.ds.education.currency.dto.error.Error;
import ru.ds.education.currency.exception.ResourceAlreadyExistException;
import ru.ds.education.currency.exception.ResourceNotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public @ResponseBody ResponseEntity<Error> handleResourceNotFoundException(Exception e){

        LocalDate timestamp = LocalDate.now();

        return new ResponseEntity<>(
                    new Error(e.getMessage(),timestamp),
                    HttpStatus.NOT_FOUND
        );
    }


    @ExceptionHandler(DateTimeParseException.class)
    public @ResponseBody ResponseEntity<Error> handleDateTimeParseException(Exception e){

        LocalDate timestamp = LocalDate.now();

        return new ResponseEntity<>(
                new Error(e.getMessage() + ". Example: 2023-06-27",timestamp),
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public @ResponseBody ResponseEntity<Error> handleResourceAlreadyExistsException(Exception e){

        LocalDate timestamp = LocalDate.now();

        return new ResponseEntity<>(
                    new Error(e.getMessage(),timestamp),
                    HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody ResponseEntity<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

        LocalDate timestamp = LocalDate.now();

        return new ResponseEntity<>(
                new Error(Objects.requireNonNull(e.getFieldError()).getDefaultMessage(), timestamp),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public @ResponseBody ResponseEntity<Error> handleMethodArgumentNotValidException(ConstraintViolationException e){

        LocalDate timestamp = LocalDate.now();

        return new ResponseEntity<>(
                new Error(Objects.requireNonNull(e.getLocalizedMessage()), timestamp),
                HttpStatus.CONFLICT
        );
    }
}
