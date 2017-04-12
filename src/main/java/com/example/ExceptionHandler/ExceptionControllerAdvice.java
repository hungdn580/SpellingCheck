package com.example.ExceptionHandler;

import com.example.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * ExceptionControllerAdvice
 * Created by Hung Do Ngoc on 4/2/2017.
 */
public class ExceptionControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage("Please contact your administrator");
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.OK);
    }

}
