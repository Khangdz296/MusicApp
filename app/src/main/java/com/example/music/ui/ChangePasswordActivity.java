package com.example.music.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.ChangePasswordResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    ImageButton btnBack;
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    Button btnSavePassword;

    ApiService apiService;
    SharedPreferences sharedPreferences;
    String sessionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_password_huy);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        sessionKey = sharedPreferences.getString("session_key", "");

        if (sessionKey.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSavePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, "Mật khẩu mới phải khác mật khẩu cũ", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "=== BẮT ĐẦU ĐỔI MẬT KHẨU ===");

        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("old_password", oldPassword);
        passwordData.put("new_password", newPassword);

        apiService.changePassword(sessionKey, passwordData).enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                Log.d(TAG, "=== NHẬN RESPONSE ===");
                Log.d(TAG, "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ChangePasswordResponse res = response.body();

                    if ("success".equals(res.getStatus())) {
                        Log.d(TAG, "✅ SUCCESS: " + res.getMessage());

                        Toast.makeText(ChangePasswordActivity.this,
                                "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();

                        edtOldPassword.setText("");
                        edtNewPassword.setText("");
                        edtConfirmPassword.setText("");

                        finish();

                    } else {
                        Log.e(TAG, "❌ FAILED: " + res.getMessage());
                        Toast.makeText(ChangePasswordActivity.this,
                                res.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "Lỗi đổi mật khẩu";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "❌ ERROR BODY: " + errorBody);

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

                    Log.e(TAG, "❌ Error Code: " + response.code());
                    Toast.makeText(ChangePasswordActivity.this,
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.e(TAG, "=== NETWORK FAILURE ===");
                Log.e(TAG, "❌ Exception: " + t.getClass().getName());
                Log.e(TAG, "❌ Message: " + t.getMessage());
                t.printStackTrace();

                Toast.makeText(ChangePasswordActivity.this,
                        "Không kết nối được server", Toast.LENGTH_LONG).show();
            }
        });
    }
}