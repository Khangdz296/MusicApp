package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.adapter.AlbumSongAdapter;
import com.example.music.model.Album;
import com.example.music.model.Song;
import com.example.music.utils.MiniPlayerManager;

import java.util.ArrayList;
import java.util.List;
import com.example.music.ui.AddToPlaylistHelper;

public class AlbumDetailActivity extends AppCompatActivity {
    private AddToPlaylistHelper addToPlaylistHelper;
    private FavoriteHelper favoriteHelper;
    private MiniPlayerManager miniPlayerManager;
    private List<Long> likedSongIds = new ArrayList<>();
    private AlbumSongAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        addToPlaylistHelper = new AddToPlaylistHelper(this);
        favoriteHelper = new FavoriteHelper(this);
        miniPlayerManager = MiniPlayerManager.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView imgCover = findViewById(R.id.imgAlbumCover);
        TextView tvName = findViewById(R.id.tvAlbumName);
        TextView tvArtist = findViewById(R.id.tvArtistName);
        RecyclerView rvSongs = findViewById(R.id.rvSongs);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        Album album = (Album) getIntent().getSerializableExtra("ALBUM_DATA");

        if (album != null) {
            tvName.setText(album.getName());

            if (album.getArtist() != null) {
                tvArtist.setText(album.getArtist().getName());
            } else {
                tvArtist.setText("Unknown Artist");
            }

            Glide.with(this)
                    .load(album.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgCover);

            List<Song> songs = album.getSongs();
            if (songs == null) songs = new ArrayList<>();

            List<Song> finalSongs = songs;

            adapter = new AlbumSongAdapter(songs, new AlbumSongAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Song song) {
                    // 👇 SỬ DỤNG MINI PLAYER thay vì mở PlayMusicActivity
                    int position = finalSongs.indexOf(song);
                    miniPlayerManager.playSong(song, finalSongs, position);
                }

                @Override
                public void onAddToPlaylistClick(Song song) {
                    addToPlaylistHelper.showAddToPlaylistDialog(song);
                }

                @Override
                public void onFavoriteClick(Song song, ImageView btnFavorite, List<Long> likedIds) {
                    favoriteHelper.toggleFavorite(song, btnFavorite, likedIds);
                }
            });

            rvSongs.setLayoutManager(new LinearLayoutManager(this));
            rvSongs.setAdapter(adapter);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        favoriteHelper.getLikedSongIds(ids -> {
            this.likedSongIds = ids;
            if (adapter != null) { // ✅ Bây giờ nó sẽ hiểu adapter là gì
                adapter.setLikedSongIds(ids);
            }
        });
    }
}