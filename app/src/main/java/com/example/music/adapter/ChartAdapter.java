package com.example.music.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.ui.ChartDetailActivity;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {

    @NonNull
    @Override
    public ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_card, parent, false);
        return new ChartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
        // Cấu hình dựa trên vị trí (position)
        if (position == 0) {
            // --- THẺ 1: MOST VIEWED (BÀI HÁT) ---
            holder.layoutBg.setBackgroundResource(R.drawable.bg_chart_vietnam);
            holder.tvName.setText("MOST VIEWED");
            holder.tvFooter.setText("BXH Nhiều Lượt Xem Nhất");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChartDetailActivity.class);
                intent.putExtra("CHART_TYPE", "SONGS"); // Gửi nhãn "SONGS"
                v.getContext().startActivity(intent);
            });

        } else {
            // --- THẺ 2: TOP ALBUMS (ALBUM) ---
            // Bạn có thể đổi màu nền khác nếu có (ví dụ bg_chart_global)
            holder.layoutBg.setBackgroundResource(R.drawable.bg_chart_vietnam);
            holder.tvName.setText("TOP ALBUMS");
            holder.tvFooter.setText("BXH Album Phổ Biến");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChartDetailActivity.class);
                intent.putExtra("CHART_TYPE", "ALBUMS"); // Gửi nhãn "ALBUMS"
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Hiển thị 2 thẻ (Bài hát và Album)
    }

    public static class ChartViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutBg;
        TextView tvName, tvFooter;

        public ChartViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBg = itemView.findViewById(R.id.layoutChartBg);
            tvName = itemView.findViewById(R.id.tvChartName);
            tvFooter = itemView.findViewById(R.id.tvFooter);
        }
    }
}