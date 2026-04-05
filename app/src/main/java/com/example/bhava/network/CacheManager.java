package com.example.bhava.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * A simple Persistent Cache Manager using SharedPreferences and GSON.
 * Perfect for small/medium dynamic data like challenges and profile info.
 */
public class CacheManager {

    private static final String PREF_NAME = "bhava_cache";
    private final SharedPreferences prefs;
    private final Gson gson;

    public CacheManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Save any object to cache as a JSON string.
     */
    public <T> void save(String key, T data) {
        if (data == null) return;
        String json = gson.toJson(data);
        prefs.edit().putString(key, json).apply();
    }

    /**
     * Retrieve an object from cache. Returns null if not found.
     */
    public <T> T get(String key, Class<T> clazz) {
        String json = prefs.getString(key, null);
        if (json == null) return null;
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieve a generic list/collection from cache.
     */
    public <T> T get(String key, Type type) {
        String json = prefs.getString(key, null);
        if (json == null) return null;
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clear all cached data.
     */
    public void clear() {
        prefs.edit().clear().apply();
    }
}
