package com.example.bhava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bhava.model.OrderModel;
import com.example.bhava.model.OrdersListResponse;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {

    private LinearLayout llOrders;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        llOrders    = findViewById(R.id.llOrders);
        tvEmpty     = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        if (!TokenManager.getInstance(this).isLoggedIn()) {
            tvEmpty.setText("Please log in to see your orders");
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        loadOrders();
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getService(this).getMyOrders().enqueue(new Callback<OrdersListResponse>() {
            @Override
            public void onResponse(Call<OrdersListResponse> call, Response<OrdersListResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    renderOrders(response.body().orders);
                } else {
                    Toast.makeText(OrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<OrdersListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrdersActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderOrders(List<OrderModel> orders) {
        llOrders.removeAllViews();

        if (orders == null || orders.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (OrderModel order : orders) {
            View row = inflater.inflate(R.layout.item_order_row, llOrders, false);

            String orderId = order.id != null ? "#" + order.id.substring(order.id.length() - 6).toUpperCase() : "#---";
            ((TextView) row.findViewById(R.id.tvOrderId)).setText(orderId);
            ((TextView) row.findViewById(R.id.tvOrderType)).setText(
                    order.type != null ? order.type.toUpperCase() : "ORDER");
            ((TextView) row.findViewById(R.id.tvOrderStatus)).setText(
                    order.status != null ? order.status : "pending");
            ((TextView) row.findViewById(R.id.tvOrderAmount)).setText("₹" + order.amount);
            ((TextView) row.findViewById(R.id.tvOrderDate)).setText(
                    order.createdAt != null ? order.createdAt.substring(0, 10) : "");
            int itemCount = (order.items != null) ? order.items.size() : 0;
            ((TextView) row.findViewById(R.id.tvOrderItems)).setText(itemCount + " item(s)");

            llOrders.addView(row);
        }
    }
}
