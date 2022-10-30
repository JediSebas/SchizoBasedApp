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
import android.widget.Button;
import android.widget.Toast;

import com.jedisebas.schizobasedapp.databinding.ActivityFullscreenBinding;

public class FullscreenActivity extends AppCompatActivity {

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

    private Button[] buttonsOne;
    private Button[] buttonsTwo;
    private String code = "";
    private final String correctCode = "2971";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityFullscreenBinding binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button fog1 = findViewById(R.id.fogBtn1);
        final Button fog2 = findViewById(R.id.fogBtn2);
        final Button fog3 = findViewById(R.id.fogBtn3);
        final Button fog4 = findViewById(R.id.fogBtn4);
        final Button fog5 = findViewById(R.id.fogBtn5);
        final Button fog6 = findViewById(R.id.fogBtn6);
        final Button fog7 = findViewById(R.id.fogBtn7);
        final Button fog8 = findViewById(R.id.fogBtn8);
        final Button fog9 = findViewById(R.id.fogBtn9);

        buttonsOne = new Button[] {fog1, fog3, fog7, fog9};
        buttonsTwo = new Button[] {fog2, fog4, fog6, fog8};

        fog1.setOnClickListener(view -> {
            code += "1";

            if (code.length() != 4) {
                resetCode();
                return;
            }

            if (checkCodeFour()) {
                return;
            }

            fog5.setVisibility(View.VISIBLE);
        });

        fog2.setOnClickListener(view -> {
            code += "2";

            if (code.length() != 1) {
                resetCode();
                return;
            }

            checkCodeOne();
        });

        fog3.setOnClickListener(view -> resetCode());

        fog4.setOnClickListener(view -> resetCode());

        fog5.setOnClickListener(view -> startActivity(new Intent(this, AntiBabylonActivity.class)));

        fog6.setOnClickListener(view -> resetCode());

        fog7.setOnClickListener(view -> {
            code += "7";

            if (code.length() != 3) {
                resetCode();
                return;
            }

            checkCodeThree();
        });

        fog8.setOnClickListener(view -> resetCode());

        fog9.setOnClickListener(view -> {
            code += "9";

            if (code.length() != 2) {
                resetCode();
                return;
            }

            checkCodeTwo();
        });

        final ShowHideFog showHideFog = new ShowHideFog();
        new Thread(showHideFog::showOne).start();
        new Thread(showHideFog::hideOne).start();

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;

        mContentView.setOnClickListener(view -> toggle());

        binding.dummyBtn.setOnTouchListener(mDelayHideTouchListener);
        binding.dummyBtn.setOnClickListener(view -> Toast.makeText(this, code, Toast.LENGTH_SHORT).show());
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

    private void resetCode() {
        code = "";
    }

    private boolean checkCodeOne() {
        if (code.charAt(0) != correctCode.charAt(0)) {
            resetCode();
            return true;
        }

        return false;
    }

    private boolean checkCodeTwo() {
        if (!checkCodeOne()) {
            if (code.charAt(1) != correctCode.charAt(1)) {
                resetCode();
                return true;
            }
        }

        return false;
    }

    private boolean checkCodeThree() {
        if (!checkCodeTwo()) {
            if (code.charAt(2) != correctCode.charAt(2)) {
                resetCode();
                return true;
            }
        }

        return false;
    }

    private boolean checkCodeFour() {
        if (!checkCodeThree()) {
            if (code.charAt(3) != correctCode.charAt(3)) {
                resetCode();
                return true;
            }
        }

        return false;
    }

    private class ShowHideFog {

        synchronized void showOne() {
            while (true) {

                new Thread(() -> runOnUiThread(() -> {
                    for (Button b: buttonsOne) {
                        b.setVisibility(View.VISIBLE);
                    }
                })).start();

                new Thread(() -> runOnUiThread(() -> {
                    for (Button b: buttonsTwo) {
                        b.setVisibility(View.INVISIBLE);
                    }
                })).start();

                notify();
                try {
                    wait();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        synchronized void hideOne() {
            while (true) {

                new Thread(() -> runOnUiThread(() -> {
                    for (Button b: buttonsOne) {
                        b.setVisibility(View.INVISIBLE);
                    }
                })).start();

                new Thread(() -> runOnUiThread(() -> {
                    for (Button b: buttonsTwo) {
                        b.setVisibility(View.VISIBLE);
                    }
                })).start();

                notify();
                try {
                    wait();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}