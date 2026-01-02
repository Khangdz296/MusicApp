package com.example.music.model;


public class LoginResponse {
    private String status;
    private String message;
    private Long user_id;
    private String session_key;

    public LoginResponse() {
    }

    public LoginResponse(String status, String message, Long user_id, String session_key) {
        this.status = status;
        this.message = message;
        this.user_id = user_id;
        this.session_key = session_key;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getSession_key() {
        return session_key;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }
}