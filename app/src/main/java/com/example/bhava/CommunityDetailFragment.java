package com.example.bhava;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bhava.model.CommunityDetailResponse;
import com.example.bhava.model.CommunityModel;
import com.example.bhava.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityDetailFragment extends Fragment {

    private static final String ARG_ID = "community_id";
    private String communityId;
    private ImageView ivCover;
    private TextView tvTitle, tvDesc;
    private LinearLayout llDynamicContent;
    private View btnShare;
    private CommunityModel currentCommunity;

    public static CommunityDetailFragment newInstance(String id) {
        CommunityDetailFragment fragment = new CommunityDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            communityId = getArguments().getString(ARG_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_detail, container, false);
        ivCover = view.findViewById(R.id.ivCoverImage);
        tvTitle = view.findViewById(R.id.tvDetailTitle);
        tvDesc = view.findViewById(R.id.tvDetailDesc);
        llDynamicContent = view.findViewById(R.id.llDynamicContent);
        btnShare = view.findViewById(R.id.btnShare);

        view.findViewById(R.id.toolbar).setOnClickListener(v -> getActivity().onBackPressed());

        btnShare.setOnClickListener(v -> shareCommunity());

        if (communityId != null) fetchDetail();
        return view;
    }

    private void fetchDetail() {
        ApiClient.getService(requireContext()).getCommunityById(communityId).enqueue(new Callback<CommunityDetailResponse>() {
            @Override
            public void onResponse(Call<CommunityDetailResponse> call, Response<CommunityDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentCommunity = response.body().data;
                    renderCommunity();
                } else {
                    Toast.makeText(getContext(), "Error loading details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommunityDetailResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderCommunity() {
        if (currentCommunity == null) return;
        tvTitle.setText(currentCommunity.name);
        tvDesc.setText(currentCommunity.description);

        if (currentCommunity.coverImage != null && !currentCommunity.coverImage.isEmpty()) {
            Glide.with(this).load(currentCommunity.coverImage).into(ivCover);
        }

        llDynamicContent.removeAllViews();
        if (currentCommunity.contentBlocks != null) {
            for (CommunityModel.CommunityBlockModel block : currentCommunity.contentBlocks) {
                View blockView = createBlockView(block);
                if (blockView != null) {
                    llDynamicContent.addView(blockView);
                }
            }
        }
    }

    private View createBlockView(CommunityModel.CommunityBlockModel block) {
        if ("text".equals(block.type)) {
            TextView tv = new TextView(getContext());
            tv.setText(block.value);
            tv.setTextColor(getResources().getColor(R.color.text_espresso));
            tv.setTextSize(16);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 24);
            tv.setLayoutParams(lp);
            return tv;
        } else if ("image".equals(block.type)) {
            ImageView iv = new ImageView(getContext());
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
            lp.setMargins(0, 0, 0, 24);
            iv.setLayoutParams(lp);
            Glide.with(this).load(block.value).into(iv);
            return iv;
        } else if ("video".equals(block.type)) {
             // Placeholder for video block - can be replaced with ExoPlayer later
            TextView tv = new TextView(getContext());
            tv.setText("\uD83D\uDCF9 [Video Content]\n" + block.value);
            tv.setPadding(16, 16, 16, 16);
            tv.setBackgroundColor(0x11000000);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 24);
            tv.setLayoutParams(lp);
            return tv;
        }
        return null;
    }

    private void shareCommunity() {
        if (currentCommunity == null) return;
        String shareMsg = "Join our Spiritual Sangha on Bhava App: " + currentCommunity.name + 
                "\n\nUse my invite link to join: http://bhava.app/community/" + currentCommunity.shareLink;
        
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Join " + currentCommunity.name);
        intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        startActivity(Intent.createChooser(intent, "Share Community"));
    }
}
