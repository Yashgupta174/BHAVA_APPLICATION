package com.example.bhava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Detailed_active_challenges_Fragment extends Fragment {

    private String type;
    private String title;

    public Detailed_active_challenges_Fragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Receive data from HomeFragment
        if (getArguments() != null) {
            type = getArguments().getString("type");
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_active_challenges_, container, false);

        // ✅ Find TextView
        TextView textView = view.findViewById(R.id.textTitle);

        // ✅ Set Data
        if (title != null) {
            textView.setText(title);
        } else {
            textView.setText("No Data");
        }

        return view;
    }
}