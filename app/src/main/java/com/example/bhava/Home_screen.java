package com.example.bhava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bhava.model.ActivityRequest;
import com.example.bhava.model.ActivityResponse;
import com.example.bhava.network.ApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home_screen extends AppCompatActivity {

    private long startTime = 0;
    private Handler handler = new Handler();
    private Runnable timerRunnable;
    private static final String PREFS_NAME = "BhavaActivityPrefs";
    private static final String KEY_DAILY_MINUTES = "daily_minutes";
    private static final String KEY_LAST_DATE = "last_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, homeFragment);
            ft.commit();
        }

        setupTimer();
    }

    private void setupTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateActivityTime();
                handler.postDelayed(this, 60000); // Check every 1 minute
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        handler.postDelayed(timerRunnable, 60000);
        checkDailyReset();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateActivityTime();
        handler.removeCallbacks(timerRunnable);
    }

    private void checkDailyReset() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastDate = prefs.getString(KEY_LAST_DATE, "");

        if (!today.equals(lastDate)) {
            prefs.edit()
                .putInt(KEY_DAILY_MINUTES, 0)
                .putString(KEY_LAST_DATE, today)
                .apply();
        }
    }

    private void updateActivityTime() {
        if (startTime == 0) return;

        long elapsedMillis = System.currentTimeMillis() - startTime;
        int deltaMinutes = (int) (elapsedMillis / 60000);
        
        if (deltaMinutes > 0) {
            startTime = System.currentTimeMillis(); // Reset start time for next interval
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int currentMinutes = prefs.getInt(KEY_DAILY_MINUTES, 0);
            int newTotal = currentMinutes + deltaMinutes;
            
            prefs.edit().putInt(KEY_DAILY_MINUTES, newTotal).apply();
            
            // Sync with backend
            syncActivityWithBackend(deltaMinutes);
            Log.d("ActivityTracker", "Logged " + deltaMinutes + " minutes. Daily total: " + newTotal);
        }
    }

    private void syncActivityWithBackend(int minutes) {
        ApiClient.getService(this).updateActivity(new ActivityRequest(minutes)).enqueue(new Callback<ActivityResponse>() {
            @Override
            public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ActivityTracker", "Successfully synced with backend. New streak count: " + response.body().streakCount);
                    // Optionally update local TokenManager if streak increased
                }
            }

            @Override
            public void onFailure(Call<ActivityResponse> call, Throwable t) {
                Log.e("ActivityTracker", "Failed to sync activity", t);
            }
        });
    }
}