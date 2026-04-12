package com.example.bhava;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.Toast;

public class Splash_Screen extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue with the app.
                    proceedToNextScreen();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied.
                    Toast.makeText(this, "Notifications disabled. You might miss important updates.", Toast.LENGTH_SHORT).show();
                    proceedToNextScreen();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable edge-to-edge before super.onCreate for maximum compatibility
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_splash_screen);
        
        // Ensure the main view handles window insets correctly
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initializeAnimations();

        // 3500ms delay for splash (premium feel), then check permissions
        new Handler().postDelayed(this::askNotificationPermission, 3500);
    }

    private void initializeAnimations() {
        View logoCard = findViewById(R.id.logoCard);
        View appName = findViewById(R.id.appName);
        View tagline = findViewById(R.id.tagline);
        View progressBar = findViewById(R.id.progressBar);

        if (logoCard == null || appName == null || tagline == null || progressBar == null) return;

        // 1. Initial states
        appName.setAlpha(0f);
        tagline.setAlpha(0f);
        progressBar.setAlpha(0f);

        // 2. Start logo heartbeat
        android.view.animation.Animation heartbeat = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.logo_heartbeat);
        logoCard.startAnimation(heartbeat);

        // 3. Sequential Reveal
        new Handler().postDelayed(() -> {
            appName.animate().alpha(1f).setDuration(800).start();
        }, 500);

        new Handler().postDelayed(() -> {
            tagline.animate().alpha(1f).setDuration(800).start();
        }, 1200);

        new Handler().postDelayed(() -> {
            progressBar.animate().alpha(1f).setDuration(500).start();
        }, 1800);
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (Tiramisu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                proceedToNextScreen();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Display an educational UI to the user. In this example, we just ask again.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            proceedToNextScreen();
        }
    }

    private void proceedToNextScreen() {
        Intent intent = new Intent(Splash_Screen.this, Login_Screen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}