package com.scanlibrary;

import android.content.ComponentCallbacks2;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


/**
 * Created by jhansi on 28/03/15.
 */
public class ScanActivity extends AppCompatActivity implements IScanner, ComponentCallbacks2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        // set an exit transition
        Fade fade = new Fade();
        fade.setDuration(1000);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        setContentView(R.layout.scan_layout);
        init();
        Utils.setLightNavigationBar(findViewById(android.R.id.content), this);
    }

    private void init() {
        PickImageFragment fragment = new PickImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ScanConstants.OPEN_INTENT_PREFERENCE, getPreferenceContent());
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        final android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (fragmentManager.getBackStackEntryCount() <= 1) {
                finish();
            }
        });

    }

    protected int getPreferenceContent() {
        return getIntent().getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
    }

    @Override
    public void onBitmapSelect(Uri uri) {
        Fragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    @Override
    public void onScanFinish(Uri uri) {
        ImageFiltersFragment fragment = new ImageFiltersFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SCANNED_RESULT, uri);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, fragment)
                .addToBackStack(ImageFiltersFragment.class.toString())
                .commit();
    }

    public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native Bitmap getBWBitmap(Bitmap bitmap);

//    public native float[] getPoints(Bitmap bitmap);

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Scanner");
    }
}