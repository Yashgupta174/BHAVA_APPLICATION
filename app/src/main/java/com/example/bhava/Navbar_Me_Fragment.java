package com.example.bhava;

import android.util.Log;
import android.os.Bundle;
import java.util.List;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.bhava.model.UserModel;
import com.example.bhava.model.UserResponse;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Navbar_Me_Fragment extends Fragment {

    private TextView tvUsername;
    private TextView tvProfileSubtitle;
    private TextView tvProfileStatus;
    private ImageView ivProfileAvatar;
    private android.widget.LinearLayout llProfileHeader;
    private View cardOrders, cardCart, cardContact, cardLogout;

    public Navbar_Me_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navbar__me_, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvProfileSubtitle = view.findViewById(R.id.tvProfileSubtitle);
        tvProfileStatus = view.findViewById(R.id.tvProfileStatus);
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar);
        llProfileHeader = view.findViewById(R.id.llProfileHeader);

        bindProfile(TokenManager.getInstance(requireContext()));
        refreshProfileFromBackend();

        llProfileHeader.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Services & Support
        cardOrders = view.findViewById(R.id.cardOrders);
        cardCart = view.findViewById(R.id.cardCart);
        cardContact = view.findViewById(R.id.cardContact);
        cardLogout = view.findViewById(R.id.cardLogout);

        if (cardOrders != null) {
            cardOrders.setOnClickListener(v -> {
                try {
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://bhava-etxy.vercel.app/products"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Unable to open orders", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardCart != null) {
            cardCart.setOnClickListener(v -> {
                try {
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://bhava-etxy.vercel.app/products"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Unable to open cart", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardContact != null) {
            cardContact.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> showLogoutConfirmation());
        }

        // Navigate to Recently Prayed
        View tvRecentlyPrayed = view.findViewById(R.id.tvRecentlyPrayedTitle);
        if (tvRecentlyPrayed != null) {
            tvRecentlyPrayed.setOnClickListener(v -> {
                RecentFragment fragment = new RecentFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        // Navigate to Favorites
        View tvFavorites = view.findViewById(R.id.tvFavoritesTitle);
        if (tvFavorites != null) {
            tvFavorites.setOnClickListener(v -> {
                Favourite_Fragment fragment = new Favourite_Fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        View cardRecentlyPrayed = view.findViewById(R.id.cardMorningOffering);
        if (cardRecentlyPrayed != null) {
            cardRecentlyPrayed.setOnClickListener(v -> {
                RecentFragment fragment = new RecentFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        View cardFavorites = view.findViewById(R.id.cardFavorites);
        if (cardFavorites != null) {
            cardFavorites.setOnClickListener(v -> {
                Favourite_Fragment fragment = new Favourite_Fragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        // Navigate to Downloads
        View tvDownloads = view.findViewById(R.id.tvDownloadsTitle);
        if (tvDownloads != null) {
            tvDownloads.setOnClickListener(v -> openDownloads());
        }
        View cardDownloads = view.findViewById(R.id.cardDownloads);
        if (cardDownloads != null) {
            cardDownloads.setOnClickListener(v -> openDownloads());
        }

        // Navigate to Joined Challenges
        View cardJoinedChallenges = view.findViewById(R.id.cardJoinedChallenges);
        if (cardJoinedChallenges != null) {
            cardJoinedChallenges.setOnClickListener(v -> {
                JoinedChallengesFragment fragment = new JoinedChallengesFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        // My Routine Card
        View cardMyRoutine = view.findViewById(R.id.cardMyRoutine);
        if (cardMyRoutine != null) {
            cardMyRoutine.setOnClickListener(v -> {
                RoutineFragment fragment = new RoutineFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        // Edit Routine
        View btnEditRoutine = view.findViewById(R.id.btnEdit);
        if (btnEditRoutine != null) {
            btnEditRoutine.setOnClickListener(v -> {
                EditRoutineFragment fragment = new EditRoutineFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        // Intentions Section
        View rlIntentionsHeader = view.findViewById(R.id.rlIntentionsHeader);
        if (rlIntentionsHeader != null) {
            rlIntentionsHeader.setOnClickListener(v -> {
                IntentionListFragment fragment = new IntentionListFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        View btnAddIntention = view.findViewById(R.id.btnAddIntention);
        View cardAddIntentions = view.findViewById(R.id.cardAddIntentions);
        View.OnClickListener addIntentionListener = v -> {
            AddIntentionDialogFragment dialog = new AddIntentionDialogFragment();
            dialog.show(getActivity().getSupportFragmentManager(), "AddIntention");
        };
        if (btnAddIntention != null) btnAddIntention.setOnClickListener(addIntentionListener);
        if (cardAddIntentions != null) cardAddIntentions.setOnClickListener(addIntentionListener);

        return view;
    }

    private void openDownloads() {
        Downloads_Fragment fragment = new Downloads_Fragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            bindProfile(TokenManager.getInstance(getContext()));
            refreshProfileFromBackend();
        }
    }

    private void bindProfile(TokenManager tm) {
        if (tm == null) return;

        String name = safeValue(tm.getUserName(), "Me");
        String email = safeValue(tm.getUserEmail(), "Profile & settings");
        String avatar = tm.getUserAvatar();

        if (tvUsername != null) tvUsername.setText(name);
        if (tvProfileSubtitle != null) tvProfileSubtitle.setText(email);
        if (tvProfileStatus != null) tvProfileStatus.setText("Tap to view and edit your profile");

        View root = getView();
        TextView tvJoinedCount = root != null ? root.findViewById(R.id.tvJoinedChallengesCount) : null;
        if (tvJoinedCount != null) {
            int count = tm.getJoinedChallengesCount();
            tvJoinedCount.setText(count + " challenges joined");
            View hint = root.findViewById(R.id.tvJoinedChallengesHint);
            if (hint != null) hint.setVisibility(count > 0 ? View.GONE : View.VISIBLE);
        }

        if (ivProfileAvatar != null) {
            String resolvedAvatar = resolveAvatarUrl(avatar);
            if (resolvedAvatar != null && !resolvedAvatar.trim().isEmpty()) {
                Glide.with(this)
                        .load(resolvedAvatar)
                        .placeholder(R.drawable.placeholder_avatar)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivProfileAvatar);
            } else {
                ivProfileAvatar.setImageResource(R.drawable.placeholder_avatar);
            }
        }
    }

    private void refreshProfileFromBackend() {
        if (!isAdded() || getContext() == null) return;

        ApiClient.getService(requireContext()).getMe().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().user != null) {
                    UserModel user = response.body().user;
                    int joinedCount = user.joinedChallenges != null ? user.joinedChallenges.size() : 0;
                    TokenManager.getInstance(requireContext())
                            .saveUserInfo(user.id, user.name, user.email, user.avatar, user.bio, user.phoneNumber, user.location, joinedCount);
                    bindProfile(TokenManager.getInstance(requireContext()));
                    if (tvProfileStatus != null) tvProfileStatus.setText("Profile synced with your account");
                } else {
                    Log.w("Navbar_Me", "Profile sync failed: code=" + response.code());
                    if (tvProfileStatus != null) tvProfileStatus.setText("Using saved profile data");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("Navbar_Me", "Unable to refresh profile", t);
                if (tvProfileStatus != null) tvProfileStatus.setText("Offline: showing saved profile");
                Toast.makeText(requireContext(), "Could not refresh profile right now", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safeValue(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value;
    }

    private String resolveAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) return null;
        if (avatarUrl.startsWith("/uploads")) {
            return ApiClient.BASE_URL + avatarUrl.substring(1);
        }
        return avatarUrl;
    }

    private void showLogoutConfirmation() {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to sign out from your account?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    TokenManager.getInstance(requireContext()).clearAll();
                    android.content.Intent intent = new android.content.Intent(getActivity(), Login_Screen.class);
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}