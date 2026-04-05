package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("message") public String message;
    @SerializedName("orderId") public String orderId;
    @SerializedName("amount")  public int amount;
}
