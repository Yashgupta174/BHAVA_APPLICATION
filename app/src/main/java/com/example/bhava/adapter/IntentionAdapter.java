package com.example.bhava.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.R;
import com.example.bhava.model.IntentionItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IntentionAdapter extends RecyclerView.Adapter<IntentionAdapter.ViewHolder> {

    private List<IntentionItem> intentions = new ArrayList<>();
    private final OnIntentionClickListener listener;

    public interface OnIntentionClickListener {
        void onDeleteClick(IntentionItem intention);
    }

    public IntentionAdapter(OnIntentionClickListener listener) {
        this.listener = listener;
    }

    public void setIntentions(List<IntentionItem> intentions) {
        this.intentions = intentions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intention, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(intentions.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return intentions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvContent;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvIntentionDate);
            tvContent = itemView.findViewById(R.id.tvIntentionContent);
            btnDelete = itemView.findViewById(R.id.btnDeleteIntention);
        }

        void bind(IntentionItem item, OnIntentionClickListener listener) {
            tvContent.setText(item.getContent());
            tvDate.setText(formatDate(item.getCreatedAt()));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
        }

        private String formatDate(String isoDate) {
            if (isoDate == null) return "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                Date date = sdf.parse(isoDate);
                SimpleDateFormat outSdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                return outSdf.format(date);
            } catch (ParseException e) {
                return isoDate;
            }
        }
    }
}
