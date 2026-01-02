package com.example.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private List<Artist> mList;

    public ArtistAdapter(List<Artist> list) { this.mList = list; }

    @NonNull @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gọi file layout item_artist.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_hoang, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = mList.get(position);
        holder.tvName.setText(artist.getName());
        holder.imgAvt.setImageResource(artist.getImageResId());
    }

    @Override
    public int getItemCount() { return mList.size(); }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvt;
        TextView tvName;
        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo ID trong item_artist.xml là imgArtist và tvArtistName
            imgAvt = itemView.findViewById(R.id.imgArtist);
            tvName = itemView.findViewById(R.id.tvArtistName);
        }
    }
}