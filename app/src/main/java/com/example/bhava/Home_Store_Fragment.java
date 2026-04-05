package com.example.bhava;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bhava.model.CartResponse;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home_Store_Fragment extends Fragment {

    private TextView tvCartCount;

    public Home_Store_Fragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home__store_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCartCount = view.findViewById(R.id.tvCartCount);

        // Cart button → open CartActivity
        view.findViewById(R.id.btnCart).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CartActivity.class));
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Profile button → open ProfileActivity if logged in, else Login
        view.findViewById(R.id.btnProfile).setOnClickListener(v -> {
            if (TokenManager.getInstance(requireContext()).isLoggedIn()) {
                startActivity(new Intent(getContext(), ProfileActivity.class));
            } else {
                startActivity(new Intent(getContext(), Login_Screen.class));
            }
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Load live cart count
        loadCartCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartCount(); // refresh count after returning from CartActivity
    }

    private void loadCartCount() {
        if (!TokenManager.getInstance(requireContext()).isLoggedIn()) {
            if (tvCartCount != null) tvCartCount.setVisibility(View.GONE);
            return;
        }

        ApiClient.getService(requireContext()).getCart()
                .enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful() && response.body() != null && tvCartCount != null) {
                            int count = response.body().itemCount;
                            tvCartCount.setText(String.valueOf(count));
                            tvCartCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        if (tvCartCount != null) tvCartCount.setVisibility(View.GONE);
                    }
                });
    }
}