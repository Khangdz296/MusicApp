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
import com.example.music.model.Category;
import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CatViewHolder> {

    private Context context;
    private List<Category> list;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public AdminCategoryAdapter(Context context, List<Category> list, OnCategoryActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Category> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category, parent, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Category cat = list.get(position);
        holder.tvName.setText(cat.getName());

        Glide.with(context)
                .load(cat.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCat);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(cat));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(cat));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class CatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgCat, btnEdit, btnDelete;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCatName);
            imgCat = itemView.findViewById(R.id.imgCat);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}