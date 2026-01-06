package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.adapter.AlbumAdapter;
import com.example.music.adapter.AlbumSongAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Album;
import com.example.music.model.Song;
import com.example.music.utils.RecentSongManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartDetailActivity extends AppCompatActivity {

    private RecyclerView rvChartSongs;
    private TextView tvTitleMain, tvTitleSub;
    private RelativeLayout headerLayout;
    private ImageView btnBack;
    private ApiService apiService;
    private String chartType; // "SONGS" hoặc "ALBUMS"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_detail);

        // Ẩn ActionBar mặc định
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // 1. Nhận dữ liệu phân loại từ Intent gửi từ ChartAdapter
        chartType = getIntent().getStringExtra("CHART_TYPE");
        if (chartType == null) chartType = "SONGS";

        initViews();

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // 2. Kiểm tra loại BXH để thay đổi giao diện và gọi API tương ứng
        if (chartType.equals("ALBUMS")) {
            setupAlbumUI();
            fetchTopAlbums();
        } else {
            setupSongUI();
            fetchTopViewSongs();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        rvChartSongs = findViewById(R.id.rvChartSongs);
        tvTitleMain = findViewById(R.id.tvTitleMain);
        tvTitleSub = findViewById(R.id.tvTitleSub);
        headerLayout = findViewById(R.id.headerLayout);
        btnBack = findViewById(R.id.btnBack);

        rvChartSongs.setLayoutManager(new LinearLayoutManager(this));
    }

    // Thiết lập giao diện khi xem Album
    private void setupAlbumUI() {
        tvTitleMain.setText("TOP ALBUMS");
        tvTitleSub.setText("Top 50 Album phổ biến nhất");
        // Đổi màu nền sang xanh/tím để phân biệt với Bài hát
        headerLayout.setBackgroundResource(R.drawable.bg_chart_global);
    }

    // Thiết lập giao diện khi xem Bài hát
    private void setupSongUI() {
        tvTitleMain.setText("MOST VIEWED");
        tvTitleSub.setText("Top 50 bài hát nhiều lượt xem nhất");
        headerLayout.setBackgroundResource(R.drawable.bg_chart_vietnam);
    }

    // --- GỌI API LẤY BÀI HÁT ---
    private void fetchTopViewSongs() {
        apiService.getTopSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = response.body();
                    AlbumSongAdapter songAdapter = new AlbumSongAdapter(songs, song -> {
                        Intent intent = new Intent(ChartDetailActivity.this, PlayMusicActivity.class);
                        intent.putExtra("song_data", song);
                        intent.putExtra("song_list", new ArrayList<>(songs));
                        startActivity(intent);
                        RecentSongManager.saveSong(ChartDetailActivity.this, song);
                    });
                    rvChartSongs.setAdapter(songAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Toast.makeText(ChartDetailActivity.this, "Lỗi tải BXH Bài hát", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- GỌI API LẤY ALBUM ---
    private void fetchTopAlbums() {
        apiService.getTop50Albums().enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Album> albums = response.body();
                    // Dùng AlbumAdapter để hiện giao diện danh sách giống Library
                    AlbumAdapter albumAdapter = new AlbumAdapter(ChartDetailActivity.this, albums, album -> {
                        Intent intent = new Intent(ChartDetailActivity.this, AlbumDetailActivity.class);
                        intent.putExtra("ALBUM_DATA", album);
                        startActivity(intent);
                    });
                    rvChartSongs.setAdapter(albumAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Toast.makeText(ChartDetailActivity.this, "Lỗi tải BXH Album", Toast.LENGTH_SHORT).show();
            }
        });
    }
}