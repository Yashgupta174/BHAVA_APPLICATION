package com.example.bhava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bhava.adapter.ChatAdapter;
import com.example.bhava.model.AiRequest;
import com.example.bhava.model.AiResponse;
import com.example.bhava.model.Message;
import com.example.bhava.network.ApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bhava_Ai_Fragment extends Fragment {

    private RecyclerView rvChat;
    private ChatAdapter chatAdapter;
    private EditText etInput;
    private ImageButton btnSend;

    public Bhava_Ai_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bhava__ai_, container, false);

        rvChat = view.findViewById(R.id.rvChat);
        etInput = view.findViewById(R.id.etInput);
        btnSend = view.findViewById(R.id.btnSend);

        // Setup RecyclerView
        chatAdapter = new ChatAdapter();
        rvChat.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

        // Add intro message
        addMessage("🙏 Namaste! I am your Bhava AI spiritual guide. How can I assist your journey today?", Message.TYPE_AI);

        btnSend.setOnClickListener(v -> sendMessage());

        // Quick action chips
        setupQuickActions(view);

        // Back button
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().onBackPressed();
            });
        }

        return view;
    }

    private void setupQuickActions(View view) {
        View p1 = view.findViewById(R.id.pillMantras);
        if (p1 != null) p1.setOnClickListener(v -> sendMessageToAi("Tell me about daily mantras."));
        
        View p2 = view.findViewById(R.id.pillFestivals);
        if (p2 != null) p2.setOnClickListener(v -> sendMessageToAi("What spiritual festivals are coming up?"));
        
        View p3 = view.findViewById(R.id.pillScriptures);
        if (p3 != null) p3.setOnClickListener(v -> sendMessageToAi("Share a verse from the Bhagavad Gita."));
        
        View p4 = view.findViewById(R.id.pillMeditation);
        if (p4 != null) p4.setOnClickListener(v -> sendMessageToAi("How do I start a meditation practice?"));
    }

    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (!text.isEmpty()) {
            sendMessageToAi(text);
            etInput.setText("");
        }
    }

    private void sendMessageToAi(String text) {
        addMessage(text, Message.TYPE_USER);
        
        // Call backend
        AiRequest request = new AiRequest(text);
        if (getContext() != null) {
            ApiClient.getService(getContext()).aiChat(request).enqueue(new Callback<AiResponse>() {
                @Override
                public void onResponse(Call<AiResponse> call, Response<AiResponse> response) {
                    if (isAdded() && response.isSuccessful() && response.body() != null && response.body().success) {
                        addMessage(response.body().data.reply, Message.TYPE_AI);
                    } else if (isAdded()) {
                        String errorMsg = "The spiritual connection is weak. Please try again.";
                        if (response.errorBody() != null) {
                            try {
                                Log.e("BhavaAI", "Error body: " + response.errorBody().string());
                            } catch (Exception ignored) {}
                        }
                        Log.e("BhavaAI", "Response code: " + response.code() + " | Success flag: " + (response.body() != null ? response.body().success : "null body"));
                        addMessage(errorMsg, Message.TYPE_AI);
                    }
                }

                @Override
                public void onFailure(Call<AiResponse> call, Throwable t) {
                    if (isAdded()) {
                        Log.e("BhavaAI", "AI call failed critically", t);
                        addMessage("I am currently in deep meditation (Offline). Please check your internet.", Message.TYPE_AI);
                    }
                }
            });
        }
    }

    private void addMessage(String content, int type) {
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        chatAdapter.addMessage(new Message(content, type, time));
        rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }
}