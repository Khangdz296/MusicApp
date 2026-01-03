package com.example.music.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.music.R;
import com.example.music.adapter.LibraryViewPagerAdapter; // ⚠️ Kiểm tra dòng import này
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LibraryViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. KẾT NỐI VỚI GIAO DIỆN XML
        // Đảm bảo bạn đã tạo file fragment_library_hoang.xml như các bước trước
        View view = inflater.inflate(R.layout.fragment_library_hoang, container, false);

        // 2. ÁNH XẠ VIEW
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // 3. KHỞI TẠO ADAPTER VÀ GÁN VÀO VIEWPAGER
        // Adapter này sẽ quản lý 3 màn hình: Playlist, Favorite, Album
        adapter = new LibraryViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 4. KẾT NỐI TABLAYOUT VỚI VIEWPAGER (QUAN TRỌNG)
        // Code này giúp đồng bộ: Bấm Tab -> Chuyển trang & Vuốt trang -> Đổi Tab
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Playlists");
                    break;
                case 1:
                    tab.setText("Favorite");
                    break;
                case 2:
                    tab.setText("Albums");
                    break;
            }
        }).attach();

        return view;
    }
}