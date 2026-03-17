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
            openDetailActiveChallengeFragment("dhyan", "21 Days Dhyan Challenge");
        });

        // 🔹 Mantra Card Click
        view.findViewById(R.id.card_mantra).setOnClickListener(v -> {
            openDetailActiveChallengeFragment("mantra", "108 Days Mantra Sadhana");
        });

        // 🔹 Geeta Card Click
        view.findViewById(R.id.card_geeta).setOnClickListener(v -> {
            openDetailActiveChallengeFragment("geeta", "40 Days Gita Journey");
        });

        // 🔹 Morning Routine Daily Pooja Card Click
        view.findViewById(R.id.card_daily_pooja).setOnClickListener(v -> {
            openDetailMorningRoutineFragment( "Daily Pooja");
        });

        // 🔹 Morning Routine Meditation Card Click
        view.findViewById(R.id.card_meditation).setOnClickListener(v -> {
            openDetailMorningRoutineFragment( "Meditation");
        });

        // 🔹 Morning Routine Pranayama Card Click
        view.findViewById(R.id.card_pranayama).setOnClickListener(v -> {
            openDetailMorningRoutineFragment( "Pranayama");
        });






        return view;
    }

    private void openDetailMorningRoutineFragment(String title) {
        Detailed_morning_routine_Fragment fragment =
                new Detailed_morning_routine_Fragment();

        Bundle bundle = new Bundle();
             // which card clicked
        bundle.putString("title", title);   // optional (for UI)

        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // 🔥 Common function to open fragment
    private void openDetailActiveChallengeFragment(String type, String title) {

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