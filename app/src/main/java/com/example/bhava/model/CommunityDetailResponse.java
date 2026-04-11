package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class CommunityDetailResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("data") public CommunityModel data;
}
