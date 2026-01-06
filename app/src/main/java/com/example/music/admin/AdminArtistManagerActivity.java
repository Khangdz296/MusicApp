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
import com.example.music.model.Artist;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminArtistManagerActivity extends AppCompatActivity {

    private RecyclerView rcv;
    private AdminArtistAdapter adapter;
    private Button btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_artist_manager);

        rcv = findViewById(R.id.rcvArtist);
        btnAddNew = findViewById(R.id.btnAddNew);

        // Setup Adapter
        adapter = new AdminArtistAdapter(this, new ArrayList<>(), new AdminArtistAdapter.OnArtistActionListener() {
            @Override
            public void onEdit(Artist artist) {
                Intent intent = new Intent(AdminArtistManagerActivity.this, AdminAddArtistActivity.class);
                intent.putExtra("DATA_ARTIST", artist); // Model Artist phải implements Serializable
                startActivity(intent);
            }

            @Override
            public void onDelete(Artist artist) {
                confirmDelete(artist);
            }
        });

        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(adapter);

        btnAddNew.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAddArtistActivity.class)));

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Load lại khi quay về từ màn hình thêm/sửa
    }

    private void loadData() {
        RetrofitClient.getClient().create(ApiService.class).getAllArtists().enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                if (response.isSuccessful()) {
                    adapter.updateData(response.body());
                }
            }
            @Override public void onFailure(Call<List<Artist>> call, Throwable t) {}
        });
    }

    private void confirmDelete(Artist artist) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Ca Sĩ")
                .setMessage("Bạn chắc muốn xóa '" + artist.getName() + "'?\n\nCẢNH BÁO: Các bài hát của ca sĩ này có thể bị lỗi.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    RetrofitClient.getClient().create(ApiService.class).deleteArtist(artist.getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(AdminArtistManagerActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                                        loadData();
                                    } else {
                                        Toast.makeText(AdminArtistManagerActivity.this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(AdminArtistManagerActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}