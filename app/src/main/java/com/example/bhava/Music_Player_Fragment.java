package com.example.bhava;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.example.bhava.model.DownloadStatusResponse;
import com.example.bhava.model.FavoriteStatusResponse;
import com.example.bhava.network.ApiClient;
import com.example.bhava.model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Music_Player_Fragment extends Fragment {

    private static final String TAG = "MusicPlayer";

    // Data from arguments
    private String title;
    private String subtitle;
    private String imageUrl;
    private String listeningCount;
    private String audioUrl;
    private String challengeId;
    private ArrayList<String> queueTitles;
    private ArrayList<String> queueSubtitles;
    private ArrayList<String> queueAudioUrls;
    private ArrayList<String> queueDurations;
    private int currentIndex = 0;

    // MediaPlayer
    private MediaPlayer mediaPlayer;
    private Handler seekHandler;
    private Runnable seekRunnable;
    private boolean isPlaying = false;
    private boolean isPrepared = false;
    private boolean isShuffleEnabled = false;
    private boolean isRepeatEnabled = false;
    private boolean isFavourite = false;
    private boolean isDownloaded = false;

    // Views
    private ImageButton btnPlayPause;
    private SeekBar seekBar;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private SeekBar volumeSeekBar;
    private View queueItem1;
    private View queueItem2;
    private View queueItem3;
    private com.example.bhava.view.VisualizerView visualizer;

    public Music_Player_Fragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title         = getArguments().getString("title");
            subtitle      = getArguments().getString("subtitle");
            imageUrl      = getArguments().getString("imageUrl");
            listeningCount = getArguments().getString("listeningCount");
            audioUrl      = getArguments().getString("audioUrl");
            queueTitles = getArguments().getStringArrayList("queueTitles");
            queueSubtitles = getArguments().getStringArrayList("queueSubtitles");
            queueAudioUrls = getArguments().getStringArrayList("queueAudioUrls");
            queueDurations = getArguments().getStringArrayList("queueDurations");
            currentIndex = Math.max(0, getArguments().getInt("selectedIndex", 0));
            challengeId = getArguments().getString("challengeId");
        }
        seekHandler = new Handler(Looper.getMainLooper());
        ensureQueueData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music__player_, container, false);

        // ── Back button ─────────────────────────────────────────────
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        // ── Populate text & artwork ──────────────────────────────────
        bindTrackInfo(view);

        visualizer = view.findViewById(R.id.visualizer);

        // ── SeekBar & time labels ────────────────────────────────────
        seekBar       = view.findViewById(R.id.seekBar);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime   = view.findViewById(R.id.tvTotalTime);
        volumeSeekBar = view.findViewById(R.id.seekBarVolume);

        // ── Play/Pause button ────────────────────────────────────────
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        ImageButton btnPrevious = view.findViewById(R.id.btnPrevious);
        ImageButton btnNext = view.findViewById(R.id.btnNext);
        ImageButton btnShuffle = view.findViewById(R.id.btnShuffle);
        ImageButton btnRepeat = view.findViewById(R.id.btnRepeat);
        ImageButton btnFavourite = view.findViewById(R.id.btnFavourite);

        queueItem1 = view.findViewById(R.id.queueItem1);
        queueItem2 = view.findViewById(R.id.queueItem2);
        queueItem3 = view.findViewById(R.id.queueItem3);
        renderQueueRows();

        // Download container (it's a LinearLayout in XML)
        View btnDownloadContainer = view.findViewById(R.id.btnDownload);
        if (btnDownloadContainer != null) {
            btnDownloadContainer.setOnClickListener(v -> addToDownloadServer(view));
        }
        checkDownloadStatus(view);

        setClickToast(view, R.id.btnQueue, "Queue synced with current session");
        setClickToast(view, R.id.btnSleepTimer, "Sleep timer will be available soon");
        setClickToast(view, R.id.tvSeeAllQueue, "Showing first 3 upcoming tracks");
        View btnShare = view.findViewById(R.id.btnShare);
        if (btnShare != null) btnShare.setOnClickListener(v -> shareCurrentTrack());

        View btnRate = view.findViewById(R.id.btnRate);
        if (btnRate != null) {
            btnRate.setOnClickListener(v -> toggleFavoriteOnServer(btnFavourite));
        }

        if (btnFavourite != null) {
            btnFavourite.setOnClickListener(v -> toggleFavoriteOnServer(btnFavourite));
        }
        fetchFavoriteStatus(btnFavourite);

        if (btnShuffle != null) {
            btnShuffle.setOnClickListener(v -> {
                isShuffleEnabled = !isShuffleEnabled;
                btnShuffle.setAlpha(isShuffleEnabled ? 1.0f : 0.6f);
                Toast.makeText(getContext(), isShuffleEnabled ? "Shuffle enabled" : "Shuffle disabled", Toast.LENGTH_SHORT).show();
            });
            btnShuffle.setAlpha(0.6f);
        }

        if (btnRepeat != null) {
            btnRepeat.setOnClickListener(v -> {
                isRepeatEnabled = !isRepeatEnabled;
                btnRepeat.setAlpha(isRepeatEnabled ? 1.0f : 0.6f);
                Toast.makeText(getContext(), isRepeatEnabled ? "Repeat enabled" : "Repeat disabled", Toast.LENGTH_SHORT).show();
            });
            btnRepeat.setAlpha(0.6f);
        }

        if (btnPrevious != null) btnPrevious.setOnClickListener(v -> playPreviousTrack());
        if (btnNext != null) btnNext.setOnClickListener(v -> playNextTrack(false));

        prepareAndPlayCurrentTrack();

        // Wiring play/pause toggle
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> togglePlayPause());
        }

        // SeekBar drag
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                    if (fromUser && isPrepared && mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);
                        updateCurrentTimeLabel(progress);
                    }
                }
                @Override public void onStartTrackingTouch(SeekBar sb) {}
                @Override public void onStopTrackingTouch(SeekBar sb) {}
            });
        }

        if (volumeSeekBar != null) {
            volumeSeekBar.setProgress(85);
            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser || mediaPlayer == null) return;
                    float volume = Math.max(0f, Math.min(1f, progress / 100f));
                    mediaPlayer.setVolume(volume, volume);
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        return view;
    }

    // ────────────────────────────────────────────────────────────────
    // MediaPlayer setup
    // ────────────────────────────────────────────────────────────────
    private void initMediaPlayer(String url) {
        try {
            releaseMediaPlayer(false);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // Non-blocking prepare

            mediaPlayer.setOnPreparedListener(mp -> {
                if (!isAdded()) return;
                isPrepared = true;

                // Unlock play button
                if (btnPlayPause != null) btnPlayPause.setEnabled(true);

                // Setup seek bar max
                int duration = mp.getDuration();
                if (seekBar != null) seekBar.setMax(duration);
                updateTotalTimeLabel(duration);

                // Auto-play
                mp.start();
                isPlaying = true;
                if (btnPlayPause != null) btnPlayPause.setImageResource(R.drawable.ic_pause);
                if (visualizer != null) visualizer.setPlaying(true);
                startSeekBarUpdater();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (!isAdded()) return;
                if (isRepeatEnabled) {
                    mp.seekTo(0);
                    mp.start();
                    startSeekBarUpdater();
                    return;
                }
                playNextTrack(true);
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", extra: " + extra);
                if (isAdded()) {
                    Toast.makeText(getContext(), "Could not load audio. Check the URL.", Toast.LENGTH_LONG).show();
                }
                playNextTrack(true);
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "initMediaPlayer failed", e);
            if (isAdded()) {
                Toast.makeText(getContext(), "Failed to load audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null || !isPrepared) return;

        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            if (btnPlayPause != null) btnPlayPause.setImageResource(R.drawable.ic_play);
            if (visualizer != null) visualizer.setPlaying(false);
            stopSeekBarUpdater();
        } else {
            mediaPlayer.start();
            isPlaying = true;
            if (btnPlayPause != null) btnPlayPause.setImageResource(R.drawable.ic_pause);
            if (visualizer != null) visualizer.setPlaying(true);
            startSeekBarUpdater();
        }
    }

    private void ensureQueueData() {
        if (queueTitles == null) queueTitles = new ArrayList<>();
        if (queueSubtitles == null) queueSubtitles = new ArrayList<>();
        if (queueAudioUrls == null) queueAudioUrls = new ArrayList<>();
        if (queueDurations == null) queueDurations = new ArrayList<>();

        if (queueTitles.isEmpty()) queueTitles.add(title != null ? title : "Music Session");
        while (queueSubtitles.size() < queueTitles.size()) queueSubtitles.add(subtitle);
        while (queueAudioUrls.size() < queueTitles.size()) queueAudioUrls.add(audioUrl);
        while (queueDurations.size() < queueTitles.size()) queueDurations.add(null);

        if (currentIndex < 0 || currentIndex >= queueTitles.size()) currentIndex = 0;
    }

    private void bindTrackInfo(View root) {
        String currentTitle = getQueueValue(queueTitles, currentIndex, title != null ? title : "Music Session");
        String currentSubtitle = getQueueValue(queueSubtitles, currentIndex, subtitle);
        setTextSafe(root, R.id.tvTrackTitle, currentTitle);
        setTextSafe(root, R.id.tvMiniTitle, currentTitle);
        setTextSafe(root, R.id.tvTrackSubtitle, currentSubtitle);
        if (listeningCount != null) setTextSafe(root, R.id.tvListeningCount, listeningCount + " Listening");

        ImageView ivArtwork = root.findViewById(R.id.ivArtwork);
        if (ivArtwork != null) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.player_artwork_default).into(ivArtwork);
            } else {
                ivArtwork.setImageResource(R.drawable.player_artwork_default);
            }
        }
    }

    private void prepareAndPlayCurrentTrack() {
        String trackUrl = getQueueValue(queueAudioUrls, currentIndex, audioUrl);
        String duration = getQueueValue(queueDurations, currentIndex, null);
        if (duration != null && tvTotalTime != null && duration.contains(":")) {
            tvTotalTime.setText(duration);
        }

        if (btnPlayPause != null) {
            btnPlayPause.setImageResource(R.drawable.ic_play);
            btnPlayPause.setEnabled(false);
        }
        updateCurrentTimeLabel(0);
        if (seekBar != null) seekBar.setProgress(0);

        if (trackUrl == null || trackUrl.trim().isEmpty()) {
            Toast.makeText(getContext(), "Audio for this track is not available yet", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Track has no audio URL at index=" + currentIndex);
            return;
        }
        initMediaPlayer(trackUrl);
    }

    private void playNextTrack(boolean fromCompletion) {
        if (queueTitles.isEmpty()) return;
        int nextIndex;
        if (isShuffleEnabled && queueTitles.size() > 1) {
            Random random = new Random();
            do {
                nextIndex = random.nextInt(queueTitles.size());
            } while (nextIndex == currentIndex);
        } else {
            nextIndex = (currentIndex + 1) % queueTitles.size();
            if (!fromCompletion && nextIndex == 0 && queueTitles.size() == 1) {
                Toast.makeText(getContext(), "Only one track in queue", Toast.LENGTH_SHORT).show();
            }
        }
        switchToTrack(nextIndex);
    }

    private void playPreviousTrack() {
        if (queueTitles.isEmpty()) return;
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) prevIndex = queueTitles.size() - 1;
        switchToTrack(prevIndex);
    }

    private void switchToTrack(int targetIndex) {
        if (targetIndex < 0 || targetIndex >= queueTitles.size()) return;
        currentIndex = targetIndex;
        View root = getView();
        if (root != null) bindTrackInfo(root);
        renderQueueRows();
        prepareAndPlayCurrentTrack();
    }

    private void renderQueueRows() {
        bindQueueRow(queueItem1, 1);
        bindQueueRow(queueItem2, 2);
        bindQueueRow(queueItem3, 3);
    }

    private void bindQueueRow(View row, int offset) {
        if (row == null || queueTitles.isEmpty()) return;
        int index = (currentIndex + offset) % queueTitles.size();
        TextView tvTitle = row.findViewById(R.id.tvQueueTitle);
        TextView tvSub = row.findViewById(R.id.tvQueueSub);
        TextView tvDuration = row.findViewById(R.id.tvQueueDuration);
        View activeBar = row.findViewById(R.id.activeBar);
        View playingOverlay = row.findViewById(R.id.playingOverlay);

        if (tvTitle != null) tvTitle.setText(getQueueValue(queueTitles, index, "Session"));
        if (tvSub != null) tvSub.setText(getQueueValue(queueSubtitles, index, "Guided Session"));
        if (tvDuration != null) tvDuration.setText(getQueueValue(queueDurations, index, "--:--"));
        if (activeBar != null) activeBar.setVisibility(View.GONE);
        if (playingOverlay != null) playingOverlay.setVisibility(View.GONE);

        row.setOnClickListener(v -> switchToTrack(index));
    }

    private String getQueueValue(List<String> list, int index, String fallback) {
        if (list == null || index < 0 || index >= list.size()) return fallback;
        String value = list.get(index);
        return (value == null || value.trim().isEmpty()) ? fallback : value;
    }

    private void updateFavouriteState(ImageButton btnFavourite) {
        if (btnFavourite == null) return;
        btnFavourite.setImageResource(isFavourite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private void fetchFavoriteStatus(ImageButton btnFavourite) {
        if (challengeId == null || challengeId.isEmpty() || getContext() == null) return;

        ApiClient.getService(getContext()).checkFavorite(challengeId).enqueue(new Callback<FavoriteStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteStatusResponse> call, @NonNull Response<FavoriteStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavourite = response.body().isFavorite();
                    updateFavouriteState(btnFavourite);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteStatusResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch favorite status", t);
            }
        });
    }

    private void toggleFavoriteOnServer(ImageButton btnFavourite) {
        if (challengeId == null || challengeId.isEmpty() || getContext() == null) {
            Toast.makeText(getContext(), "Cannot favorite this track", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getService(getContext()).toggleFavorite(challengeId).enqueue(new Callback<FavoriteStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteStatusResponse> call, @NonNull Response<FavoriteStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavourite = response.body().isFavorite();
                    updateFavouriteState(btnFavourite);
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteStatusResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClickToast(View root, int id, String message) {
        View view = root.findViewById(id);
        if (view != null) view.setOnClickListener(v -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void shareCurrentTrack() {
        if (!isAdded()) return;
        String trackTitle = getQueueValue(queueTitles, currentIndex, title != null ? title : "Bhava Session");
        String trackUrl = getQueueValue(queueAudioUrls, currentIndex, audioUrl);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Bhava Session");
        share.putExtra(Intent.EXTRA_TEXT, trackUrl != null ? trackTitle + "\n" + trackUrl : trackTitle);
        startActivity(Intent.createChooser(share, "Share session"));
    }

    // ────────────────────────────────────────────────────────────────
    // SeekBar auto-updater
    // ────────────────────────────────────────────────────────────────
    private void startSeekBarUpdater() {
        seekRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPrepared && isPlaying) {
                    int pos = mediaPlayer.getCurrentPosition();
                    if (seekBar != null) seekBar.setProgress(pos);
                    updateCurrentTimeLabel(pos);
                    seekHandler.postDelayed(this, 500);
                }
            }
        };
        seekHandler.post(seekRunnable);
    }

    private void stopSeekBarUpdater() {
        if (seekRunnable != null) seekHandler.removeCallbacks(seekRunnable);
    }

    // ────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────
    private void setTextSafe(View root, int viewId, String text) {
        if (text == null) return;
        TextView tv = root.findViewById(viewId);
        if (tv != null) tv.setText(text);
    }

    private void updateCurrentTimeLabel(int millis) {
        if (tvCurrentTime != null) tvCurrentTime.setText(formatTime(millis));
    }

    private void updateTotalTimeLabel(int millis) {
        if (tvTotalTime != null) tvTotalTime.setText(formatTime(millis));
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    private void releaseMediaPlayer(boolean stopPlayback) {
        stopSeekBarUpdater();
        if (mediaPlayer != null) {
            try {
                if (stopPlayback && mediaPlayer.isPlaying()) mediaPlayer.stop();
            } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPrepared = false;
        isPlaying = false;
    }

    // ────────────────────────────────────────────────────────────────
    // Lifecycle — release resources
    // ────────────────────────────────────────────────────────────────
    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            if (btnPlayPause != null) btnPlayPause.setImageResource(R.drawable.ic_play);
            stopSeekBarUpdater();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseMediaPlayer(true);
    }

    private void checkDownloadStatus(View root) {
        if (challengeId == null || getContext() == null || root == null) return;

        ApiClient.getService(getContext()).checkDownload(challengeId).enqueue(new Callback<DownloadStatusResponse>() {
            @Override
            public void onResponse(Call<DownloadStatusResponse> call, Response<DownloadStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isDownloaded = response.body().isDownloaded();
                    updateDownloadIcon(root);
                }
            }

            @Override
            public void onFailure(Call<DownloadStatusResponse> call, Throwable t) {
                Log.e(TAG, "Error checking download status", t);
            }
        });
    }

    private void addToDownloadServer(View root) {
        if (challengeId == null || getContext() == null) {
            Toast.makeText(getContext(), "Unable to add to downloads", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDownloaded) {
            ApiClient.getService(getContext()).removeDownload(challengeId).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        isDownloaded = false;
                        updateDownloadIcon(root);
                        Toast.makeText(getContext(), "Removed from downloads", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ApiClient.getService(getContext()).addDownload(challengeId).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        isDownloaded = true;
                        updateDownloadIcon(root);
                        Toast.makeText(getContext(), "Added to downloads on server", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to add to downloads", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateDownloadIcon(View root) {
        if (root == null) return;
        ImageView ivIcon = root.findViewById(R.id.ivDownloadIcon);
        if (ivIcon == null) return;
        if (isDownloaded) {
            ivIcon.setImageResource(R.drawable.ic_check_circle);
        } else {
            ivIcon.setImageResource(R.drawable.ic_download);
        }
    }
}