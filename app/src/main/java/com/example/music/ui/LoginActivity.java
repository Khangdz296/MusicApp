package com.example.music.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.LoginRequest;
import com.example.music.model.LoginResponse;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView tvRegister, tvForgotPassword;
    ApiService apiService;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_huy);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        if (isLoggedIn()) {
            navigateToHome();
            return;
        }

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> handleLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "=== BẮT ĐẦU LOGIN ===");
        Log.d(TAG, "Username: " + username);
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "=== NHẬN RESPONSE ===");
                Log.d(TAG, "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if ("success".equals(loginResponse.getStatus())) {
                        Log.d(TAG, "LOGIN SUCCESS");
                        Log.d(TAG, "User ID: " + loginResponse.getUser_id());
                        Log.d(TAG, "Session Key: " + loginResponse.getSession_key());

                        saveLoginSession(
                                loginResponse.getUser_id(),
                                loginResponse.getSession_key(),
                                username
                        );

                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        navigateToHome();
                        finish();

                    } else {
                        Log.e(TAG, "LOGIN FAILED: " + loginResponse.getMessage());
                        Toast.makeText(LoginActivity.this,
                                loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    String errorMsg = "Lỗi đăng nhập";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "ERROR BODY: " + errorBody);

                            JSONObject errorJson = new JSONObject(errorBody);
                            if (errorJson.has("message")) {
                                errorMsg = errorJson.getString("message");
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IOException: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }

                    Log.e(TAG, "LOGIN FAILED - Code: " + response.code());
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "=== NETWORK FAILURE ===");
                Log.e(TAG, "Exception: " + t.getClass().getName());
                Log.e(TAG, "Message: " + t.getMessage());
                t.printStackTrace();

                String errorMsg = "Không kết nối được server.\n" +
                        "Kiểm tra: Backend có chạy không?";

                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveLoginSession(Long userId, String sessionKey, String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("user_id", userId);
        editor.putString("session_key", sessionKey);
        editor.putString("username", username);
        editor.putBoolean("is_logged_in", true);
        editor.apply();

        Log.d(TAG, "Đã lưu session vào SharedPreferences");
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
