package com.example.music.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.adapter.SongAdapter;
import com.example.music.adapter.SongAdapterK;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Song;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.music.ui.AddToPlaylistHelper;
public class CategoryDetailActivity extends AppCompatActivity {

    private RecyclerView rcvSongs;
    private SongAdapterK songAdapter;
    private TextView tvTitle;
    private View viewHeader;
    private ImageView btnBack;
    private AddToPlaylistHelper addToPlaylistHelper;
    // 👇 Lưu danh sách bài hát để truyền sang PlayMusicActivity
    private List<Song> currentSongList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail_khang);
        // 2. Khởi tạo Helper (truyền Context vào)
        addToPlaylistHelper = new AddToPlaylistHelper(this);
        // 1. Ánh xạ
        tvTitle = findViewById(R.id.tvCategoryTitleDetail);
        viewHeader = findViewById(R.id.viewHeaderBackground);
        rcvSongs = findViewById(R.id.rcvDetailSongs);
        btnBack = findViewById(R.id.btnBack);

        // 2. Nhận dữ liệu từ Intent
        long catId = getIntent().getLongExtra("CAT_ID", -1);
        String catName = getIntent().getStringExtra("CAT_NAME");
        int catColor = getIntent().getIntExtra("CAT_COLOR", Color.GRAY);

        // 3. Update UI
        tvTitle.setText(catName);
        viewHeader.setBackgroundColor(catColor);
        btnBack.setOnClickListener(v -> finish());

        // 4. Setup RecyclerView
        rcvSongs.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapterK(this, new ArrayList<>(), new SongAdapterK.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // 👇 Tìm vị trí bài hát trong danh sách
                int position = currentSongList.indexOf(song);

                // 👇 Chuyển sang PlayMusicActivity với đầy đủ thông tin
                Intent intent = new Intent(CategoryDetailActivity.this, PlayMusicActivity.class);
                intent.putExtra("song_data", song);
                intent.putExtra("current_position", position);
                intent.putExtra("song_list", new ArrayList<>(currentSongList));
                startActivity(intent);
            }
            @Override
            public void onAddToPlaylistClick(Song song) {
                addToPlaylistHelper.showAddToPlaylistDialog(song);
            }
        });
        rcvSongs.setAdapter(songAdapter);

        // 5. Gọi API lấy bài hát
        if (catId != -1) {
            loadSongsByCategory(catId);
        }
    }

    private void loadSongsByCategory(long id) {
        RetrofitClient.getClient().create(ApiService.class).getSongsByCategory(id).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 👇 Lưu danh sách vào biến để dùng khi click
                    currentSongList = response.body();
                    songAdapter.updateData(currentSongList);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Toast.makeText(CategoryDetailActivity.this, "Không tải được bài hát", Toast.LENGTH_SHORT).show();
            }
        });
    }
}