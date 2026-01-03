package com.example.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Import 3 màn hình con mà chúng ta đã tạo lúc nãy
import com.example.music.ui.MyPlaylistsFragment;
import com.example.music.ui.FavoriteSongsFragment;
import com.example.music.ui.MyAlbumsFragment;

public class LibraryViewPagerAdapter extends FragmentStateAdapter {

    public LibraryViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Dựa vào vị trí tab để trả về màn hình tương ứng
        switch (position) {
            case 0:
                return new MyPlaylistsFragment();    // Tab 1: Danh sách Playlist
            case 1:
                return new FavoriteSongsFragment();  // Tab 2: Bài hát yêu thích
            case 2:
                return new MyAlbumsFragment();       // Tab 3: Album
            default:
                return new MyPlaylistsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Tổng cộng có 3 Tab
    }
}