package com.example.music.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Category;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddCategoryActivity extends AppCompatActivity {

    private EditText edtName, edtImage;
    private Button btnSave, btnCancel;
    private TextView tvHeader;
    private Category mCategoryEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_category);

        edtName = findViewById(R.id.edtName);
        edtImage = findViewById(R.id.edtImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        tvHeader = findViewById(R.id.tvHeader);

        btnCancel.setOnClickListener(v -> finish());

        // Kiểm tra xem có dữ liệu truyền qua để Sửa không
        if (getIntent().getExtras() != null) {
            mCategoryEdit = (Category) getIntent().getSerializableExtra("DATA_CATEGORY");
        }

        if (mCategoryEdit != null) {
            setupEditMode();
        }

        btnSave.setOnClickListener(v -> {
            if (edtName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Nhập tên thể loại!", Toast.LENGTH_SHORT).show();
                return;
            }
            saveData();
        });
    }

    private void setupEditMode() {
        tvHeader.setText("Cập Nhật Thể Loại");
        btnSave.setText("LƯU THAY ĐỔI");
        edtName.setText(mCategoryEdit.getName());
        edtImage.setText(mCategoryEdit.getImageUrl());
    }

    private void saveData() {
        // Tạo object mới hoặc dùng object cũ
        Category category = (mCategoryEdit == null) ? new Category() : mCategoryEdit;

        category.setName(edtName.getText().toString());
        category.setImageUrl(edtImage.getText().toString());

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Category> call;

        if (mCategoryEdit == null) {
            // Thêm mới (POST)
            call = api.addCategory(category);
        } else {
            // Cập nhật (PUT)
            call = api.updateCategory(category.getId(), category);
        }

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddCategoryActivity.this, "Thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminAddCategoryActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(AdminAddCategoryActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}