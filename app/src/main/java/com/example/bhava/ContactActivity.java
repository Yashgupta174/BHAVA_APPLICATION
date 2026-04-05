package com.example.bhava;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhava.model.ApiResponse;
import com.example.bhava.model.ContactRequest;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {

    private EditText etName, etMobile, etEmail, etMessage;
    private View btnSubmit, tvBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        etName      = findViewById(R.id.etName);
        etMobile    = findViewById(R.id.etMobile);
        etEmail     = findViewById(R.id.etEmail);
        etMessage   = findViewById(R.id.etMessage);
        btnSubmit   = findViewById(R.id.btnSubmit);
        tvBack      = findViewById(R.id.tvBack);

        progressBar = findViewById(R.id.progressBar);

        // Pre-fill from token if logged in
        TokenManager tm = TokenManager.getInstance(this);
        if (tm.isLoggedIn()) {
            etName.setText(tm.getUserName());
            etEmail.setText(tm.getUserEmail());
        }

        btnSubmit.setOnClickListener(v -> submitContact());
        btnSubmit.setOnClickListener(v -> submitContact());
        tvBack.setOnClickListener(v -> finish());
    }

    private void submitContact() {
        String name    = etName.getText().toString().trim();
        String mobile  = etMobile.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(name))    { etName.setError("Name required");    return; }
        if (TextUtils.isEmpty(mobile))  { etMobile.setError("Mobile required"); return; }
        if (message.length() < 10)     { etMessage.setError("Min 10 characters"); return; }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        ApiClient.getService(this)
                .submitContact(new ContactRequest(name, mobile, email, message))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ContactActivity.this,
                                    response.body().message, Toast.LENGTH_LONG).show();
                            clearForm();
                        } else {
                            Toast.makeText(ContactActivity.this,
                                    "Submission failed. Check your inputs.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        Toast.makeText(ContactActivity.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearForm() {
        etMobile.setText("");
        etMessage.setText("");
    }
}
