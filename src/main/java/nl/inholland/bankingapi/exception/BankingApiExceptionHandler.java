package nl.inholland.bankingapi.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.dto.ExceptionDTO;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
@Log
public class BankingApiExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {DataIntegrityViolationException.class, JdbcSQLIntegrityConstraintViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolation(Exception e
            , WebRequest webRequest) {
        log.severe(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        400,
                        e.getMessage(),
                        e.getClass().getName()
                ));
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException,
                                                                WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ExceptionDTO(
                                404,
                                entityNotFoundException.getMessage(),
                                entityNotFoundException.getClass().getName()
                                )
                );
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException authenticationException,
                                                                WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        new ExceptionDTO(
                                403,
                                authenticationException.getMessage(),
                                authenticationException.getClass().getName()
                        )
                );
    }
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException apiRequestException,
                                                                WebRequest webRequest) {
        return ResponseEntity.status(apiRequestException.getStatus())
                .body(
                        new ExceptionDTO(
                                apiRequestException.getStatus().value(),
                                apiRequestException.getMessage(),
                                apiRequestException.getClass().getName()
                        )
                );
    }
}
