package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class SingleChallengeResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName(value = "data", alternate = {"challenge"})
    private ChallengeItem data;

    public boolean isSuccess() { return success; }
    public ChallengeItem getData() { return data; }
}
