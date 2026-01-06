package com.example.music.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.Song;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddSongActivity extends AppCompatActivity {

    private EditText edtTitle, edtArtistId, edtImage, edtFile, edtDuration, edtCatId;
    private Button btnUpload, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_song);

        // 1. Ánh xạ View
        edtTitle = findViewById(R.id.edtTitle);
        edtArtistId = findViewById(R.id.edtArtist);
        edtImage = findViewById(R.id.edtImage);
        edtFile = findViewById(R.id.edtFile);
        edtDuration = findViewById(R.id.edtDuration);
        edtCatId = findViewById(R.id.edtCatId);
        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // 2. Xử lý Upload
        btnUpload.setOnClickListener(v -> {
            if (validateInput()) {
                performUpload();
            }
        });
    }

    private boolean validateInput() {
        if (edtTitle.getText().toString().isEmpty() || edtCatId.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performUpload() {
        // Tạo object Song từ dữ liệu nhập
        Song song = new Song();
        song.setTitle(edtTitle.getText().toString());
        Artist artist = new Artist();
        try {
            artist.setId(Long.parseLong(edtArtistId.getText().toString()));
        } catch (NumberFormatException e) {
            artist.setId(1L); // Mặc định ID 1 nếu nhập lỗi
        }
        song.setArtist(artist);
        song.setImageUrl(edtImage.getText().toString());
        song.setFileUrl(edtFile.getText().toString());

        // Xử lý số nguyên (Duration)
        try {
            song.setDuration(Integer.parseInt(edtDuration.getText().toString()));
        } catch (NumberFormatException e) {
            song.setDuration(0);
        }

        // Xử lý Category (Backend cần Object Category chứa ID)
        Category category = new Category();
        try {
            category.setId(Long.parseLong(edtCatId.getText().toString()));
        } catch (NumberFormatException e) {
            category.setId(1L); // Mặc định ID 1 nếu lỗi
        }
        song.setCategory(category);

        song.setFavorite(false);

        // Gọi API
        RetrofitClient.getClient().create(ApiService.class).addSong(song).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddSongActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình
                } else {
                    Toast.makeText(AdminAddSongActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {
                Toast.makeText(AdminAddSongActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}