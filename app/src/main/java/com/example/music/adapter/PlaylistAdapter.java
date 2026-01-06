package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // 👇 Nhớ import Glide
import com.example.music.R;
import com.example.music.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlists;
    private Context context;
    private OnPlaylistClickListener listener;

    // Interface cho sự kiện click
    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    public PlaylistAdapter(Context context, List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Load layout item_playlist_grid.xml (Dạng ô vuông)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_grid, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        if (playlist == null) return;

        // 1. Set Tên Playlist
        holder.tvName.setText(playlist.getName());

        // 2. Set Tên Người tạo (Logic mới: Lấy từ User object)
        String ownerName = "Unknown";
        if (playlist.getUser() != null && playlist.getUser().getUsername() != null) {
            ownerName = playlist.getUser().getUsername();
        }
        holder.tvOwner.setText("Bởi: " + ownerName);

        // 3. Load ảnh bằng Glide
        Glide.with(context)
                .load(playlist.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ
                .error(R.drawable.ic_launcher_background)       // Ảnh lỗi
                .into(holder.imgCover);

        // 4. Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistClick(playlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvName, tvOwner;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo file item_playlist_grid.xml có các ID này
            imgCover = itemView.findViewById(R.id.imgPlaylistCover);
            tvName = itemView.findViewById(R.id.tvPlaylistName);
            tvOwner = itemView.findViewById(R.id.tvPlaylistOwner);
        }
    }
}