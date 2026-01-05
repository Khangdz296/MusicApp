package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.RegisterResponse;
import com.example.music.model.User;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    EditText edtName, edtEmail, edtPassword;
    ImageButton btnSubmit;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_huy);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = name;

        User user = new User(username, email, name, password);

        Log.d(TAG, "=== BẮT ĐẦU ĐĂNG KÝ ===");
        Log.d(TAG, "Username: " + username);
        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Name: " + name);
        Log.d(TAG, "Password: " + password);
        Log.d(TAG, "API URL: " + RetrofitClient.getClient().baseUrl());

        apiService.register(user).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d(TAG, "=== NHẬN RESPONSE ===");
                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Response Message: " + response.message());

                if (response.isSuccessful()) {
                    RegisterResponse res = response.body();
                    if (res != null) {
                        Log.d(TAG, "SUCCESS: " + res.getMessage());
                        Log.d(TAG, "Test OTP: " + res.getTest_otp());

                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(RegisterActivity.this, VerifyOtpActivity.class);
                        i.putExtra("username", username);
                        i.putExtra("otp_test", res.getTest_otp());
                        startActivity(i);
                        finish();
                    } else {
                        Log.e(TAG, "Response body is null");
                        Toast.makeText(RegisterActivity.this,
                                "Lỗi: Không nhận được dữ liệu từ server", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "Lỗi không xác định";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "ERROR BODY: " + errorBody);

                            // Parse JSON error
                            JSONObject errorJson = new JSONObject(errorBody);
                            if (errorJson.has("message")) {
                                errorMsg = errorJson.getString("message");
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IOException khi đọc error body: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Exception khi parse error: " + e.getMessage());
                    }

                    Log.e(TAG, "ĐĂNG KÝ THẤT BẠI");
                    Log.e(TAG, "Error Code: " + response.code());
                    Log.e(TAG, "Error Message: " + errorMsg);

                    Toast.makeText(RegisterActivity.this,
                            "Lỗi đăng ký: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e(TAG, "=== NETWORK FAILURE ===");
                Log.e(TAG, "Exception: " + t.getClass().getName());
                Log.e(TAG, "Message: " + t.getMessage());
                Log.e(TAG, "Cause: " + (t.getCause() != null ? t.getCause().getMessage() : "null"));

                t.printStackTrace();

                String errorMsg = "Không kết nối được server";

                if (t instanceof IOException) {
                    errorMsg = "Lỗi kết nối mạng. Kiểm tra:\n" +
                            "1. Backend có chạy không?\n" +
                            "2. URL có đúng không?\n" +
                            "3. Emulator dùng 10.0.2.2 thay vì localhost";
                } else if (t instanceof IllegalStateException) {
                    errorMsg = "Lỗi parse JSON response";
                }

                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

}