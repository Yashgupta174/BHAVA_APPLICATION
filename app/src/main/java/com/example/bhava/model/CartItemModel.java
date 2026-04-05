package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class CartItemModel {
    @SerializedName("_id")       public String id;
    @SerializedName("productId") public int productId;
    @SerializedName("title")     public String title;
    @SerializedName("price")     public String price;
    @SerializedName("priceNum")  public int priceNum;
    @SerializedName("image")     public String image;
    @SerializedName("category")  public String category;
    @SerializedName("quantity")  public int quantity;
}
