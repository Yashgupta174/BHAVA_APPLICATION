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

public class Splash_Screen extends AppCompatActivity {

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

        // 2000ms delay for splash
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash_Screen.this, Login_Screen.class);
            startActivity(intent);
            // Apply standard transition to avoid abrupt layout shifts
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }, 2000);
    }
}