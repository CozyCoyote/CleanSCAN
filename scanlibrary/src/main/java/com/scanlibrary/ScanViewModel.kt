package com.scanlibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class ScanViewModel : ViewModel() {

    fun cropImage(
        context: Context,
        uri: Uri,
        rotationDegrees: Int,
        scanner: IScanner,
        viewSize: Pair<Int, Int>,
        points: Map<Int, PointF>,
        callback: (Uri) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val clippedImage = saveImage(
                context = context,
                rotationDegrees = rotationDegrees,
                scanner = scanner,
                viewSize = viewSize,
                points = points,
                uri = uri,
            )
            callback(clippedImage)
        }
    }


    private suspend fun saveImage(
        context: Context,
        uri: Uri,
        rotationDegrees: Int,
        scanner: IScanner,
        viewSize: Pair<Int, Int>,
        points: Map<Int, PointF>
    ): Uri = withContext(Dispatchers.IO) {

        val bitmap = context.contentResolver.openInputStream(uri).use {
            BitmapFactory.decodeStream(it, null, null)
        }!!
        val matrix = Matrix()
        matrix.postRotate((rotationDegrees % 360).toFloat())
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )

        val (width, height) = with(viewSize) { if (rotationDegrees % 180 == 0) (first to second) else (second to first) }

        val xRatio = rotatedBitmap.width.toFloat() / width
        val yRatio = rotatedBitmap.height.toFloat() / height

        points.forEach {
            var x = it.value.x
            x *= max(xRatio, yRatio)
            x = min(x, rotatedBitmap.width.toFloat())
            x = max(x, 0F)
            it.value.x = x

            var y = it.value.y
            y *= max(xRatio, yRatio)
            y = min(y, rotatedBitmap.height.toFloat())
            y = max(y, 0F)
            it.value.y = y
        }

        val clipping = scanner.getScannedBitmap(
            rotatedBitmap,
            points[0]!!.x,
            points[0]!!.y,
            points[1]!!.x,
            points[1]!!.y,
            points[2]!!.x,
            points[2]!!.y,
            points[3]!!.x,
            points[3]!!.y,
        )

        bitmap.recycle()
        rotatedBitmap.recycle()

        val clippingPath = createStagingPath(context)

        context.contentResolver.openOutputStream(clippingPath).use {
            clipping.compress(Bitmap.CompressFormat.JPEG, 90, it)
            clipping.recycle()
            return@withContext clippingPath
        }
    }

}