package com.example.music.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.adapter.SongAdapter;
import com.example.music.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    private RecyclerView rvSongs;
    private SongAdapter songAdapter;
    private ImageView btnHeart, btnDownload;
    private FloatingActionButton btnPlayBig; // Nút Play xanh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // GỌI ĐÚNG TÊN FILE XML CỦA BẠN
        setContentView(R.layout.activity_public_playlist_detail_dhuy);

        // 1. Ánh xạ View
        rvSongs = findViewById(R.id.rvSongs);
        btnHeart = findViewById(R.id.btnHeart);
        btnDownload = findViewById(R.id.btnDownload);

        // Đã thêm ID trong XML thì giờ ánh xạ được rồi
        btnPlayBig = findViewById(R.id.btnPlayBig);

        // 2. Setup RecyclerView
        rvSongs.setLayoutManager(new LinearLayoutManager(this));

        // Giả lập danh sách nhạc
        List<Song> songs = new ArrayList<>();
        songs.add(new Song("1", "Easy", "Troye Sivan", "", "", 200));
        songs.add(new Song("2", "Chance with you", "mehro", "", "", 180));

        // Truyền 'this' vào Adapter (vì Adapter bạn đã update có context)
        songAdapter = new SongAdapter(this, songs);
        rvSongs.setAdapter(songAdapter);

        // 3. Sự kiện Click nút Play xanh
        btnPlayBig.setOnClickListener(v -> {
            Toast.makeText(this, "Phát ngẫu nhiên tất cả!", Toast.LENGTH_SHORT).show();
        });
    }
}