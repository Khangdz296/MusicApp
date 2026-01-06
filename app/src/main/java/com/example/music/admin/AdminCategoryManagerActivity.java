package com.example.music.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music.R;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Category;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryManagerActivity extends AppCompatActivity {

    private RecyclerView rcv;
    private AdminCategoryAdapter adapter;
    private Button btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_manager);

        rcv = findViewById(R.id.rcvCategory);
        btnAddNew = findViewById(R.id.btnAddNew);

        // Setup Adapter
        adapter = new AdminCategoryAdapter(this, new ArrayList<>(), new AdminCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                // Chuyển sang màn hình sửa
                Intent intent = new Intent(AdminCategoryManagerActivity.this, AdminAddCategoryActivity.class);
                intent.putExtra("DATA_CATEGORY", category); // Model Category cần implements Serializable
                startActivity(intent);
            }

            @Override
            public void onDelete(Category category) {
                confirmDelete(category);
            }
        });

        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(adapter);

        // Nút thêm mới
        btnAddNew.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddCategoryActivity.class)));

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Load lại khi quay về
    }

    private void loadData() {
        RetrofitClient.getClient().create(ApiService.class).getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    adapter.updateData(response.body());
                }
            }
            @Override public void onFailure(Call<List<Category>> call, Throwable t) {}
        });
    }

    private void confirmDelete(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Thể Loại")
                .setMessage("Bạn chắc muốn xóa '" + category.getName() + "'?\n\nCẢNH BÁO: Các bài hát thuộc thể loại này có thể bị lỗi.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    RetrofitClient.getClient().create(ApiService.class).deleteCategory(category.getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(AdminCategoryManagerActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                                        loadData();
                                    } else {
                                        Toast.makeText(AdminCategoryManagerActivity.this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(AdminCategoryManagerActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}