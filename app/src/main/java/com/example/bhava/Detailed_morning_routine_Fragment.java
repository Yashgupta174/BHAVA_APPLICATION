package com.example.bhava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Detailed_morning_routine_Fragment extends Fragment {

    private String title;

    public Detailed_morning_routine_Fragment() {
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

        View view = inflater.inflate(R.layout.fragment_detailed_morning_routine_, container, false);

        // ✅ Find TextView
        TextView textView = view.findViewById(R.id.textMorningTitle);

        // ✅ Set title
        if (title != null) {
            textView.setText(title);
        } else {
            textView.setText("Morning Routine");
        }

        return view;
    }
}