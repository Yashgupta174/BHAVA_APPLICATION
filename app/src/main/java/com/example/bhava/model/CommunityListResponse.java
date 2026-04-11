package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CommunityListResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("data") public List<CommunityModel> data;
}
