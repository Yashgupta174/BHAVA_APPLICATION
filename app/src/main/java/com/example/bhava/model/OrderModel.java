package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderModel {
    @SerializedName("_id")        public String id;
    @SerializedName("type")       public String type;
    @SerializedName("status")     public String status;
    @SerializedName("amount")     public int amount;
    @SerializedName("items")      public List<CartItemModel> items;
    @SerializedName("createdAt")  public String createdAt;
    @SerializedName("guestName")  public String guestName;
    @SerializedName("guestEmail") public String guestEmail;
    @SerializedName("notes")      public String notes;
}
