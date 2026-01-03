package com.example.music.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.api.ApiService;     // Import Interface API
import com.example.music.api.RetrofitClient; // 👇 Import đúng file của bạn
import com.example.music.adapter.ArtistAdapter;
import com.example.music.adapter.CategoryAdapter;
import com.example.music.adapter.SongAdapter;
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvBanner, rvNewReleases, rvCharts, rvRecentlyPlayed, rvArtists, rvCategories;
    private ApiService apiService; // Biến này dùng để gọi API

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_hoang, container, false);

        // 1. Ánh xạ View
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed);
        rvBanner = view.findViewById(R.id.rvHighlight);
        rvNewReleases = view.findViewById(R.id.rvNewReleases);
        rvCharts = view.findViewById(R.id.rvCharts);
        rvArtists = view.findViewById(R.id.rvArtists);
        rvCategories = view.findViewById(R.id.rvCategories);

        // 2. KHỞI TẠO API SERVICE (Sửa lại cho đúng với RetrofitClient của bạn) 🛠️
        // RetrofitClient.getClient() trả về Retrofit -> dùng .create() để tạo ApiService
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // 3. GỌI DỮ LIỆU TỪ SERVER
        fetchSongsData();

        // Setup phần Nghệ sĩ & Thể loại (Mock tạm)
        setupArtists();
        setupCategories();

        return view;
    }

    // --- HÀM GỌI API (Giữ nguyên) ---
    private void fetchSongsData() {
        // GỌI API 1: Lấy toàn bộ bài hát
        apiService.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> allSongs = response.body();

                    // Đổ dữ liệu vào các Recycler View
                    setupSection(rvBanner, allSongs, SongAdapter.TYPE_BANNER);
                    setupSection(rvCharts, allSongs, SongAdapter.TYPE_STANDARD);
                    setupSection(rvRecentlyPlayed, allSongs, SongAdapter.TYPE_RECENT);

                    Log.d("API_MUSIC", "Lấy thành công: " + allSongs.size() + " bài");
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                    Toast.makeText(getContext(), "Không lấy được dữ liệu nhạc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng! Kiểm tra Server Spring Boot", Toast.LENGTH_LONG).show();
            }
        });

        // GỌI API 2: Lấy nhạc Mới (Nếu bạn đã có endpoint này bên ApiService)
        apiService.getNewSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> newSongs = response.body();
                    setupSection(rvNewReleases, newSongs, SongAdapter.TYPE_STANDARD);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                // Ignore error
            }
        });
    }

    // --- SETUP ADAPTER ---
    private void setupSection(RecyclerView rv, List<Song> data, int type) {
        if (getContext() == null) return;

        SongAdapter adapter = new SongAdapter(data, type, song -> {
            Toast.makeText(getContext(), "Đang mở bài: " + song.getTitle(), Toast.LENGTH_SHORT).show();
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    // --- MOCK DATA ARTIST ---
    private void setupArtists() {
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("1", "Sơn Tùng", "https://picsum.photos/200/200?random=10"));
        artists.add(new Artist("2", "JustaTee", "https://picsum.photos/200/200?random=11"));

        ArtistAdapter adapter = new ArtistAdapter(getContext(), artists, artist -> {
            Toast.makeText(getContext(), "Ca sĩ: " + artist.getName(), Toast.LENGTH_SHORT).show();
        });
        rvArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvArtists.setAdapter(adapter);
    }

    // --- MOCK DATA CATEGORY ---
    private void setupCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "V-Pop", "https://picsum.photos/200/200?random=20"));
        categories.add(new Category(2L, "K-Pop", "https://picsum.photos/200/200?random=21"));

        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories, category -> {
            Toast.makeText(getContext(), "Thể loại: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);
    }
}