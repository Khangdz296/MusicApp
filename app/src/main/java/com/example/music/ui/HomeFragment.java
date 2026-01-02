package com.example.music.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.music.R;

public class HomeFragment extends Fragment {

    // Khai báo các biến RecyclerView tương ứng với 6 mục trong giao diện
    private RecyclerView rvBanner, rvNewReleases, rvCharts, rvRecentlyPlayed, rvArtists, rvCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. KẾT NỐI VỚI FILE GIAO DIỆN XML CỦA BẠN
        View view = inflater.inflate(R.layout.fragment_home_hoang, container, false);

        // 2. ÁNH XẠ VIEW (Tìm các RecyclerView trong file XML bằng ID)
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed); // Nghe gần đây
        rvBanner = view.findViewById(R.id.rvHighlight);            // Banner (Highlight)
        rvNewReleases = view.findViewById(R.id.rvNewReleases);     // Nhạc mới
        rvCharts = view.findViewById(R.id.rvCharts);               // Bảng xếp hạng
        rvArtists = view.findViewById(R.id.rvArtists);             // Nghệ sĩ
        rvCategories = view.findViewById(R.id.rvCategories);       // Thể loại

        // 3. KHỞI TẠO DỮ LIỆU & ADAPTER CHO TỪNG MỤC

        // --- MỤC 1: NGHE GẦN ĐÂY (Dùng Type RECENT - Hình tròn/nhỏ) ---
        setupSection(rvRecentlyPlayed, getMockSongs("Sơn Tùng M-TP", "Lạc Trôi"), SongAdapter.TYPE_RECENT);

        // --- MỤC 2: BANNER (Dùng Type BANNER - Hình chữ nhật to) ---
        setupSection(rvBanner, getMockSongs("Top Hits", "Banner Hot"), SongAdapter.TYPE_BANNER);

        // --- MỤC 3: NHẠC MỚI (Dùng Type STANDARD - Hình vuông chuẩn) ---
        setupSection(rvNewReleases, getMockSongs("MONO", "Waiting For You"), SongAdapter.TYPE_STANDARD);

        // --- MỤC 4: BẢNG XẾP HẠNG (Dùng Type STANDARD) ---
        setupSection(rvCharts, getMockSongs("Global", "Top 50"), SongAdapter.TYPE_STANDARD);

        // --- MỤC 5: NGHỆ SĨ (Dùng ArtistAdapter riêng) ---
        setupArtists();

        // --- MỤC 6: THỂ LOẠI (Dùng CategoryAdapter riêng) ---
        setupCategories();

        return view;
    }


    /**
     * Hàm dùng chung để setup cho các list bài hát (Banner, Nhạc mới, BXH...)
     */
    private void setupSection(RecyclerView rv, List<Song> data, int type) {
        // Tạo Adapter với dữ liệu và kiểu hiển thị (Type)
        SongAdapter adapter = new SongAdapter(data, type);

        // Setup LayoutManager: Hiển thị danh sách nằm ngang (Horizontal)
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Gán Adapter vào RecyclerView
        rv.setAdapter(adapter);
    }

    /**
     * Hàm setup riêng cho mục Nghệ sĩ
     */
    private void setupArtists() {
        List<Artist> artists = new ArrayList<>();
        // Tạo dữ liệu giả Artist (Ảnh dùng tạm ic_launcher_background hoặc ảnh có sẵn trong drawable)
        artists.add(new Artist("Sơn Tùng", R.drawable.ic_launcher_background));
        artists.add(new Artist("JustaTee", R.drawable.ic_launcher_background));
        artists.add(new Artist("Đen Vâu", R.drawable.ic_launcher_background));
        artists.add(new Artist("HIEUTHUHAI", R.drawable.ic_launcher_background));

        ArtistAdapter adapter = new ArtistAdapter(artists);
        rvArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvArtists.setAdapter(adapter);
    }

    /**
     * Hàm setup riêng cho mục Thể loại
     */
    private void setupCategories() {
        List<Category> categories = new ArrayList<>();
        // Tạo dữ liệu giả Category
        categories.add(new Category("V-Pop", R.drawable.ic_launcher_background));
        categories.add(new Category("K-Pop", R.drawable.ic_launcher_background));
        categories.add(new Category("US-UK", R.drawable.ic_launcher_background));
        categories.add(new Category("Indie", R.drawable.ic_launcher_background));

        CategoryAdapter adapter = new CategoryAdapter(categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);
    }

    private List<Song> getMockSongs(String artistName, String titlePrefix) {
        List<Song> list = new ArrayList<>();

        String demoUrl = "https://picsum.photos/600/600";

        for (int i = 1; i <= 6; i++) {
            list.add(new Song(
                    "id_" + i,              // ID
                    titlePrefix + " #" + i, // Tên bài hát
                    artistName,             // Tên ca sĩ
                    demoUrl,                // Link ảnh (String URL)
                    "",                     // File nhạc (Để trống)
                    300,                    // Thời lượng
                    false                   // Yêu thích
            ));
        }
        return list;
    }
}