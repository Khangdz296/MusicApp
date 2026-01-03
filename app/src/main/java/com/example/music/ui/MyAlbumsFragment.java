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

import com.example.music.adapter.AlbumAdapter;
import com.example.music.model.Album;

import java.util.ArrayList;
import java.util.List;

public class MyAlbumsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setBackgroundColor(0xFF121212);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Mock Data Album
        List<Album> list = new ArrayList<>();
        list.add(new Album("1", "Chúng Ta Của Hiện Tại", "Sơn Tùng M-TP", "https://picsum.photos/200/200?random=4"));
        list.add(new Album("2", "Đánh Đổi", "Obito", "https://picsum.photos/200/200?random=5"));
        list.add(new Album("3", "99%", "MCK", "https://picsum.photos/200/200?random=6"));

        AlbumAdapter adapter = new AlbumAdapter(getContext(), list, album -> {
            // Click listener
        });
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}