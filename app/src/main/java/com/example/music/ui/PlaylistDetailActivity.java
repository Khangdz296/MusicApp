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
import com.example.music.adapter.SongAdapter;
import com.example.music.adapter.SongAdapterK;
import com.example.music.model.Playlist;
import com.example.music.model.Song;
import com.example.music.utils.MiniPlayerManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PlaylistDetailActivity extends AppCompatActivity {

    private ImageView imgCover, btnBack;
    private TextView tvName, tvDesc, tvMeta;
    private RecyclerView rvSongs;
    private FloatingActionButton fabPlay;
    private MiniPlayerManager miniPlayerManager;

    private Playlist mPlaylist;
    private SongAdapterK songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail); // File XML của bạn

        // 1. Nhận dữ liệu từ màn hình trước
        mPlaylist = (Playlist) getIntent().getSerializableExtra("object_playlist");

        initViews();
        setupData();
    }

    private void initViews() {
        imgCover = findViewById(R.id.imgPlaylistCover);
        tvName = findViewById(R.id.tvPlaylistName);
        tvDesc = findViewById(R.id.tvPlaylistDesc);
        tvMeta = findViewById(R.id.tvPlaylistMeta);
        rvSongs = findViewById(R.id.rvSongs);
        fabPlay = findViewById(R.id.fabPlay);
        btnBack = findViewById(R.id.btnBack); // Nhớ thêm ID btnBack vào XML ở Bước 1
        miniPlayerManager = MiniPlayerManager.getInstance();

        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        // Nếu playlist null hoặc list song null thì khởi tạo list rỗng
        songAdapter = new SongAdapterK(this, new ArrayList<>(), new SongAdapterK.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // Tìm vị trí của bài hát trong danh sách
                int position = findSongPosition(song);

                Log.d("SEARCH_CLICK", "Người dùng đã chọn bài: " + song.getTitle());
                Log.d("SEARCH_CLICK", "ID bài hát: " + song.getId());
                Log.d("SEARCH_CLICK", "Vị trí: " + position);

                // Phát nhạc qua MiniPlayerManager
                miniPlayerManager.playSong(song, mPlaylist.getSongs(), position);

                // Chuyển sang màn hình PlayMusicActivity
                Intent intent = new Intent(PlaylistDetailActivity.this, PlayMusicActivity.class);
                intent.putExtra("song_data", song);
                intent.putExtra("song_list", new ArrayList<>(mPlaylist.getSongs()));
                intent.putExtra("current_position", position);
                startActivity(intent);
            }
        });
        rvSongs.setAdapter(songAdapter);
    }

    private void setupData() {
        if (mPlaylist == null) return;

        // Đổ dữ liệu Header
        tvName.setText(mPlaylist.getName());

        String owner = (mPlaylist.getUser() != null) ? mPlaylist.getUser().getUsername() : "Unknown";
        tvDesc.setText("Người tạo: " + owner);

        Glide.with(this).load(mPlaylist.getImageUrl()).into(imgCover);

        // Đổ dữ liệu List nhạc
        if (mPlaylist.getSongs() != null && !mPlaylist.getSongs().isEmpty()) {
            songAdapter.updateData(mPlaylist.getSongs());
            tvMeta.setText("Spotify • " + mPlaylist.getSongs().size() + " bài hát");
        } else {
            tvMeta.setText("Spotify • 0 bài hát");
        }
    }
    private int findSongPosition(Song song) {
        if (song == null || mPlaylist.getSongs().isEmpty()) {
            return 0;
        }

        for (int i = 0; i < mPlaylist.getSongs().size(); i++) {
            if (mPlaylist.getSongs().get(i).getId() == song.getId()) {
                return i;
            }
        }
        return 0;
    }
}