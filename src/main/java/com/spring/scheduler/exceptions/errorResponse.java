package com.spring.scheduler.exceptions;
import org.springframework.http.HttpStatus;
import java.io.Serializable;
public class errorResponse  {		


    public String errorType;

    public String message;

    public String description;

    public errorResponse() {
    	
    	
    }
     

    public errorResponse(String errorType,String message,String description){
        this.errorType = errorType;
        this.message = message;
        this.description = description;
    }

}
