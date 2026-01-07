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
import com.example.music.utils.MiniPlayerManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.music.ui.AddToPlaylistHelper;

public class CategoryDetailActivity extends AppCompatActivity {
    private FavoriteHelper favoriteHelper;
    private RecyclerView rcvSongs;
    private SongAdapterK songAdapter;
    private TextView tvTitle;
    private View viewHeader;
    private ImageView btnBack;
    private AddToPlaylistHelper addToPlaylistHelper;
    private MiniPlayerManager miniPlayerManager;
    private List<Long> likedSongIds = new ArrayList<>();

    private List<Song> currentSongList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail_khang);

        addToPlaylistHelper = new AddToPlaylistHelper(this);
        favoriteHelper = new FavoriteHelper(this);
        miniPlayerManager = MiniPlayerManager.getInstance();

        tvTitle = findViewById(R.id.tvCategoryTitleDetail);
        viewHeader = findViewById(R.id.viewHeaderBackground);
        rcvSongs = findViewById(R.id.rcvDetailSongs);
        btnBack = findViewById(R.id.btnBack);

        long catId = getIntent().getLongExtra("CAT_ID", -1);
        String catName = getIntent().getStringExtra("CAT_NAME");
        int catColor = getIntent().getIntExtra("CAT_COLOR", Color.GRAY);

        tvTitle.setText(catName);
        viewHeader.setBackgroundColor(catColor);
        btnBack.setOnClickListener(v -> finish());

        rcvSongs.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapterK(this, new ArrayList<>(), new SongAdapterK.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // 👇 SỬ DỤNG MINI PLAYER thay vì mở PlayMusicActivity
                int position = currentSongList.indexOf(song);
                miniPlayerManager.playSong(song, currentSongList, position);
            }

            @Override
            public void onAddToPlaylistClick(Song song) {
                addToPlaylistHelper.showAddToPlaylistDialog(song);
            }

            @Override
            public void onFavoriteClick(Song song, ImageView btnFavorite, List<Long> ids) {
                favoriteHelper.toggleFavorite(song, btnFavorite, ids);
            }
        });
        rcvSongs.setAdapter(songAdapter);

        if (catId != -1) {
            loadSongsByCategory(catId);
        }
    }

    private void loadSongsByCategory(long id) {
        RetrofitClient.getClient().create(ApiService.class).getSongsByCategory(id).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
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
    @Override
    protected void onResume() {
        super.onResume();

        // 👇 GỌI 1 DÒNG LÀ XONG (Helper lo hết)
        favoriteHelper.getLikedSongIds(ids -> {
            // Cập nhật list ID vào biến cục bộ
            this.likedSongIds = ids;

            // Cập nhật Adapter
            if (songAdapter != null) {
                songAdapter.setLikedSongIds(ids);
            }
        });
    }
}