package com.school.school.response;

public class ResponseWrapper<T> {
    private T data;
    private String message;
    private boolean success;

    // Constructor for success case
    public ResponseWrapper(T data, boolean success) {
        this.data = data;
        this.success = success;
    }

    // Constructor for error case
    public ResponseWrapper(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
