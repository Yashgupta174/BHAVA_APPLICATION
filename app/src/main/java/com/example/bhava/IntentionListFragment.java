package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.adapter.IntentionAdapter;
import com.example.bhava.model.ApiResponse;
import com.example.bhava.model.IntentionItem;
import com.example.bhava.model.IntentionsResponse;
import com.example.bhava.network.ApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntentionListFragment extends Fragment implements IntentionAdapter.OnIntentionClickListener {

    private RecyclerView rvIntentions;
    private IntentionAdapter adapter;
    private ProgressBar pbLoading;
    private LinearLayout llEmptyState;
    private final List<IntentionItem> intentions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intention_list, container, false);

        rvIntentions = view.findViewById(R.id.rvIntentions);
        pbLoading = view.findViewById(R.id.pbLoading);
        llEmptyState = view.findViewById(R.id.llEmptyState);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddIntention);

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        fabAdd.setOnClickListener(v -> showAddIntentionDialog());

        setupRecyclerView();
        fetchIntentions();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new IntentionAdapter(this);
        rvIntentions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIntentions.setAdapter(adapter);
    }

    private void fetchIntentions() {
        if (getContext() == null) return;
        pbLoading.setVisibility(View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);

        ApiClient.getService(getContext()).getIntentions().enqueue(new Callback<IntentionsResponse>() {
            @Override
            public void onResponse(Call<IntentionsResponse> call, Response<IntentionsResponse> response) {
                if (!isAdded()) return;
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    intentions.clear();
                    if (response.body().getData() != null) {
                        intentions.addAll(response.body().getData());
                    }
                    adapter.setIntentions(intentions);
                    llEmptyState.setVisibility(intentions.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<IntentionsResponse> call, Throwable t) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    Log.e("IntentionList", "Error fetching", t);
                }
            }
        });
    }

    private void showAddIntentionDialog() {
        AddIntentionDialogFragment dialog = new AddIntentionDialogFragment();
        dialog.setTargetFragment(this, 1);
        dialog.show(requireActivity().getSupportFragmentManager(), "AddIntention");
    }

    public void onIntentionAdded() {
        fetchIntentions();
    }

    @Override
    public void onDeleteClick(IntentionItem intention) {
        if (getContext() == null) return;
        ApiClient.getService(getContext()).deleteIntention(intention.getId()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    fetchIntentions();
                } else {
                    Toast.makeText(getContext(), "Could not delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
