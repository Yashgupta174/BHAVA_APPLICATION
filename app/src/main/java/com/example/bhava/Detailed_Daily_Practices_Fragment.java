package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bhava.adapter.SessionAdapter;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.network.ApiClient;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detailed_Daily_Practices_Fragment extends Fragment {

    private String challengeId;
    private String title;
    private SessionAdapter sessionAdapter;
    private ChallengeItem challengeData;

    public Detailed_Daily_Practices_Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString("challengeId");
            title = getArguments().getString("title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed__daily__practices_, container, false);

        TextView tvTitle = view.findViewById(R.id.textDailyTitle);
        if (tvTitle != null && title != null) tvTitle.setText(title);

        RecyclerView rvSessions = view.findViewById(R.id.rvSessions);
        if (rvSessions != null) {
            sessionAdapter = new SessionAdapter(session -> {
                openMusicPlayerFragment(session.getTitle(), session.getSubtitle(),
                        challengeData != null ? challengeData.getImage() : null,
                        challengeData != null ? challengeData.getJoinedCount() : "0",
                        session.getAudioUrl());
            });
            rvSessions.setAdapter(sessionAdapter);
        }

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        View btnPlay = view.findViewById(R.id.btnPlay);
        if (btnPlay != null) btnPlay.setOnClickListener(v -> {
            if (challengeData != null) {
                if (challengeData.getSessions() != null && !challengeData.getSessions().isEmpty()) {
                    com.example.bhava.model.SessionItem firstSession = challengeData.getSessions().get(0);
                    openMusicPlayerFragment(firstSession.getTitle(), firstSession.getSubtitle(), 
                            challengeData.getImage(), challengeData.getJoinedCount(), firstSession.getAudioUrl());
                } else {
                    openMusicPlayerFragment(challengeData.getTitle(), challengeData.getBadgeText(), 
                            challengeData.getImage(), challengeData.getJoinedCount(), null);
                }
            } else {
                android.widget.Toast.makeText(getContext(), "Please wait for content to load...", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        if (challengeId != null) fetchDetails(view);

        return view;
    }

    private void fetchDetails(View view) {
        if (getContext() == null) return;
        ApiClient.getService(getContext()).getChallengeById(challengeId)
                .enqueue(new Callback<com.example.bhava.model.SingleChallengeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<com.example.bhava.model.SingleChallengeResponse> call, @NonNull Response<com.example.bhava.model.SingleChallengeResponse> response) {
                        if (isAdded() && response.isSuccessful() && response.body() != null) {
                            challengeData = response.body().getData();
                            if (challengeData != null) {
                                bindData(view, challengeData);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<com.example.bhava.model.SingleChallengeResponse> call, @NonNull Throwable t) {
                        if (isAdded()) Log.e("DailyPractices", "Error fetching details", t);
                    }
                });
    }

    private void bindData(View view, ChallengeItem challenge) {
        TextView tvTitle = view.findViewById(R.id.textDailyTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvSessionSubtitle);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvSummary = view.findViewById(R.id.tvSummary);
        TextView tvGuideName = view.findViewById(R.id.tvGuideName);
        ImageView ivHero = view.findViewById(R.id.ivHero);

        if (tvTitle != null) tvTitle.setText(challenge.getTitle());
        
        // Guide Name binding
        if (tvGuideName != null && !challenge.getHosts().isEmpty()) {
            tvGuideName.setText(challenge.getHosts().get(0).getName());
        }

        // Subtitle handling
        if (tvSubtitle != null) {
            String sub = challenge.getFullSubtitle();
            if (sub == null || sub.isEmpty()) sub = challenge.getBadgeText();
            tvSubtitle.setText(sub);
        }

        // Summary handling
        if (tvSummary != null) {
            String summary = challenge.getDescription();
            if (summary != null && !summary.isEmpty()) {
                tvSummary.setText(summary);
                tvSummary.setVisibility(View.VISIBLE);
            } else {
                tvSummary.setVisibility(View.GONE);
            }
        }

        // Full Content handling
        if (tvDescription != null) {
            String desc = challenge.getDetailsLongDescription();
            if (desc != null && !desc.isEmpty()) {
                tvDescription.setText(desc);
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setText(challenge.getDescription());
                tvDescription.setVisibility((challenge.getDescription() != null && !challenge.getDescription().isEmpty()) ? View.VISIBLE : View.GONE);
            }
        }
        
        if (ivHero != null && challenge.getImage() != null) Glide.with(this).load(challenge.getImage()).into(ivHero);

        if (sessionAdapter != null && challenge.getSessions() != null) {
            sessionAdapter.setSessions(challenge.getSessions());
        }

        LinearLayout hostsContainer = view.findViewById(R.id.hostsContainer);
        if (hostsContainer != null && challenge.getHosts() != null) {
            hostsContainer.removeAllViews();
            LayoutInflater inf = LayoutInflater.from(getContext());
            for (ChallengeItem.HostItem host : challenge.getHosts()) {
                View hostView = inf.inflate(R.layout.item_host_info, hostsContainer, false);
                TextView tvName = hostView.findViewById(R.id.tvHostName);
                TextView tvTitleHost = hostView.findViewById(R.id.tvHostTitle);
                TextView tvInitials = hostView.findViewById(R.id.tvHostInitials);
                if (tvName != null) tvName.setText(host.getName());
                if (tvTitleHost != null) tvTitleHost.setText(host.getTitle());
                if (tvInitials != null) tvInitials.setText(host.getInitials());
                hostsContainer.addView(hostView);
            }
        }
    }

    private void openMusicPlayerFragment(String trackTitle, String trackSubtitle, String imageUrl, String joinedCount, String audioUrl) {
        Music_Player_Fragment fragment = new Music_Player_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", trackTitle != null ? trackTitle : "Music Player");
        bundle.putString("subtitle", trackSubtitle);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("listeningCount", joinedCount);
        bundle.putString("audioUrl", audioUrl);
        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }
}