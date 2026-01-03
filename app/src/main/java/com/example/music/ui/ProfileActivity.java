package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music.R;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    ImageButton btnBack;
    EditText edtFirstName, edtLastName, edtUsername, edtEmail;
    Button btnChangePassword;

    // Dữ liệu nhận từ MenuActivity
    private Long userId;
    private String username;
    private String email;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_huy); // ✅ PHẢI DÙNG layout_profile_huy

        initViews();
        loadDataFromIntent();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getLongExtra("user_id", 0L);
            username = intent.getStringExtra("username");
            email = intent.getStringExtra("email");
            fullName = intent.getStringExtra("full_name");

            Log.d(TAG, "📥 Nhận dữ liệu từ MenuActivity:");
            Log.d(TAG, "User ID: " + userId);
            Log.d(TAG, "Username: " + username);
            Log.d(TAG, "Email: " + email);
            Log.d(TAG, "Full Name: " + fullName);

            // Hiển thị dữ liệu lên UI
            displayUserData();
        } else {
            Log.e(TAG, "❌ Không nhận được Intent data");
            Toast.makeText(this, "Lỗi: Không có dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayUserData() {
        // Tách full name thành first name và last name
        if (fullName != null && !fullName.isEmpty()) {
            String[] nameParts = fullName.split(" ", 2);
            if (nameParts.length == 2) {
                edtFirstName.setText(nameParts[0]);
                edtLastName.setText(nameParts[1]);
            } else {
                edtFirstName.setText(fullName);
                edtLastName.setText("");
            }
        } else {
            // Nếu không có full name, dùng username
            edtFirstName.setText(username);
            edtLastName.setText("");
        }

        // Hiển thị username và email
        edtUsername.setText(username);
        edtEmail.setText(email);

        Log.d(TAG, "✅ Đã hiển thị dữ liệu lên UI");
    }

    private void setupListeners() {
        // Nút Back - Quay lại MenuActivity
        btnBack.setOnClickListener(v -> {
            Log.d(TAG, "🔙 Back button clicked");
            finish(); // Đóng ProfileActivity và quay về MenuActivity
        });

        // Nút Change Password
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change Password đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }
}