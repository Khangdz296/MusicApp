package com.example.music.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music.R;
import com.example.music.model.Category;
import java.util.List;

public class CategoryAdapterK extends RecyclerView.Adapter<CategoryAdapterK.CategoryViewHolder> {

    private List<Category> list;
    private IClickCategoryListener listener;

    // Interface để callback khi click vào item
    public interface IClickCategoryListener {
        void onClick(Category category, int color);
    }

    // Mảng màu giả lập
    private final String[] colors = {
            "#E91E63", "#8C67AC", "#E64E1E", "#27856A",
            "#1E3264", "#283EA3", "#608108", "#D84000"
    };

    public CategoryAdapterK(List<Category> list, IClickCategoryListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Category> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card_khang, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = list.get(position);
        if (category == null) return;

        holder.tvName.setText(category.getName());

        // Random màu
        String colorHex = colors[position % colors.length];
        int colorInt = Color.parseColor(colorHex);
        holder.cardView.setCardBackgroundColor(colorInt);

        holder.itemView.setOnClickListener(v -> {
            listener.onClick(category, colorInt);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CardView cardView;
        ImageView ivThumb;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            cardView = (CardView) itemView; // Root layout là CardView
            ivThumb = itemView.findViewById(R.id.ivCategoryThumb);
        }
    }
}