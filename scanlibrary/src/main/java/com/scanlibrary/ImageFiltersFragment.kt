package com.scanlibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        original.setOnClickListener(originalClickListener())
        magicColor.setOnClickListener(magicColorClickListener())
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


        doneButton.setOnClickListener(DoneButtonClickListener())

        addBtn.setOnClickListener(AddButtonClickListener())

//        val sd = Environment.getExternalStorageDirectory()
//        val stagingDirPath = getString(R.string.base_staging_path)
//        val stagingDir = File(sd, stagingDirPath)
//        if (stagingDir.listFiles() != null && stagingDir.listFiles().isNotEmpty()) {
//            pageNumber.text = (stagingDir.listFiles().size + 1).toString()
//        } else {
//            pageNumber.text = "1"
//        }
    }

    //        Uri uri = getUri();
//        try {
//            original = Utils.getBitmap(getActivity(), uri);
//
//            final File sd = Environment.getExternalStorageDirectory();
//            File fdelete = new File(sd, uri.getPath());
//            boolean isDeleted = fdelete.delete();
//
//            //getActivity().getContentResolver().delete(uri, null, null);
//            return original;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    val bitmap: Bitmap?
//        private get() =//        Uri uri = getUri();
//        try {
//            original = Utils.getBitmap(getActivity(), uri);
//
//            final File sd = Environment.getExternalStorageDirectory();
//            File fdelete = new File(sd, uri.getPath());
//            boolean isDeleted = fdelete.delete();
//
//            //getActivity().getContentResolver().delete(uri, null, null);
//            return original;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//            null


    private inner class DoneButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            AsyncTask.execute {
                try {
//                        Intent data = new Intent();
//                        Bitmap bitmap = transformed;
//                        if (bitmap == null) {
//                            bitmap = original;
//                        }
//                        Uri uri = Utils.getUri(getActivity(), bitmap);
//                        data.putExtra(ScanConstants.SCANNED_RESULT, uri);
//                        data.putExtra(ScanConstants.SCAN_MORE, false);
//                        getActivity().setResult(Activity.RESULT_OK, data);
//                        original.recycle();
//                        System.gc();
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                dismissDialog();
//                                getActivity().finish();
//                            }
//                        });
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private inner class AddButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            AsyncTask.execute {
                try {
//                        Intent data = new Intent();
//                        Bitmap bitmap = transformed;
//                        if (bitmap == null) {
//                            bitmap = original;
//                        }
//                        Uri uri = Utils.getUri(getActivity(), bitmap);
//                        data.putExtra(ScanConstants.SCANNED_RESULT, uri);
//                        data.putExtra(ScanConstants.SCAN_MORE, true);
//                        getActivity().setResult(Activity.RESULT_OK, data);
//
//                        original.recycle();
//                        System.gc();
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                dismissDialog();
//                                getActivity().finish();
//                            }
//                        });
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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

    private inner class magicColorClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    transformed = scanner.getMagicColorBitmap(original)
                }
                binding.scannedImage.setImageBitmap(transformed)
            }
        }
    }

    private inner class originalClickListener : View.OnClickListener {
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