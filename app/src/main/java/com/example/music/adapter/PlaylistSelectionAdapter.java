package com.example.music.adapter;

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
import java.util.ArrayList;
import java.util.List;

public class PlaylistSelectionAdapter extends RecyclerView.Adapter<PlaylistSelectionAdapter.ViewHolder> {

    private List<Playlist> playlists = new ArrayList<>();
    private OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    public PlaylistSelectionAdapter(OnPlaylistClickListener listener) {
        this.listener = listener;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist_selection_dhuy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlaylistCover;
        TextView tvPlaylistName, tvSongCount;

        ViewHolder(View itemView) {
            super(itemView);
            imgPlaylistCover = itemView.findViewById(R.id.imgPlaylistCover);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvSongCount = itemView.findViewById(R.id.tvSongCount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlaylistClick(playlists.get(position));
                }
            });
        }

        void bind(Playlist playlist) {
            tvPlaylistName.setText(playlist.getName());
            tvSongCount.setText(playlist.getSongCount() + " bài hát");

            // Load ảnh playlist
            Glide.with(itemView.getContext())
                    .load(playlist.getCoverImage())
                    .placeholder(R.drawable.ic_playlist)
                    .into(imgPlaylistCover);
        }
    }
}