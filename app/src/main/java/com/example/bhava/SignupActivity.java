package com.example.bhava;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhava.model.AuthResponse;
import com.example.bhava.model.SignupRequest;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnSignup;
    private ProgressBar progressBar;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName      = findViewById(R.id.etName);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnSignup   = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);
        tvLogin     = findViewById(R.id.tvLogin);

        btnSignup.setOnClickListener(v -> attemptSignup());
        tvLogin.setOnClickListener(v -> finish()); // go back to login
    }

    private void attemptSignup() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Name required"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Email required"); return; }
        if (password.length() < 6)  { etPassword.setError("Min 6 characters"); return; }

        setLoading(true);

        // Show a "Waking up server..." message after 5 seconds if still loading
        // This helps manage user expectations during Render's cold starts.
        final android.os.Handler handler = new android.os.Handler();
        final Runnable wakeupRunnable = () -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                Toast.makeText(SignupActivity.this, "Waking up server... please wait (cold start)", Toast.LENGTH_LONG).show();
            }
        };
        handler.postDelayed(wakeupRunnable, 5000);

        ApiClient.getService(this)
                .signup(new SignupRequest(name, email, password))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        handler.removeCallbacks(wakeupRunnable);
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            AuthResponse body = response.body();
                            TokenManager tm = TokenManager.getInstance(SignupActivity.this);
                            tm.saveToken(body.token);
                            tm.saveUserInfo(body.user.id, body.user.name,
                                    body.user.email, body.user.avatar,
                                    body.user.bio, body.user.phoneNumber, body.user.location,
                                    (body.user.joinedChallenges != null ? body.user.joinedChallenges.size() : 0),
                                    body.user.streakCount);
                            Toast.makeText(SignupActivity.this,
                                    "Account created! Welcome, " + body.user.name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, Home_screen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            String errorMsg = "Signup failed";
                            try {
                                if (response.errorBody() != null) {
                                    String errorStr = response.errorBody().string();
                                    // If Render is still loading, it returns HTML. Check for that.
                                    if (errorStr.toLowerCase().contains("<!doctype html>") || errorStr.toLowerCase().contains("<html>")) {
                                        errorMsg = "Server is still waking up. Please try again in a few seconds.";
                                    } else if (errorStr.contains("\"message\":\"")) {
                                        int start = errorStr.indexOf("\"message\":\"") + 11;
                                        int end = errorStr.indexOf("\"", start);
                                        if (start > 10 && end > start) {
                                            errorMsg = errorStr.substring(start, end);
                                        }
                                    }
                                }
                            } catch (Exception ignored) {}
                            
                            Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        handler.removeCallbacks(wakeupRunnable);
                        setLoading(false);
                        Toast.makeText(SignupActivity.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnSignup.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
