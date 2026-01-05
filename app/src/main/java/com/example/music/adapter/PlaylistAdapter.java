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

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private Context context;
    private List<Playlist> playlists;
    private OnPlaylistClickListener listener;

    // Interface để bắt sự kiện click
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
        // Sử dụng layout item_playlist_grid.xml bạn đã có
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_grid, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        holder.tvName.setText(playlist.getName());
        holder.tvOwner.setText("Bởi: " + playlist.getOwnerName());

        // Dùng Glide load ảnh (nếu có URL), tạm thời load ảnh demo
        Glide.with(context)
                .load(playlist.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background) // Nhớ đổi ảnh placeholder nếu muốn
                .into(holder.imgCover);

        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
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
            // Ánh xạ ID theo file item_playlist_grid.xml của bạn
            imgCover = itemView.findViewById(R.id.imgPlaylistCover);
            tvName = itemView.findViewById(R.id.tvPlaylistName);
            tvOwner = itemView.findViewById(R.id.tvPlaylistOwner);
        }
    }
}