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
        // CẤU HÌNH THẺ DUY NHẤT: TOP VIEWS

        // 1. Set màu nền
        holder.layoutBg.setBackgroundResource(R.drawable.bg_chart_vietnam);

        // 2. Set nội dung
        holder.tvName.setText("MOST VIEWED");
        holder.tvFooter.setText("BXH Nhiều Lượt Xem Nhất");

        // 👇 3. BẮT SỰ KIỆN CLICK (Đã sửa hoàn chỉnh)
        holder.itemView.setOnClickListener(v -> {
            // Tạo Intent chuyển sang màn hình Chi tiết BXH
            Intent intent = new Intent(v.getContext(), ChartDetailActivity.class);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return 1; // Chỉ hiện 1 thẻ
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