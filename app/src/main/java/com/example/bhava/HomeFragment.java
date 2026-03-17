package com.example.bhava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        // 🔹 Dhyan Card Click
        view.findViewById(R.id.card_dhyan).setOnClickListener(v -> {
            openDetailFragment("dhyan", "21 Days Dhyan Challenge");
        });

        // 🔹 Mantra Card Click
        view.findViewById(R.id.card_mantra).setOnClickListener(v -> {
            openDetailFragment("mantra", "108 Days Mantra Sadhana");
        });

        // 🔹 Geeta Card Click
        view.findViewById(R.id.card_geeta).setOnClickListener(v -> {
            openDetailFragment("geeta", "40 Days Gita Journey");
        });

        return view;
    }

    // 🔥 Common function to open fragment
    private void openDetailFragment(String type, String title) {

        Detailed_active_challenges_Fragment fragment =
                new Detailed_active_challenges_Fragment();

        Bundle bundle = new Bundle();
        bundle.putString("type", type);     // which card clicked
        bundle.putString("title", title);   // optional (for UI)

        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}