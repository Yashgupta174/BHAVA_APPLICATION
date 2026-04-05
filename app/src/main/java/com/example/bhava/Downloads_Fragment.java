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

public class Downloads_Fragment extends Fragment {

    private RecyclerView rvDownloads;
    private ChallengeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshDownloads;
    private ProgressBar pbDownloadsLoading;
    private TextView tvDownloadsEmpty;
    private TextView txtCollectionCount, txtSessionCount;
    private List<ChallengeItem> downloadList = new ArrayList<>();

    public Downloads_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads_, container, false);

        rvDownloads = view.findViewById(R.id.rvDownloads);
        swipeRefreshDownloads = view.findViewById(R.id.swipeRefreshDownloads);
        pbDownloadsLoading = view.findViewById(R.id.pbDownloadsLoading);
        tvDownloadsEmpty = view.findViewById(R.id.tvDownloadsEmpty);
        txtCollectionCount = view.findViewById(R.id.txtCollectionCount);
        txtSessionCount = view.findViewById(R.id.txtSessionCount);

        setupRecyclerView();

        swipeRefreshDownloads.setOnRefreshListener(this::fetchDownloads);

        fetchDownloads();

        return view;
    }

    private void setupRecyclerView() {
        rvDownloads.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChallengeAdapter(challenge -> {
            openDetailFragment(challenge);
        });
        adapter.setChallenges(downloadList);
        rvDownloads.setAdapter(adapter);
    }

    private void openDetailFragment(ChallengeItem challenge) {
        GenericDetailFragment detailFragment = new GenericDetailFragment();
        Bundle args = new Bundle();
        args.putString("challengeId", challenge.getId());
        args.putString("title", challenge.getTitle());
        args.putString("subtitle", challenge.getFullSubtitle());
        args.putString("imageUrl", challenge.getImage());
        args.putString("listeningCount", challenge.getJoinedCount());
        detailFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchDownloads() {
        if (getContext() == null) return;

        if (!swipeRefreshDownloads.isRefreshing()) {
            pbDownloadsLoading.setVisibility(View.VISIBLE);
        }
        tvDownloadsEmpty.setVisibility(View.GONE);

        ApiClient.getService(getContext()).getDownloads().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengesResponse> call, @NonNull Response<ChallengesResponse> response) {
                if (!isAdded()) return;
                pbDownloadsLoading.setVisibility(View.GONE);
                swipeRefreshDownloads.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    downloadList.clear();
                    if (response.body().getData() != null) {
                        downloadList.addAll(response.body().getData());
                    }
                    adapter.setChallenges(downloadList);

                    txtCollectionCount.setText(downloadList.size() + " collections");
                    txtSessionCount.setText((downloadList.size() * 3) + " sessions"); // Dummy multiplier

                    if (downloadList.isEmpty()) {
                        tvDownloadsEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load downloads", Toast.LENGTH_SHORT).show();
                    Log.e("DownloadsFragment", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengesResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    pbDownloadsLoading.setVisibility(View.GONE);
                    swipeRefreshDownloads.setRefreshing(false);
                    Toast.makeText(getContext(), "Network error fetching downloads", Toast.LENGTH_SHORT).show();
                    Log.e("DownloadsFragment", "Failure", t);
                }
            }
        });
    }
}
