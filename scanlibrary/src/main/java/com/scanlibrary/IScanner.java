package com.scanlibrary;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by jhansi on 04/04/15.
 */
public interface IScanner {

    void onBitmapSelect(Uri uri);

    Bitmap getGrayBitmap(Bitmap bitmap);

    Bitmap getMagicColorBitmap(Bitmap bitmap);

    Bitmap getBWBitmap(Bitmap bitmap);

    void onScanFinish(Uri uri);

    Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

}
