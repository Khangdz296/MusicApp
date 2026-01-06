package com.example.music.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music.R;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // 1. Chuyển sang màn hình Thêm bài hát
        findViewById(R.id.btnGoToAddSong).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAddSongActivity.class));
        });

        // 2. Chuyển sang màn hình Quản lý (Xóa)
        findViewById(R.id.btnGoToManage).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminSongManagerActivity.class));
        });

        findViewById(R.id.btnManageArtists).setOnClickListener(v ->
                startActivity(new Intent(this, AdminArtistManagerActivity.class))); // Sẽ tạo ở dưới

        findViewById(R.id.btnManageCategories).setOnClickListener(v ->
                startActivity(new Intent(this, AdminCategoryManagerActivity.class)));

        // 3. Đăng xuất (Về màn hình chính hoặc Login)
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            finish(); // Đóng Admin Activity
        });
    }
}