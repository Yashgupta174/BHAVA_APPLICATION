package com.example.bhava.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.R;
import com.example.bhava.model.InspirationModel;

import java.util.ArrayList;
import java.util.List;

public class InspirationAdapter extends RecyclerView.Adapter<InspirationAdapter.ViewHolder> {

    private List<InspirationModel> inspirations = new ArrayList<>();
    private final Context context;

    public InspirationAdapter(Context context) {
        this.context = context;
    }

    public void setInspirations(List<InspirationModel> inspirations) {
        this.inspirations = inspirations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inspiration_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InspirationModel model = inspirations.get(position);
        
        holder.tvSource.setText(model.getSource() != null ? model.getSource().toUpperCase() : "");
        holder.tvContent.setText(model.getContent() != null ? model.getContent() : "");
        
        if (model.getAuthor() != null && !model.getAuthor().trim().isEmpty()) {
            holder.tvAuthor.setText("— " + model.getAuthor());
            holder.tvAuthor.setVisibility(View.VISIBLE);
        } else {
            holder.tvAuthor.setVisibility(View.GONE);
        }

        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = "\"" + model.getContent() + "\"\n\n— " + 
                    (model.getAuthor() != null ? model.getAuthor() : model.getSource());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "Share Daily Inspiration"));
        });
    }

    @Override
    public int getItemCount() {
        return inspirations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSource, tvContent, tvAuthor;
        ImageView btnShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSource = itemView.findViewById(R.id.tvInspirationSource);
            tvContent = itemView.findViewById(R.id.tvInspirationContent);
            tvAuthor = itemView.findViewById(R.id.tvInspirationAuthor);
            btnShare = itemView.findViewById(R.id.btnShareInspiration);
        }
    }
}
