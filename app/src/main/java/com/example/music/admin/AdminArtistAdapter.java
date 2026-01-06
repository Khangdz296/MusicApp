package com.example.music.admin;

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
import com.example.music.model.Artist; // Nhớ import Artist
import java.util.List;

public class AdminArtistAdapter extends RecyclerView.Adapter<AdminArtistAdapter.ArtistViewHolder> {

    private Context context;
    private List<Artist> list;
    private OnArtistActionListener listener;

    public interface OnArtistActionListener {
        void onEdit(Artist artist);
        void onDelete(Artist artist);
    }

    public AdminArtistAdapter(Context context, List<Artist> list, OnArtistActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Artist> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = list.get(position);
        if (artist == null) return;

        holder.tvName.setText(artist.getName());

        // Load ảnh tròn (Optional: dùng .circleCrop() của Glide nếu muốn)
        Glide.with(context)
                .load(artist.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgArtist);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(artist));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(artist));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgArtist, btnEdit, btnDelete;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvArtistName);
            imgArtist = itemView.findViewById(R.id.imgArtist);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}