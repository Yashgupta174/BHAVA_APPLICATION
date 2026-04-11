package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CommunityModel {
    @SerializedName("_id") public String id;
    @SerializedName("name") public String name;
    @SerializedName("description") public String description;
    @SerializedName("coverImage") public String coverImage;
    @SerializedName("membersCount") public int membersCount;
    @SerializedName("shareLink") public String shareLink;
    @SerializedName("contentBlocks") public List<CommunityBlockModel> contentBlocks;

    public static class CommunityBlockModel {
        @SerializedName("type") public String type; // "text", "image", "video"
        @SerializedName("value") public String value;
        @SerializedName("order") public int order;
    }
}
