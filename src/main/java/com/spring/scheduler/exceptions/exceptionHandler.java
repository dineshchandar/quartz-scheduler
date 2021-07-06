 package com.spring.scheduler.exceptions;

 import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;


 @ControllerAdvice
 public class exceptionHandler {
	 
	Logger log = LoggerFactory.getLogger(this.getClass());
	private static String responsemessage =null;
	private static int ConstraintCounter =0; 
	private static int TransactionCounter =0;
	private static int MethodargumentCounter =0;
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handle404Exception(NotFoundException e) {
    	errorResponse err = new errorResponse(HttpStatus.NOT_FOUND.name(), e.getMessage(),e.getLocalizedMessage());
       return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    } 

   
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullException(NullPointerException e) {
      	errorResponse err = new errorResponse(HttpStatus.BAD_REQUEST.name(), e.getMessage(),e.getLocalizedMessage());
       return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
    
   	
 	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?>  handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
      	ex.getBindingResult().getFieldErrors().forEach(error -> {
      		if(MethodargumentCounter ==0) {
				responsemessage =error.getDefaultMessage();
			}else {
				responsemessage =responsemessage+","+error.getDefaultMessage();
			}
      		MethodargumentCounter++;
      	});
      	
      	errorResponse err = new errorResponse(HttpStatus.BAD_REQUEST.name(),responsemessage,"Validation Errors");
      	MethodargumentCounter =0;
		return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
		Map<String, String> errors = new HashMap<>();
		 String errormessages ="";
	
		ex.getConstraintViolations().forEach(cv -> {
			errors.put("message",cv.getMessageTemplate());
			if(ConstraintCounter ==0) {
				responsemessage =cv.getMessageTemplate();
			}else {
				responsemessage =responsemessage+","+cv.getMessageTemplate();
			}
			ConstraintCounter++;
		});	
		
		errorResponse err = new errorResponse(HttpStatus.BAD_REQUEST.name(),responsemessage,"Validation Errors");
		ConstraintCounter =0;
		return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({TransactionSystemException.class})
	protected ResponseEntity<Object> handlePersistenceException(final Exception ex) {
	    Throwable cause = ((TransactionSystemException) ex).getRootCause();
	    if (cause instanceof ConstraintViolationException) {        
	       ConstraintViolationException consEx= (ConstraintViolationException) cause;
	        final List<String> errors = new ArrayList<String>();
	        for (final ConstraintViolation<?> violation : consEx.getConstraintViolations()) {
	        	   if(TransactionCounter ==0) {
	   				responsemessage =violation.getMessageTemplate();
	   			}else {
	   				responsemessage =responsemessage+","+violation.getMessageTemplate();
	   			}
	        	   TransactionCounter++;
	        }

	    }
	    errorResponse err = new errorResponse(HttpStatus.BAD_REQUEST.name(),responsemessage,"Validation Errors");
	    TransactionCounter=0;
	    return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
	}
    
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnhandledException(Exception e) {
    	errorResponse err = new errorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), "Internal Error","Message Processing failed due to some internal technical error");
    	Throwable cause = e.getCause();
        
        if (cause instanceof SQLIntegrityConstraintViolationException) {

            SQLIntegrityConstraintViolationException consEx = (SQLIntegrityConstraintViolationException) cause;
            /*final ApiErrorResponse apiError =  ApiErrorResponse.newBuilder()
                    .message(consEx.getLocalizedMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());*/
        }
        if (cause instanceof DataIntegrityViolationException) {

        	DataIntegrityViolationException consEx = (DataIntegrityViolationException) cause;
            /*final ApiErrorResponse apiError =  ApiErrorResponse.newBuilder()
                    .message(consEx.getLocalizedMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());*/
        }
        
        
       return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    

 }