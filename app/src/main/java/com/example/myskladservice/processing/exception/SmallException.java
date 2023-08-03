package com.example.myskladservice.processing.exception;

public class SmallException extends Exception {
    private int errorCode;
    private String errorMessage;

    public SmallException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}