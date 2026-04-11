package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class ActivityRequest {
    @SerializedName("minutes")
    public int minutes;

    public ActivityRequest(int minutes) {
        this.minutes = minutes;
    }
}
