package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bhava.model.CommunityListResponse;
import com.example.bhava.model.CommunityModel;
import com.example.bhava.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityListFragment extends Fragment {

    private RecyclerView rvCommunities;
    private CommunityAdapter adapter;
    private List<CommunityModel> communityList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_list, container, false);
        rvCommunities = view.findViewById(R.id.rvCommunities);
        rvCommunities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityAdapter(communityList);
        rvCommunities.setAdapter(adapter);

        fetchCommunities();
        return view;
    }

    private void fetchCommunities() {
        ApiClient.getService(requireContext()).getCommunities().enqueue(new Callback<CommunityListResponse>() {
            @Override
            public void onResponse(Call<CommunityListResponse> call, Response<CommunityListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    communityList.clear();
                    communityList.addAll(response.body().data);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load communities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommunityListResponse> call, Throwable t) {
                Log.e("CommunityList", "Error fetching communities", t);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
        private List<CommunityModel> items;

        public CommunityAdapter(List<CommunityModel> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CommunityModel item = items.get(position);
            holder.tvName.setText(item.name);
            holder.tvDesc.setText(item.description);

            if (item.coverImage != null && !item.coverImage.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(item.coverImage)
                        .placeholder(R.drawable.placeholder_avatar)
                        .into(holder.ivCover);
            }

            holder.itemView.setOnClickListener(v -> {
                CommunityDetailFragment fragment = CommunityDetailFragment.newInstance(item.id);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDesc;
            ImageView ivCover;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvCommunityName);
                tvDesc = itemView.findViewById(R.id.tvCommunityDesc);
                ivCover = itemView.findViewById(R.id.ivCommunityCover);
            }
        }
    }
}
