package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrdersListResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("count")   public int count;
    @SerializedName("orders")  public List<OrderModel> orders;
}
