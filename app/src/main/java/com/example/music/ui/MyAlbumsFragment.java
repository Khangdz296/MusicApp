package com.example.music.ui;

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

import com.example.music.adapter.AlbumAdapter;
import com.example.music.api.ApiService;
import com.example.music.api.RetrofitClient;
import com.example.music.model.Album;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAlbumsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private List<Album> albumList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Setup UI
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212); // Nền đen
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Setup Adapter
        albumList = new ArrayList<>();
        adapter = new AlbumAdapter(getContext(), albumList, album -> {
            Toast.makeText(getContext(), "Album: " + album.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Mở chi tiết Album sau
        });
        recyclerView.setAdapter(adapter);

        // 3. Gọi API
        fetchAlbums();

        return recyclerView;
    }

    private void fetchAlbums() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getAllAlbums().enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    albumList.clear();
                    albumList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ALBUM", "Lỗi tải dữ liệu");
                }
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.e("API_ALBUM", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(albumList.isEmpty()) fetchAlbums();
    }
}