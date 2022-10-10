package com.scanlibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Float.max
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

suspend fun getScaledBitmap(context: Context, uri: Uri, viewSize: Pair<Int, Int>): Bitmap =
    withContext(
        Dispatchers.IO
    ) {
        val originalSize = bitmapSize(context, uri)
        val ratio = calculateSampleSize(originalSize, viewSize)

        val stream = context.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inSampleSize = ratio
        }
        val bitmap = BitmapFactory.decodeStream(stream, null, options)
        stream?.close()
        bitmap!!
    }

fun bitmapSize(context: Context, uri: Uri): Pair<Int, Int> {
    return with(context.contentResolver.openInputStream(uri)!!) {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(this, null, options)
        close()
        options.outWidth to options.outHeight
    }
}

fun calculateSampleSize(bitmapSize: Pair<Int, Int>, viewSize: Pair<Int, Int>): Int {
    var sampleSize = 1
    while (bitmapSize.first / sampleSize > viewSize.first && bitmapSize.second / sampleSize > viewSize.second) {
        sampleSize *= 2
    }
    return sampleSize
}

fun createStagingPath(context: Context): Uri {
    val file = createInternalFile(context)
    return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
}

fun createInternalFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File(context.cacheDir, "IMG_$timeStamp.jpg")
}

inline fun <reified T> T.log(message: String) {
    Log.e(T::class.simpleName + " scale lib", message)
}
