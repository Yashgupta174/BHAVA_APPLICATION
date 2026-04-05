package com.example.bhava;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.adapter.NotificationsAdapter;
import com.example.bhava.model.NotificationModel;
import com.example.bhava.network.NotificationHelper;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private NotificationHelper helper;
    private NotificationsAdapter adapter;
    private List<NotificationModel> notificationsList;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        helper = new NotificationHelper(this);
        
        // Initialize UI
        RecyclerView rv = findViewById(R.id.rvNotifications);
        emptyState = findViewById(R.id.emptyState);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnClearAll = findViewById(R.id.btnClearAll);

        rv.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NotificationsAdapter(position -> {
            // Delete individual notification
            notificationsList.remove(position);
            saveCurrentList();
            updateList();
            Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
        });
        rv.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        
        btnClearAll.setOnClickListener(v -> {
            if (notificationsList.isEmpty()) return;
            
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear All Notifications")
                .setMessage("Are you sure you want to clear your notification history?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    helper.clearAll();
                    loadNotifications();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        loadNotifications();
        
        // Mark as read when opening history
        helper.markAsRead();
    }

    private void loadNotifications() {
        notificationsList = helper.getNotifications();
        updateList();
    }

    private void updateList() {
        if (notificationsList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }
        adapter.setNotifications(notificationsList);
    }

    private void saveCurrentList() {
        // Simple way to save the modified list back
        helper.clearAll();
        for (int i = notificationsList.size() - 1; i >= 0; i--) {
            helper.saveNotification(notificationsList.get(i));
        }
        // Since helper.saveNotification() also sets unread = true, 
        // we reset it here as we are in the activity
        helper.markAsRead(); 
    }
}
