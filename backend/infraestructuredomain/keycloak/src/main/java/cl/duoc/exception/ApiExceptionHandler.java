package cl.duoc.exception;

import java.net.UnknownHostException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<StandardAPIException> handleUnknownHostException(UnknownHostException ex) {
        StandardAPIException response = new StandardAPIException("Error de conexion","error-1024",ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
     @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<StandardAPIException> handleBusinessRuleException(BusinessRuleException ex) {
        StandardAPIException response = new StandardAPIException("Error de validacion",ex.getCode(),ex.getMessage());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

}
