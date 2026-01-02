package com.example.music.model;


public class VerifyOtpRequest {
    private String username;
    private String otpCode;

    public VerifyOtpRequest(String username, String otpCode) {
        this.username = username;
        this.otpCode = otpCode;
    }
    public String getUsername() { return username; }
    public String getOtpCode() { return otpCode; }
}

