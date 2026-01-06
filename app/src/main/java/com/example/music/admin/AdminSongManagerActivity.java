//package com.example.music.admin;
//
//import android.os.Bundle;
//import android.widget.Toast;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.music.R;
//import com.example.music.admin.AdminSongAdapter; // Adapter mới có nút xóa
//import com.example.music.api.ApiService;
//import com.example.music.api.RetrofitClient;
//import com.example.music.model.Song;
//
//import java.util.ArrayList;
//import java.util.List;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class AdminSongManagerActivity extends AppCompatActivity {
//
//    private RecyclerView rcvAdminSongs;
//    private AdminSongAdapter adapter;
//    private ApiService apiService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_song_manager); // Layout chứa 1 RecyclerView
//
//        rcvAdminSongs = findViewById(R.id.rcvAdminSongs);
//        rcvAdminSongs.setLayoutManager(new LinearLayoutManager(this));
//        apiService = RetrofitClient.getClient().create(ApiService.class);
//
//        adapter = new AdminSongAdapter(this, new ArrayList<>(), song -> {
//            // Khi bấm nút xóa -> Hiện dialog xác nhận
//            confirmDelete(song);
//        });
//        rcvAdminSongs.setAdapter(adapter);
//
//        loadAllSongs();
//    }
//
////    private void loadAllSongs() {
////        apiService.getAllSongsAdmin().enqueue(new Callback<List<Song>>() {
////            @Override
////            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
////                if (response.isSuccessful() && response.body() != null) {
////                    adapter.updateData(response.body());
////                }
////            }
////            @Override
////            public void onFailure(Call<List<Song>> call, Throwable t) {}
////        });
////    }
//
//    private void confirmDelete(Song song) {
//        new AlertDialog.Builder(this)
//                .setTitle("Xác nhận xóa")
//                .setMessage("Bạn có chắc muốn xóa bài: " + song.getTitle() + "?")
//                .setPositiveButton("Xóa", (dialog, which) -> deleteSong(song.getId()))
//                .setNegativeButton("Hủy", null)
//                .show();
//    }
//
////    private void deleteSong(Long id) {
////        apiService.deleteSong(id).enqueue(new Callback<Void>() {
////            @Override
////            public void onResponse(Call<Void> call, Response<Void> response) {
////                if (response.isSuccessful()) {
////                    Toast.makeText(AdminSongManagerActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
////                    loadAllSongs(); // Load lại danh sách
////                } else {
////                    Toast.makeText(AdminSongManagerActivity.this, "Không xóa được!", Toast.LENGTH_SHORT).show();
////                }
////            }
////            @Override
////            public void onFailure(Call<Void> call, Throwable t) {
////                Toast.makeText(AdminSongManagerActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
//}