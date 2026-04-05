package com.example.bhava.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.bhava.R;
import com.example.bhava.model.ChallengeItem;
import java.util.ArrayList;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private List<ChallengeItem> challenges = new ArrayList<>();
    private OnChallengeClickListener listener;
    private int layoutResId = R.layout.item_challenge; // Default

    public interface OnChallengeClickListener {
        void onChallengeClick(ChallengeItem challenge);
    }

    public ChallengeAdapter(OnChallengeClickListener listener) {
        this.listener = listener;
    }

    public ChallengeAdapter(int layoutResId, OnChallengeClickListener listener) {
        this.layoutResId = layoutResId;
        this.listener = listener;
    }

    public void setChallenges(List<ChallengeItem> challenges) {
        this.challenges = challenges;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChallengeItem challenge = challenges.get(position);
        holder.bind(challenge, listener);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgChallenge;
        TextView tvBadge, tvTitle, tvSubtitle, tvJoined, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChallenge = itemView.findViewById(R.id.challenge_image);
            tvBadge = itemView.findViewById(R.id.badge_text);
            tvTitle = itemView.findViewById(R.id.challenge_title);
            tvSubtitle = itemView.findViewById(R.id.challenge_subtitle);
            tvJoined = itemView.findViewById(R.id.joined_badge);
            tvDuration = itemView.findViewById(R.id.duration_badge);
        }

        public void bind(ChallengeItem challenge, OnChallengeClickListener listener) {
            if (tvTitle != null) tvTitle.setText(challenge.getTitle());
            if (tvSubtitle != null) tvSubtitle.setText(challenge.getDescription());
            if (tvBadge != null) tvBadge.setText(challenge.getBadgeText());
            if (tvJoined != null) tvJoined.setText("✓ " + challenge.getJoinedCount());
            if (tvDuration != null) tvDuration.setText(challenge.getDurationText());

            Glide.with(itemView.getContext())
                    .load(challenge.getImage())
                    .placeholder(R.drawable.challenge_21days)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgChallenge);

            itemView.setOnClickListener(v -> listener.onChallengeClick(challenge));
        }
    }
}
