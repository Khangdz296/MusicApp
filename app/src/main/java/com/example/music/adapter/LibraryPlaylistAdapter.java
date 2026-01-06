package com.example.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Playlist;

import java.util.List;

public class LibraryPlaylistAdapter extends RecyclerView.Adapter<LibraryPlaylistAdapter.ViewHolder> {

    private Context context;
    private List<Playlist> playlists;
    private OnItemClickListener listener;

    // Interface bắt sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Playlist playlist);
    }

    public LibraryPlaylistAdapter(Context context, List<Playlist> playlists, OnItemClickListener listener) {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dùng layout item_library_row_hoang.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_row_hoang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        if (playlist == null) return;

        // 1. Gán tên Playlist
        holder.tvTitle.setText(playlist.getName());

        // 2. Gán tên người tạo (Logic mới: Lấy từ User object)
        String ownerName = "Unknown";
        if (playlist.getUser() != null) {
            // Kiểm tra null để tránh lỗi crash
            if (playlist.getUser().getUsername() != null) {
                ownerName = playlist.getUser().getUsername();
            }
        }
        holder.tvSubtitle.setText("Playlist • " + ownerName);

        // 3. Load ảnh bìa
        Glide.with(context)
                .load(playlist.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ
                .error(R.drawable.ic_launcher_background)       // Ảnh lỗi
                .into(holder.imgThumb);

        // 4. Bắt sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(playlist);
        });
    }

    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    // ViewHolder ánh xạ view
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}