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

public class PublicPlaylistAdapter extends RecyclerView.Adapter<PublicPlaylistAdapter.ViewHolder> {

    private List<Playlist> playlists = new ArrayList<>();
    private OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    public PublicPlaylistAdapter(OnPlaylistClickListener listener) {
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
                .inflate(R.layout.item_public_playlist_dhuy, parent, false);
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
        ImageView imgPlaylist;
        TextView tvPlaylistName, tvCreatorAndCount;

        ViewHolder(View itemView) {
            super(itemView);
            imgPlaylist = itemView.findViewById(R.id.imgPlaylist);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvCreatorAndCount = itemView.findViewById(R.id.tvCreatorAndCount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlaylistClick(playlists.get(position));
                }
            });
        }

        void bind(Playlist playlist) {
            tvPlaylistName.setText(playlist.getName());
            tvCreatorAndCount.setText(playlist.getCreatorAndSongCount());

            // Load ảnh
            Glide.with(itemView.getContext())
                    .load(playlist.getCoverImage())
                    .placeholder(R.drawable.ic_playlist)
                    .error(R.drawable.ic_playlist)
                    .into(imgPlaylist);
        }
    }
}