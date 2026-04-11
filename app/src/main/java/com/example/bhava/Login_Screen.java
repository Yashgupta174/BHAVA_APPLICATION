package com.example.bhava;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bhava.model.AuthResponse;
import com.example.bhava.model.GoogleLoginRequest;
import com.example.bhava.model.LoginRequest;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Screen extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If user is already logged in, skip straight to Home
        if (TokenManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, Home_screen.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Main Login Logic
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Forgot Password link
        findViewById(R.id.tvForgotPassword).setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        // Redirect to Signup
        findViewById(R.id.tvSignupLink).setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));

        // Guest Login
        findViewById(R.id.guestLogin).setOnClickListener(v ->
                startActivity(new Intent(this, Home_screen.class)));

        // Google Sign-In Setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Requires google-services.json
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleLogin).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        findViewById(R.id.appleLogin).setOnClickListener(v ->
                Toast.makeText(this, "Apple Sign-In coming soon!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                // Signed in successfully, now authenticate with your backend
                String idToken = account.getIdToken();
                firebaseAuthWithGoogle(idToken);
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Google sign in failed: " + e.getStackTrace(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        setLoading(true);
        ApiClient.getService(this)
                .googleLogin(new GoogleLoginRequest(idToken))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            handleSuccessLogin(response.body());
                        } else {
                            Toast.makeText(Login_Screen.this, "Backend authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(Login_Screen.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleSuccessLogin(AuthResponse body) {
        TokenManager tm = TokenManager.getInstance(Login_Screen.this);
        tm.saveToken(body.token);
        tm.saveUserInfo(body.user.id, body.user.name,
                body.user.email, body.user.avatar,
                body.user.bio, body.user.phoneNumber, body.user.location,
                body.user.joinedChallenges != null ? body.user.joinedChallenges.size() : 0,
                body.user.streakCount);
        
        Toast.makeText(Login_Screen.this,
                "Welcome back, " + body.user.name + "!", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(Login_Screen.this, Home_screen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        setLoading(true);

        // Show a "Waking up server..." message after 5 seconds if still loading
        // This helps manage user expectations during Render's cold starts.
        final android.os.Handler handler = new android.os.Handler();
        final Runnable wakeupRunnable = () -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                Toast.makeText(Login_Screen.this, "Waking up server... please wait (cold start)", Toast.LENGTH_LONG).show();
            }
        };
        handler.postDelayed(wakeupRunnable, 5000);

        ApiClient.getService(this)
                .login(new LoginRequest(email, password))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        handler.removeCallbacks(wakeupRunnable);
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            handleSuccessLogin(response.body());
                        } else {
                            String errorMsg = "Login failed";
                            try {
                                if (response.errorBody() != null) {
                                    String errorStr = response.errorBody().string();
                                    // If Render is still loading, it returns HTML. Check for that.
                                    if (errorStr.toLowerCase().contains("<!doctype html>") || errorStr.toLowerCase().contains("<html>")) {
                                        errorMsg = "Server is still waking up. Please try again in a few seconds.";
                                    } else if (errorStr.contains("\"message\":\"")) {
                                        // Simple JSON extraction
                                        int start = errorStr.indexOf("\"message\":\"") + 11;
                                        int end = errorStr.indexOf("\"", start);
                                        if (start > 10 && end > start) {
                                            errorMsg = errorStr.substring(start, end);
                                        }
                                    }
                                }
                            } catch (Exception ignored) {}
                            
                            Toast.makeText(Login_Screen.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        handler.removeCallbacks(wakeupRunnable);
                        setLoading(false);
                        Toast.makeText(Login_Screen.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}