package com.example.bhava.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bhava.model.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationHelper {
    private static final String PREF_NAME = "bhava_notifications";
    private static final String KEY_NOTIFICATIONS = "notifications_list";
    private static final String KEY_HAS_UNREAD = "has_unread_notifications";
    
    private final SharedPreferences prefs;
    private final Gson gson;

    public NotificationHelper(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveNotification(NotificationModel notification) {
        List<NotificationModel> list = getNotifications();
        // Add to the beginning of the list (newest first)
        list.add(0, notification);
        
        // Limit to 50 notifications to save space
        if (list.size() > 50) {
            list = list.subList(0, 50);
        }

        String json = gson.toJson(list);
        prefs.edit()
            .putString(KEY_NOTIFICATIONS, json)
            .putBoolean(KEY_HAS_UNREAD, true)
            .apply();
    }

    public List<NotificationModel> getNotifications() {
        String json = prefs.getString(KEY_NOTIFICATIONS, null);
        if (json == null) return new ArrayList<>();
        
        Type type = new TypeToken<ArrayList<NotificationModel>>() {}.getType();
        try {
            List<NotificationModel> list = gson.fromJson(json, type);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void markAsRead() {
        prefs.edit().putBoolean(KEY_HAS_UNREAD, false).apply();
    }

    public boolean hasUnread() {
        return prefs.getBoolean(KEY_HAS_UNREAD, false);
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
