package com.example.music.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.adapter.LibraryPlaylistAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Playlist;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlaylistsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LibraryPlaylistAdapter adapter;
    private List<Playlist> playlistList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Setup giao diện
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212); // Nền đen
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Setup Adapter rỗng trước
        playlistList = new ArrayList<>();
        adapter = new LibraryPlaylistAdapter(getContext(), playlistList, playlist -> {
            // KHI CLICK VÀO PLAYLIST -> CHUYỂN SANG TRANG DETAIL
            Intent intent = new Intent(getContext(), PlaylistDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("object_playlist", playlist);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 3. Gọi API lấy dữ liệu thật
        fetchUserPlaylists();

        return recyclerView;
    }

    private void fetchUserPlaylists() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Sau này làm Login xong sẽ thay bằng ID người dùng thật
        Long fakeUserId = 1L;

        apiService.getUserPlaylists(fakeUserId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body()); // Cập nhật list
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.e("API_PLAYLIST", "Lỗi: " + t.getMessage());
            }
        });
    }

    // Load lại khi quay lại màn hình
    @Override
    public void onResume() {
        super.onResume();
        fetchUserPlaylists();
    }
}