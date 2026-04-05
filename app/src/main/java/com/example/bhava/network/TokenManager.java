package com.example.bhava.network;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Manages secure JWT token storage using EncryptedSharedPreferences.
 */
public class TokenManager {

    private static final String PREFS_NAME = "bhava_secure_prefs";
    private static final String KEY_TOKEN   = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME    = "user_name";
    private static final String KEY_EMAIL   = "user_email";
    private static final String KEY_AVATAR  = "user_avatar";
    private static final String KEY_BIO     = "user_bio";
    private static final String KEY_PHONE   = "user_phone";
    private static final String KEY_LOCATION= "user_location";
    private static final String KEY_JOINED_COUNT = "joined_count";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        SharedPreferences p;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            p = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to regular prefs if encryption fails
            p = context.getSharedPreferences(PREFS_NAME + "_fallback", Context.MODE_PRIVATE);
        }
        prefs = p;
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void saveUserInfo(String userId, String name, String email, String avatar, String bio, String phone, String location, int joinedCount) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_AVATAR, avatar != null ? avatar : "")
                .putString(KEY_BIO, bio != null ? bio : "")
                .putString(KEY_PHONE, phone != null ? phone : "")
                .putString(KEY_LOCATION, location != null ? location : "")
                .putInt(KEY_JOINED_COUNT, joinedCount)
                .apply();
    }

    public String getUserId()    { return prefs.getString(KEY_USER_ID, ""); }
    public String getUserName()  { return prefs.getString(KEY_NAME, ""); }
    public String getUserEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getUserAvatar(){ return prefs.getString(KEY_AVATAR, ""); }
    public String getUserBio()   { return prefs.getString(KEY_BIO, ""); }
    public String getUserPhone() { return prefs.getString(KEY_PHONE, ""); }
    public String getUserLocation(){ return prefs.getString(KEY_LOCATION, ""); }
    public int getJoinedChallengesCount() { return prefs.getInt(KEY_JOINED_COUNT, 0); }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
