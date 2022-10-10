package com.babanomania.pdfscanner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.babanomania.pdfscanner.databinding.ActivityMainBinding
import com.babanomania.pdfscanner.persistance.DocumentViewModel
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import com.scanlibrary.createStagingPath

class MainActivity : AppCompatActivity() {
    private var fileUri: Uri? = null

    private val viewModel: DocumentViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.pendingDocument.observe(this) { document ->
            val adapter = PagesAdapter(
                document?.pages ?: emptyList(),
                { pickImage() },
                { deleteUri ->
                    viewModel.removePage(deleteUri)
                }
            )
            binding.rw.adapter = adapter
        }

        binding.rw.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == ScanConstants.PICKFILE_REQUEST_CODE) {
            processImage(data?.data ?: fileUri)
        }
        if (requestCode == ScanConstants.START_CAMERA_REQUEST_CODE) {
            viewModel.addPage(data?.getStringExtra(ScanActivity.EXTRA_RESUL_IMAGE_NAME))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.default_menu, menu)
        return true
    }

    fun goToSearch(mi: MenuItem?) {

    }

    fun goToPreferences(mi: MenuItem?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun pickImage() {
        val chooser =
            Intent.createChooser(openMediaContent(), getString(R.string.image_picker_title))
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(openCamera()))
        startActivityForResult(chooser, ScanConstants.PICKFILE_REQUEST_CODE)
    }

    private fun openMediaContent(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        return intent
    }

    private fun openCamera(): Intent {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileUri = createStagingPath(this)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        cameraIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        return cameraIntent
    }


    private fun processImage(uri: Uri?) {
        startActivityForResult(
            ScanActivity.makeIntent(this, uri!!),
            ScanConstants.START_CAMERA_REQUEST_CODE
        )
    }

}
