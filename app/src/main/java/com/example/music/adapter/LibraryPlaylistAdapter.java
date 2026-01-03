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

// Đặt tên khác đi để không trùng với file của người kia
public class LibraryPlaylistAdapter extends RecyclerView.Adapter<LibraryPlaylistAdapter.ViewHolder> {

    private Context context;
    private List<Playlist> playlists;
    private OnItemClickListener listener;

    // Interface riêng cho Adapter này
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
        // 👇 Dùng đúng layout CỦA BẠN (Dạng danh sách)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_row_hoang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        if (playlist == null) return;

        // Gán dữ liệu
        holder.tvTitle.setText(playlist.getName());

        String owner = (playlist.getOwnerName() != null) ? playlist.getOwnerName() : "Unknown";
        holder.tvSubtitle.setText("Playlist • " + owner);

        // Load ảnh
        Glide.with(context)
                .load(playlist.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgThumb);

        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(playlist);
        });
    }

    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ đúng ID trong item_library_row_hoang.xml
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}