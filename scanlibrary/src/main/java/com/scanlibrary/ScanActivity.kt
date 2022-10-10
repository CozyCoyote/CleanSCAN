package com.scanlibrary

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * Created by jhansi on 28/03/15.
 */
class ScanActivity : AppCompatActivity(), IScanner, ComponentCallbacks2 {

    companion object {
        init {
            System.loadLibrary("opencv_java3")
            System.loadLibrary("Scanner")
        }

        private const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESUL_IMAGE_NAME = "extra_resul_image_name"

        fun makeIntent(context: Context, uri: Uri) =
            Intent(context, ScanActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_URI, uri)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_layout)
        init(intent.extras?.getParcelable(EXTRA_IMAGE_URI)!!)
    }

    private fun init(uri: Uri) {
        val fragment: Fragment = ScanFragment()
        val bundle = Bundle()
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri)
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .add(R.id.content, fragment)
            .commit()
    }

    override fun onScanFinish(uri: Uri) {
        val fragment = ImageFiltersFragment()
        val bundle = Bundle()
        bundle.putParcelable(ScanConstants.SCANNED_RESULT, uri)
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .add(R.id.content, fragment)
            .addToBackStack(ImageFiltersFragment::class.java.toString())
            .commit()
    }

    external override fun getScannedBitmap(
        bitmap: Bitmap,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        x4: Float,
        y4: Float
    ): Bitmap

    external override fun getGrayBitmap(bitmap: Bitmap): Bitmap
    external override fun getMagicColorBitmap(bitmap: Bitmap): Bitmap
    external override fun getBWBitmap(bitmap: Bitmap): Bitmap


}
