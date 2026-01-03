package com.example.music.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.adapter.LibraryPlaylistAdapter; // Dùng adapter riêng của bạn
import com.example.music.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class MyPlaylistsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tạo RecyclerView bằng code (đỡ phải tạo file XML riêng)
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212); // Nền đen

        // Setup LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo dữ liệu giả
        List<Playlist> list = new ArrayList<>();
        list.add(new Playlist("1", "Nhạc Code Dạo", "Hoàng", "https://picsum.photos/200/200?random=1"));
        list.add(new Playlist("2", "Chill Cuối Tuần", "Spotify", "https://picsum.photos/200/200?random=2"));
        list.add(new Playlist("3", "Tập Gym", "Gym Lord", "https://picsum.photos/200/200?random=3"));

        // Gọi Adapter
        LibraryPlaylistAdapter adapter = new LibraryPlaylistAdapter(getContext(), list, playlist -> {
            // Xử lý khi click vào playlist (để sau)
        });
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}