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
import com.example.music.adapter.AlbumSongAdapter; // 👇 Dùng Adapter mới
import com.example.music.model.Album;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;
import com.example.music.ui.AddToPlaylistHelper;
public class AlbumDetailActivity extends AppCompatActivity {
    private AddToPlaylistHelper addToPlaylistHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        // 2. Khởi tạo Helper
        addToPlaylistHelper = new AddToPlaylistHelper(this);
        // 👇 1. ẨN ACTION BAR MẶC ĐỊNH
        // (Vì layout XML của mình đã có Header đẹp và nút Back riêng rồi)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 👇 2. ÁNH XẠ CÁC VIEW (Khớp với ID trong XML mới nhất)
        ImageView btnBack = findViewById(R.id.btnBack);       // Nút quay lại
        ImageView imgCover = findViewById(R.id.imgAlbumCover); // Ảnh bìa to
        TextView tvName = findViewById(R.id.tvAlbumName);     // Tên Album
        TextView tvArtist = findViewById(R.id.tvArtistName);   // Tên Ca sĩ
        RecyclerView rvSongs = findViewById(R.id.rvSongs);    // List nhạc

        // 👇 3. XỬ LÝ SỰ KIỆN NÚT BACK (QUAN TRỌNG)
        btnBack.setOnClickListener(v -> {
            onBackPressed(); // Quay lại màn hình trước
        });

        // 4. NHẬN DỮ LIỆU TỪ INTENT
        Album album = (Album) getIntent().getSerializableExtra("ALBUM_DATA");

        if (album != null) {
            // Hiển thị thông tin lên Header
            tvName.setText(album.getName());

            // Check null cho an toàn
            if (album.getArtist() != null) {
                tvArtist.setText(album.getArtist().getName());
            } else {
                tvArtist.setText("Unknown Artist");
            }

            Glide.with(this)
                    .load(album.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgCover);

            // Lấy danh sách bài hát
            List<Song> songs = album.getSongs();
            if (songs == null) songs = new ArrayList<>();

            List<Song> finalSongs = songs; // Lưu biến final để dùng trong lambda

            AlbumSongAdapter adapter = new AlbumSongAdapter(songs, new AlbumSongAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Song song) {
                    int position = finalSongs.indexOf(song); // ✅ Tìm vị trí bài hát

                    Intent intent = new Intent(AlbumDetailActivity.this, PlayMusicActivity.class);
                    intent.putExtra("song_data", song);
                    intent.putExtra("current_position", position);        // ✅ Thêm vị trí
                    intent.putExtra("song_list", new ArrayList<>(finalSongs)); // ✅ Thêm danh sách
                    startActivity(intent);
                }
                @Override
                public void onAddToPlaylistClick(Song song) {addToPlaylistHelper.showAddToPlaylistDialog(song);}
            });

            rvSongs.setLayoutManager(new LinearLayoutManager(this)); // Xếp dọc
            rvSongs.setAdapter(adapter);
        }
    }
}