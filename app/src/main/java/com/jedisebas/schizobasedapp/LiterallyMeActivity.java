package com.jedisebas.schizobasedapp;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Arrays;
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

        final MyPersonality bateman = new MyPersonality(R.mipmap.bateman, binding.batemanIv, BatemanActivity.class, this);
        final MyPersonality joker = new MyPersonality(R.mipmap.joker, binding.jokerIv, BatemanActivity.class, this);
        final MyPersonality gosling = new MyPersonality(R.mipmap.gosling, binding.goslingIv, GoslingActivity.class, this);
        final MyPersonality durden = new MyPersonality(R.mipmap.durden, binding.durdenIv, DurdenActivity.class, this);

        MyAllPersonalities.initialize(bateman, joker, gosling, durden);

        for (final MyPersonality personality : MyAllPersonalities.personalities) {
            personality.onClicked();
        }

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
    
    private class MyPersonality {
        private final int imageResource;
        private final ImageView imageView;
        private final Class<?> aClass;
        private final Context context;
        private boolean entry;
        
        MyPersonality(final int imageResource, final ImageView imageView,
                      final Class<?> aClass, final Context context) {
            this.imageResource = imageResource;
            this.imageView = imageView;
            this.aClass = aClass;
            this.context = context;
            this.entry = false;
        }

        public void onClicked() {
            imageView.setOnClickListener(view -> {
                if (entry) {
                    startActivity(new Intent(context, aClass));
                    return;
                }

                setRandomImages(MyAllPersonalities.getImageViews(), MyAllPersonalities.getImageResources());
                MyAllPersonalities.setEntriesFalse();
                entry = true;
            });
        }

        private void setRandomImages(final ArrayList<ImageView> imageViews, final ArrayList<Integer> imageResources) {
            for (final ImageView image: imageViews) {
                image.setImageResource(imageResources.get(new Random().nextInt(imageResources.size())));
            }
        }

        public int getImageResource() {
            return imageResource;
        }
        
        public ImageView getImageView() {
            return imageView;
        }

        public void setEntry(final boolean entry) {
            this.entry = entry;
        }
    }
    
    private static class MyAllPersonalities {

        public static ArrayList<MyPersonality> personalities;

        public static void initialize(final MyPersonality ... myPersonalities) {
            personalities = new ArrayList<>(Arrays.asList(myPersonalities));
        }

        public static ArrayList<Integer> getImageResources() {
            final ArrayList<Integer> resources = new ArrayList<>();
            for (final MyPersonality personality : personalities) {
                resources.add(personality.getImageResource());
            }
            return resources;
        }

        public static ArrayList<ImageView> getImageViews() {
            final ArrayList<ImageView> views = new ArrayList<>();
            for (final MyPersonality personality : personalities) {
                views.add(personality.getImageView());
            }
            return views;
        }

        public static void setEntriesFalse() {
            for (final MyPersonality personality : personalities) {
                personality.setEntry(false);
            }
        }
    }
}