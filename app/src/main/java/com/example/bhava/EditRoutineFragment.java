package com.example.bhava;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.adapter.RoutinePickerAdapter;
import com.example.bhava.model.ApiResponse;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.model.ChallengesResponse;
import com.example.bhava.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditRoutineFragment extends Fragment implements RoutinePickerAdapter.OnRoutineClickListener {

    private RecyclerView rvAllChallenges;
    private RoutinePickerAdapter adapter;
    private ProgressBar pbLoading;
    private EditText etSearch;
    private List<ChallengeItem> allChallenges = new ArrayList<>();
    private List<ChallengeItem> filteredList = new ArrayList<>();
    private List<String> routineIds = new ArrayList<>();

    public EditRoutineFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_routine, container, false);

        rvAllChallenges = view.findViewById(R.id.rvAllChallenges);
        pbLoading = view.findViewById(R.id.pbLoading);
        etSearch = view.findViewById(R.id.etSearchRoutine);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        setupRecyclerView();
        setupSearch();

        fetchData();

        return view;
    }

    private void setupRecyclerView() {
        rvAllChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoutinePickerAdapter(this);
        rvAllChallenges.setAdapter(adapter);
    }

    private void setupSearch() {
        if (etSearch == null) return;
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(allChallenges);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (ChallengeItem item : allChallenges) {
                if (item.getTitle().toLowerCase().contains(lowerQuery) || 
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerQuery))) {
                    filteredList.add(item);
                }
            }
        }
        adapter.setData(filteredList, routineIds);
    }

    private void fetchData() {
        if (getContext() == null) return;
        pbLoading.setVisibility(View.VISIBLE);

        // 1. Fetch all challenges
        ApiClient.getService(getContext()).getChallenges().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(Call<ChallengesResponse> call, Response<ChallengesResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    allChallenges.clear();
                    if (response.body().getData() != null) {
                        allChallenges.addAll(response.body().getData());
                    }
                    // 2. Fetch current routines to mark already added ones
                    fetchRoutines();
                } else {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load challenges", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChallengesResponse> call, Throwable t) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchRoutines() {
        if (getContext() == null) return;
        ApiClient.getService(getContext()).getRoutines().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(Call<ChallengesResponse> call, Response<ChallengesResponse> response) {
                if (!isAdded()) return;
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    routineIds.clear();
                    if (response.body().getData() != null) {
                        for (ChallengeItem item : response.body().getData()) {
                            routineIds.add(item.getId());
                        }
                    }
                    filter(etSearch.getText().toString());
                }
            }

            @Override
            public void onFailure(Call<ChallengesResponse> call, Throwable t) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    filter(etSearch.getText().toString());
                }
            }
        });
    }

    @Override
    public void onToggleRoutine(ChallengeItem challenge, boolean isAdding) {
        if (getContext() == null || challenge == null) return;

        if (isAdding) {
            ApiClient.getService(getContext()).addToRoutine(challenge.getId()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        routineIds.add(challenge.getId());
                        adapter.setData(filteredList, routineIds);
                        Toast.makeText(getContext(), "Added to routine", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed up add", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ApiClient.getService(getContext()).removeFromRoutine(challenge.getId()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        routineIds.remove(challenge.getId());
                        adapter.setData(filteredList, routineIds);
                        Toast.makeText(getContext(), "Removed from routine", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
