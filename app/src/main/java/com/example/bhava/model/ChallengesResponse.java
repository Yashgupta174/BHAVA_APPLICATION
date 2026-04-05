package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChallengesResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("data")
    private List<ChallengeItem> data;

    // Getters
    public boolean isSuccess() { return success; }
    public int getCount() { return count; }
    public List<ChallengeItem> getData() { return data; }
}
