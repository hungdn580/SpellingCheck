package com.example.ExceptionHandler;

/**
 * custom class to handler exception
 * Created by Hung Do Ngoc on 4/2/2017.
 */
public class ExceptionHanlder extends Exception {
    private static final long serialVersionUID = 1L;
    private String errorMessage;

    public ExceptionHanlder(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public ExceptionHanlder() {
        super();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
