package cl.duoc.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper=false)
public class BusinessRuleException extends Exception {
    private long id;
    private String code;
    private HttpStatus httpStatus;
    private String title;
    
    public BusinessRuleException(long id, String code, HttpStatus httpStatus, String message) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
        this.title = "Business Rule Exception";
    }
    
    public BusinessRuleException(String code, HttpStatus httpStatus, String message) {
        super(message);
        this.id = 0;
        this.code = code;
        this.httpStatus = httpStatus;
        this.title = "Business Rule Exception";
    }
    
     public BusinessRuleException(HttpStatus httpStatus, String message) {
        super(message);
        this.id = 0;
        this.code = "400";
        this.httpStatus = httpStatus;
        this.title = "Business Rule Exception";
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getCode() {
        return code;
    }
}
