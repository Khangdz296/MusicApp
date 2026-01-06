package com.example.music.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Artist; // Nhớ import Artist
import com.example.music.model.Category;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddSongActivity extends AppCompatActivity {

    // Đổi tên biến edtArtist thành edtArtistId cho rõ nghĩa
    private EditText edtTitle, edtArtistId, edtImage, edtFile, edtDuration;
    private Spinner spinnerCategory;
    private Button btnUpload, btnBack;
    private Song mSongToEdit = null;

    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_song);

        // 1. Ánh xạ
        edtTitle = findViewById(R.id.edtTitle);
        edtArtistId = findViewById(R.id.edtArtist); // ID trong XML vẫn là edtArtist
        edtImage = findViewById(R.id.edtImage);
        edtFile = findViewById(R.id.edtFile);
        edtDuration = findViewById(R.id.edtDuration);

        spinnerCategory = findViewById(R.id.spinnerCategory);

        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // 2. Nhận dữ liệu Edit (nếu có)
        if (getIntent().getExtras() != null) {
            mSongToEdit = (Song) getIntent().getSerializableExtra("SONG_DATA");
        }

        loadCategoriesToSpinner();

        // 3. Setup giao diện Edit
        if (mSongToEdit != null) {
            setupEditMode();
        }

        // 4. Xử lý nút bấm
        btnUpload.setOnClickListener(v -> {
            if (validateInput()) {
                if (mSongToEdit == null) {
                    performUpload(); // Thêm mới
                } else {
                    performUpdate(); // Cập nhật
                }
            }
        });
    }

    private void setupEditMode() {
        btnUpload.setText("CẬP NHẬT BÀI HÁT");

        edtTitle.setText(mSongToEdit.getTitle());

        // --- SỬA LOGIC ARTIST: Hiển thị ID thay vì Tên ---
        if (mSongToEdit.getArtist() != null) {
            edtArtistId.setText(String.valueOf(mSongToEdit.getArtist().getId()));
        }
        // ------------------------------------------------

        edtImage.setText(mSongToEdit.getImageUrl());
        edtFile.setText(mSongToEdit.getFileUrl());
        edtDuration.setText(String.valueOf(mSongToEdit.getDuration()));
    }

    private void loadCategoriesToSpinner() {
        RetrofitClient.getClient().create(ApiService.class).getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    categoryNames.clear();
                    categoryNames.add("--- Chọn Thể Loại ---");

                    for (Category cat : categoryList) {
                        categoryNames.add(cat.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminAddSongActivity.this,
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);

                    // Logic chọn lại category cũ
                    if (mSongToEdit != null && mSongToEdit.getCategory() != null) {
                        long oldCatId = mSongToEdit.getCategory().getId();
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getId() == oldCatId) {
                                spinnerCategory.setSelection(i + 1);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AdminAddSongActivity.this, "Lỗi tải thể loại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        if (edtTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Chưa nhập tên bài hát!", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Kiểm tra nhập ID Artist
        if (edtArtistId.getText().toString().isEmpty()) {
            Toast.makeText(this, "Chưa nhập ID Nghệ sĩ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn thể loại!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performUpload() {
        Song song = new Song();
        song.setTitle(edtTitle.getText().toString());
        song.setImageUrl(edtImage.getText().toString());
        song.setFileUrl(edtFile.getText().toString());

        // --- SỬA LOGIC ARTIST: Tạo Object và set ID ---
        Artist artist = new Artist();
        try {
            artist.setId(Long.parseLong(edtArtistId.getText().toString()));
        } catch (NumberFormatException e) {
            artist.setId(1L); // Default nếu nhập sai
        }
        song.setArtist(artist);
        // ---------------------------------------------

        try {
            song.setDuration(Integer.parseInt(edtDuration.getText().toString()));
        } catch (NumberFormatException e) {
            song.setDuration(0);
        }

        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        Category selectedCategory = categoryList.get(selectedPosition - 1);
        song.setCategory(selectedCategory);

        song.setFavorite(false);

        RetrofitClient.getClient().create(ApiService.class).addSong(song).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddSongActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminAddSongActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Song> call, Throwable t) {
                Toast.makeText(AdminAddSongActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performUpdate() {
        mSongToEdit.setTitle(edtTitle.getText().toString());
        mSongToEdit.setImageUrl(edtImage.getText().toString());
        mSongToEdit.setFileUrl(edtFile.getText().toString());

        // --- SỬA LOGIC ARTIST (Update) ---
        Artist artist = new Artist();
        try {
            artist.setId(Long.parseLong(edtArtistId.getText().toString()));
        } catch (NumberFormatException e) {
            artist.setId(1L);
        }
        mSongToEdit.setArtist(artist);
        // ---------------------------------

        try {
            mSongToEdit.setDuration(Integer.parseInt(edtDuration.getText().toString()));
        } catch (NumberFormatException e) {
            mSongToEdit.setDuration(0);
        }

        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        Category selectedCategory = categoryList.get(selectedPosition - 1);
        mSongToEdit.setCategory(selectedCategory);

        // Lưu ý: Kiểm tra lại xem bạn dùng @PUT hay @POST ở ApiService
        RetrofitClient.getClient().create(ApiService.class)
                .updateSong(mSongToEdit.getId(), mSongToEdit)
                .enqueue(new Callback<Song>() {
                    @Override
                    public void onResponse(Call<Song> call, Response<Song> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminAddSongActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AdminAddSongActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Song> call, Throwable t) {
                        Toast.makeText(AdminAddSongActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}