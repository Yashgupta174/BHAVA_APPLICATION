package com.example.bhava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.adapter.ChallengeAdapter;
import com.example.bhava.adapter.InspirationAdapter;
import com.example.bhava.model.ChallengeItem;
import com.example.bhava.model.ChallengesResponse;
import com.example.bhava.model.InspirationModel;
import com.example.bhava.model.InspirationsListResponse;
import com.example.bhava.model.SessionItem;
import com.example.bhava.network.ApiClient;
import com.example.bhava.network.CacheManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.bhava.network.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.widget.ProgressBar;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private CacheManager cacheManager;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar homeProgressBar;
    private ChallengeAdapter[] adapters;
    private InspirationAdapter inspirationAdapter;
    private ChallengeItem currentHero;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        // 🔹 Active Challenges RecyclerView (Special category)
        RecyclerView rvChallenges = view.findViewById(R.id.rvChallenges);
        ChallengeAdapter adapterChallenges = null;
        if (rvChallenges != null) {
            adapterChallenges = new ChallengeAdapter(this::openGenericDetail);
            rvChallenges.setAdapter(adapterChallenges);
        }

        // 🔹 Dynamic Sections Initialization
        ChallengeAdapter adapterMorning = initDynamicSection(view, R.id.rvMorningRoutines, this::openGenericDetail);
        ChallengeAdapter adapterDaily = initDynamicSection(view, R.id.rvDailyPractices, this::openGenericDetail);
        ChallengeAdapter adapterLearning = initDynamicSection(view, R.id.rvLearningPaths, this::openGenericDetail);
        ChallengeAdapter adapterWisdom = initDynamicSection(view, R.id.rvTimelessWisdom, this::openGenericDetail);
        ChallengeAdapter adapterTeachings = initDynamicSection(view, R.id.rvLatestTeachings, this::openGenericDetail);
        ChallengeAdapter adapterCommunity = initDynamicSection(view, R.id.rvCommunityCampaigns, R.layout.item_community_campaign, this::openGenericDetail);

        this.adapters = new ChallengeAdapter[]{adapterChallenges, adapterMorning, adapterDaily, adapterLearning, adapterWisdom, adapterTeachings, adapterCommunity};

        // 🔹 Inspirations Initialization
        RecyclerView rvInspirations = view.findViewById(R.id.rvInspirations);
        if (rvInspirations != null) {
            inspirationAdapter = new InspirationAdapter(getContext());
            rvInspirations.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvInspirations.setAdapter(inspirationAdapter);
        }

        // 🔹 Cache & Refresh Setup
        cacheManager = new CacheManager(requireContext());
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        homeProgressBar = view.findViewById(R.id.homeProgressBar);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> fetchAllData(true));
            swipeRefresh.setColorSchemeResources(R.color.action_button_bg);
        }

        // 🔹 Load Cache Immediately (Instant UI)
        loadFromCache(view);

        // 🔹 Fetch Fresh Data in Background
        fetchAllData(false);

        // 🔹 Quick Actions
        View askBhava = view.findViewById(R.id.ask_bhava);
        if (askBhava != null) askBhava.setOnClickListener(v -> openNavbarBhavaAiFragment());

        View favourite = view.findViewById(R.id.favourite);
        if (favourite != null) favourite.setOnClickListener(v -> openFavouriteFragment());

        View routine = view.findViewById(R.id.routine);
        if (routine != null) routine.setOnClickListener(v -> openRoutineFragment());

        View recent = view.findViewById(R.id.recent);
        if (recent != null) recent.setOnClickListener(v -> openRecentFragment());

        // 🔹 Notification History
        View notificationIcon = view.findViewById(R.id.notificationIcon);
        View notificationBadge = view.findViewById(R.id.notificationBadge);
        if (notificationIcon != null) {
            notificationIcon.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), NotificationsActivity.class));
                if (notificationBadge != null) notificationBadge.setVisibility(View.GONE);
            });
        }
        updateNotificationBadge(view);

        // 🔹 Social media handling
        setupSocialMedia(view);

        // 🔹 Bottom Navigation Handling
        setupBottomNavigation(view);

        // 🔹 Hero Section Tap
        View header = view.findViewById(R.id.headerContainer);
        if (header != null) {
            header.setOnClickListener(v -> {
                if (currentHero != null) openGenericDetail(currentHero);
            });
        }

        setupParallaxEffect(view);
        setupQuickActionAnimations(view);

        return view;
    }

    private void setupParallaxEffect(View root) {
        View scrollView = root.findViewById(R.id.scrollView);
        View heroImage = root.findViewById(R.id.headerBackgroundImage);
        if (scrollView instanceof androidx.core.widget.NestedScrollView && heroImage != null) {
            ((androidx.core.widget.NestedScrollView) scrollView).setOnScrollChangeListener(
                (androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    heroImage.setTranslationY(scrollY * 0.5f);
                }
            );
        }
    }

    private void setupQuickActionAnimations(View root) {
        int[] ids = {R.id.favourite, R.id.ask_bhava, R.id.routine, R.id.recent};
        for (int id : ids) {
            View v = root.findViewById(id);
            if (v != null) {
                v.setOnTouchListener((view, event) -> {
                    switch (event.getAction()) {
                        case android.view.MotionEvent.ACTION_DOWN:
                            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(150).start();
                            break;
                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:
                            view.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                            break;
                    }
                    return false;
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotificationBadge(getView());
    }

    private void updateNotificationBadge(View root) {
        if (root == null || getContext() == null) return;
        View notificationBadge = root.findViewById(R.id.notificationBadge);
        if (notificationBadge != null) {
            com.example.bhava.network.NotificationHelper helper = new com.example.bhava.network.NotificationHelper(getContext());
            notificationBadge.setVisibility(helper.hasUnread() ? View.VISIBLE : View.GONE);
        }
    }

    private interface DynamicNavigation {
        void navigate(ChallengeItem challenge);
    }

    private ChallengeAdapter initDynamicSection(View parent, int rvId, DynamicNavigation nav) {
        return initDynamicSection(parent, rvId, R.layout.item_challenge, nav);
    }

    private ChallengeAdapter initDynamicSection(View parent, int rvId, int layoutId, DynamicNavigation nav) {
        RecyclerView rv = parent.findViewById(rvId);
        if (rv != null) {
            ChallengeAdapter adapter = new ChallengeAdapter(layoutId, nav::navigate);
            rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setAdapter(adapter);
            rv.setNestedScrollingEnabled(false); // 🔹 Better scrolling inside ScrollView
            return adapter;
        }
        return null;
    }

    private void loadFromCache(View view) {
        ChallengesResponse cached = cacheManager.get("all_challenges", ChallengesResponse.class);
        if (cached != null && cached.getData() != null) {
            Log.d("HomeFragment", "Loading challenges from local cache");
            distributeData(cached.getData(), adapters);
        } else {
            // No cache available, show progress bar for initial fetch
            if (homeProgressBar != null) homeProgressBar.setVisibility(View.VISIBLE);
        }

        // Load inspirations from cache
        InspirationsListResponse cachedInspirations = cacheManager.get("daily_inspirations", InspirationsListResponse.class);
        if (cachedInspirations != null && cachedInspirations.getData() != null && inspirationAdapter != null) {
            inspirationAdapter.setInspirations(cachedInspirations.getData());
        }

        // Load hero from cache
        ChallengeItem cachedHero = cacheManager.get("home_hero", ChallengeItem.class);
        if (cachedHero != null) {
            bindHeroData(view != null ? view : getView(), cachedHero);
        }
    }

    private void fetchAllData(boolean isManualRefresh) {
        if (getContext() == null) return;

        // Fetch Challenges
        ApiClient.getService(getContext()).getChallenges().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengesResponse> call, @NonNull Response<ChallengesResponse> response) {
                if (isAdded()) {
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    if (homeProgressBar != null) homeProgressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<ChallengeItem> all = response.body().getData();
                        cacheManager.save("all_challenges", response.body());
                        distributeData(all, adapters);
                    } else if (!isManualRefresh) {
                        Log.e("HomeFragment", "API Error: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengesResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    if (homeProgressBar != null) homeProgressBar.setVisibility(View.GONE);
                    Log.e("HomeFragment", "Network failure", t);
                }
            }
        });

        // Fetch Inspirations
        fetchInspirations();

        // Fetch Hero
        fetchHeroChallenge();
    }

    private void fetchHeroChallenge() {
        if (getContext() == null) return;
        Log.d("HomeFragment", "Fetching hero challenge...");
        ApiClient.getService(getContext()).getHeroChallenge().enqueue(new Callback<ChallengesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengesResponse> call, @NonNull Response<ChallengesResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                        ChallengeItem hero = response.body().getData().get(0);
                        Log.d("HomeFragment", "Hero found: " + hero.getTitle());
                        cacheManager.save("home_hero", hero);
                        bindHeroData(getView(), hero);
                    } else {
                        Log.d("HomeFragment", "No hero challenge returned from API");
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ChallengesResponse> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Failed to fetch hero from network", t);
            }
        });
    }

    private void bindHeroData(View root, ChallengeItem hero) {
        if (root == null || hero == null) return;
        this.currentHero = hero;
        Log.d("HomeFragment", "Binding hero data: " + hero.getTitle());

        ImageView ivHero = root.findViewById(R.id.headerBackgroundImage);
        TextView tvTitle = root.findViewById(R.id.challengeTitle);
        TextView tvSession = root.findViewById(R.id.sessionInfo);
        
        // Dynamic Fields
        if (tvTitle != null) tvTitle.setText(hero.getTitle());
        if (tvSession != null) tvSession.setText(hero.getFullSubtitle());

        if (ivHero != null) {
            String imageUrl = hero.getImage();
            if (imageUrl != null && !imageUrl.startsWith("http") && !imageUrl.isEmpty()) {
                imageUrl = ApiClient.BASE_URL + (imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl);
            }
            Log.d("HomeFragment", "Loading hero image: " + imageUrl);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.challenge_21days)
                    .error(R.drawable.challenge_21days)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivHero);
        }

        // 🔹 UI Cleanup: Hide hardcoded static elements for a dynamic look
        View progressContainer = root.findViewById(R.id.progressFill); 
        if (progressContainer != null) {
            // If it's a featured hero, we might want to hide the progress bar to keep it clean
            View progressText = root.findViewById(R.id.progressText);
            View progressPercentage = root.findViewById(R.id.progressPercentage);
            if (progressText != null) progressText.setVisibility(View.GONE);
            if (progressPercentage != null) progressPercentage.setVisibility(View.GONE);
            
            // Just to be sure, find the parent linear layout and hide it too
            View progressRow = (View) progressText.getParent();
            if (progressRow != null) progressRow.setVisibility(View.GONE);
            
            View progressFillParent = (View) progressContainer.getParent();
            if (progressFillParent != null) progressFillParent.setVisibility(View.GONE);
        }

        View carouselIndicators = root.findViewById(R.id.carouselIndicators);
        if (carouselIndicators != null) carouselIndicators.setVisibility(View.GONE);
    }

    private void fetchInspirations() {
        if (getContext() == null) return;

        ApiClient.getService(getContext()).getLatestInspirations().enqueue(new Callback<InspirationsListResponse>() {
            @Override
            public void onResponse(@NonNull Call<InspirationsListResponse> call, @NonNull Response<InspirationsListResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<InspirationModel> data = response.body().getData();
                    cacheManager.save("daily_inspirations", response.body());
                    if (inspirationAdapter != null) {
                        inspirationAdapter.setInspirations(data);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<InspirationsListResponse> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Failed to fetch inspirations", t);
            }
        });
    }

    private void distributeData(List<ChallengeItem> all, ChallengeAdapter... adapters) {
        if (all == null) {
            Log.e("HomeFragment", "distributeData: ALL list is null");
            return;
        }
        if (adapters == null) {
            Log.e("HomeFragment", "distributeData: ADAPTERS list is null");
            return;
        }

        Log.d("HomeFragment", "distributeData: total items from API: " + all.size());

        // Order: Challenges, Morning, Daily, Learning, Wisdom, Teachings, Community
        String[] categories = {"Active Challenges", "Morning Routine", "Daily Practise", "Learning Path", "Timeless Wisdom", "Latest Teachings", "Community Campaign"};

        ChallengeItem firstActiveChallenge = null;
        ChallengeItem foundHero = null;

        for (int i = 0; i < adapters.length && i < categories.length; i++) {
            ChallengeAdapter adapter = adapters[i];
            if (adapter == null) {
                Log.w("HomeFragment", "distributeData: Adapter at index " + i + " is null");
                continue;
            }

            String category = categories[i];
            List<ChallengeItem> filtered = new ArrayList<>();
            for (ChallengeItem item : all) {
                if (item.getCategory() != null && category.equalsIgnoreCase(item.getCategory())) {
                    filtered.add(item);
                    // Check for hero flag in the main list as a backup
                    if (item.isHero()) foundHero = item;
                }
            }
            
            // Capture the first Active Challenge for the hero section update
            if (category.equalsIgnoreCase("Active Challenges") && !filtered.isEmpty()) {
                firstActiveChallenge = filtered.get(0);
            }

            Log.d("HomeFragment", "distributeData: Found " + filtered.size() + " items for " + category);
            adapter.setChallenges(filtered);
        }

        // 🔹 Update Hero Section with the most relevant content
        // Priority: 1. Admin-selected hero (foundHero) | 2. First Active Challenge (fallback)
        if (foundHero != null) {
            Log.d("HomeFragment", "distributeData: Setting Admin-selected HERO");
            bindHeroData(getView(), foundHero);
        } else if (firstActiveChallenge != null) {
            Log.d("HomeFragment", "distributeData: No specific hero set, falling back to FIRST Active Challenge");
            bindHeroData(getView(), firstActiveChallenge);
        }

        if (all.isEmpty()) {
            Toast.makeText(getContext(), "No challenges found in database", Toast.LENGTH_LONG).show();
        }
    }

    private void setupSocialMedia(View view) {
        View insta = view.findViewById(R.id.instagram);
        if (insta != null) insta.setOnClickListener(v -> openSocialMedia("instagram://user?username=thespiritualcompany108", "https://www.instagram.com/thespiritualcompany108"));

        View fb = view.findViewById(R.id.facebook);
        if (fb != null) fb.setOnClickListener(v -> openSocialMedia("fb://page/profile.php?id=61583433762092", "https://www.facebook.com/profile.php?id=61583433762092"));

        View yt = view.findViewById(R.id.youtube);
        if (yt != null) yt.setOnClickListener(v -> openSocialMedia("vnd.youtube://@TheSpiritualCompany", "https://www.youtube.com/@TheSpiritualCompany"));
    }

    private void setupBottomNavigation(View view) {
        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_me) openNavbarMeFragment();
                else if (id == R.id.nav_divine_store) openNavbarHomeStoreFragment();
                else if (id == R.id.nav_bhava_ai) openNavbarBhavaAiFragment();
                else if (id == R.id.nav_home) { /* Already here */ }
                return true;
            });
        }
    }

    private void openSocialMedia(String appUrl, String webUrl) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
        }
    }

    private void openGenericDetail(ChallengeItem challenge) {
        if (challenge == null) {
            Toast.makeText(getContext(), "Unable to open challenge details right now.", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = challenge.getId();
        String title = challenge.getTitle();
        if (id == null || id.trim().isEmpty()) {
            Log.e("HomeFragment", "openGenericDetail: challengeId is missing for title=" + title);
            Toast.makeText(getContext(), "Unable to open challenge details right now.", Toast.LENGTH_SHORT).show();
            return; // Prevent navigation if ID is missing
        }

        GenericDetailFragment fragment = new GenericDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("challengeId", id);
        bundle.putString("title", title);
        bundle.putString("subtitle", challenge.getFullSubtitle());
        bundle.putString("imageUrl", challenge.getImage());
        bundle.putString("listeningCount", challenge.getJoinedCount());
        SessionItem firstSession = challenge.getSessions().isEmpty() ? null : challenge.getSessions().get(0);
        bundle.putString("audioUrl", firstSession != null ? firstSession.getAudioUrl() : null);
        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    private void openNavbarHomeStoreFragment() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bhava-etxy.vercel.app/products"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to open store", Toast.LENGTH_SHORT).show();
        }
    }
    private void openRecentFragment() { replaceFragment(new RecentFragment()); }
    private void openRoutineFragment() { replaceFragment(new RoutineFragment()); }
    private void openFavouriteFragment() { replaceFragment(new Favourite_Fragment()); }
    private void openNavbarBhavaAiFragment() { replaceFragment(new Bhava_Ai_Fragment()); }
    private void openNavbarMeFragment() { replaceFragment(new Navbar_Me_Fragment()); }

    private void replaceFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }
}