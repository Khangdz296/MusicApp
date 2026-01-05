package com.example.music.model;

public class ChangePasswordResponse {
    private String status;
    private String message;

    public ChangePasswordResponse() {
    }

    public ChangePasswordResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
