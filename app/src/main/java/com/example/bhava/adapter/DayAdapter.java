package com.example.bhava.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.R;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private final List<String> days;
    private final OnDayClickListener listener;
    private int selectedPosition = 0;

    public interface OnDayClickListener {
        void onDayClick(String day);
    }

    public DayAdapter(List<String> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    public void setSelectedDay(String day) {
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).equals(day)) {
                int oldPos = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_selector, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        String day = days.get(position);
        holder.tvDay.setText(day);

        if (position == selectedPosition) {
            holder.cardDay.setCardBackgroundColor(Color.parseColor("#E65100")); // Theme Deep Orange
            holder.tvDay.setTextColor(Color.WHITE);
        } else {
            holder.cardDay.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.tvDay.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            listener.onDayClick(day);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        CardView cardDay;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDayName);
            cardDay = itemView.findViewById(R.id.cardDay);
        }
    }
}
