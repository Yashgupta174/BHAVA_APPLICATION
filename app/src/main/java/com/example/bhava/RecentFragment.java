package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bhava.adapter.ChallengeAdapter;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.model.ChallengesResponse;
import com.example.bhava.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentFragment extends Fragment {

    private RecyclerView rvRecentChallenges;
    private ChallengeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshRecent;
    private ProgressBar pbRecentLoading;
    private TextView tvRecentEmpty;
    private List<ChallengeItem> recentList = new ArrayList<>();

    public RecentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        rvRecentChallenges = view.findViewById(R.id.rvRecentChallenges);
        swipeRefreshRecent = view.findViewById(R.id.swipeRefreshRecent);
        pbRecentLoading = view.findViewById(R.id.pbRecentLoading);
        tvRecentEmpty = view.findViewById(R.id.tvRecentEmpty);

        setupRecyclerView();

        swipeRefreshRecent.setOnRefreshListener(this::fetchRecentActivity);

        fetchRecentActivity();

        return view;
    }

    private void setupRecyclerView() {
        rvRecentChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChallengeAdapter(challenge -> {
            // Open GenericDetailFragment for the clicked challenge
            GenericDetailFragment detailFragment = new GenericDetailFragment();
            Bundle args = new Bundle();
            args.putString("challengeId", challenge.getId());
            args.putString("title", challenge.getTitle());
            args.putString("subtitle", challenge.getFullSubtitle());
            args.putString("imageUrl", challenge.getImage());
            args.putString("listeningCount", String.valueOf(challenge.getJoinedCount()));
            detailFragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null).commit();
        });
        adapter.setChallenges(recentList);
        rvRecentChallenges.setAdapter(adapter);
    }

    private void fetchRecentActivity() {
        if (getContext() == null) return;
        
        if (!swipeRefreshRecent.isRefreshing()) {
            pbRecentLoading.setVisibility(View.VISIBLE);
        }
        tvRecentEmpty.setVisibility(View.GONE);

        ApiClient.getService(getContext()).getRecentActivity().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengesResponse> call, @NonNull Response<ChallengesResponse> response) {
                if (!isAdded()) return;
                pbRecentLoading.setVisibility(View.GONE);
                swipeRefreshRecent.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    recentList.clear();
                    if (response.body().getData() != null) {
                        recentList.addAll(response.body().getData());
                    }
                    adapter.setChallenges(recentList);

                    if (recentList.isEmpty()) {
                        tvRecentEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    String errorMsg = "Failed to load recent activity";
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            // If it's JSON with "message" field
                            if (errorBodyStr.contains("\"message\"")) {
                                // Simple extraction without a JSON parser for speed/safety
                                int start = errorBodyStr.indexOf("\"message\":\"") + 11;
                                int end = errorBodyStr.indexOf("\"", start);
                                if (start > 10 && end > start) {
                                    errorMsg = errorBodyStr.substring(start, end);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("RecentFragment", "Error parsing error body", e);
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("RecentFragment", "Error code: " + response.code() + ", Message: " + errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengesResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    pbRecentLoading.setVisibility(View.GONE);
                    swipeRefreshRecent.setRefreshing(false);
                    String errorMsg = "Network error: " + t.getMessage();
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("RecentFragment", "Failure", t);
                }
            }
        });
    }
}