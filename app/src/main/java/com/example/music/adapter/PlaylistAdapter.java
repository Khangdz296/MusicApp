package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlists;
    private Context context;
    private OnPlaylistClickListener listener; // Interface để xử lý click

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
        // Load layout item_playlist_grid.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_grid, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        holder.tvName.setText(playlist.getName());
        holder.tvOwner.setText("Bởi: " + playlist.getOwnerName());

        // Load ảnh bằng Glide (nếu có URL), tạm thời set ảnh demo
        // Glide.with(context).load(playlist.getImageUrl()).into(holder.imgCover);
        holder.imgCover.setImageResource(R.drawable.ic_launcher_background);

        // Bắt sự kiện click vào cả cái ô playlist
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistClick(playlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvName, tvOwner;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgPlaylistCover);
            tvName = itemView.findViewById(R.id.tvPlaylistName);
            tvOwner = itemView.findViewById(R.id.tvPlaylistOwner);
        }
    }
}