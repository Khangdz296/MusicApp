package com.example.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.music.ui.HomeFragment;
import com.example.music.ui.LibraryFragment;
import com.example.music.ui.LoginActivity;
import com.example.music.ui.MenuActivity;
import com.example.music.ui.SearchFragment;
import com.example.music.utils.MiniPlayerManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private View miniPlayerContainer;
    private MiniPlayerManager miniPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo views
        bottomNavigationView = findViewById(R.id.bottom_nav_container);
        miniPlayerContainer = findViewById(R.id.miniPlayerContainer);

        // Khởi tạo Mini Player Manager
        miniPlayerManager = MiniPlayerManager.getInstance();
        miniPlayerManager.initialize(this, miniPlayerContainer);

        // Load fragment đầu tiên
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Setup bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                }
                else if (id == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                }
                else if (id == R.id.nav_library) {
                    selectedFragment = new LibraryFragment();
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

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Cập nhật intent hiện tại
        handleIntent(intent); // Xử lý logic chuyển tab
    }

    // 👇 LOGIC CHUYỂN TAB DỰA TRÊN "open_fragment"
    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("open_fragment")) {
            String fragmentToOpen = intent.getStringExtra("open_fragment");

            if ("playlist".equals(fragmentToOpen)) {
                // Tự động bấm vào nút Library trên BottomNavigation
                // Nó sẽ kích hoạt listener bên trên và mở LibraryFragment (chứa Playlist)
                bottomNavigationView.setSelectedItemId(R.id.nav_library);
            }
            else if ("favorite".equals(fragmentToOpen)) {
                // Tương tự, mở LibraryFragment
                // (Nếu muốn mở đúng tab Favorite bên trong Library, ta cần gửi thêm dữ liệu vào Fragment sau)
                bottomNavigationView.setSelectedItemId(R.id.nav_library);
            }
        }
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private boolean isLoggedIn() {
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String sessionKey = sp.getString("session_key", "");
        return !sessionKey.isEmpty();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo mini player hiển thị nếu đang có nhạc phát
        if (miniPlayerManager.getCurrentSong() != null) {
            miniPlayerContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // KHÔNG release MediaPlayer ở đây vì mini player cần chạy xuyên suốt app
        // Chỉ release khi user tắt hoàn toàn hoặc logout
    }
}