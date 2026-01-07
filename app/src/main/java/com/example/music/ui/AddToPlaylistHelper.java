package com.example.music.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences; // 👇 Nhớ import
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

    public AddToPlaylistHelper(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // Hàm chính để mở BottomSheet
    public void showAddToPlaylistDialog(Song songToAdd) {
        // 👇 1. KIỂM TRA ĐĂNG NHẬP NGAY TẠI ĐÂY
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        Long realUserId = prefs.getLong("user_id", -1L);

        if (realUserId == -1L) {
            // Nếu chưa đăng nhập -> Báo lỗi và thoát luôn
            Toast.makeText(context, "Vui lòng đăng nhập để thêm vào Playlist!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 👇 2. NẾU ĐÃ ĐĂNG NHẬP THÌ MỚI CHẠY TIẾP CODE DƯỚI
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_add_playlist_dhuy, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rvUserPlaylists = view.findViewById(R.id.rvUserPlaylists);
        LinearLayout btnCreateNewPlaylist = view.findViewById(R.id.btnCreateNewPlaylist);

        rvUserPlaylists.setLayoutManager(new LinearLayoutManager(context));

        // Adapter rỗng ban đầu
        List<Playlist> playlistList = new ArrayList<>();
        LibraryPlaylistAdapter adapter = new LibraryPlaylistAdapter(context, playlistList, selectedPlaylist -> {
            addSongToPlaylistApi(selectedPlaylist.getId(), songToAdd.getId(), bottomSheetDialog);
        });
        rvUserPlaylists.setAdapter(adapter);

        // Gọi API lấy danh sách playlist của User thật
        loadUserPlaylists(adapter, realUserId);

        // Xử lý nút "Tạo playlist mới"
        btnCreateNewPlaylist.setOnClickListener(v -> {
            showCreatePlaylistDialog(adapter, realUserId);
        });

        bottomSheetDialog.show();
    }

    // Các hàm phụ trợ (đã nhận userId thật)
    private void loadUserPlaylists(LibraryPlaylistAdapter adapter, Long userId) {
        apiService.getUserPlaylists(userId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                // Toast.makeText(context, "Lỗi tải playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSongToPlaylistApi(Long playlistId, Long songId, BottomSheetDialog dialog) {
        apiService.addSongToPlaylist(playlistId, songId).enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã thêm vào playlist thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Bài hát đã có trong playlist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreatePlaylistDialog(LibraryPlaylistAdapter adapter, Long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tạo Playlist Mới");

        final EditText input = new EditText(context);
        input.setHint("Nhập tên playlist...");
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String playlistName = input.getText().toString();
            if (!playlistName.isEmpty()) {
                createPlaylistApi(playlistName, adapter, userId);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createPlaylistApi(String name, LibraryPlaylistAdapter adapter, Long userId) {
        Playlist newPlaylist = new Playlist(name, "https://www.redchair.com.au/images/Productions/Playlist_landscape.png");

        apiService.createPlaylist(userId, newPlaylist).enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Tạo playlist thành công!", Toast.LENGTH_SHORT).show();
                    loadUserPlaylists(adapter, userId);
                }
            }
            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Toast.makeText(context, "Lỗi tạo playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}