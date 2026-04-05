package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartResponse {
    @SerializedName("success")    public boolean success;
    @SerializedName("message")    public String message;
    @SerializedName("items")      public List<CartItemModel> items;
    @SerializedName("itemCount")  public int itemCount;
    @SerializedName("totalPrice") public int totalPrice;
}
