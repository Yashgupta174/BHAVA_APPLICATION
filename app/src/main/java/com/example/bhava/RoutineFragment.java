package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.adapter.ChallengeAdapter;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.model.ChallengesResponse;
import com.example.bhava.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutineFragment extends Fragment {

    private RecyclerView rvRoutines;
    private ChallengeAdapter adapter;
    private ProgressBar pbLoading;
    private LinearLayout llEmptyState;
    private List<ChallengeItem> routines = new ArrayList<>();

    public RoutineFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        rvRoutines = view.findViewById(R.id.rvRoutines);
        pbLoading = view.findViewById(R.id.pbLoading);
        llEmptyState = view.findViewById(R.id.llEmptyState);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        Button btnAdd = view.findViewById(R.id.btnAddRoutine);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> openEditRoutine());
        }

        setupRecyclerView();
        fetchRoutines();

        return view;
    }

    private void setupRecyclerView() {
        rvRoutines.setLayoutManager(new LinearLayoutManager(getContext()));
        // Use a vertical list layout for the routine list
        adapter = new ChallengeAdapter(R.layout.item_routine_list, this::openDetail);
        rvRoutines.setAdapter(adapter);
    }

    private void fetchRoutines() {
        if (getContext() == null) return;
        pbLoading.setVisibility(View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);

        ApiClient.getService(getContext()).getRoutines().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(Call<ChallengesResponse> call, Response<ChallengesResponse> response) {
                if (!isAdded()) return;
                pbLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    routines.clear();
                    if (response.body().getData() != null) {
                        routines.addAll(response.body().getData());
                    }
                    
                    adapter.setChallenges(routines);
                    
                    if (routines.isEmpty()) {
                        llEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load routines", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChallengesResponse> call, Throwable t) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    Log.e("RoutineFragment", "Error fetching routines", t);
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openDetail(ChallengeItem challenge) {
        GenericDetailFragment detailFragment = new GenericDetailFragment();
        Bundle args = new Bundle();
        args.putString("challengeId", challenge.getId());
        args.putString("title", challenge.getTitle());
        args.putString("subtitle", challenge.getFullSubtitle());
        args.putString("imageUrl", challenge.getImage());
        args.putString("listeningCount", challenge.getJoinedCount());
        detailFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null).commit();
    }

    private void openEditRoutine() {
        EditRoutineFragment fragment = new EditRoutineFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }
}