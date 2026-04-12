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

public class Favourite_Fragment extends Fragment {

    private RecyclerView rvFavorites;
    private ChallengeAdapter adapter;
    private List<ChallengeItem> favoriteList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshFav;
    private ProgressBar pbFavLoading;
    private View tvFavEmpty;
    private TextView txtSessionCount;

    public Favourite_Fragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_, container, false);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        swipeRefreshFav = view.findViewById(R.id.swipeRefreshFav);
        pbFavLoading = view.findViewById(R.id.pbFavLoading);
        tvFavEmpty = view.findViewById(R.id.tvFavEmpty);
        txtSessionCount = view.findViewById(R.id.txtSessionCount);

        rvFavorites.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
        adapter = new ChallengeAdapter(R.layout.item_challenge_grid, challenge -> {
            openDetailFragment(challenge);
        });
        adapter.setChallenges(favoriteList);
        rvFavorites.setAdapter(adapter);

        swipeRefreshFav.setOnRefreshListener(this::fetchFavorites);
        swipeRefreshFav.setColorSchemeResources(R.color.bhava_primary);

        // Back button
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        fetchFavorites();

        return view;
    }

    private void fetchFavorites() {
        if (getContext() == null) return;

        pbFavLoading.setVisibility(View.VISIBLE);
        tvFavEmpty.setVisibility(View.GONE);

        ApiClient.getService(getContext()).getFavorites().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengesResponse> call, @NonNull Response<ChallengesResponse> response) {
                if (isAdded()) {
                    pbFavLoading.setVisibility(View.GONE);
                    swipeRefreshFav.setRefreshing(false);

                    if (response.isSuccessful() && response.body() != null) {
                        favoriteList.clear();
                        if (response.body().getData() != null) {
                            favoriteList.addAll(response.body().getData());
                        }
                        adapter.setChallenges(favoriteList);

                        if (txtSessionCount != null) {
                            txtSessionCount.setText(favoriteList.size() + " items");
                        }

                        if (favoriteList.isEmpty()) {
                            tvFavEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengesResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    pbFavLoading.setVisibility(View.GONE);
                    swipeRefreshFav.setRefreshing(false);
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("FavouriteFragment", "Error", t);
                }
            }
        });
    }

    private void openDetailFragment(ChallengeItem challenge) {
        GenericDetailFragment fragment = new GenericDetailFragment();
        Bundle args = new Bundle();
        args.putString("challengeId", challenge.getId());
        args.putString("title", challenge.getTitle());
        args.putString("subtitle", challenge.getBadgeText());
        args.putString("imageUrl", challenge.getImage());
        args.putString("listeningCount", challenge.getJoinedCount());
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}