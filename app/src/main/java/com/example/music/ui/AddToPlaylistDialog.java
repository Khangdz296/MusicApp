package com.example.music.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music.R;
import com.example.music.adapter.PlaylistSelectionAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Playlist;
import com.example.music.utils.SharedPrefManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddToPlaylistDialog extends BottomSheetDialogFragment {

    private RecyclerView rvUserPlaylists;
    private LinearLayout btnCreateNewPlaylist;
    private PlaylistSelectionAdapter adapter;
    private Long songId;
    private ApiService apiService;

    public static AddToPlaylistDialog newInstance(Long songId) {
        AddToPlaylistDialog dialog = new AddToPlaylistDialog();
        Bundle args = new Bundle();
        args.putLong("songId", songId);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_playlist_dhuy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            songId = getArguments().getLong("songId");
        }

        rvUserPlaylists = view.findViewById(R.id.rvUserPlaylists);
        btnCreateNewPlaylist = view.findViewById(R.id.btnCreateNewPlaylist);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        setupRecyclerView();
        loadUserPlaylists();

        btnCreateNewPlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
    }

    private void setupRecyclerView() {
        adapter = new PlaylistSelectionAdapter(playlist -> {
            addSongToPlaylist(playlist.getId());
        });
        rvUserPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserPlaylists.setAdapter(adapter);
    }

    private void loadUserPlaylists() {
        Long userId = SharedPrefManager.getInstance(getContext()).getUserId();

        if (userId == -1) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        apiService.getUserPlaylists(userId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setPlaylists(response.body());
                } else {
                    Toast.makeText(getContext(), "Không thể tải playlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSongToPlaylist(Long playlistId) {
        apiService.addSongToPlaylist(playlistId, songId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã thêm vào playlist", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Lỗi thêm bài hát", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tạo playlist mới");

        final EditText input = new EditText(getContext());
        input.setHint("Tên playlist");
        input.setPadding(50, 30, 50, 30);
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                createPlaylist(playlistName);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập tên playlist", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void createPlaylist(String name) {
        Long userId = SharedPrefManager.getInstance(getContext()).getUserId();
        Playlist newPlaylist = new Playlist(name, userId);

        apiService.createPlaylist(newPlaylist).enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Đã tạo playlist", Toast.LENGTH_SHORT).show();
                    loadUserPlaylists(); // Reload list
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tạo playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}