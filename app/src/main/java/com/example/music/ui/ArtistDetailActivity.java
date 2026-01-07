package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.adapter.AlbumAdapter;
import com.example.music.adapter.SongAdapterK;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Album;
import com.example.music.model.Artist;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.music.ui.AddToPlaylistHelper;
public class ArtistDetailActivity extends AppCompatActivity {

    private ImageView imgArtist, btnBack;
    private TextView tvArtistName, tvDescription;
    private RecyclerView rvSongs, rvAlbums;
    private FavoriteHelper favoriteHelper; // 1. Khai báo
    private Artist artist;
    private SongAdapterK songAdapter;
    private AlbumAdapter albumAdapter;
    private ApiService apiService;
    // 1. Khai báo Helper
    private AddToPlaylistHelper addToPlaylistHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);
        //  Khởi tạo Helper (truyền Context vào)
        addToPlaylistHelper = new AddToPlaylistHelper(this);
        favoriteHelper = new FavoriteHelper(this); // 2. Khởi tạo
        // Khởi tạo ApiService
        apiService = RetrofitClient.getClient().create(ApiService.class);

        initViews();

        // 1. Nhận dữ liệu Artist từ Intent
        artist = (Artist) getIntent().getSerializableExtra("ARTIST_OBJ");

        if (artist != null) {
            displayArtistInfo();
            // 2. Gọi API lấy danh sách bài hát
            fetchSongsByArtist(artist.getId());
            // 3. Gọi API lấy danh sách album
            fetchAlbumsByArtist(artist.getId());
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu nghệ sĩ", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        imgArtist = findViewById(R.id.imgArtist);
        btnBack = findViewById(R.id.btnBack);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvDescription = findViewById(R.id.tvDescription);

        // Cấu hình danh sách bài hát (Dọc)
        rvSongs = findViewById(R.id.rvSongs);
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setNestedScrollingEnabled(false); // Giúp cuộn mượt trong NestedScrollView

        // Cấu hình danh sách Album (Ngang)
        rvAlbums = findViewById(R.id.rvAlbums);
        rvAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void displayArtistInfo() {
        tvArtistName.setText(artist.getName());

        if (artist.getDescription() != null && !artist.getDescription().isEmpty()) {
            tvDescription.setText(artist.getDescription());
        } else {
            tvDescription.setText("Chưa có thông tin mô tả về nghệ sĩ này.");
        }

        Glide.with(this)
                .load(artist.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgArtist);
    }

    private void fetchSongsByArtist(Long artistId) {
        apiService.getSongsByArtistId(artistId).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = response.body();
                    if (songs.isEmpty()) return;
                    songAdapter = new SongAdapterK(ArtistDetailActivity.this, songs, new SongAdapterK.OnSongClickListener() {
                        @Override
                        public void onSongClick(Song song) {
                            Intent intent = new Intent(ArtistDetailActivity.this, PlayMusicActivity.class);
                            intent.putExtra("song_data", song);
                            intent.putExtra("song_list", new ArrayList<>(songs));
                            startActivity(intent);
                        }

                        @Override
                        public void onAddToPlaylistClick(Song song) {
                            // 3. GỌI HELPER ĐỂ HIỆN BOTTOM SHEET
                            addToPlaylistHelper.showAddToPlaylistDialog(song);
                        }
                        @Override
                        public void onFavoriteClick(Song song, ImageView btnFavorite, List<Long> ids) {
                            // 👇 GỌI HELPER VỚI DANH SÁCH ID
                            favoriteHelper.toggleFavorite(song, btnFavorite, ids);
                        }
//                    songAdapter = new SongAdapterK(ArtistDetailActivity.this, songs, song -> {
//                        Intent intent = new Intent(ArtistDetailActivity.this, PlayMusicActivity.class);
//                        intent.putExtra("song_data", song);
//                        intent.putExtra("song_list", new ArrayList<>(songs));
//                        startActivity(intent);
                    });
                    rvSongs.setAdapter(songAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("API_ERROR", "Songs: " + t.getMessage());
            }
        });
    }

    private void fetchAlbumsByArtist(Long artistId) {
        apiService.getAlbumsByArtistId(artistId).enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Album> albums = response.body();
                    if (albums.isEmpty()) return;

                    albumAdapter = new AlbumAdapter(ArtistDetailActivity.this, albums, album -> {
                        Intent intent = new Intent(ArtistDetailActivity.this, AlbumDetailActivity.class);
                        intent.putExtra("ALBUM_DATA", album);
                        startActivity(intent);
                    });
                    rvAlbums.setAdapter(albumAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.e("API_ERROR", "Albums: " + t.getMessage());
            }
        });
    }
}