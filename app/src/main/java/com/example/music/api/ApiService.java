package com.example.music.api;

import com.example.music.model.LoginRequest;
import com.example.music.model.LoginResponse;
import com.example.music.model.ProfileResponse;
import com.example.music.model.RegisterResponse;
import com.example.music.model.User;
import com.example.music.model.VerifyOtpRequest;
import com.example.music.model.VerifyOtpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("api/register")
    Call<RegisterResponse> register(@Body User user);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);  // ĐỔI CHỖ NÀY

    @Headers("Content-Type: application/json")
    @POST("api/verify-otp")
    Call<VerifyOtpResponse> verifyOtp(@Body VerifyOtpRequest req);

    @GET("api/profile")
    Call<ProfileResponse> getProfile(@Header("X-Session-Key") String sessionKey);

}