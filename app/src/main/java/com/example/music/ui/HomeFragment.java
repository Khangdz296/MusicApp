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

import java.util.ArrayList;
import java.util.Collections;
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
        fetchBannerAndCharts(); // Xử lý 3 mục: Banner, BXH, Gần đây
        fetchNewSongs();        // Xử lý mục: Nhạc mới phát hành
        setupArtists();         // Xử lý mục: Nghệ sĩ (API thật)
        setupCategories();      // Xử lý mục: Thể loại (API thật)

        return view;
    }

    private void fetchBannerAndCharts() {
        apiService.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> allSongs = response.body();

                    if (allSongs.isEmpty()) return;

                    // A. BANNER: Lấy 5 bài đầu tiên
                    List<Song> bannerList = new ArrayList<>();
                    if (allSongs.size() >= 5) {
                        bannerList = allSongs.subList(0, 5);
                    } else {
                        bannerList = allSongs;
                    }
                    setupSection(rvBanner, bannerList, SongAdapter.TYPE_BANNER);

                    // B. BXH (CHARTS): Xáo trộn danh sách ngẫu nhiên -> Lấy 7 bài
                    List<Song> chartList = new ArrayList<>(allSongs);
                    Collections.shuffle(chartList);
                    if (chartList.size() > 7) {
                        chartList = chartList.subList(0, 7);
                    }
                    setupSection(rvCharts, chartList, SongAdapter.TYPE_STANDARD);

                    // C. NGHE GẦN ĐÂY: Lấy 3 bài cuối danh sách (Giả lập)
                    List<Song> recentList = new ArrayList<>();
                    if (allSongs.size() > 3) {
                        recentList = allSongs.subList(allSongs.size() - 3, allSongs.size());
                    } else {
                        recentList = allSongs;
                    }
                    setupSection(rvRecentlyPlayed, recentList, SongAdapter.TYPE_RECENT);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_SONG", "Lỗi lấy All Songs: " + t.getMessage());
            }
        });
    }

    private void fetchNewSongs() {
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
                Log.e("API_SONG", "Lỗi lấy New Songs: " + t.getMessage());
            }
        });
    }

    // Hàm chung để cài đặt Adapter cho các list nhạc
    private void setupSection(RecyclerView rv, List<Song> data, int type) {
        if (getContext() == null || data == null || data.isEmpty()) {
            rv.setVisibility(View.GONE); // Ẩn nếu không có dữ liệu
            return;
        }
        rv.setVisibility(View.VISIBLE);

        SongAdapter adapter = new SongAdapter(data, type, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // SỰ KIỆN CLICK VÀO BÀI HÁT
                Toast.makeText(getContext(), "Phát: " + song.getTitle(), Toast.LENGTH_SHORT).show();

                // TODO: Chuyển sang PlayMusicActivity
                // Intent intent = new Intent(getContext(), PlayMusicActivity.class);
                // intent.putExtra("song_data", song); // Song phải implements Serializable
                // startActivity(intent);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }


    private void setupArtists() {
        apiService.getAllArtists().enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Artist> artists = response.body();

                    if (artists.isEmpty()) {
                        rvArtists.setVisibility(View.GONE);
                        return;
                    }
                    rvArtists.setVisibility(View.VISIBLE);

                    ArtistAdapter adapter = new ArtistAdapter(getContext(), artists, artist -> {
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

    private void setupCategories() {
        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();

                    if (categories.isEmpty()) {
                        rvCategories.setVisibility(View.GONE);
                        return;
                    }
                    rvCategories.setVisibility(View.VISIBLE);

                    CategoryAdapter adapter = new CategoryAdapter(getContext(), categories, category -> {
                        Toast.makeText(getContext(), "Thể loại: " + category.getName(), Toast.LENGTH_SHORT).show();
                    });

                    rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvCategories.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_CATEGORY", "Lỗi: " + t.getMessage());
            }
        });
    }
}