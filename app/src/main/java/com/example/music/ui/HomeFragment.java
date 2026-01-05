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
import com.example.music.adapter.ArtistAdapter;
import com.example.music.adapter.CategoryAdapter;
import com.example.music.adapter.SongAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.Song;
import com.example.music.utils.RecentSongManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvBanner, rvNewReleases, rvCharts, rvRecentlyPlayed, rvArtists, rvCategories;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_hoang, container, false);

        // 1. ÁNH XẠ VIEW
        rvBanner = view.findViewById(R.id.rvHighlight);
        rvNewReleases = view.findViewById(R.id.rvNewReleases);
        rvCharts = view.findViewById(R.id.rvCharts);
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed);
        rvArtists = view.findViewById(R.id.rvArtists);
        rvCategories = view.findViewById(R.id.rvCategories);

        // 2. KHỞI TẠO API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // 3. GỌI DỮ LIỆU TỪ SERVER
        fetchBanner();          // Lấy Banner từ Server
        fetchCharts();          // Lấy BXH Top Views
        fetchNewSongs();        // Lấy Nhạc mới
        setupArtists();         // Lấy Nghệ sĩ
        setupCategories();      // Lấy Thể loại

        // 4. LOAD LỊCH SỬ NGHE (Từ bộ nhớ máy)
        loadRecentSongs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentSongs();
    }

    // --- 1. LOGIC LOAD LỊCH SỬ TỪ MÁY (SHALED PREF) ---
    private void loadRecentSongs() {
        if (getContext() == null) return;

        // Lấy danh sách thật từ RecentSongManager
        List<Song> recentList = RecentSongManager.getRecentSongs(getContext());

        if (!recentList.isEmpty()) {
            rvRecentlyPlayed.setVisibility(View.VISIBLE);
            // Dùng TYPE_RECENT để hiển thị kiểu danh sách ngang nhỏ gọn
            setupSection(rvRecentlyPlayed, recentList, SongAdapter.TYPE_RECENT);
        } else {
            // Nếu chưa nghe bài nào thì ẩn đi cho gọn
            rvRecentlyPlayed.setVisibility(View.GONE);
        }
    }

    // --- 2. LOGIC LẤY BANNER (Đã tách phần Recent ra) ---
    private void fetchBanner() {
        apiService.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> allSongs = response.body();
                    if (allSongs.isEmpty()) return;

                    // Lấy 5 bài đầu làm Banner
                    List<Song> bannerList = new ArrayList<>();
                    if (allSongs.size() >= 5) {
                        bannerList = allSongs.subList(0, 5);
                    } else {
                        bannerList = allSongs;
                    }
                    setupSection(rvBanner, bannerList, SongAdapter.TYPE_BANNER);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_SONG", "Lỗi lấy Banner: " + t.getMessage());
            }
        });
    }

    // --- 3. LOGIC TOP VIEWS ---
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

    // --- 4. LOGIC NHẠC MỚI ---
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

    // --- HÀM CHUNG SETUP ADAPTER (QUAN TRỌNG: CÓ LOGIC LƯU LỊCH SỬ) ---
    private void setupSection(RecyclerView rv, List<Song> data, int type) {
        if (getContext() == null || data == null || data.isEmpty()) {
            rv.setVisibility(View.GONE);
            return;
        }
        rv.setVisibility(View.VISIBLE);

        SongAdapter adapter = new SongAdapter(data, type, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // 1. Thông báo
                Toast.makeText(getContext(), "Phát: " + song.getTitle(), Toast.LENGTH_SHORT).show();

                // 2. 👇 LƯU BÀI VỪA BẤM VÀO LỊCH SỬ
                RecentSongManager.saveSong(getContext(), song);

                // 3. 👇 LOAD LẠI MỤC "NGHE GẦN ĐÂY" NGAY LẬP TỨC ĐỂ THẤY SỰ THAY ĐỔI
                loadRecentSongs();

                // 4. TODO: Chuyển sang PlayMusicActivity (Sau này làm)
                // Intent intent = new Intent(getContext(), PlayMusicActivity.class);
                // intent.putExtra("song_data", song);
                // startActivity(intent);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    // --- CÁC HÀM KHÁC (Giữ nguyên) ---
    private void setupArtists() {
        apiService.getAllArtists().enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Artist> artists = response.body();
                    if (artists.isEmpty()) { rvArtists.setVisibility(View.GONE); return; }
                    rvArtists.setVisibility(View.VISIBLE);

                    ArtistAdapter adapter = new ArtistAdapter(getContext(), artists, artist -> {
                        Toast.makeText(getContext(), "Ca sĩ: " + artist.getName(), Toast.LENGTH_SHORT).show();
                    });
                    rvArtists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvArtists.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) { Log.e("API_ARTIST", "Lỗi: " + t.getMessage()); }
        });
    }

    private void setupCategories() {
        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    if (categories.isEmpty()) { rvCategories.setVisibility(View.GONE); return; }
                    rvCategories.setVisibility(View.VISIBLE);

                    CategoryAdapter adapter = new CategoryAdapter(getContext(), categories, category -> {
                        Toast.makeText(getContext(), "Thể loại: " + category.getName(), Toast.LENGTH_SHORT).show();
                    });
                    rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvCategories.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) { Log.e("API_CATEGORY", "Lỗi: " + t.getMessage()); }
        });
    }
}