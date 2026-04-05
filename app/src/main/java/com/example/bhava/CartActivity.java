package com.example.bhava;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import com.example.bhava.model.CartItemModel;
import com.example.bhava.model.CartResponse;
import com.example.bhava.model.PlaceOrderRequest;
import com.example.bhava.model.OrderResponse;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private LinearLayout llCartItems;
    private TextView tvEmpty, tvTotal;
    private Button btnCheckout, btnClear;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        llCartItems = findViewById(R.id.llCartItems);
        tvEmpty     = findViewById(R.id.tvEmpty);
        tvTotal     = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClear    = findViewById(R.id.btnClear);
        progressBar = findViewById(R.id.progressBar);

        if (!TokenManager.getInstance(this).isLoggedIn()) {
            tvEmpty.setText("Please log in to view your cart");
            tvEmpty.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(false);
            return;
        }

        loadCart();

        btnClear.setOnClickListener(v -> clearCart());
        btnCheckout.setOnClickListener(v -> placeOrder());
    }

    private void loadCart() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService(this).getCart().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    renderCart(response.body());
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderCart(CartResponse cart) {
        llCartItems.removeAllViews();
        List<CartItemModel> items = cart.items;

        if (items == null || items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(false);
            tvTotal.setText("₹0");
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        btnCheckout.setEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (CartItemModel item : items) {
            View row = inflater.inflate(R.layout.item_cart_row, llCartItems, false);

            ((TextView) row.findViewById(R.id.tvItemTitle)).setText(item.title);
            ((TextView) row.findViewById(R.id.tvItemPrice)).setText(item.price + " × " + item.quantity);
            ((TextView) row.findViewById(R.id.tvItemTotal)).setText("₹" + (item.priceNum * item.quantity));

            row.findViewById(R.id.btnRemove).setOnClickListener(v -> removeItem(item.id));

            llCartItems.addView(row);
        }

        tvTotal.setText("₹" + cart.totalPrice);
    }

    private void removeItem(String itemId) {
        ApiClient.getService(this).removeFromCart(itemId)
                .enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            renderCart(response.body());
                            Toast.makeText(CartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Error removing item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearCart() {
        ApiClient.getService(this).clearCart()
                .enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            renderCart(response.body() != null ? response.body() : new CartResponse());
                            Toast.makeText(CartActivity.this, "Cart cleared", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Error clearing cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void placeOrder() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_address_selection, null);
        TextInputEditText etLine1   = dialogView.findViewById(R.id.etLine1);
        TextInputEditText etCity    = dialogView.findViewById(R.id.etCity);
        TextInputEditText etState   = dialogView.findViewById(R.id.etState);
        TextInputEditText etPincode = dialogView.findViewById(R.id.etPincode);
        TextInputEditText etNotes   = dialogView.findViewById(R.id.etNotes);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Confirm Order", (dialog, which) -> {
                    String line1   = etLine1.getText().toString().trim();
                    String city    = etCity.getText().toString().trim();
                    String state   = etState.getText().toString().trim();
                    String pincode = etPincode.getText().toString().trim();
                    String notes   = etNotes.getText().toString().trim();

                    if (TextUtils.isEmpty(line1) || TextUtils.isEmpty(city) ||
                        TextUtils.isEmpty(state) || TextUtils.isEmpty(pincode)) {
                        Toast.makeText(this, "Please fill all address fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PlaceOrderRequest.AddressModel address = new PlaceOrderRequest.AddressModel(line1, city, state, pincode);
                    executePlaceOrder(address, notes);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void executePlaceOrder(PlaceOrderRequest.AddressModel address, String notes) {
        progressBar.setVisibility(View.VISIBLE);
        btnCheckout.setEnabled(false);

        PlaceOrderRequest req = new PlaceOrderRequest(address, notes);

        ApiClient.getService(this).placeOrder(req)
                .enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        btnCheckout.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(CartActivity.this,
                                    "Order placed! ₹" + response.body().amount, Toast.LENGTH_LONG).show();
                            loadCart(); // cart will be empty now
                        } else {
                            Toast.makeText(CartActivity.this, "Order failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnCheckout.setEnabled(true);
                        Toast.makeText(CartActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
