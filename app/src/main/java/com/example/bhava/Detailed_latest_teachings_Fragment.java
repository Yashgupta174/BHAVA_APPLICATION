package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bhava.network.ApiClient;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.adapter.SessionAdapter;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detailed_latest_teachings_Fragment extends Fragment {

    private String challengeId;
    private String title;
    private SessionAdapter sessionAdapter;
    private ChallengeItem challengeData;

    public Detailed_latest_teachings_Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString("challengeId");
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed__latest__teachings_, container, false);

        // ✅ Basic title
        TextView textView = view.findViewById(R.id.textLatestTitle);
        if (title != null && textView != null) textView.setText(title);

        // ✅ Session RecyclerView
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

        // ✅ Fetch full details from API
        if (challengeId != null) fetchChallengeDetails(view);

        // ✅ Begin Session button
        Button btnPlay = view.findViewById(R.id.btnPlay);
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

        // ✅ Back button
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

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
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<com.example.bhava.model.SingleChallengeResponse> call, @NonNull Throwable t) {
                        if (isAdded()) Log.e("LatestTeachings", "Error fetching details", t);
                    }
                });
    }

    private void bindData(View view, ChallengeItem data) {
        TextView tvLatestTitle = view.findViewById(R.id.textLatestTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvSessionSubtitle);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvSummary = view.findViewById(R.id.tvSummary);
        TextView tvGuideName = view.findViewById(R.id.tvGuideName);
        ImageView ivHero = view.findViewById(R.id.ivHero);

        if (tvLatestTitle != null) tvLatestTitle.setText(data.getTitle());

        // Guide Name binding
        if (tvGuideName != null && !data.getHosts().isEmpty()) {
            tvGuideName.setText(data.getHosts().get(0).getName());
        }

        // Subtitle handling
        if (tvSubtitle != null) {
            String sub = data.getFullSubtitle();
            if (sub == null || sub.isEmpty()) sub = data.getBadgeText();
            tvSubtitle.setText(sub);
        }

        // Summary handling
        if (tvSummary != null) {
            String summary = data.getDescription();
            if (summary != null && !summary.isEmpty()) {
                tvSummary.setText(summary);
                tvSummary.setVisibility(View.VISIBLE);
            } else {
                tvSummary.setVisibility(View.GONE);
            }
        }

        // Full Content handling
        if (tvDescription != null) {
            String desc = data.getDetailsLongDescription();
            if (desc != null && !desc.isEmpty()) {
                tvDescription.setText(desc);
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setText(data.getDescription());
                tvDescription.setVisibility((data.getDescription() != null && !data.getDescription().isEmpty()) ? View.VISIBLE : View.GONE);
            }
        }

        // Image
        if (ivHero != null) {
            Glide.with(this).load(data.getImage()).placeholder(R.drawable.hero_session_bg).into(ivHero);
        }

        // Meta (Optional depending on UI)
        TextView tvListening = view.findViewById(R.id.tvListeningCount);
        if (tvListening != null) tvListening.setText(data.getBadgeText());

        TextView tvDuration = view.findViewById(R.id.tvMediaDuration);
        if (tvDuration != null) tvDuration.setText(data.getDurationText());

        // Sessions
        if (sessionAdapter != null && data.getSessions() != null) {
            sessionAdapter.setSessions(data.getSessions());
        }

        // Hosts
        LinearLayout hostsContainer = view.findViewById(R.id.hostsContainer);
        if (hostsContainer != null && data.getHosts() != null) {
            hostsContainer.removeAllViews();
            LayoutInflater inf = LayoutInflater.from(getContext());
            for (ChallengeItem.HostItem host : data.getHosts()) {
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
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}