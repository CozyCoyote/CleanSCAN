package com.babanomania.pdfscanner.persistance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: DocumentDao = createDatabase(application).documentDao()

    val pendingDocument: LiveData<Document?>
        get() = dao.pendingDocument(true)

    fun saveDocument(document: Document) {
        runInBackground { dao.save(document) }
    }

    fun removePage(page: String) {
        pendingDocument.value?.let {
            with(it.pages?.toMutableList()) {
                this?.remove(page)
                it.pages = this
            }
            updateDocument(it)
        }
    }

    fun addPage(uri: String?) {
        uri?.let {
            val currentDoc = pendingDocument.value ?: Document()

            val pages = currentDoc.pages?.toMutableList() ?: mutableListOf()
            pages.add(it)

            currentDoc.pages = pages

            saveDocument(currentDoc)
        }
    }

    fun updateDocument(document: Document) {
        runInBackground { dao.update(document) }
    }

    fun deleteDocument(document: Document) {
        runInBackground { dao.delete(document) }
    }

    fun saveDocument() {
//        val pdf = PdfDocument()
//        pages.forEach { page ->
//            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, page)
//            pdf.writeA4Page(bitmap, 0)
//            bitmap.recycle()
//        }
//        val filePath = context.createPdfFile(currentTime().toString() + ".pdf")
//        pdf.writeTo(FileOutputStream(filePath))
//        pdf.close()
    }

}

fun ViewModel.runInBackground(block: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
    block()
}
