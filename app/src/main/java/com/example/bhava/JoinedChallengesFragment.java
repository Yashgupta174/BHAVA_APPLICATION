package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class JoinedChallengesFragment extends Fragment {

    private RecyclerView rvJoinedChallenges;
    private ChallengeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshJoined;
    private ProgressBar pbJoinedLoading;
    private TextView tvJoinedEmpty;
    private TextView txtJoinedCount;
    private List<ChallengeItem> joinedList = new ArrayList<>();

    public JoinedChallengesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joined_challenges, container, false);

        rvJoinedChallenges = view.findViewById(R.id.rvJoinedChallenges);
        swipeRefreshJoined = view.findViewById(R.id.swipeRefreshJoined);
        pbJoinedLoading = view.findViewById(R.id.pbJoinedLoading);
        tvJoinedEmpty = view.findViewById(R.id.tvJoinedEmpty);
        txtJoinedCount = view.findViewById(R.id.txtJoinedCount);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        setupRecyclerView();

        swipeRefreshJoined.setOnRefreshListener(this::fetchJoinedChallenges);

        fetchJoinedChallenges();

        return view;
    }

    private void setupRecyclerView() {
        rvJoinedChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
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
        adapter.setChallenges(joinedList);
        rvJoinedChallenges.setAdapter(adapter);
    }

    private void fetchJoinedChallenges() {
        if (getContext() == null) return;

        if (!swipeRefreshJoined.isRefreshing()) {
            pbJoinedLoading.setVisibility(View.VISIBLE);
        }
        tvJoinedEmpty.setVisibility(View.GONE);

        ApiClient.getService(getContext()).getMyJoinedChallenges().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengesResponse> call, @NonNull Response<ChallengesResponse> response) {
                if (!isAdded()) return;
                pbJoinedLoading.setVisibility(View.GONE);
                swipeRefreshJoined.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    joinedList.clear();
                    if (response.body().getData() != null) {
                        joinedList.addAll(response.body().getData());
                    }
                    adapter.setChallenges(joinedList);

                    txtJoinedCount.setText(joinedList.size() + " challenges active");

                    if (joinedList.isEmpty()) {
                        tvJoinedEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load joined challenges", Toast.LENGTH_SHORT).show();
                    Log.e("JoinedChallenges", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengesResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    pbJoinedLoading.setVisibility(View.GONE);
                    swipeRefreshJoined.setRefreshing(false);
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("JoinedChallenges", "Failure", t);
                }
            }
        });
    }
}
