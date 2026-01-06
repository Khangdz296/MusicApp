package com.example.music.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.ProfileResponse;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    CircleImageView imgProfile;
    TextView txtUsername, txtEmail;
    ImageButton btnSettings, btnBackToHome; // 👇 Thêm nút Back
    LinearLayout menuFavourites, menuDownloads, menuEditProfile, menuProfile, menuPlaylist, menuLogout, btnChangePassword;

    ApiService apiService;
    SharedPreferences sharedPreferences;
    String sessionKey;

    private ProfileResponse.UserData currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu_huy);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        sessionKey = sharedPreferences.getString("session_key", "");

        if (sessionKey.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        initViews();
        loadProfile();
        setupMenuListeners();
    }

    private void initViews() {
        imgProfile = findViewById(R.id.imgProfile);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        btnSettings = findViewById(R.id.btnSettings);
        btnBackToHome = findViewById(R.id.btnBackToHome); // 👇 Ánh xạ nút Back

        menuFavourites = findViewById(R.id.menuFavourites);
        menuDownloads = findViewById(R.id.menuDownloads);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        menuPlaylist = findViewById(R.id.menuPlaylist);
        menuLogout = findViewById(R.id.menuLogout);
    }

    private void loadProfile() {
        Log.d(TAG, "📡 Loading profile with session: " + sessionKey);

        apiService.getProfile(sessionKey).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                Log.d(TAG, "📥 Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profileResponse = response.body();

                    if ("success".equals(profileResponse.getStatus())) {
                        currentUser = profileResponse.getUser();

                        if (currentUser != null) {
                            Log.d(TAG, "✅ Profile loaded successfully");
                            Log.d(TAG, "User ID: " + currentUser.getUser_id());
                            Log.d(TAG, "Username: " + currentUser.getUsername());
                            Log.d(TAG, "Email: " + currentUser.getEmail());
                            Log.d(TAG, "Full Name: " + currentUser.getFull_name());

                            txtUsername.setText(currentUser.getUsername());
                            txtEmail.setText(currentUser.getEmail());
                        }

                    } else {
                        Log.e(TAG, "❌ Status not success");
                        txtUsername.setText("User");
                        txtEmail.setText("");
                    }
                } else {
                    Log.e(TAG, "❌ Response failed: " + response.code());

                    if (response.code() == 403) {
                        Toast.makeText(MenuActivity.this,
                                "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                        handleLogout();
                    } else {
                        txtUsername.setText("User");
                        txtEmail.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "❌ Network error: " + t.getMessage());
                t.printStackTrace();

                txtUsername.setText("User");
                txtEmail.setText("");
                Toast.makeText(MenuActivity.this,
                        "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMenuListeners() {
        // 👇 NÚT QUAY VỀ HOME
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings đang phát triển", Toast.LENGTH_SHORT).show();
        });

        menuFavourites.setOnClickListener(v -> {
            Toast.makeText(this, "Favourites đang phát triển", Toast.LENGTH_SHORT).show();
        });

        menuDownloads.setOnClickListener(v -> {
            Toast.makeText(this, "Downloads đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        menuPlaylist.setOnClickListener(v -> {
            Toast.makeText(this, "Playlist đang phát triển", Toast.LENGTH_SHORT).show();
        });

        menuLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> handleLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void handleLogout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Log.d(TAG, "✅ Đã logout");
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
}