package com.jedisebas.schizobasedapp;

import android.annotation.SuppressLint;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import com.jedisebas.schizobasedapp.databinding.ActivityDurdenBinding;

public class DurdenActivity extends AppCompatActivity {

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

    private TextView nitricOxideTv;
    private TextView sodiumHydroxideTv;
    private TextView nitroglycerinTv;
    private TextView nitricAcidVTv;
    private TextView sodiumNitrateVTv;
    private TextView nitricAcidIIITv;
    private TextView sodiumNitrateIIITv;
    private TextView tntTv;
    private TextView nitricOxide2Tv;

    private TextView substrateThreeTv;
    private TextView productTv;

    private View secondPlusView;
    private View arrowRightView;

    final boolean[] mainGot = new boolean[] {false, false};

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityDurdenBinding binding = ActivityDurdenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;

        final TextView glycerineTv = findViewById(R.id.glycerineTv);
        final TextView nitroTv = findViewById(R.id.nitroTv);
        final TextView oxygenTv = findViewById(R.id.oxygenTv);
        final TextView waterTv = findViewById(R.id.waterTv);
        final TextView sawdustTv = findViewById(R.id.sawdustTv);
        final TextView sodiumTv = findViewById(R.id.sodiumTv);
        nitricOxideTv = findViewById(R.id.nitricOxideTv);
        sodiumHydroxideTv = findViewById(R.id.sodiumHydroxideTv);
        nitroglycerinTv = findViewById(R.id.nitroglycerinTv);
        nitricAcidVTv = findViewById(R.id.nitricAcidVTv);
        sodiumNitrateVTv = findViewById(R.id.sodiumNitrateVTv);
        nitricAcidIIITv = findViewById(R.id.nitricAcidIIITv);
        sodiumNitrateIIITv = findViewById(R.id.sodiumNitrateIIITv);
        tntTv = findViewById(R.id.tntTv);
        nitricOxide2Tv = findViewById(R.id.nitricOxide2Tv);

        final TextView substrateOneTv = findViewById(R.id.substrateOneTv);
        final TextView substrateTwoTv = findViewById(R.id.substrateTwoTv);
        substrateThreeTv = findViewById(R.id.substrateThreeTv);

        productTv = findViewById(R.id.productTv);

        secondPlusView = findViewById(R.id.secondPlusView);
        arrowRightView = findViewById(R.id.arrowRightView);

        setDragAndDrop(glycerineTv);
        setDragAndDrop(nitroTv);
        setDragAndDrop(oxygenTv);
        setDragAndDrop(waterTv);
        setDragAndDrop(nitricOxideTv);
        setDragAndDrop(sodiumHydroxideTv);
        setDragAndDrop(nitroglycerinTv);
        setDragAndDrop(sawdustTv);
        setDragAndDrop(nitricAcidVTv);
        setDragAndDrop(sodiumNitrateVTv);
        setDragAndDrop(nitricAcidIIITv);
        setDragAndDrop(sodiumNitrateIIITv);
        setDragAndDrop(tntTv);
        setDragAndDrop(nitricOxide2Tv);
        setDragAndDrop(sodiumTv);

        substrateOneTv.setOnDragListener(getOnDragListener(substrateOneTv, productTv, substrateTwoTv, substrateThreeTv));
        substrateTwoTv.setOnDragListener(getOnDragListener(substrateTwoTv, productTv, substrateOneTv, substrateThreeTv));
        substrateThreeTv.setOnDragListener(getOnDragListener(substrateThreeTv, productTv, substrateOneTv, substrateTwoTv));

        mContentView.setOnClickListener(view -> toggle());

        binding.dummyButton.setOnTouchListener(mDelayHideTouchListener);
        binding.dummyButton.setOnClickListener(view -> super.onBackPressed());
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDragAndDrop(final TextView textView) {
        textView.setOnLongClickListener(view -> {
            final CharSequence sequence = textView.getText().toString().trim();
            final ClipData.Item item = new ClipData.Item(sequence);

            final ClipData dragData = new ClipData(
                    sequence,
                    new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                    item);

            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(textView);
            view.startDragAndDrop(dragData, myShadow, null, 0);
            return true;
        });
    }

    private View.OnDragListener getOnDragListener(final TextView mainSubstrate, final TextView productTv, final TextView substrate2, final TextView substrate3) {
        return (view, dragEvent) -> {
            switch(dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        view.invalidate();
                        return true;
                    }
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    view.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DROP:
                    final ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                    final CharSequence dragData = item.getText();

                    if (!productTv.getText().toString().trim().equals("")) {
                        productTv.setText("");
                    }

                    mainSubstrate.setText(dragData);

                    if (substrate3.getText().toString().trim().equals("")) {
                        performChemicalReaction(mainSubstrate.getText().toString().trim(),
                                                substrate2.getText().toString().trim(),
                                                productTv);

                        performChemicalReaction(substrate2.getText().toString().trim(),
                                                mainSubstrate.getText().toString().trim(),
                                                productTv);
                    } else {
                        performFinalReaction(mainSubstrate.getText().toString().trim(),
                                             substrate2.getText().toString().trim(),
                                             substrate3.getText().toString().trim());

                        performFinalReaction(mainSubstrate.getText().toString().trim(),
                                             substrate3.getText().toString().trim(),
                                             substrate2.getText().toString().trim());

                        performFinalReaction(substrate2.getText().toString().trim(),
                                             mainSubstrate.getText().toString().trim(),
                                             substrate3.getText().toString().trim());

                        performFinalReaction(substrate2.getText().toString().trim(),
                                             substrate3.getText().toString().trim(),
                                             mainSubstrate.getText().toString().trim());

                        performFinalReaction(substrate3.getText().toString().trim(),
                                             substrate2.getText().toString().trim(),
                                             mainSubstrate.getText().toString().trim());

                        performFinalReaction(substrate3.getText().toString().trim(),
                                             mainSubstrate.getText().toString().trim(),
                                             substrate2.getText().toString().trim());
                    }

                    view.invalidate();
                    return true;
                default:
                    Log.e("DragDrop Example","Unknown action type received by View.OnDragListener.");
                    break;
            }

            return false;
        };
    }

    private void performChemicalReaction(final String substrate1, final String substrate2, final TextView productTv) {
        if (substrate1.equals(getString(R.string.sodium_hydroxide)) && substrate2.equals(getString(R.string.nitric_acidV))) {
            productTv.setText(getText(R.string.sodium_nitrateV));
            mainGot[0] = true;
            sodiumNitrateVTv.setVisibility(View.VISIBLE);
        }

        if (substrate1.equals(getString(R.string.glycerine)) && substrate2.equals(getString(R.string.nitric_acidV))) {
            productTv.setText(getText(R.string.nitroglycerin));
            nitroglycerinTv.setVisibility(View.VISIBLE);
            mainGot[1] = true;
        }

        for (boolean b : mainGot) {
            if (!b) {
                break;
            }

            substrateThreeTv.setVisibility(View.VISIBLE);
            secondPlusView.setVisibility(View.VISIBLE);
            arrowRightView.setVisibility(View.GONE);
            productTv.setVisibility(View.GONE);
            return;
        }

        if (substrate1.equals(getString(R.string.sodium)) && substrate2.equals(getString(R.string.water))) {
            productTv.setText(getText(R.string.sodium_hydroxide));
            sodiumHydroxideTv.setVisibility(View.VISIBLE);
            return;
        }

        if (substrate1.equals(getString(R.string.sodium_hydroxide)) && substrate2.equals(getString(R.string.nitric_acidIII))) {
            productTv.setText(getText(R.string.sodium_nitrateIII));
            sodiumNitrateIIITv.setVisibility(View.VISIBLE);
            return;
        }

        if (substrate1.equals(getString(R.string.nitro)) && substrate2.equals(getString(R.string.oxygen))) {
            productTv.setText(getText(R.string.nitric_oxide));
            nitricOxideTv.setVisibility(View.VISIBLE);
            nitricOxide2Tv.setVisibility(View.VISIBLE);
            return;
        }

        if (substrate1.equals(getString(R.string.nitric_oxide)) && substrate2.equals(getString(R.string.water))) {
            productTv.setText(getText(R.string.nitric_acidV));
            nitricAcidVTv.setVisibility(View.VISIBLE);
            return;
        }

        if (substrate1.equals(getString(R.string.nitric_oxide2)) && substrate2.equals(getString(R.string.water))) {
            productTv.setText(getText(R.string.nitric_acidIII));
            nitricAcidIIITv.setVisibility(View.VISIBLE);
        }
    }

    private void performFinalReaction(final String substrate1, final String substrate2, final String substrate3) {
        if (substrate1.equals(getString(R.string.nitroglycerin)) &&
                substrate2.equals(getString(R.string.sawdust)) &&
                substrate3.equals(getString(R.string.sodium_nitrateV))) {
            tntTv.setVisibility(View.VISIBLE);
            Toast.makeText(getBaseContext(), "Finally, you got this", Toast.LENGTH_SHORT).show();
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