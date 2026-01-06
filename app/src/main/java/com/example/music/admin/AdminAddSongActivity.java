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
import com.example.music.model.Artist;
import com.example.music.model.Category;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddSongActivity extends AppCompatActivity {

    private EditText edtTitle, edtImage, edtFile, edtDuration;
    // Thay EditText bằng 2 Spinner
    private Spinner spinnerCategory, spinnerArtist;
    private Button btnUpload, btnBack;
    private Song mSongToEdit = null;

    // List cho Category
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    // List cho Artist (MỚI)
    private List<Artist> artistList = new ArrayList<>();
    private List<String> artistNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_song);

        // 1. Ánh xạ
        edtTitle = findViewById(R.id.edtTitle);
        edtImage = findViewById(R.id.edtImage);
        edtFile = findViewById(R.id.edtFile);
        edtDuration = findViewById(R.id.edtDuration);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerArtist = findViewById(R.id.spinnerArtist); // Ánh xạ Spinner Artist mới

        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // 2. Nhận dữ liệu Edit
        if (getIntent().getExtras() != null) {
            mSongToEdit = (Song) getIntent().getSerializableExtra("SONG_DATA");
        }

        // 3. Load dữ liệu cho 2 Spinner
        loadCategoriesToSpinner();
        loadArtistsToSpinner(); // <-- GỌI HÀM MỚI

        // 4. Setup giao diện Edit (Điền text đơn giản)
        if (mSongToEdit != null) {
            btnUpload.setText("CẬP NHẬT BÀI HÁT");
            edtTitle.setText(mSongToEdit.getTitle());
            edtImage.setText(mSongToEdit.getImageUrl());
            edtFile.setText(mSongToEdit.getFileUrl());
            edtDuration.setText(String.valueOf(mSongToEdit.getDuration()));
        }

        // 5. Xử lý nút bấm
        btnUpload.setOnClickListener(v -> {
            if (validateInput()) {
                if (mSongToEdit == null) {
                    performUpload();
                } else {
                    performUpdate();
                }
            }
        });
    }

    // --- LOGIC LOAD ARTIST SPINNER (Y HỆT CATEGORY) ---
    private void loadArtistsToSpinner() {
        RetrofitClient.getClient().create(ApiService.class).getAllArtists().enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artistList = response.body();
                    artistNames.clear();
                    artistNames.add("--- Chọn Ca Sĩ ---"); // Dòng Hint

                    for (Artist art : artistList) {
                        artistNames.add(art.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminAddSongActivity.this,
                            R.layout.item_spinner_white, artistNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerArtist.setAdapter(adapter);

                    // Nếu đang Edit -> Tự chọn lại Artist cũ
                    if (mSongToEdit != null && mSongToEdit.getArtist() != null) {
                        long oldId = mSongToEdit.getArtist().getId();
                        for (int i = 0; i < artistList.size(); i++) {
                            if (artistList.get(i).getId() == oldId) {
                                spinnerArtist.setSelection(i + 1); // +1 vì có dòng hint
                                break;
                            }
                        }
                    }
                }
            }
            @Override public void onFailure(Call<List<Artist>> call, Throwable t) {}
        });
    }

    // --- LOGIC LOAD CATEGORY SPINNER (GIỮ NGUYÊN) ---
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
                            R.layout.item_spinner_white, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);

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
            @Override public void onFailure(Call<List<Category>> call, Throwable t) {}
        });
    }

    private boolean validateInput() {
        if (edtTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Chưa nhập tên bài hát!", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Kiểm tra Spinner Category
        if (spinnerCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn thể loại!", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Kiểm tra Spinner Artist (MỚI)
        if (spinnerArtist.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn ca sĩ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performUpload() {
        Song song = new Song();
        song.setTitle(edtTitle.getText().toString());
        song.setImageUrl(edtImage.getText().toString());
        song.setFileUrl(edtFile.getText().toString());

        try { song.setDuration(Integer.parseInt(edtDuration.getText().toString())); }
        catch (Exception e) { song.setDuration(0); }

        // 1. Lấy Category từ Spinner (Trừ 1 do hint)
        int catPos = spinnerCategory.getSelectedItemPosition();
        song.setCategory(categoryList.get(catPos - 1));

        // 2. Lấy Artist từ Spinner (MỚI - Trừ 1 do hint)
        int artPos = spinnerArtist.getSelectedItemPosition();
        song.setArtist(artistList.get(artPos - 1));

        song.setFavorite(false);

        RetrofitClient.getClient().create(ApiService.class).addSong(song).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddSongActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else Toast.makeText(AdminAddSongActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<Song> call, Throwable t) {
                Toast.makeText(AdminAddSongActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performUpdate() {
        mSongToEdit.setTitle(edtTitle.getText().toString());
        mSongToEdit.setImageUrl(edtImage.getText().toString());
        mSongToEdit.setFileUrl(edtFile.getText().toString());

        try { mSongToEdit.setDuration(Integer.parseInt(edtDuration.getText().toString())); }
        catch (Exception e) { mSongToEdit.setDuration(0); }

        // Update Category
        int catPos = spinnerCategory.getSelectedItemPosition();
        mSongToEdit.setCategory(categoryList.get(catPos - 1));

        // Update Artist (MỚI)
        int artPos = spinnerArtist.getSelectedItemPosition();
        mSongToEdit.setArtist(artistList.get(artPos - 1));

        RetrofitClient.getClient().create(ApiService.class)
                .updateSong(mSongToEdit.getId(), mSongToEdit)
                .enqueue(new Callback<Song>() {
                    @Override
                    public void onResponse(Call<Song> call, Response<Song> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminAddSongActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else Toast.makeText(AdminAddSongActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(Call<Song> call, Throwable t) {
                        Toast.makeText(AdminAddSongActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}