package com.example.bhava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login_Screen extends AppCompatActivity {

    Button GoogleLogin, EmailLogin, AppleLogin,GuestLogin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        GoogleLogin = findViewById(R.id.googleLogin);
        GuestLogin = findViewById(R.id.guestLogin);
        EmailLogin = findViewById(R.id.emailLogin);
        AppleLogin = findViewById(R.id.appleLogin);

        View.OnClickListener loginListener = v -> {
            Intent intent = new Intent(Login_Screen.this, Home_screen.class);
            startActivity(intent);
        };

        GoogleLogin.setOnClickListener(loginListener);
        EmailLogin.setOnClickListener(loginListener);
        AppleLogin.setOnClickListener(loginListener);
        GuestLogin.setOnClickListener(loginListener);


    }

}