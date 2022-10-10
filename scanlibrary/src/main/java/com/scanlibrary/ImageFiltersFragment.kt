package com.scanlibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.scanlibrary.ScanActivity.Companion.EXTRA_RESUL_IMAGE_NAME
import com.scanlibrary.databinding.ResultLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by jhansi on 29/03/15.
 */
class ImageFiltersFragment : Fragment() {

    private var transformed: Bitmap? = null

    private lateinit var scanner: IScanner
    private lateinit var binding: ResultLayoutBinding

    private val uri: Uri by lazy {
        arguments?.getParcelable(ScanConstants.SCANNED_RESULT)!!
    }

    private val original: Bitmap? by lazy {
        context?.contentResolver?.openInputStream(uri).use {
            BitmapFactory.decodeStream(it, null, null)
        }
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        if (activity !is IScanner) {
            throw ClassCastException("Activity must implement IScanner")
        }
        scanner = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ResultLayoutBinding.inflate(inflater)
        init()
        return binding.root
    }

    private fun init() = with(binding) {
        original.setOnClickListener(OriginalClickListener())
        magicColor.setOnClickListener(MagicColorClickListener())
        grayMode.setOnClickListener(GrayButtonClickListener())
        BWMode.setOnClickListener(BWModeClickListener())

        binding.scannedImage.post {
            GlobalScope.launch(Dispatchers.Main) {
                val bitmap = getScaledBitmap(
                    context = requireContext(),
                    uri = uri,
                    viewSize = with(binding.scannedImage) { width to height }
                )
                binding.scannedImage.setImageBitmap(bitmap)
            }
        }

        addBtn.setOnClickListener {
            saveAndExit()
        }

    }

    fun saveAndExit() {
        GlobalScope.launch(Dispatchers.Main) {
            val intent = Intent()
            withContext(Dispatchers.IO) {
                val fileUri = createInternalFile(requireContext()).toUri()
                context?.contentResolver?.openOutputStream(fileUri).use {
                    (transformed ?: original)?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                intent.putExtra(EXTRA_RESUL_IMAGE_NAME, fileUri.lastPathSegment)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
    }

    private inner class BWModeClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    transformed = scanner.getBWBitmap(original)
                }
                binding.scannedImage.setImageBitmap(transformed)
            }
        }
    }

    private inner class MagicColorClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    transformed = scanner.getMagicColorBitmap(original)
                }
                binding.scannedImage.setImageBitmap(transformed)
            }
        }
    }


    private inner class OriginalClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            try {
                transformed = original
                binding.scannedImage.setImageBitmap(original)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }
        }
    }

    private inner class GrayButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    transformed = scanner.getGrayBitmap(original)
                }
                binding.scannedImage.setImageBitmap(transformed)
            }
        }
    }

}
