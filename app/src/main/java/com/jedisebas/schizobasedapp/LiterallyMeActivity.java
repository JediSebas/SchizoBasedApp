package com.jedisebas.schizobasedapp;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.Toast;

import com.jedisebas.schizobasedapp.databinding.ActivityLiterallyMeBinding;

import java.util.Random;

public class LiterallyMeActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;

    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };

    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (AUTO_HIDE) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
            default:
                break;
        }
        return false;
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityLiterallyMeBinding binding = ActivityLiterallyMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;

        binding.meTv.setOnClickListener(view -> Toast.makeText(this, "Ok, you got it", Toast.LENGTH_SHORT).show());

        final int[] imageResources = {R.mipmap.joker, R.mipmap.gosling, R.mipmap.durden, R.mipmap.bateman};
        final ImageView[] imageViews = new ImageView[imageResources.length];

        imageViews[0] = binding.batemanIv;
        imageViews[1] = binding.jokerIv;
        imageViews[2] = binding.goslingIv;
        imageViews[3] = binding.durdenIv;

        final boolean[] entry = new boolean[] {false, false, false, false};

        imageViews[0].setOnClickListener(view -> {
            if (entry[0]) {
                startActivity(new Intent(this, BatemanActivity.class));
                return;
            }

            setRandomImages(imageViews, imageResources);

            entry[0] = true;
            entry[1] = false;
            entry[2] = false;
            entry[3] = false;
        });

        imageViews[1].setOnClickListener(view -> {
            if (entry[1]) {
                startActivity(new Intent());
                return;
            }

            setRandomImages(imageViews, imageResources);

            entry[0] = false;
            entry[1] = true;
            entry[2] = false;
            entry[3] = false;
        });

        imageViews[2].setOnClickListener(view -> {
            if (entry[2]) {
                startActivity(new Intent(this, GoslingActivity.class));
                return;
            }

            setRandomImages(imageViews, imageResources);

            entry[0] = false;
            entry[1] = false;
            entry[2] = true;
            entry[3] = false;
        });

        imageViews[3].setOnClickListener(view -> {
            if (entry[3]) {
                startActivity(new Intent(this, DurdenActivity.class));
                return;
            }

            setRandomImages(imageViews, imageResources);

            entry[0] = false;
            entry[1] = false;
            entry[2] = false;
            entry[3] = true;
        });

        mContentView.setOnClickListener(view -> toggle());

        binding.dummyButton.setOnTouchListener(mDelayHideTouchListener);
        binding.dummyButton.setOnClickListener(view -> Toast.makeText(this, getString(R.string.paul), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void setRandomImages(final ImageView[] imageViews, final int[] imageResources) {
        for (final ImageView image: imageViews) {
            image.setImageResource(imageResources[new Random().nextInt(imageResources.length)]);
        }
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(final int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}