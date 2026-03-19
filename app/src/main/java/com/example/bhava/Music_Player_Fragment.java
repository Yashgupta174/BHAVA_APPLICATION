package com.example.bhava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Music_Player_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Music_Player_Fragment extends Fragment {

    private String title;

    public Music_Player_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ✅ Receive title from detail fragments
        if (getArguments() != null) {
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music__player_, container, false);

        // ✅ Set up back button to pop back to previous fragment
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // ✅ Optionally update the title displayed in the music player
        if (title != null) {
            TextView tvMiniTitle = view.findViewById(R.id.tvMiniTitle);
            if (tvMiniTitle != null) {
                tvMiniTitle.setText(title);
            }
        }

        return view;
    }
}