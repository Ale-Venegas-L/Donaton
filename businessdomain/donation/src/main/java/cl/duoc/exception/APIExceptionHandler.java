package cl.duoc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        ExceptionResponse response = new ExceptionResponse(
            "Técnico",
            "Error inesperado",
            "500",
            ex.getMessage(),
            ""
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessRuleException(BusinessRuleException ex) {
        ExceptionResponse response = new ExceptionResponse(
            "Negocio",
            ex.getTitle(),
            ex.getCode(),
            ex.getMessage(),
            ""
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
