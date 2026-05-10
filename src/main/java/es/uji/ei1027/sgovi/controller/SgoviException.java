package es.uji.ei1027.sgovi.controller;

public class SgoviException extends RuntimeException {
    private String message;
    private String errorName;

    public SgoviException(String message, String errorName) {
        this.message = message;
        this.errorName = errorName;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getErrorName() { return errorName; }
    public void setErrorName(String errorName) { this.errorName = errorName; }
}
