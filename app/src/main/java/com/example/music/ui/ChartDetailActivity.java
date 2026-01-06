package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.adapter.AlbumSongAdapter; // 👇 1. DÙNG ADAPTER NÀY
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Song;
import com.example.music.utils.RecentSongManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartDetailActivity extends AppCompatActivity {

    private RecyclerView rvChartSongs;
    private AlbumSongAdapter adapter; // 👇 2. Đổi tên class Adapter
    private List<Song> mListSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_detail);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        rvChartSongs = findViewById(R.id.rvChartSongs);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        mListSongs = new ArrayList<>();

        // 👇 3. KHỞI TẠO AlbumSongAdapter (Thay vì SongAdapter)
        adapter = new AlbumSongAdapter(mListSongs, new AlbumSongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                // Click vào bài hát -> Chuyển sang phát nhạc
                Intent intent = new Intent(ChartDetailActivity.this, PlayMusicActivity.class);
                intent.putExtra("song_data", song);
                startActivity(intent);

                // Lưu lịch sử
                RecentSongManager.saveSong(ChartDetailActivity.this, song);
            }
        });

        rvChartSongs.setLayoutManager(new LinearLayoutManager(this));
        rvChartSongs.setAdapter(adapter);

        // Gọi API
        fetchTopViewSongs();
    }

    private void fetchTopViewSongs() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getTopSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mListSongs.clear();
                    mListSongs.addAll(response.body());
                    // Lưu ý: AlbumSongAdapter dùng setList hoặc notifyDataSetChanged tuỳ code cũ của bạn
                    // Nếu adapter không tự update, bạn có thể set lại list:
                    // adapter = new AlbumSongAdapter(mListSongs, ...);
                    // rvChartSongs.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Toast.makeText(ChartDetailActivity.this, "Lỗi tải BXH", Toast.LENGTH_SHORT).show();
            }
        });
    }
}