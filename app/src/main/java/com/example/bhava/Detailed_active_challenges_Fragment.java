package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.adapter.SessionAdapter;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.model.SessionItem;
import com.example.bhava.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detailed_active_challenges_Fragment extends Fragment {

    private String challengeId;
    private SessionAdapter sessionAdapter;
    private ChallengeItem challengeData;

    public Detailed_active_challenges_Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString("challengeId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_active_challenges_, container, false);

        // 🔹 Initialize Session RecyclerView
        RecyclerView rvSessions = view.findViewById(R.id.rvSessions);
        if (rvSessions != null) {
            sessionAdapter = new SessionAdapter(session -> openMusicPlayerFragment(
                    session.getTitle(),
                    session.getSubtitle(),
                    challengeData != null ? challengeData.getImage() : null,
                    challengeData != null ? challengeData.getJoinedCount() : "0",
                    session.getAudioUrl()
            ));
            rvSessions.setAdapter(sessionAdapter);
        }

        View btnPlay = view.findViewById(R.id.btnPlay);
        if (btnPlay != null) btnPlay.setOnClickListener(v -> {
            if (challengeData != null) {
                if (!challengeData.getSessions().isEmpty()) {
                    SessionItem firstSession = challengeData.getSessions().get(0);
                    openMusicPlayerFragment(firstSession.getTitle(), firstSession.getSubtitle(),
                            challengeData.getImage(), challengeData.getJoinedCount(), firstSession.getAudioUrl());
                } else {
                    openMusicPlayerFragment(challengeData.getTitle(), challengeData.getBadgeText(),
                            challengeData.getImage(), challengeData.getJoinedCount(), null);
                }
            } else {
                openMusicPlayerFragment("Music Player", null, null, "0", null);
                android.widget.Toast.makeText(getContext(), "Content is still loading. Player opened in preview mode.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 Back button
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // 🔹 Fetch Details from API
        if (challengeId != null && !challengeId.trim().isEmpty()) {
            fetchChallengeDetails(view);
        } else {
            Log.e("Detailed_Active", "Missing challengeId in arguments. Dynamic details cannot load.");
        }

        return view;
    }

    private void fetchChallengeDetails(View view) {
        if (getContext() == null) return;
        ApiClient.getService(getContext())
            .getChallengeById(challengeId)
            .enqueue(new Callback<com.example.bhava.model.SingleChallengeResponse>() {
                @Override
                public void onResponse(@NonNull Call<com.example.bhava.model.SingleChallengeResponse> call, @NonNull Response<com.example.bhava.model.SingleChallengeResponse> response) {
                    if (isAdded() && response.isSuccessful() && response.body() != null) {
                        challengeData = response.body().getData();
                        if (challengeData != null) {
                            bindData(view, challengeData);
                        } else {
                            Log.e("Detailed_Active", "Challenge payload is null for id=" + challengeId);
                        }
                    } else if (isAdded()) {
                        Log.e("Detailed_Active", "Failed detail fetch. code=" + response.code() + ", id=" + challengeId);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<com.example.bhava.model.SingleChallengeResponse> call, @NonNull Throwable t) {
                    if (isAdded()) Log.e("Detailed_Active", "Error fetching details", t);
                }
            });
    }

    private void bindData(View view, ChallengeItem challenge) {
        TextView tvTitle = view.findViewById(R.id.textTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvSessionSubtitle);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvSummary = view.findViewById(R.id.tvSummary);
        TextView tvGuideName = view.findViewById(R.id.tvGuideName);

        if (tvTitle != null) tvTitle.setText(challenge.getTitle());

        // 🔹 HIGHLIGHT: Dynamic Mentor / Guide Name binding
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

        if (challenge.getSessions() != null) {
            sessionAdapter.setSessions(challenge.getSessions());
        }

        android.widget.LinearLayout hostsContainer = view.findViewById(R.id.hostsContainer);
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