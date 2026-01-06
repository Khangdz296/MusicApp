package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.adapter.ArtistAdapter;
import com.example.music.adapter.CategoryAdapterK; // Nhớ import đúng Adapter của bạn
import com.example.music.adapter.SongAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.Song;
import com.example.music.utils.RecentSongManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    // Khai báo biến
    private RecyclerView rvBanner; // Đây chính là rvHighlight trong XML
    private RecyclerView rvNewReleases, rvCharts, rvRecentlyPlayed, rvArtists, rvCategories;
    private TextView tvRandomTitle; // Tiêu đề mục random
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_hoang, container, false); // Đảm bảo đúng tên file XML

        // 1. ÁNH XẠ VIEW
        // Lưu ý: rvBanner trong code này ánh xạ vào rvHighlight trong XML
        rvBanner = view.findViewById(R.id.rvHighlight);

        rvNewReleases = view.findViewById(R.id.rvNewReleases);
        rvCharts = view.findViewById(R.id.rvCharts);
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed);
        rvArtists = view.findViewById(R.id.rvArtists);
        rvCategories = view.findViewById(R.id.rvCategories);

        // 2. KHỞI TẠO API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // 3. GỌI DỮ LIỆU TỪ SERVER
        fetchRandomSongsForHighlight(); // 👇 LOGIC MỚI: Lấy nhạc ngẫu nhiên
        fetchCharts();                  // Lấy BXH Top Views
        fetchNewSongs();                // Lấy Nhạc mới phát hành
        setupArtists();                 // Lấy Nghệ sĩ
        setupCategories();              // Lấy Thể loại

        // 4. LOAD LỊCH SỬ NGHE (Từ bộ nhớ máy)
        loadRecentSongs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Mỗi lần quay lại màn hình Home thì cập nhật lại list nghe gần đây
        loadRecentSongs();
    }

    // --- 1. LOGIC LOAD LỊCH SỬ TỪ MÁY (SHARED PREF) ---
    private void loadRecentSongs() {
        if (getContext() == null) return;

        List<Song> recentList = RecentSongManager.getRecentSongs(getContext());

        if (!recentList.isEmpty()) {
            rvRecentlyPlayed.setVisibility(View.VISIBLE);
            // Dùng TYPE_RECENT (hoặc STANDARD) tuỳ giao diện bạn muốn
            setupSection(rvRecentlyPlayed, recentList, SongAdapter.TYPE_STANDARD);
        } else {
            rvRecentlyPlayed.setVisibility(View.GONE);
        }
    }

    // --- 2. LOGIC RANDOM SONGS (Thay thế Banner cũ) ---
    private void fetchRandomSongsForHighlight() {
        // Gọi API Random
        apiService.getRandomSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> randomList = response.body();

                    // Dùng TYPE_BANNER để hiển thị ảnh to đẹp (giữ nguyên style cũ của bạn)
                    setupSection(rvBanner, randomList, SongAdapter.TYPE_BANNER);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_RANDOM", "Lỗi lấy Random Songs: " + t.getMessage());
                // Nếu lỗi thì ẩn đi
                rvBanner.setVisibility(View.GONE);
            }
        });
    }

    // --- 3. LOGIC TOP VIEWS (BXH) ---
    private void fetchCharts() {
        apiService.getTopSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupSection(rvCharts, response.body(), SongAdapter.TYPE_STANDARD);
                }
            }
            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_CHART", "Lỗi lấy BXH: " + t.getMessage());
            }
        });
    }

    // --- 4. LOGIC NHẠC MỚI (NEW RELEASES) ---
    private void fetchNewSongs() {
        apiService.getNewSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupSection(rvNewReleases, response.body(), SongAdapter.TYPE_STANDARD);
                }
            }
            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_NEW", "Lỗi lấy New Songs: " + t.getMessage());
            }
        });
    }

    // --- HÀM CHUNG ĐỂ SETUP ADAPTER CHO BÀI HÁT ---
    private void setupSection(RecyclerView rv, List<Song> data, int type) {
        if (getContext() == null || data == null || data.isEmpty()) {
            rv.setVisibility(View.GONE);
            return;
        }
        rv.setVisibility(View.VISIBLE);

        // Khởi tạo Adapter với đúng Type (Banner hoặc Standard)
        SongAdapter adapter = new SongAdapter(data, type, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // Xử lý khi bấm vào bài hát:

                // 1. Lưu vào lịch sử
                RecentSongManager.saveSong(getContext(), song);

                // 2. Load lại mục Recently Played ngay lập tức
                loadRecentSongs();

                // 3. Chuyển sang màn hình phát nhạc
                Intent intent = new Intent(getContext(), PlayMusicActivity.class);
                intent.putExtra("song_data", song); // Truyền object Song sang
                startActivity(intent);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    // --- 5. SETUP ARTIST (NGHỆ SĨ) ---
    private void setupArtists() {
        apiService.getAllArtists().enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Artist> artists = response.body();
                    if (artists.isEmpty()) { rvArtists.setVisibility(View.GONE); return; }

                    rvArtists.setVisibility(View.VISIBLE);
                    ArtistAdapter adapter = new ArtistAdapter(getContext(), artists, artist -> {
                        // Xử lý khi bấm vào ca sĩ (Ví dụ mở trang chi tiết ca sĩ)
                        Toast.makeText(getContext(), "Ca sĩ: " + artist.getName(), Toast.LENGTH_SHORT).show();
                    });
                    rvArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvArtists.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                Log.e("API_ARTIST", "Lỗi: " + t.getMessage());
            }
        });
    }

    // --- 6. SETUP CATEGORY (THỂ LOẠI) ---
    private void setupCategories() {
        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    if (categories.isEmpty()) { rvCategories.setVisibility(View.GONE); return; }

                    rvCategories.setVisibility(View.VISIBLE);
                    // Dùng CategoryAdapterK của bạn
                    CategoryAdapterK categoryAdapter = new CategoryAdapterK(categories, new CategoryAdapterK.IClickCategoryListener() {
                        @Override
                        public void onClick(Category category, int color) {
                            // Chuyển sang màn hình chi tiết thể loại
                            Intent intent = new Intent(getContext(), CategoryDetailActivity.class);
                            intent.putExtra("CAT_ID", category.getId());
                            intent.putExtra("CAT_NAME", category.getName());
                            intent.putExtra("CAT_COLOR", color); // Truyền màu sang cho đẹp
                            startActivity(intent);
                        }
                    });
                    rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvCategories.setAdapter(categoryAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_CATEGORY", "Lỗi: " + t.getMessage());
            }
        });
    }
}