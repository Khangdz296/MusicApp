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
import com.example.music.model.Category;
import com.example.music.model.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddSongActivity extends AppCompatActivity {

    private EditText edtTitle, edtArtist, edtImage, edtFile, edtDuration;
    private Spinner spinnerCategory; // Thay EditText bằng Spinner
    private Button btnUpload, btnBack;
    private Song mSongToEdit = null;

    // List để lưu trữ dữ liệu Category tải về
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>(); // Chỉ để hiển thị tên lên Spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_song);

        // 1. Ánh xạ
        edtTitle = findViewById(R.id.edtTitle);
        edtArtist = findViewById(R.id.edtArtist);
        edtImage = findViewById(R.id.edtImage);
        edtFile = findViewById(R.id.edtFile);
        edtDuration = findViewById(R.id.edtDuration);

        spinnerCategory = findViewById(R.id.spinnerCategory); // Ánh xạ Spinner mới

        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // 2. Gọi API lấy danh sách Category để đổ vào Spinner
        if (getIntent().getExtras() != null) {
            mSongToEdit = (Song) getIntent().getSerializableExtra("SONG_DATA");
        }

        loadCategoriesToSpinner(); // Load danh sách thể loại

        // 2. NẾU LÀ CHẾ ĐỘ SỬA -> ĐIỀN DỮ LIỆU CŨ VÀO FORM
        if (mSongToEdit != null) {
            setupEditMode();
        }

        // 3. XỬ LÝ NÚT BẤM (Phân chia Add hay Update)
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
        // Đổi tên nút và tiêu đề cho hợp lý
        btnUpload.setText("CẬP NHẬT BÀI HÁT");
        // ((TextView)findViewById(R.id.tvHeader)).setText("Sửa Bài Hát");

        // Điền dữ liệu cũ
        edtTitle.setText(mSongToEdit.getTitle());
        edtArtist.setText(mSongToEdit.getArtist());
        edtImage.setText(mSongToEdit.getImageUrl());
        edtFile.setText(mSongToEdit.getFileUrl());
        edtDuration.setText(String.valueOf(mSongToEdit.getDuration()));

        // *Lưu ý: Việc set selection cho Spinner phải làm sau khi API Category load xong
        // (Xem xử lý ở hàm loadCategoriesToSpinner bên dưới)
    }
    private void loadCategoriesToSpinner() {
        RetrofitClient.getClient().create(ApiService.class).getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();

                    categoryNames.clear();

                    // --- SỬA 1: THÊM DÒNG HINT ---
                    categoryNames.add("--- Chọn Thể Loại ---");
                    // -----------------------------

                    for (Category cat : categoryList) {
                        categoryNames.add(cat.getName());
                    }

                    // Adapter setup... (Giữ nguyên)
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminAddSongActivity.this,
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);

                    // Logic chọn lại category cũ khi Edit
                    if (mSongToEdit != null && mSongToEdit.getCategory() != null) {
                        long oldCatId = mSongToEdit.getCategory().getId();
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getId() == oldCatId) {
                                // +1 là đúng, vì index 0 là dòng "--- Chọn ---"
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

        if (spinnerCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn thể loại!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void performUpload() {
        // ... Code tạo song và set Title, Artist giữ nguyên ...
        Song song = new Song();
        song.setTitle(edtTitle.getText().toString());
        song.setArtist(edtArtist.getText().toString());
        song.setImageUrl(edtImage.getText().toString());
        song.setFileUrl(edtFile.getText().toString());

        try {
            song.setDuration(Integer.parseInt(edtDuration.getText().toString()));
        } catch (NumberFormatException e) {
            song.setDuration(0);
        }

        // --- SỬA 3: TRỪ ĐI 1 ĐỂ LẤY ĐÚNG INDEX TRONG LIST ---
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        Category selectedCategory = categoryList.get(selectedPosition - 1);
        song.setCategory(selectedCategory);
        // ----------------------------------------------------

        song.setFavorite(false);

        // ... Gọi API giữ nguyên ...
        RetrofitClient.getClient().create(ApiService.class).addSong(song);
    }
    private void performUpdate() {
        // --- SỬA 4: PHẢI LẤY DỮ LIỆU TỪ EDIT TEXT GÁN VÀO OBJECT ---
        mSongToEdit.setTitle(edtTitle.getText().toString());
        mSongToEdit.setArtist(edtArtist.getText().toString());
        mSongToEdit.setImageUrl(edtImage.getText().toString());
        mSongToEdit.setFileUrl(edtFile.getText().toString());
        try {
            mSongToEdit.setDuration(Integer.parseInt(edtDuration.getText().toString()));
        } catch (NumberFormatException e) {
            mSongToEdit.setDuration(0);
        }
        // -----------------------------------------------------------

        // Lấy Category từ Spinner (Code cũ bạn viết đúng rồi, giữ nguyên logic -1)
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        Category selectedCategory = categoryList.get(selectedPosition - 1);
        mSongToEdit.setCategory(selectedCategory);

        // GỌI API UPDATE
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
                    @Override public void onFailure(Call<Song> call, Throwable t) {
                        Toast.makeText(AdminAddSongActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}