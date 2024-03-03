package fr.tp.models;

public class AuthResponse {
    private String message;
    private boolean result;
    private String exceptionMsg;

    public AuthResponse(String message, boolean result, String exceptionMsg) {
        this.message = message;
        this.result = result;
        this.exceptionMsg = exceptionMsg;
    }

    public AuthResponse(String message, boolean result) {
        this(message, result, null);
    }

    // Getters et setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }
}