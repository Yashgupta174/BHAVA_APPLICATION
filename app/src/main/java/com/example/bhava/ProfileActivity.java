package com.example.bhava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.bhava.model.UserModel;
import com.example.bhava.model.UserResponse;
import com.example.bhava.model.UpdateProfileRequest;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etLocation, etBio;
    private EditText etEmail; // Disabled edittext in new layout
    private Button btnUpdate, btnLogout;
    private ProgressBar progressBar;
    private ImageView ivAvatar;
    private View fabEditAvatar; // Changed from FloatingActionButton to View/FrameLayout
    private TextView tvSyncStatus;
    private com.google.android.material.appbar.MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar     = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        etName      = findViewById(R.id.etName);
        etEmail     = findViewById(R.id.etEmail);
        etPhone     = findViewById(R.id.etPhone);
        etLocation  = findViewById(R.id.etLocation);
        etBio       = findViewById(R.id.etBio);
        btnUpdate   = findViewById(R.id.btnUpdate);
        btnLogout   = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBar);
        ivAvatar    = findViewById(R.id.ivAvatar);
        fabEditAvatar = findViewById(R.id.fabEditAvatar);
        tvSyncStatus = findViewById(R.id.tvSyncStatus);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Pre-fill from local cache for instant display
        bindUserToViews(TokenManager.getInstance(this));

        // Refresh from server
        loadProfile();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnLogout.setOnClickListener(v -> logout());
        if (fabEditAvatar != null) {
            fabEditAvatar.setOnClickListener(v ->
                    Toast.makeText(this, "Avatar upload can be added next", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null) {
            loadProfile();
        }
    }

    private void loadProfile() {
        setLoading(true, "Syncing profile with server...");
        ApiClient.getService(this).getMe().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                setLoading(false, null);
                if (response.isSuccessful() && response.body() != null && response.body().user != null) {
                    UserModel user = response.body().user;
                    bindUserToViews(user);
                    
                    // Sync local cache
                    TokenManager tm = TokenManager.getInstance(ProfileActivity.this);
                    int jCount = (user.joinedChallenges != null) ? user.joinedChallenges.size() : tm.getJoinedChallengesCount();
                    tm.saveUserInfo(user.id, user.name, user.email, user.avatar, user.bio, user.phoneNumber, user.location, jCount, user.streakCount);
                    if (tvSyncStatus != null) tvSyncStatus.setText("Profile synced from backend");
                } else {
                    if (tvSyncStatus != null) tvSyncStatus.setText("Using saved profile data");
                    Toast.makeText(ProfileActivity.this, "Could not refresh profile right now", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                setLoading(false, null);
                if (tvSyncStatus != null) tvSyncStatus.setText("Offline: using saved profile");
                Toast.makeText(ProfileActivity.this, "Could not load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (name.length() < 2) { 
            etName.setError("Name must be at least 2 characters"); 
            return; 
        }

        setLoading(true, "Saving changes...");

        String currentAvatar = TokenManager.getInstance(this).getUserAvatar();
        UpdateProfileRequest request = new UpdateProfileRequest(name, currentAvatar, bio, phone, location);

        ApiClient.getService(this)
                .updateMe(request)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        setLoading(false, null);
                        if (response.isSuccessful() && response.body() != null) {
                            UserModel user = response.body().user != null ? response.body().user : null;
                            TokenManager tm = TokenManager.getInstance(ProfileActivity.this);
                            if (user != null) {
                                bindUserToViews(user);
                                int jCount = (user.joinedChallenges != null) ? user.joinedChallenges.size() : tm.getJoinedChallengesCount();
                                tm.saveUserInfo(user.id, user.name, user.email, user.avatar, user.bio, user.phoneNumber, user.location, jCount, user.streakCount);
                            } else {
                                tm.saveUserInfo(tm.getUserId(), name, tm.getUserEmail(), tm.getUserAvatar(), bio, phone, location, tm.getJoinedChallengesCount(), tm.getStreakCount());
                                bindUserToViews(tm);
                            }
                            if (tvSyncStatus != null) tvSyncStatus.setText("Changes saved to your account");
                            Toast.makeText(ProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (tvSyncStatus != null) tvSyncStatus.setText("Save failed - showing local data");
                            Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        setLoading(false, null);
                        if (tvSyncStatus != null) tvSyncStatus.setText("Network error - save not completed");
                        Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        TokenManager.getInstance(this).clearAll();
        Intent intent = new Intent(ProfileActivity.this, Login_Screen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }

    private void bindUserToViews(TokenManager tm) {
        if (tm == null) return;
        etName.setText(safeValue(tm.getUserName(), ""));
        etEmail.setText(safeValue(tm.getUserEmail(), ""));
        etPhone.setText(safeValue(tm.getUserPhone(), ""));
        etLocation.setText(safeValue(tm.getUserLocation(), ""));
        etBio.setText(safeValue(tm.getUserBio(), ""));
        bindAvatar(tm.getUserAvatar());
        if (tvSyncStatus != null) tvSyncStatus.setText("Loaded saved profile");
    }

    private void bindUserToViews(UserModel user) {
        if (user == null) return;
        etName.setText(safeValue(user.name, ""));
        etEmail.setText(safeValue(user.email, ""));
        etPhone.setText(safeValue(user.phoneNumber, ""));
        etLocation.setText(safeValue(user.location, ""));
        etBio.setText(safeValue(user.bio, ""));
        bindAvatar(user.avatar);
    }

    private void bindAvatar(String avatarUrl) {
        if (ivAvatar == null) return;
        String resolvedAvatar = resolveAvatarUrl(avatarUrl);
        if (resolvedAvatar != null && !resolvedAvatar.trim().isEmpty()) {
            Glide.with(this)
                    .load(resolvedAvatar)
                    .placeholder(R.drawable.placeholder_avatar)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.placeholder_avatar);
        }
    }

    private String resolveAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) return null;
        if (avatarUrl.startsWith("/uploads")) {
            return ApiClient.BASE_URL + avatarUrl.substring(1);
        }
        return avatarUrl;
    }

    private String safeValue(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value;
    }

    private void setLoading(boolean loading, String message) {
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (btnUpdate != null) btnUpdate.setEnabled(!loading);
        if (message != null && tvSyncStatus != null) tvSyncStatus.setText(message);
    }
}
