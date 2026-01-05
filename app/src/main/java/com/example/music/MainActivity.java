package com.example.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.music.ui.HomeFragment;
import com.example.music.ui.LibraryFragment;
import com.example.music.ui.LoginActivity;
import com.example.music.ui.MenuActivity;
import com.example.music.ui.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Gắn file giao diện tổng (Activity chứa cả Menu và Khung nội dung)
        setContentView(R.layout.activity_main);

        // 2. Tìm cái thanh Menu đáy bằng ID
        // Lưu ý: ID này là ID bạn đặt cho thẻ <include> trong activity_main.xml
        bottomNavigationView = findViewById(R.id.bottom_nav_container);

        // 3. Mặc định load màn hình Home đầu tiên khi mở App
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // 4. Bắt sự kiện khi bấm vào các nút dưới đáy
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                }
                else if (id == R.id.nav_search) {
                    // Nếu chưa có SearchFragment, tạm thời load Home để test không bị lỗi
                    selectedFragment = new SearchFragment();
                    // selectedFragment = new SearchFragment();
                }
                else if (id == R.id.nav_library) {
                    // Tương tự, tạm thời load Home
                    selectedFragment = new LibraryFragment();
                    // selectedFragment = new LibraryFragment();
                }
                else if (id == R.id.nav_profile) {

                    if (isLoggedIn()) {
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    return false;
                }


                // Thực hiện thay thế màn hình
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    // === HÀM CHUYỂN ĐỔI FRAGMENT (Code 1 lần dùng nhiều nơi) ===
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                // R.id.fragment_container là cái khung trống nằm trên menu trong activity_main.xml
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    private boolean isLoggedIn() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String sessionKey = sp.getString("session_key", "");
        return !sessionKey.isEmpty();
    }

}