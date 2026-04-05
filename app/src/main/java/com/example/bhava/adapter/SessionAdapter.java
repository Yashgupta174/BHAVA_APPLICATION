package com.example.bhava.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bhava.R;
import com.example.bhava.model.SessionItem;
import java.util.ArrayList;
import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<SessionItem> sessions = new ArrayList<>();
    private OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onSessionClick(SessionItem session);
    }

    public SessionAdapter(OnSessionClickListener listener) {
        this.listener = listener;
    }

    public void setSessions(List<SessionItem> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SessionItem session = sessions.get(position);
        holder.bind(session, listener);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber, tvTitle, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tvSessionNumber); // Matches item_session.xml
            tvTitle = itemView.findViewById(R.id.tvSessionName);       // Matches item_session.xml
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }

        public void bind(SessionItem session, OnSessionClickListener listener) {
            if (session.getDay() != null) {
                tvDayNumber.setText(String.valueOf(session.getDay()));
            } else {
                tvDayNumber.setText("-");
            }
            
            tvTitle.setText(session.getTitle());
            tvDuration.setText(session.getDuration());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onSessionClick(session);
            });
        }
    }
}
