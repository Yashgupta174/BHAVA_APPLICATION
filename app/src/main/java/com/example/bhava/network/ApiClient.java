package com.example.bhava.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit singleton. Automatically attaches the JWT token to every request.
 */
public class ApiClient {

    // ── Local Development URL (Your Computer's IP) ──
    // IMPORTANT: Update this to your local Wi-Fi IP if testing on a physical device.
    public static final String BASE_URL = "https://bhava-web-2.onrender.com/";

    // ── Production Backend URL (Vercel) ──
    // public static final String BASE_URL = "https://bhava-fkv3.vercel.app/";

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            TokenManager tokenManager = TokenManager.getInstance(context);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        String token = tokenManager.getToken();
                        Request.Builder builder = original.newBuilder()
                                .header("Content-Type", "application/json");
                        if (token != null) {
                            builder.header("Authorization", "Bearer " + token);
                        }
                        
                        okhttp3.Response response = chain.proceed(builder.build());
                        
                        // Handle 401 Unauthorized globally
                        if (response.code() == 401) {
                            handleUnauthorized(context);
                        }
                        
                        return response;
                    })
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static BhavaApiService getService(Context context) {
        return getInstance(context).create(BhavaApiService.class);
    }

    private static void handleUnauthorized(final Context context) {
        // Run on main thread because we're showing UI
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            TokenManager.getInstance(context).clearAll();
            android.content.Intent intent = new android.content.Intent(context, com.example.bhava.Login_Screen.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            android.widget.Toast.makeText(context, "Session expired. Please log in again.", android.widget.Toast.LENGTH_LONG).show();
        });
    }
}
