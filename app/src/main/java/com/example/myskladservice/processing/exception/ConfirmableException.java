package com.example.myskladservice.processing.exception;

public class ConfirmableException extends Exception {
    private int type;
    private String title;
    private String massage;
    private String positive;
    private String negative;

    public ConfirmableException(int type, String title, String massage, String negative, String positive) {
        super(massage);
        this.type = type;
        this.title = title;
        this.massage = massage;
        this.positive = positive;
        this.negative = negative;
    }

    public int getErrorType() {
        return type;
    }
    public String getErrorTitle() {
        return title;
    }
    public String getErrorMessage() {
        return massage;
    }
    public String getErrorPositive() {
        return positive;
    }
    public String getErrorNegative() {
        return negative;
    }
}
