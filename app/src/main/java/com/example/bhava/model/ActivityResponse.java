package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class ActivityResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("streakCount")
    public int streakCount;

    @SerializedName("dailyActiveMinutes")
    public int dailyActiveMinutes;
}
