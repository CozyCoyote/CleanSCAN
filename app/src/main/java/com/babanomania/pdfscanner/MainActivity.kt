package com.babanomania.pdfscanner

import androidx.appcompat.app.AppCompatActivity
import com.babanomania.pdfscanner.fileView.FLAdapter2
import com.babanomania.pdfscanner.persistance.DocumentViewModel
import android.widget.LinearLayout
import android.os.Bundle
import android.content.Intent
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import android.app.ActivityOptions
import android.graphics.Bitmap
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.babanomania.pdfscanner.databinding.ActivityMainBinding
import com.babanomania.pdfscanner.fileView.FLAdapter
import com.babanomania.pdfscanner.persistance.Document
import com.babanomania.pdfscanner.utils.*
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private val scannedBitmaps: MutableList<Uri> = ArrayList()

    private val viewModel: DocumentViewModel by viewModels()

    private lateinit var fileAdapter: FLAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        applyTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        UIUtil.setLightNavigationBar(binding.rw, this)

        val emptyLayout: LinearLayout = findViewById(R.id.empty_list)
        fileAdapter = FLAdapter(viewModel, this)
        binding.rw.adapter = fileAdapter
        viewModel.allDocuments.observe(this) { documents ->
            emptyLayout.visibility = if (documents.isNotEmpty()) View.GONE else View.VISIBLE
            fileAdapter.setData(documents)
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rw.layoutManager = linearLayoutManager
        val dividerItemDecoration =
            DividerItemDecoration(this, linearLayoutManager.orientation)
        binding.rw.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.default_menu, menu)
        return true
    }

    fun goToSearch(mi: MenuItem?) {
        val intent = Intent(this, SearchableActivity::class.java)
        startActivity(intent)
    }

    fun goToPreferences(mi: MenuItem?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun openCamera(v: View?) {
        scannedBitmaps.clear()
        val stagingDirPath = applicationContext.getString(R.string.base_staging_path)
        val scanningTmpDirectory = applicationContext.getString(R.string.base_scantmp_path)
        FileIOUtils.clearDirectory(stagingDirPath)
        FileIOUtils.clearDirectory(scanningTmpDirectory)
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)

        //startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE);
        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle())
    }

    //    public void openGallery(View v){
    //        scannedBitmaps.clear();
    //
    //        String stagingDirPath = getApplicationContext().getString( R.string.base_staging_path );
    //        FileIOUtils.clearDirectory( stagingDirPath );
    //
    //        Intent intent = new Intent(this, ScanActivity.class);
    //        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA);
    //        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
    //    }
    private fun saveBitmap(bitmap: Bitmap, addMore: Boolean) {
        val baseDirectory =
            applicationContext.getString(if (addMore) R.string.base_staging_path else R.string.base_storage_path)
        val sd = Environment.getExternalStorageDirectory()
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy_hh-mm-ss")
        val timestamp = simpleDateFormat.format(Date())
        if (addMore) {
            try {
                val filename = "SCANNED_STG_$timestamp.png"
                FileIOUtils.writeFile(
                    baseDirectory,
                    filename
                ) { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
                bitmap.recycle()
                System.gc()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        } else {
            DialogUtil.askUserFilaname(this, null, null) { textValue, category ->
                try {
                    val pdfWriter = PDFWriterUtil()
                    val stagingDirPath = applicationContext.getString(R.string.base_staging_path)
                    val stagingFiles = FileIOUtils.getAllFiles(stagingDirPath)
                    for (stagedFile in stagingFiles) {
                        pdfWriter.addFile(stagedFile)
                    }
                    pdfWriter.addBitmap(bitmap)
                    val itemName = textValue.replace("[^a-zA-Z0-9\\s]".toRegex(), "")
                    val filename = "$timestamp-$itemName.pdf"
                    FileIOUtils.mkdir("$baseDirectory/$category")
                    FileIOUtils.writeFile("$baseDirectory/$category/", filename) { out ->
                        try {
                            pdfWriter.write(out)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    fileAdapter!!.notifyDataSetChanged()
                    FileIOUtils.clearDirectory(stagingDirPath)
                    val simpleDateFormatView = SimpleDateFormat("dd-MM-yyyy hh:mm")
                    val timestampView = simpleDateFormatView.format(Date())
                    val newDocument = Document()
                    newDocument.name = textValue
                    newDocument.category = category
                    newDocument.path = "$category/$filename"
                    newDocument.scanned = timestampView
                    newDocument.pageCount = pdfWriter.pageCount
                    viewModel!!.saveDocument(newDocument)
                    pdfWriter.close()
                    bitmap.recycle()
                    System.gc()
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }
        }
    }

    private fun savePdf() {
        val baseDirectory = applicationContext.getString(R.string.base_storage_path)
        val sd = Environment.getExternalStorageDirectory()
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy_hh-mm-ss")
        val timestamp = simpleDateFormat.format(Date())
        DialogUtil.askUserFilaname(this, null, null) { textValue, category ->
            try {
                val pdfWriter = PDFWriterUtil()
                val stagingDirPath = applicationContext.getString(R.string.base_staging_path)
                val stagingFiles = FileIOUtils.getAllFiles(stagingDirPath)
                for (stagedFile in stagingFiles) {
                    pdfWriter.addFile(stagedFile)
                }
                val itemName = textValue.replace("[^a-zA-Z0-9\\s]".toRegex(), "")
                val filename = "$timestamp-$itemName.pdf"
                FileIOUtils.mkdir("$baseDirectory/$category/")
                FileIOUtils.writeFile("$baseDirectory/$category/", filename) { out ->
                    try {
                        pdfWriter.write(out)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                fileAdapter!!.notifyDataSetChanged()
                FileIOUtils.clearDirectory(stagingDirPath)
                val simpleDateFormatView = SimpleDateFormat("dd-MM-yyyy hh:mm")
                val timestampView = simpleDateFormatView.format(Date())
                val newDocument = Document()
                newDocument.name = textValue
                newDocument.category = category
                newDocument.path = "$category/$filename"
                newDocument.scanned = timestampView
                newDocument.pageCount = pdfWriter.pageCount
                viewModel!!.saveDocument(newDocument)
                pdfWriter.close()
                System.gc()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == ScanConstants.PICKFILE_REQUEST_CODE || requestCode == ScanConstants.START_CAMERA_REQUEST_CODE) &&
            resultCode == RESULT_OK
        ) {
            val saveMode = if (data!!.extras!!
                    .containsKey(ScanConstants.SAVE_PDF)
            ) data.extras!!.getBoolean(ScanConstants.SAVE_PDF) else java.lang.Boolean.FALSE
            if (saveMode) {
                savePdf()
            } else {
                val uri = data.extras!!.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)
                val doScanMore = data.extras!!.getBoolean(ScanConstants.SCAN_MORE)
                val sd = Environment.getExternalStorageDirectory()
                val src = File(sd, uri!!.path)
                val bitmap = BitmapFactory.decodeFile(src.absolutePath)

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                saveBitmap(bitmap, doScanMore)
                if (doScanMore) {
                    scannedBitmaps.add(uri)
                    val intent = Intent(this, MultiPageActivity::class.java)
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)
                    startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE)
                }

                //getContentResolver().delete(uri, null, null);
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

//        val nightFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
//        delegate.applyDayNight()
    }

}