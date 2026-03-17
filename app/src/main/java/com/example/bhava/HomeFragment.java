package com.example.bhava;

import android.content.Intent;
import android.net.Uri;
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

        // 🔹 Daily Practise Daily Pooja Card Click
        view.findViewById(R.id.card_daily_practise_daily_pooja).setOnClickListener(v -> {
            openDetailMorningRoutineFragment( "Daily Practise Daily Pooja");
        });

        // 🔹 Daily Practise Mantra Chanting Card Click
        view.findViewById(R.id.card_daily_practise_mantra_chanting).setOnClickListener(v -> {
            openDetailMorningRoutineFragment( "Daily Practise Mantra Chanting");
        });

        // 🔹 Daily Practise Yoga Practise Card Click
        view.findViewById(R.id.card_daily_practise_yoga_practice).setOnClickListener(v -> {
            openDetailDailyPractiseFragment( "Daily Practise Yoga Practice");
        });

        // 🔹 Learning Path 108 Days of Devotion Card Click
        view.findViewById(R.id.card_learning_path_108_days_of_devotion).setOnClickListener(v -> {
            openDetailLearningathFragment( "Learning Path 108 Days of Devotion");
        });

        // 🔹 Learning Path Bhagavad Gita Journey Card Click
        view.findViewById(R.id.card_learning_path_bhagavad_gita_journey).setOnClickListener(v -> {
            openDetailLearningathFragment( "Learning Path Bhagavad Gita Journey");
        });

        // 🔹 Learning Path Mantra Mastery Card Click
        view.findViewById(R.id.card_learning_path_mantra_mastery).setOnClickListener(v -> {
            openDetailLearningathFragment( "Learning Path Mantra Mastery");
        });

        // 🔹 Timeless Wisdom Karma Yoga Card Click
        view.findViewById(R.id.card_timeless_wisom_karma_yoga).setOnClickListener(v -> {
            openDetailTimelessWisdomFragment( "Timeless Wisdom Karma Yoga");
        });

        // 🔹 Timeless Wisdom Bhakti Sutras Card Click
        view.findViewById(R.id.card_timeless_wisdom_bhakti_sutras).setOnClickListener(v -> {
            openDetailTimelessWisdomFragment( "Timeless Wisdom Bhakti Sutras");
        });

        // 🔹 Timeless Wisdom Yoga Sutras Card Click
        view.findViewById(R.id.card_timeless_wisdom_yoga_sutras).setOnClickListener(v -> {
            openDetailTimelessWisdomFragment( "Timeless Wisdom Yoga Sutras");
        });

        // 🔹 Latest Teaching Karma and Dharma Card Click
        view.findViewById(R.id.card_latest_teaching_karma_dharma).setOnClickListener(v -> {
            openDetailTimelessWisdomFragment( "Latest Teaching Karma and Dharma");
        });

        // 🔹 Latest Teaching Divine Grace Card Click
        view.findViewById(R.id.card_latest_teaching_divine_grace).setOnClickListener(v -> {
            openDetailTimelessWisdomFragment( "Latest Teaching Divine Grace");
        });

            // 🔹 Latest Teaching Inner Peace Card Click
        view.findViewById(R.id.card_latest_teaching_inner_peace).setOnClickListener(v -> {
            openDetailTimelessWisdomFragment( "Latest Teaching Inner Peace");
        });


        //  Social media handling
        view.findViewById(R.id.instagram).setOnClickListener(v -> {
            openSocialMedia(        "instagram://user?username=thespiritualcompany108",
                    "https://www.instagram.com/thespiritualcompany108");
        });

        view.findViewById(R.id.facebook).setOnClickListener(v -> {
            openSocialMedia("fb://page/profile.php?id=61583433762092",
                    "https://www.facebook.com/profile.php?id=61583433762092");
        });

        view.findViewById(R.id.youtube).setOnClickListener(v -> {
            openSocialMedia("vnd.youtube://@TheSpiritualCompany",
                    "https://www.youtube.com/@TheSpiritualCompany");
        });


        view.findViewById(R.id.nav_me).setOnClickListener(v -> {
            openNavbarMeFragment();
        });

        view.findViewById(R.id.nav_bhava_ai).setOnClickListener(v -> {
            openNavbarBhavaAiFragment();
        });

        view.findViewById(R.id.ask_bhava).setOnClickListener(v -> {
            openNavbarBhavaAiFragment();
        });

        view.findViewById(R.id.favourite).setOnClickListener(v -> {
            openFavouriteFragment();
        });

        view.findViewById(R.id.routine).setOnClickListener(v -> {
            openRoutineFragment();
        });

        view.findViewById(R.id.recent).setOnClickListener(v -> {
            openRecentFragment();
        });











        return view;
    }

    private void openRecentFragment() {
        RecentFragment fragment = new RecentFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openRoutineFragment() {

        RoutineFragment fragment = new RoutineFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openFavouriteFragment() {

        Favourite_Fragment fragment = new Favourite_Fragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openNavbarBhavaAiFragment() {
        Bhava_Ai_Fragment fragment = new Bhava_Ai_Fragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openNavbarMeFragment() {
        Navbar_Me_Fragment fragment = new Navbar_Me_Fragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void openSocialMedia(String appUrl, String webUrl) {
        try {
            // Try to open app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
            startActivity(intent);
        } catch (Exception e) {
            // If app not installed → open website
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
            startActivity(intent);
        }
    }

    private void openDetailTimelessWisdomFragment(String title) {
        Detailed_timeless_wisdom_Fragment fragment =
                new Detailed_timeless_wisdom_Fragment();

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

    private void openDetailLearningathFragment(String title) {
        Detailed_learning_path_Fragment fragment =
                new Detailed_learning_path_Fragment();

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

    private void openDetailDailyPractiseFragment(String title) {
        Detailed_Daily_Practices_Fragment fragment =
                new Detailed_Daily_Practices_Fragment();

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