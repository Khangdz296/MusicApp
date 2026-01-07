package com.example.music.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.adapter.LibraryPlaylistAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Playlist;
import com.example.music.model.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddToPlaylistHelper {

    private Context context;
    private ApiService apiService;
    private Long currentUserId = 1L; // Fake ID như bạn đang dùng

    public AddToPlaylistHelper(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // Hàm chính để mở BottomSheet
    public void showAddToPlaylistDialog(Song songToAdd) {
        // 1. Khởi tạo BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme); // Cần define theme hoặc bỏ null nếu mặc định
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_add_playlist_dhuy, null);
        bottomSheetDialog.setContentView(view);

        // 2. Ánh xạ View trong BottomSheet
        RecyclerView rvUserPlaylists = view.findViewById(R.id.rvUserPlaylists);
        LinearLayout btnCreateNewPlaylist = view.findViewById(R.id.btnCreateNewPlaylist);

        rvUserPlaylists.setLayoutManager(new LinearLayoutManager(context));

        // Adapter rỗng ban đầu
        List<Playlist> playlistList = new ArrayList<>();
        LibraryPlaylistAdapter adapter = new LibraryPlaylistAdapter(context, playlistList, selectedPlaylist -> {
            // == LOGIC KHI CHỌN PLAYLIST ĐỂ THÊM NHẠC ==
            addSongToPlaylistApi(selectedPlaylist.getId(), songToAdd.getId(), bottomSheetDialog);
        });
        rvUserPlaylists.setAdapter(adapter);

        // 3. Gọi API lấy danh sách Playlist của User
        loadUserPlaylists(adapter);

        // 4. Xử lý nút "Tạo playlist mới"
        btnCreateNewPlaylist.setOnClickListener(v -> {
            showCreatePlaylistDialog(adapter);
        });

        bottomSheetDialog.show();
    }

    // API: Lấy danh sách playlist
    private void loadUserPlaylists(LibraryPlaylistAdapter adapter) {
        apiService.getUserPlaylists(currentUserId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Toast.makeText(context, "Lỗi tải playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // API: Thêm bài hát vào playlist đã chọn
    private void addSongToPlaylistApi(Long playlistId, Long songId, BottomSheetDialog dialog) {
        apiService.addSongToPlaylist(playlistId, songId).enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã thêm vào playlist thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Đóng BottomSheet
                } else {
                    Toast.makeText(context, "Thêm thất bại (Có thể bài hát đã tồn tại)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Logic: Hiện Dialog nhập tên playlist mới
    private void showCreatePlaylistDialog(LibraryPlaylistAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tạo Playlist Mới");

        final EditText input = new EditText(context);
        input.setHint("Nhập tên playlist...");
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String playlistName = input.getText().toString();
            if (!playlistName.isEmpty()) {
                createPlaylistApi(playlistName, adapter);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // API: Tạo playlist mới
    private void createPlaylistApi(String name, LibraryPlaylistAdapter adapter) {
        // Tạo object Playlist mới (Image để null hoặc link default)
        Playlist newPlaylist = new Playlist(name, "https://link_anh_mac_dinh.com/image.png");

        apiService.createPlaylist(currentUserId, newPlaylist).enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Tạo playlist thành công!", Toast.LENGTH_SHORT).show();
                    // Load lại danh sách playlist trong BottomSheet
                    loadUserPlaylists(adapter);
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Toast.makeText(context, "Lỗi tạo playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}