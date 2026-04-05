package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IntentionsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("count")
    private int count;

    @SerializedName("data")
    private List<IntentionItem> data;

    public boolean isSuccess() { return success; }
    public int getCount() { return count; }
    public List<IntentionItem> getData() { return data; }
}
