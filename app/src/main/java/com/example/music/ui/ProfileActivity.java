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
import com.example.music.model.ProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    ImageButton btnBack, btnEditAvatar;
    EditText edtFirstName, edtLastName, edtUsername, edtEmail;
    Button btnChangePassword;

    ApiService apiService;
    SharedPreferences sharedPreferences;

    String sessionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_huy);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        sessionKey = sharedPreferences.getString("session_key", "");

        if (sessionKey.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadProfile();

        btnBack.setOnClickListener(v -> finish());
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }

    private void loadProfile() {
        Log.d(TAG, "Loading profile with session: " + sessionKey);

        apiService.getProfile(sessionKey).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profileResponse = response.body();

                    if ("success".equals(profileResponse.getStatus())) {
                        ProfileResponse.UserData user = profileResponse.getUser();

                        Log.d(TAG, "✅ Profile loaded");
                        Log.d(TAG, "Username: " + user.getUsername());
                        Log.d(TAG, "Email: " + user.getEmail());
                        Log.d(TAG, "Full Name: " + user.getFull_name());

                        displayProfile(user);

                    } else {
                        Log.e(TAG, "❌ Status not success");
                        Toast.makeText(ProfileActivity.this,
                                "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "❌ Response failed: " + response.code());

                    if (response.code() == 403) {
                        Toast.makeText(ProfileActivity.this,
                                "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        logout();
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "Lỗi tải profile: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "❌ Network error: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(ProfileActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayProfile(ProfileResponse.UserData user) {
        String fullName = user.getFull_name() != null ? user.getFull_name() : "";
        String[] nameParts = splitName(fullName);

        edtFirstName.setText(nameParts[0]);
        edtLastName.setText(nameParts[1]);
        edtUsername.setText(user.getUsername());
        edtEmail.setText(user.getEmail());

        edtUsername.setEnabled(false);
        edtEmail.setEnabled(false);
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[]{"", ""};
        }

        String[] parts = fullName.trim().split("\\s+", 2);
        if (parts.length == 2) {
            return parts;
        } else if (parts.length == 1) {
            return new String[]{parts[0], ""};
        } else {
            return new String[]{"", ""};
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}