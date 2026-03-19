package com.example.bhava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Detailed_Daily_Practices_Fragment extends Fragment {

    private String title;

    public Detailed_Daily_Practices_Fragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Receive data
        if (getArguments() != null) {
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed__daily__practices_, container, false);

        // ✅ Find TextView
        TextView textView = view.findViewById(R.id.textDailyTitle);

        // ✅ Set title
        if (title != null) {
            textView.setText(title);
        } else {
            textView.setText("Daily Practice");
        }

        // ✅ Set up btnPlay click listener to open Music Player Fragment
        Button btnPlay = view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(v -> {
            openMusicPlayerFragment();
        });

        return view;
    }

    // ✅ Open Music Player Fragment
    private void openMusicPlayerFragment() {
        Music_Player_Fragment fragment = new Music_Player_Fragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", title != null ? title : "Music Player");
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}