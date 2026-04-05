package com.example.bhava;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhava.model.ApiResponse;
import com.example.bhava.model.AuthResponse;
import com.example.bhava.model.ForgotPasswordRequest;
import com.example.bhava.model.ResetPasswordRequest;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private LinearLayout layoutStep1, layoutStep2;
    private EditText etEmail, etCode, etNewPassword;
    private Button btnSendCode, btnResetPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);
        etEmail = findViewById(R.id.etEmail);
        etCode = findViewById(R.id.etCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBar);

        btnSendCode.setOnClickListener(v -> handleRequestCode());
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    private void handleRequestCode() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }

        setLoading(true);
        ApiClient.getService(this).forgotPassword(new ForgotPasswordRequest(email))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Reset code sent to your email!", Toast.LENGTH_LONG).show();
                            layoutStep1.setVisibility(View.GONE);
                            layoutStep2.setVisibility(View.VISIBLE);
                        } else {
                            String errorMsg = "Failed to send code.";
                            try {
                                if (response.errorBody() != null) {
                                    String errorJson = response.errorBody().string();
                                    if (errorJson.contains("message")) {
                                        int start = errorJson.indexOf("\"message\":\"") + 11;
                                        int end = errorJson.indexOf("\"", start);
                                        errorMsg = errorJson.substring(start, end);
                                    }
                                }
                            } catch (Exception ignored) {}
                            Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleResetPassword() {
        String token = etCode.getText().toString().trim();
        String password = etNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(token)) {
            etCode.setError("Code required");
            return;
        }
        if (password.length() < 6) {
            etNewPassword.setError("Minimum 6 characters");
            return;
        }

        setLoading(true);
        ApiClient.getService(this).resetPassword(new ResetPasswordRequest(token, password))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            AuthResponse body = response.body();
                            TokenManager tm = TokenManager.getInstance(ForgotPasswordActivity.this);
                            tm.saveToken(body.token);
                            tm.saveUserInfo(body.user.id, body.user.name, body.user.email, body.user.avatar, body.user.bio, body.user.phoneNumber, body.user.location,
                                    body.user.joinedChallenges != null ? body.user.joinedChallenges.size() : 0);

                            Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully! Logged in.", Toast.LENGTH_SHORT).show();
                            
                            Intent intent = new Intent(ForgotPasswordActivity.this, Home_screen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Invalid code or expired.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnSendCode.setEnabled(!loading);
        btnResetPassword.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
