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
// Import đúng Adapter và Model
import com.example.music.adapter.ArtistAdapter;
import com.example.music.adapter.CategoryAdapter;
import com.example.music.adapter.SongAdapter;
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.Song;

public class HomeFragment extends Fragment {

    private RecyclerView rvBanner, rvNewReleases, rvCharts, rvRecentlyPlayed, rvArtists, rvCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_hoang, container, false);

        // Ánh xạ View
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed);
        rvBanner = view.findViewById(R.id.rvHighlight);
        rvNewReleases = view.findViewById(R.id.rvNewReleases);
        rvCharts = view.findViewById(R.id.rvCharts);
        rvArtists = view.findViewById(R.id.rvArtists);
        rvCategories = view.findViewById(R.id.rvCategories);

        // --- SETUP CÁC MỤC SONG ---
        setupSection(rvRecentlyPlayed, getMockSongs("Sơn Tùng M-TP", "Lạc Trôi"), SongAdapter.TYPE_RECENT);
        setupSection(rvBanner, getMockSongs("Top Hits", "Banner Hot"), SongAdapter.TYPE_BANNER);
        setupSection(rvNewReleases, getMockSongs("MONO", "Waiting For You"), SongAdapter.TYPE_STANDARD);
        setupSection(rvCharts, getMockSongs("Global", "Top 50"), SongAdapter.TYPE_STANDARD);

        // --- SETUP NGHỆ SĨ & THỂ LOẠI (Đã sửa lại cho khớp Model mới) ---
        setupArtists();
        setupCategories();

        return view;
    }

    // Hàm setup cho danh sách bài hát (Giữ nguyên)
    private void setupSection(RecyclerView rv, List<Song> data, int type) {
        // Lưu ý: Nếu SongAdapter của bạn cũng cần Context, hãy thêm getContext() vào đây
        SongAdapter adapter = new SongAdapter(data, type);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    // --- SỬA HÀM SETUP ARTIST (Quan trọng) ---
    private void setupArtists() {
        List<Artist> artists = new ArrayList<>();

        // Sửa: Dùng Constructor mới (id, name, imageUrl)
        artists.add(new Artist("1", "Sơn Tùng", "https://picsum.photos/200/200?random=10"));
        artists.add(new Artist("2", "JustaTee", "https://picsum.photos/200/200?random=11"));
        artists.add(new Artist("3", "Đen Vâu", "https://picsum.photos/200/200?random=12"));
        artists.add(new Artist("4", "HIEUTHUHAI", "https://picsum.photos/200/200?random=13"));

        // Sửa: Truyền đủ 3 tham số (Context, List, Listener)
        ArtistAdapter adapter = new ArtistAdapter(getContext(), artists, artist -> {
            // Xử lý khi click vào ca sĩ (để trống tạm)
        });

        rvArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvArtists.setAdapter(adapter);
    }

    // --- SỬA HÀM SETUP CATEGORY (Quan trọng) ---
    private void setupCategories() {
        List<Category> categories = new ArrayList<>();

        // Sửa: Dùng Constructor mới (id, name, imageUrl)
        categories.add(new Category("1", "V-Pop", "https://picsum.photos/200/200?random=20"));
        categories.add(new Category("2", "K-Pop", "https://picsum.photos/200/200?random=21"));
        categories.add(new Category("3", "US-UK", "https://picsum.photos/200/200?random=22"));
        categories.add(new Category("4", "Indie", "https://picsum.photos/200/200?random=23"));

        // Sửa: Truyền đủ 3 tham số (Context, List, Listener)
        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories, category -> {
            // Xử lý khi click vào thể loại (để trống tạm)
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);
    }

    // Hàm tạo dữ liệu giả cho Song (Đã chuẩn với Model Song)
    private List<Song> getMockSongs(String artistName, String titlePrefix) {
        List<Song> list = new ArrayList<>();
        String demoUrl = "https://picsum.photos/600/600";

        for (int i = 1; i <= 6; i++) {
            list.add(new Song(
                    "id_" + i,              // ID
                    titlePrefix + " #" + i, // Title
                    artistName,             // Artist
                    demoUrl,                // Image URL
                    "",                     // File URL
                    300,                    // Duration
                    false                   // Is Favorite
            ));
        }
        return list;
    }
}