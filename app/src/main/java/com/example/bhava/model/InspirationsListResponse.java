package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InspirationsListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<InspirationModel> data;

    public boolean isSuccess() { return success; }
    public List<InspirationModel> getData() { return data; }
}
