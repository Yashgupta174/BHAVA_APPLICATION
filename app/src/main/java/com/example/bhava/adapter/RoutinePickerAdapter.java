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

public class RoutinePickerAdapter extends RecyclerView.Adapter<RoutinePickerAdapter.ViewHolder> {

    private List<ChallengeItem> challenges = new ArrayList<>();
    private List<String> routineIds = new ArrayList<>();
    private OnRoutineClickListener listener;

    public interface OnRoutineClickListener {
        void onToggleRoutine(ChallengeItem challenge, boolean isAdding);
    }

    public RoutinePickerAdapter(OnRoutineClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<ChallengeItem> challenges, List<String> routineIds) {
        this.challenges = challenges;
        this.routineIds = routineIds != null ? routineIds : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine_picker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChallengeItem challenge = challenges.get(position);
        boolean isInRoutine = routineIds.contains(challenge.getId());
        holder.bind(challenge, isInRoutine, listener);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgChallenge;
        TextView tvTitle, tvSubtitle, btnToggle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChallenge = itemView.findViewById(R.id.ivRoutineImage);
            tvTitle = itemView.findViewById(R.id.tvRoutineTitle);
            tvSubtitle = itemView.findViewById(R.id.tvRoutineSubtitle);
            btnToggle = itemView.findViewById(R.id.btnToggleRoutine);
        }

        public void bind(ChallengeItem challenge, boolean isInRoutine, OnRoutineClickListener listener) {
            tvTitle.setText(challenge.getTitle());
            tvSubtitle.setText(challenge.getDescription());

            if (isInRoutine) {
                btnToggle.setText("Added");
                btnToggle.setBackgroundResource(R.drawable.bg_routine_added);
                btnToggle.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else {
                btnToggle.setText("Add");
                btnToggle.setBackgroundResource(R.drawable.bg_routine_add);
                btnToggle.setTextColor(itemView.getContext().getResources().getColor(R.color.text_espresso));
            }

            Glide.with(itemView.getContext())
                    .load(challenge.getImage())
                    .placeholder(R.drawable.challenge_21days)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgChallenge);

            btnToggle.setOnClickListener(v -> listener.onToggleRoutine(challenge, !isInRoutine));
            itemView.setOnClickListener(v -> listener.onToggleRoutine(challenge, !isInRoutine));
        }
    }
}
