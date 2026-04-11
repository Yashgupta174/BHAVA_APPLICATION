package com.example.bhava.network;

import com.example.bhava.model.ApiResponse;
import com.example.bhava.model.AuthResponse;
import com.example.bhava.model.CartResponse;
import com.example.bhava.model.ContactRequest;
import com.example.bhava.model.ForgotPasswordRequest;
import com.example.bhava.model.GoogleLoginRequest;
import com.example.bhava.model.InspirationModel;
import com.example.bhava.model.InspirationsListResponse;
import com.example.bhava.model.LoginRequest;
import com.example.bhava.model.OrderResponse;
import com.example.bhava.model.OrdersListResponse;
import com.example.bhava.model.PlaceOrderRequest;
import com.example.bhava.model.PreorderRequest;
import com.example.bhava.model.ResetPasswordRequest;
import com.example.bhava.model.SignupRequest;
import com.example.bhava.model.AddToCartRequest;
import com.example.bhava.model.UpdateCartItemRequest;
import com.example.bhava.model.UpdateProfileRequest;
import com.example.bhava.model.UserResponse;
import com.example.bhava.model.ChallengesResponse;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.model.CommunityListResponse;
import com.example.bhava.model.CommunityDetailResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * All Bhava API endpoints as a Retrofit interface.
 */
public interface BhavaApiService {

    // ── Auth ─────────────────────────────────────────────────────────
    @POST("api/auth/signup")
    Call<AuthResponse> signup(@Body SignupRequest body);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @GET("api/auth/me")
    Call<UserResponse> getMe();

    @PATCH("api/auth/me")
    Call<UserResponse> updateMe(@Body UpdateProfileRequest body);

    @POST("api/auth/forgot-password")
    Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest body);

    @POST("api/auth/reset-password")
    Call<AuthResponse> resetPassword(@Body ResetPasswordRequest body);

    @POST("api/auth/google")
    Call<AuthResponse> googleLogin(@Body GoogleLoginRequest request);

    @GET("api/challenges")
    Call<ChallengesResponse> getChallenges();

    @GET("api/challenges/{id}")
    Call<com.example.bhava.model.SingleChallengeResponse> getChallengeById(@Path("id") String id);

    @POST("api/challenges/{id}/join")
    Call<ApiResponse> joinChallenge(@Path("id") String id);

    // ── Cart ──────────────────────────────────────────────────────────
    @GET("api/cart")
    Call<CartResponse> getCart();

    @POST("api/cart")
    Call<CartResponse> addToCart(@Body AddToCartRequest body);

    @PATCH("api/cart/{itemId}")
    Call<CartResponse> updateCartItem(@Path("itemId") String itemId,
                                      @Body UpdateCartItemRequest body);

    @DELETE("api/cart/{itemId}")
    Call<CartResponse> removeFromCart(@Path("itemId") String itemId);

    @DELETE("api/cart")
    Call<CartResponse> clearCart();

    // ── Orders ────────────────────────────────────────────────────────
    @POST("api/orders/preorder")
    Call<OrderResponse> createPreorder(@Body PreorderRequest body);

    @POST("api/orders")
    Call<OrderResponse> placeOrder(@Body PlaceOrderRequest body);

    @GET("api/orders")
    Call<OrdersListResponse> getMyOrders();

    // ── Contact ───────────────────────────────────────────────────────
    @POST("api/contact")
    Call<ApiResponse> submitContact(@Body ContactRequest body);

    // ── Health ────────────────────────────────────────────────────────
    @GET("api/health")
    Call<ApiResponse> health();

    // ── Inspirations ──────────────────────────────────────────────────
    @GET("api/inspirations/latest")
    Call<InspirationsListResponse> getLatestInspirations();

    // ── AI Chat ───────────────────────────────────────────────────────
    @POST("api/ai/chat")
    Call<com.example.bhava.model.AiResponse> aiChat(@Body com.example.bhava.model.AiRequest body);

    // ── Recent Activity ───────────────────────────────────────────────
    @POST("api/recent/{id}")
    Call<ApiResponse> addToRecent(@Path("id") String id);

    @GET("api/recent")
    Call<ChallengesResponse> getRecentActivity();

    // ── Favorites ─────────────────────────────────────────────────────
    @GET("api/favorites/check/{id}")
    Call<com.example.bhava.model.FavoriteStatusResponse> checkFavorite(@Path("id") String id);

    @POST("api/favorites/toggle/{id}")
    Call<com.example.bhava.model.FavoriteStatusResponse> toggleFavorite(@Path("id") String id);

    @GET("api/favorites")
    Call<ChallengesResponse> getFavorites();

    // ── Downloads ─────────────────────────────────────────────────────
    @GET("api/downloads/check/{id}")
    Call<com.example.bhava.model.DownloadStatusResponse> checkDownload(@Path("id") String id);

    @POST("api/downloads/{id}")
    Call<ApiResponse> addDownload(@Path("id") String id);

    @DELETE("api/downloads/{id}")
    Call<ApiResponse> removeDownload(@Path("id") String id);

    @GET("api/downloads")
    Call<ChallengesResponse> getDownloads();

    @GET("api/challenges/my/joined")
    Call<ChallengesResponse> getMyJoinedChallenges();

    @GET("api/routines")
    Call<ChallengesResponse> getRoutines(@retrofit2.http.Query("day") String day);

    @POST("api/routines/{id}")
    Call<ApiResponse> addToRoutine(@Path("id") String challengeId, @Body com.example.bhava.model.RoutineRequest body);

    @GET("api/routines/check/{id}")
    Call<com.example.bhava.model.RoutineStatusResponse> checkRoutine(@Path("id") String challengeId);

    @DELETE("api/routines/{id}")
    Call<ApiResponse> removeFromRoutine(@Path("id") String challengeId);

    @PATCH("api/routines/{id}/days")
    Call<ApiResponse> updateRoutineDays(@Path("id") String challengeId, @Body java.util.List<String> days);

    @GET("api/intentions")
    Call<com.example.bhava.model.IntentionsResponse> getIntentions();

    @POST("api/intentions")
    Call<ApiResponse> addIntention(@Body com.example.bhava.model.IntentionRequest request);

    @GET("api/challenges/hero")
    Call<com.example.bhava.model.ChallengesResponse> getHeroChallenge();

    @DELETE("api/intentions/{id}")
    Call<ApiResponse> deleteIntention(@Path("id") String id);

    @PATCH("api/auth/activity")
    Call<com.example.bhava.model.ActivityResponse> updateActivity(@Body com.example.bhava.model.ActivityRequest body);

    @GET("api/community")
    Call<CommunityListResponse> getCommunities();

    @GET("api/community/{id}")
    Call<CommunityDetailResponse> getCommunityById(@Path("id") String id);
}
