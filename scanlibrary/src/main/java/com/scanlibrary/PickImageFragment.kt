package com.scanlibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scanlibrary.databinding.PickImageFragmentBinding

/**
 * Created by jhansi on 04/04/15.
 */
class PickImageFragment : Fragment() {
    private var fileUri: Uri? = null
    private var scanner: IScanner? = null

    private lateinit var binding: PickImageFragmentBinding

    private val intentPreference: Int by lazy {
        arguments?.getInt(ScanConstants.OPEN_INTENT_PREFERENCE, 0) ?: 0
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
        binding = PickImageFragmentBinding.inflate(inflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.cameraButton.setOnClickListener(CameraButtonClickListener())
        binding.selectButton.setOnClickListener(GalleryClickListener())
//        if (isIntentPreferenceSet) {
//            handleIntentPreference()
//        } else {
//            activity?.finish()
//        }
    }

    private fun handleIntentPreference() {
        val preference = intentPreference
        if (preference == ScanConstants.OPEN_CAMERA) {
            openCamera()
        } else if (preference == ScanConstants.OPEN_MEDIA) {
            openMediaContent()
        }
    }

    private inner class CameraButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            openCamera()
        }
    }

    private inner class GalleryClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            openMediaContent()
        }
    }

    fun openMediaContent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE)
    }

    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileUri = createStagingPath(requireContext())
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            ScanConstants.START_CAMERA_REQUEST_CODE -> scanner?.onBitmapSelect(fileUri)
            ScanConstants.PICKFILE_REQUEST_CODE -> scanner?.onBitmapSelect(data?.data)
        }
    }

}