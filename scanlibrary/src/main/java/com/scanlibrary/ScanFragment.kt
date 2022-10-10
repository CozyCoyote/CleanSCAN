package com.scanlibrary

import android.content.Context
import android.content.res.Resources
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.scanlibrary.databinding.ScanFragmentLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by jhansi on 29/03/15.
 */
class ScanFragment : Fragment() {

    private val viewModel: ScanViewModel by viewModels()

    private lateinit var binding: ScanFragmentLayoutBinding
    private lateinit var scanner: IScanner
    private var rotationDegrees: Int = 0

    private val uri: Uri by lazy {
        arguments?.getParcelable(ScanConstants.SELECTED_BITMAP)!!
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        if (activity !is IScanner) {
            throw ClassCastException("Activity must implement IScanner")
        }
        scanner = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ScanFragmentLayoutBinding.inflate(inflater)
        init()
        return binding.root
    }

    private fun init() = with(binding) {
        rotateLeft.setOnClickListener {
            rotationDegrees += 90
            sourceImageView.rotation = rotationDegrees.toFloat()
            sourceImageView.post {
                updatePolygons()
            }
        }
        scanButton.setOnClickListener(ScanButtonClickListener())
        rotateRight.setOnClickListener {
            rotationDegrees -= 90
            sourceImageView.rotation = rotationDegrees.toFloat()
            sourceImageView.post {
                updatePolygons()
            }
        }
        sourceFrame.post {
            setBitmap()
        }
        polygonView.setValidPolygonListener {
            scanButton.isEnabled = it
        }
    }

    private fun setBitmap() {
        with(binding) {
            GlobalScope.launch {
                val scaledBitmap =
                    getScaledBitmap(requireContext(), uri, sourceFrame.width to sourceFrame.height)
                withContext(Dispatchers.Main) {
                    sourceImageView.setImageBitmap(scaledBitmap)
                    sourceImageView.post {
                        updatePolygons()
                    }
                }
            }
        }
    }

    private fun updatePolygons() = with(binding) {
        val padding = resources.getDimensionPixelSize(R.dimen.polygonViewCircleWidth)
        val (width, height) = with(sourceImageView) { if (rotationDegrees % 180 == 0) (width to height) else (height to width) }
        val layoutParams = FrameLayout.LayoutParams(
            width + padding, height + padding
        )
        layoutParams.gravity = Gravity.CENTER
        polygonView.layoutParams = layoutParams
        polygonView.forceLayout()

        polygonView.points = getOutlinePoints(
            width.toFloat(), height.toFloat()
        )
        polygonView.invalidate()

        polygonView.visibility = View.VISIBLE
    }

    private fun getOutlinePoints(width: Float, height: Float): Map<Int, PointF> {
        return mapOf(
            0 to PointF(0F, 0F),
            1 to PointF(width, 0F),
            2 to PointF(0F, height),
            3 to PointF(width, height),
        )
    }

    private inner class ScanButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            viewModel.cropImage(
                context = requireContext(),
                rotationDegrees = rotationDegrees,
                scanner = scanner,
                viewSize = with(binding.sourceImageView) { width to height },
                points = binding.polygonView.points,
                uri = uri,
            ) { scanner.onScanFinish(it) }
        }
    }

}

fun Context.dpToPx(dp: Float) = com.scanlibrary.dpToPx(dp, this.resources)

fun dpToPx(dp: Float, resources: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, resources.displayMetrics)
}