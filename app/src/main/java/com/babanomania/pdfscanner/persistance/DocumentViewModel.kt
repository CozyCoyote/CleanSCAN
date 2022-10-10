package com.babanomania.pdfscanner.persistance

import android.app.Application
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import com.babanomania.pdfscanner.persistance.DocumentDao
import androidx.lifecycle.LiveData
import com.babanomania.pdfscanner.R
import com.babanomania.pdfscanner.persistance.DocumentDatabase
import com.babanomania.pdfscanner.utils.DialogUtil
import com.babanomania.pdfscanner.utils.FileIOUtils
import com.babanomania.pdfscanner.utils.PDFWriterUtil
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: DocumentDao
    private val executorService: ExecutorService

    init {
        dao = DocumentDatabase.getInstance(application).documentDao()
        executorService = Executors.newSingleThreadExecutor()

        //TODO move to internal storage
        val baseStorageDirectory = application.getString(R.string.base_storage_path)
        FileIOUtils.mkdir(baseStorageDirectory)
        val baseStagingDirectory = application.getString(R.string.base_staging_path)
        FileIOUtils.mkdir(baseStagingDirectory)
        val scanningTmpDirectory = application.getString(R.string.base_scantmp_path)
        FileIOUtils.mkdir(scanningTmpDirectory)
    }

    val allDocuments: LiveData<List<Document>>
        get() = dao.findAll()

    fun search(text: String?): LiveData<List<Document>> {
        return dao.search(text)
    }

    fun saveDocument(document: Document) {
        executorService.execute { dao.save(document) }
    }

    fun updateDocument(document: Document) {
        executorService.execute { dao.update(document) }
    }

    fun deleteDocument(document: Document) {
        executorService.execute { dao.delete(document) }
    }

    fun saveBitmap(uri: Uri, multiPage: Boolean) {

//        File(uri.path).copyTo()

//        val baseDirectory = if (multiPage) "/stage/" else "/data/"
//            applicationContext.getString(if (addMore) R.string.base_staging_path else R.string.base_storage_path)
//        val sd = getApplication<Application>().cacheDir
//        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy_hh-mm-ss")
//        val timestamp = simpleDateFormat.format(Date())
//        if (multiPage) {
//            try {
//                val filename = "SCANNED_STG_$timestamp.png"
//                FileIOUtils.writeFile(
//                    baseDirectory,
//                    filename
//                ) { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
//                bitmap.recycle()
//                System.gc()
//            } catch (ioe: IOException) {
//                ioe.printStackTrace()
//            }
//        } else {
//            DialogUtil.askUserFilaname(this, null, null) { textValue, category ->
//                try {
//                    val pdfWriter = PDFWriterUtil()
//                    val stagingDirPath = applicationContext.getString(R.string.base_staging_path)
//                    val stagingFiles = FileIOUtils.getAllFiles(stagingDirPath)
//                    for (stagedFile in stagingFiles) {
//                        pdfWriter.addFile(stagedFile)
//                    }
//                    pdfWriter.addBitmap(bitmap)
//                    val itemName = textValue.replace("[^a-zA-Z0-9\\s]".toRegex(), "")
//                    val filename = "$timestamp-$itemName.pdf"
//                    FileIOUtils.mkdir("$baseDirectory/$category")
//                    FileIOUtils.writeFile("$baseDirectory/$category/", filename) { out ->
//                        try {
//                            pdfWriter.write(out)
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//                    }
//                    fileAdapter!!.notifyDataSetChanged()
//                    FileIOUtils.clearDirectory(stagingDirPath)
//                    val simpleDateFormatView = SimpleDateFormat("dd-MM-yyyy hh:mm")
//                    val timestampView = simpleDateFormatView.format(Date())
//                    val newDocument = Document()
//                    newDocument.name = textValue
//                    newDocument.category = category
//                    newDocument.path = "$category/$filename"
//                    newDocument.scanned = timestampView
//                    newDocument.pageCount = pdfWriter.pageCount
//                    viewModel!!.saveDocument(newDocument)
//                    pdfWriter.close()
//                    bitmap.recycle()
//                    System.gc()
//                } catch (ioe: IOException) {
//                    ioe.printStackTrace()
//                }
//            }
//        }
    }

}