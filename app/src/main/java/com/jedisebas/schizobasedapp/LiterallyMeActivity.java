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

        final ImageView bateman = binding.batemanIv;
        final ImageView joker = binding.jokerIv;
        final ImageView gosling = binding.goslingIv;
        final ImageView durden = binding.durdenIv;

        final boolean[] entry = new boolean[] {false, false, false, false};

        bateman.setOnClickListener(view -> {
            if (entry[0]) {
                startActivity(new Intent(this, BatemanActivity.class));
                return;
            }
            bateman.setImageResource(R.mipmap.joker);
            joker.setImageResource(R.mipmap.gosling);
            gosling.setImageResource(R.mipmap.durden);
            durden.setImageResource(R.mipmap.bateman);

            entry[0] = true;
            entry[1] = false;
            entry[2] = false;
            entry[3] = false;
        });

        joker.setOnClickListener(view -> {
            if (entry[1]) {
                startActivity(new Intent());
                return;
            }
            bateman.setImageResource(R.mipmap.gosling);
            joker.setImageResource(R.mipmap.durden);
            gosling.setImageResource(R.mipmap.joker);
            durden.setImageResource(R.mipmap.bateman);

            entry[0] = false;
            entry[1] = true;
            entry[2] = false;
            entry[3] = false;
        });

        gosling.setOnClickListener(view -> {
            if (entry[2]) {
                startActivity(new Intent());
                return;
            }
            bateman.setImageResource(R.mipmap.durden);
            joker.setImageResource(R.mipmap.bateman);
            gosling.setImageResource(R.mipmap.gosling);
            durden.setImageResource(R.mipmap.joker);

            entry[0] = false;
            entry[1] = false;
            entry[2] = true;
            entry[3] = false;
        });

        durden.setOnClickListener(view -> {
            if (entry[3]) {
                startActivity(new Intent());
                return;
            }
            bateman.setImageResource(R.mipmap.bateman);
            joker.setImageResource(R.mipmap.joker);
            gosling.setImageResource(R.mipmap.bateman);
            durden.setImageResource(R.mipmap.gosling);

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