package com.example.bhava;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bhava.model.ApiResponse;
import com.example.bhava.model.IntentionRequest;
import com.example.bhava.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddIntentionDialogFragment extends DialogFragment {

    private EditText etIntention;
    private Button btnSave;
    private TextView btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_intention, container, false);

        etIntention = view.findViewById(R.id.etIntention);
        btnSave = view.findViewById(R.id.btnSaveIntention);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveIntention());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }

    private void saveIntention() {
        String content = etIntention.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an intention", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getContext() == null) return;
        btnSave.setEnabled(false);

        ApiClient.getService(getContext()).addIntention(new IntentionRequest(content)).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (getTargetFragment() instanceof IntentionListFragment) {
                        ((IntentionListFragment) getTargetFragment()).onIntentionAdded();
                    }
                    dismiss();
                } else {
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to save", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                Log.e("AddIntention", "Error", t);
            }
        });
    }
}
