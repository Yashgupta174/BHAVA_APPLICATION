package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class DownloadStatusResponse {
    @SerializedName("isDownloaded")
    private boolean isDownloaded;

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
