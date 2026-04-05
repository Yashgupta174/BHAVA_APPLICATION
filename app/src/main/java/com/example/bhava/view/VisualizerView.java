package com.example.bhava.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

/**
 * A premium-looking animated visualizer for the Bhava Music Player.
 * Uses smooth bar animations to create a sense of rhythm and flow.
 */
public class VisualizerView extends View {

    private final int BAR_COUNT = 32;
    private final float[] heights = new float[BAR_COUNT];
    private final float[] targetHeights = new float[BAR_COUNT];
    private Paint paint;
    private Random random = new Random();
    private boolean isPlaying = false;

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < BAR_COUNT; i++) {
            heights[i] = 10f;
            targetHeights[i] = 10f;
        }
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
        if (playing) invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float barWidth = (width / BAR_COUNT) * 0.7f;
        float gap = (width / BAR_COUNT) * 0.3f;

        // Gradient color for bars (Gold to Maroon)
        if (paint.getShader() == null) {
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    0xFFFFD700, 0xFF800000, Shader.TileMode.CLAMP));
        }

        for (int i = 0; i < BAR_COUNT; i++) {
            if (isPlaying) {
                // Smoothly interpolate toward target heights
                if (Math.abs(heights[i] - targetHeights[i]) < 5) {
                    targetHeights[i] = random.nextInt((int) (height * 0.8f)) + 10;
                }
                heights[i] += (targetHeights[i] - heights[i]) * 0.15f;
            } else {
                // Return to baseline when paused
                heights[i] += (10f - heights[i]) * 0.1f;
            }

            float left = i * (barWidth + gap);
            float top = height - heights[i];
            float right = left + barWidth;
            float bottom = height;

            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, barWidth / 2, barWidth / 2, paint);
        }

        if (isPlaying || getPausedActive()) {
            invalidate();
        }
    }

    private boolean getPausedActive() {
        for (float h : heights) if (h > 11f) return true;
        return false;
    }
}
