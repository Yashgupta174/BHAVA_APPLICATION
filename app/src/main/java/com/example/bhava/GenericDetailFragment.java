package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.bhava.adapter.SessionAdapter;
import com.example.bhava.model.ApiResponse;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.model.SessionItem;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.CacheManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * A single, efficient fragment that displays details for any category
 * (Morning Routine, Daily Practices, etc.) fetched dynamically from the admin side.
 */
public class GenericDetailFragment extends Fragment {

    private String challengeId;
    private String title;
    private String previewSubtitle;
    private String previewImageUrl;
    private String previewListeningCount;
    private String previewAudioUrl;
    private SessionAdapter sessionAdapter;
    private ChallengeItem challengeData;
    private CacheManager cacheManager;
    private View rootView;
    private ProgressBar pbSessionsLoading;

    public GenericDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString("challengeId");
            title = getArguments().getString("title");
            previewSubtitle = getArguments().getString("subtitle");
            previewImageUrl = getArguments().getString("imageUrl");
            previewListeningCount = getArguments().getString("listeningCount");
            previewAudioUrl = getArguments().getString("audioUrl");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic_detail, container, false);
        rootView = view;

        // Pre-set title if available from HomeFragment
        TextView tvTitle = view.findViewById(R.id.tvDetailMainTitle);
        if (tvTitle != null && title != null) tvTitle.setText(title);
        TextView tvSubtitle = view.findViewById(R.id.tvSessionSubtitle);
        if (tvSubtitle != null && previewSubtitle != null) tvSubtitle.setText(previewSubtitle);
        TextView tvListeningCount = view.findViewById(R.id.tvListeningCount);
        if (tvListeningCount != null && previewListeningCount != null) {
            tvListeningCount.setText(previewListeningCount + " Listening Now");
        }
        ImageView ivHero = view.findViewById(R.id.ivHero);
        if (ivHero != null && previewImageUrl != null && !previewImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(previewImageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivHero);
        }

        // Initialize Session RecyclerView
        RecyclerView rvSessions = view.findViewById(R.id.rvSessions);
        if (rvSessions != null) {
            sessionAdapter = new SessionAdapter(session -> {
                int selectedIndex = getSessionIndex(session);
                openMusicPlayerFragment(challengeData, session, selectedIndex);
            });
            rvSessions.setAdapter(sessionAdapter);
        }
        pbSessionsLoading = view.findViewById(R.id.pbSessionsLoading);

        // Back button
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Begin Session button: always route to player, with best available data
        View btnPlay = view.findViewById(R.id.btnPlay);
        if (btnPlay != null) {
            btnPlay.setOnClickListener(v -> {
                if (challengeData != null) {
                    SessionItem firstSession = challengeData.getSessions().isEmpty() ? null : challengeData.getSessions().get(0);
                    openMusicPlayerFragment(challengeData, firstSession, 0);
                } else {
                    openMusicPlayerFragment(null, null, 0);
                    Toast.makeText(getContext(), "Content is still loading. Player opened in preview mode.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        cacheManager = new CacheManager(requireContext());
        if (challengeId != null && !challengeId.trim().isEmpty()) {
            loadFromCache(view);
            fetchDetails(view);
            trackRecentView();
        } else {
            Log.e("GenericDetail", "Missing challengeId in fragment arguments. Dynamic details cannot load.");
            bindFallbackPreviewData(view);
            Toast.makeText(getContext(), "Could not load dynamic details for this challenge.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void trackRecentView() {
        if (challengeId == null || getContext() == null) return;
        ApiClient.getService(getContext()).addToRecent(challengeId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("GenericDetail", "Tracked recent view for: " + challengeId);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.w("GenericDetail", "Failed to track recent view", t);
            }
        });
    }

    private void loadFromCache(View view) {
        ChallengeItem cached = cacheManager.get("challenge_" + challengeId, ChallengeItem.class);
        if (cached != null) {
            Log.d("GenericDetail", "Loaded details from cache for " + challengeId);
            challengeData = cached;
            bindData(view, challengeData);
        }
    }

    private void fetchDetails(View view) {
        if (getContext() == null) return;
        if (pbSessionsLoading != null) pbSessionsLoading.setVisibility(View.VISIBLE);

        ApiClient.getService(getContext()).getChallengeById(challengeId)
                .enqueue(new Callback<com.example.bhava.model.SingleChallengeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<com.example.bhava.model.SingleChallengeResponse> call, @NonNull Response<com.example.bhava.model.SingleChallengeResponse> response) {
                        if (!isAdded()) return;
                        if (pbSessionsLoading != null) pbSessionsLoading.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            challengeData = response.body().getData();
                            if (challengeData != null) {
                                cacheManager.save("challenge_" + challengeId, challengeData);
                                bindData(view, challengeData);
                            } else {
                                Log.e("GenericDetail", "Challenge details payload is null for id=" + challengeId);
                            }
                        } else {
                            Log.e("GenericDetail", "Failed detail fetch. code=" + response.code() + ", id=" + challengeId);
                            if (challengeData == null) bindFallbackPreviewData(view);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<com.example.bhava.model.SingleChallengeResponse> call, @NonNull Throwable t) {
                        if (isAdded()) {
                            if (pbSessionsLoading != null) pbSessionsLoading.setVisibility(View.GONE);
                            Log.e("GenericDetail", "Error fetching details for " + challengeId, t);
                            if (challengeData == null) bindFallbackPreviewData(view);
                        }
                    }
                });
    }

    private void bindData(View view, ChallengeItem challenge) {
        TextView tvTitle = view.findViewById(R.id.tvDetailMainTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvSessionSubtitle);
        TextView tvSummary = view.findViewById(R.id.tvSummary);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvMediaDuration = view.findViewById(R.id.tvMediaDuration);
        TextView tvListeningCount = view.findViewById(R.id.tvListeningCount);
        ImageView ivHero = view.findViewById(R.id.ivHero);

        if (tvTitle != null) tvTitle.setText(challenge.getTitle());

        // Dynamic Subtitle handling
        if (tvSubtitle != null) {
            String sub = challenge.getFullSubtitle();
            if (sub == null || sub.isEmpty()) sub = challenge.getBadgeText();
            if (sub == null || sub.isEmpty()) sub = previewSubtitle;
            tvSubtitle.setText(sub);
        }

        // 🔹 HIGHLIGHT: Dynamic Mentor / Guide Name binding
        TextView tvGuideName = view.findViewById(R.id.tvGuideName);
        if (tvGuideName != null && !challenge.getHosts().isEmpty()) {
            tvGuideName.setText(challenge.getHosts().get(0).getName());
            Log.d("GenericDetail", "Mentor assigned: " + challenge.getHosts().get(0).getName());
        } else if (tvGuideName != null) {
            tvGuideName.setText("Bhava Guide");
        }

        // Quick Summary handling
        if (tvSummary != null) {
            String summary = challenge.getDescription();
            if (summary != null && !summary.isEmpty()) {
                tvSummary.setText(summary);
                tvSummary.setVisibility(View.VISIBLE);
            } else {
                tvSummary.setVisibility(View.GONE);
            }
        }

        // Full Detailed Description handling
        if (tvDescription != null) {
            String desc = challenge.getDetailsLongDescription();
            if (desc != null && !desc.isEmpty()) {
                tvDescription.setText(desc);
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                // Fallback to summary if description is empty, but ensure it's not hidden
                tvDescription.setText(challenge.getDescription());
                tvDescription.setVisibility((challenge.getDescription() != null && !challenge.getDescription().isEmpty()) ? View.VISIBLE : View.GONE);
            }
        }

        if (tvListeningCount != null && challenge.getJoinedCount() != null) {
            tvListeningCount.setText(challenge.getJoinedCount() + " Listening Now");
        } else if (tvListeningCount != null && previewListeningCount != null) {
            tvListeningCount.setText(previewListeningCount + " Listening Now");
        }

        if (tvMediaDuration != null) {
            String duration = challenge.getDurationText();
            if ((duration == null || duration.isEmpty()) && !challenge.getSessions().isEmpty()) {
                duration = challenge.getSessions().get(0).getDuration();
            }
            tvMediaDuration.setText((duration == null || duration.isEmpty()) ? "-- min" : duration);
        }

        // Load Hero Image
        if (ivHero != null && challenge.getImage() != null) {
            Glide.with(this)
                    .load(challenge.getImage())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivHero);
        } else if (ivHero != null && previewImageUrl != null && !previewImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(previewImageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivHero);
        }

        // Sessions list
        if (sessionAdapter != null && challenge.getSessions() != null) {
            sessionAdapter.setSessions(challenge.getSessions());
        }

        // 🔹 Join Challenge Button logic
        View btnJoin = view.findViewById(R.id.btnJoinChallenge);
        if (btnJoin != null) {
            if ("Active Challenges".equalsIgnoreCase(challenge.getCategory())) {
                btnJoin.setVisibility(View.VISIBLE);
                btnJoin.setOnClickListener(v -> joinActiveChallenge());
            } else {
                btnJoin.setVisibility(View.GONE);
            }
        }

        // Mentors/Hosts Section
        LinearLayout hostsContainer = view.findViewById(R.id.hostsContainer);
        View featuringLabel = view.findViewById(R.id.tvFeaturingLabel);

        if (hostsContainer != null) {
            if (challenge.getHosts().isEmpty()) {
                if (featuringLabel != null) featuringLabel.setVisibility(View.GONE);
                hostsContainer.setVisibility(View.GONE);
            } else {
                if (featuringLabel != null) featuringLabel.setVisibility(View.VISIBLE);
                hostsContainer.setVisibility(View.VISIBLE);
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
    }

    private void joinActiveChallenge() {
        if (challengeId == null || getContext() == null) return;

        ApiClient.getService(getContext()).joinChallenge(challengeId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "🎉 Successfully joined the challenge!", Toast.LENGTH_LONG).show();
                        // Update UI - maybe change button text or hide it
                        View btnJoin = rootView.findViewById(R.id.btnJoinChallenge);
                        if (btnJoin instanceof TextView) {
                            ((TextView) btnJoin).setText("Joined");
                            btnJoin.setEnabled(false);
                            btnJoin.setAlpha(0.6f);
                        }
                    } else {
                        Toast.makeText(getContext(), "You have already joined this challenge.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindFallbackPreviewData(View view) {
        TextView tvTitle = view.findViewById(R.id.tvDetailMainTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvSessionSubtitle);
        TextView tvListeningCount = view.findViewById(R.id.tvListeningCount);
        if (tvTitle != null && title != null) tvTitle.setText(title);
        if (tvSubtitle != null && previewSubtitle != null) tvSubtitle.setText(previewSubtitle);
        if (tvListeningCount != null && previewListeningCount != null) {
            tvListeningCount.setText(previewListeningCount + " Listening Now");
        }
    }

    private int getSessionIndex(SessionItem selected) {
        if (challengeData == null || selected == null || challengeData.getSessions().isEmpty()) return 0;
        List<SessionItem> sessions = challengeData.getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            SessionItem item = sessions.get(i);
            if (item == selected) return i;
            if (item.getId() != null && item.getId().equals(selected.getId())) return i;
        }
        return 0;
    }

    private void openMusicPlayerFragment(ChallengeItem challenge, SessionItem selectedSession, int selectedIndex) {
        List<SessionItem> queue = challenge != null ? challenge.getSessions() : new ArrayList<>();
        Music_Player_Fragment fragment = new Music_Player_Fragment();
        Bundle bundle = new Bundle();

        String fallbackTitle = title != null ? title : "Music Player";
        String trackTitle = selectedSession != null && selectedSession.getTitle() != null
                ? selectedSession.getTitle()
                : (challenge != null && challenge.getTitle() != null ? challenge.getTitle() : fallbackTitle);
        String trackSubtitle = selectedSession != null && selectedSession.getSubtitle() != null
                ? selectedSession.getSubtitle()
                : (challenge != null ? challenge.getFullSubtitle() : previewSubtitle);
        String audioUrl = selectedSession != null ? selectedSession.getAudioUrl() : previewAudioUrl;

        bundle.putString("challengeId", challenge != null ? challenge.getId() : (challengeId != null ? challengeId : ""));
        bundle.putString("title", trackTitle);
        bundle.putString("subtitle", trackSubtitle);
        bundle.putString("imageUrl", challenge != null ? challenge.getImage() : previewImageUrl);
        bundle.putString("listeningCount", challenge != null ? challenge.getJoinedCount() : previewListeningCount);
        bundle.putString("audioUrl", audioUrl);

        ArrayList<String> queueTitles = new ArrayList<>();
        ArrayList<String> queueSubtitles = new ArrayList<>();
        ArrayList<String> queueAudioUrls = new ArrayList<>();
        ArrayList<String> queueDurations = new ArrayList<>();

        for (SessionItem item : queue) {
            queueTitles.add(item.getTitle() != null ? item.getTitle() : "Session");
            queueSubtitles.add(item.getSubtitle());
            queueAudioUrls.add(item.getAudioUrl());
            queueDurations.add(item.getDuration());
        }

        if (queueTitles.isEmpty()) {
            queueTitles.add(trackTitle);
            queueSubtitles.add(trackSubtitle);
            queueAudioUrls.add(audioUrl);
            queueDurations.add(challenge != null ? challenge.getDurationText() : null);
            selectedIndex = 0;
        }

        bundle.putStringArrayList("queueTitles", queueTitles);
        bundle.putStringArrayList("queueSubtitles", queueSubtitles);
        bundle.putStringArrayList("queueAudioUrls", queueAudioUrls);
        bundle.putStringArrayList("queueDurations", queueDurations);
        bundle.putInt("selectedIndex", Math.max(0, Math.min(selectedIndex, queueTitles.size() - 1)));

        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }
}
