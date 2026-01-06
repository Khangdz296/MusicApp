package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.adapter.ArtistAdapter;
import com.example.music.adapter.CategoryAdapterK;
import com.example.music.adapter.ChartAdapter;
import com.example.music.adapter.SongAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.Song;
import com.example.music.utils.MiniPlayerManager;
import com.example.music.utils.RecentSongManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvBanner, rvNewReleases, rvCharts, rvRecentlyPlayed, rvArtists, rvCategories;
    private ImageView icSettings;
    private ApiService apiService;
    private MiniPlayerManager miniPlayerManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_hoang, container, false);

        // Khởi tạo Mini Player Manager
        miniPlayerManager = MiniPlayerManager.getInstance();

        rvBanner = view.findViewById(R.id.rvHighlight);
        rvNewReleases = view.findViewById(R.id.rvNewReleases);
        rvCharts = view.findViewById(R.id.rvCharts);
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed);
        rvArtists = view.findViewById(R.id.rvArtists);
        rvCategories = view.findViewById(R.id.rvCategories);
        icSettings = view.findViewById(R.id.icSettings);

        icSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MenuActivity.class);
            startActivity(intent);
        });

        apiService = RetrofitClient.getClient().create(ApiService.class);

        fetchRandomSongsForHighlight();
        fetchNewSongs();
        setupArtists();
        setupCategories();
        setupFixedCharts();
        loadRecentSongs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentSongs();
    }

    private void setupFixedCharts() {
        if (getContext() == null) return;
        ChartAdapter chartAdapter = new ChartAdapter();
        rvCharts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCharts.setAdapter(chartAdapter);
        rvCharts.setVisibility(View.VISIBLE);
    }

    private void loadRecentSongs() {
        if (getContext() == null) return;
        List<Song> recentList = RecentSongManager.getRecentSongs(getContext());
        if (!recentList.isEmpty()) {
            rvRecentlyPlayed.setVisibility(View.VISIBLE);
            setupSection(rvRecentlyPlayed, recentList, SongAdapter.TYPE_STANDARD);
        } else {
            rvRecentlyPlayed.setVisibility(View.GONE);
        }
    }

    private void fetchRandomSongsForHighlight() {
        apiService.getRandomSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupSection(rvBanner, response.body(), SongAdapter.TYPE_BANNER);
                }
            }
            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                rvBanner.setVisibility(View.GONE);
                Log.e("API_RANDOM", "Lỗi: " + t.getMessage());
            }
        });
    }

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
                Log.e("API_NEW", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void setupSection(RecyclerView rv, List<Song> data, int type) {
        if (getContext() == null || data == null || data.isEmpty()) {
            rv.setVisibility(View.GONE);
            return;
        }
        rv.setVisibility(View.VISIBLE);

        SongAdapter adapter = new SongAdapter(data, type, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // Lưu vào lịch sử
                RecentSongManager.saveSong(getContext(), song);
                loadRecentSongs();

                // 👇 SỬ DỤNG MINI PLAYER thay vì mở PlayMusicActivity
                miniPlayerManager.playSong(song, data, data.indexOf(song));

                // Hoặc nếu muốn mở full player:
                // Intent intent = new Intent(getContext(), PlayMusicActivity.class);
                // intent.putExtra("song_data", song);
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
                    if (artists.isEmpty()) { rvArtists.setVisibility(View.GONE); return; }
                    rvArtists.setVisibility(View.VISIBLE);

                    ArtistAdapter adapter = new ArtistAdapter(getContext(), artists, artist -> {
                        Intent intent = new Intent(getContext(), ArtistDetailActivity.class);
                        intent.putExtra("ARTIST_OBJ", artist);
                        startActivity(intent);
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
                    if (categories.isEmpty()) { rvCategories.setVisibility(View.GONE); return; }
                    rvCategories.setVisibility(View.VISIBLE);
                    CategoryAdapterK categoryAdapter = new CategoryAdapterK(categories, (category, color) -> {
                        Intent intent = new Intent(getContext(), CategoryDetailActivity.class);
                        intent.putExtra("CAT_ID", category.getId());
                        intent.putExtra("CAT_NAME", category.getName());
                        intent.putExtra("CAT_COLOR", color);
                        startActivity(intent);
                    });
                    rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvCategories.setAdapter(categoryAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });
    }
}