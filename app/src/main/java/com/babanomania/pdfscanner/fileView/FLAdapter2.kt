package com.babanomania.pdfscanner.fileView

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.babanomania.pdfscanner.R
import com.babanomania.pdfscanner.databinding.FileItemViewBinding
import com.babanomania.pdfscanner.persistance.Document
import java.io.File
import java.util.*

class FLAdapter2(
    private val context: Context,
    private val actionModeCallback: ActionMode.Callback,
) : RecyclerView.Adapter<FLViewHolder2>() {

    var multiSelect = false
    val documentList: MutableList<Document> = ArrayList()
    var selectedItems: MutableList<Document> = ArrayList()

    private lateinit var mActionMode: ActionMode

    fun setData(documents: List<Document>?) {
        documentList.clear()
        documentList.addAll(documents!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FLViewHolder2 {
        return FLViewHolder2(
            FileItemViewBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
            { document, root ->
                if (multiSelect) {
                    selectItem(document, root)
                    if (selectedItems.size == 0) {
                        mActionMode.finish()
                    } else {
                        mActionMode.invalidate()
                    }
                } else {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val sd = Environment.getExternalStorageDirectory()
                    val baseDirectory = context.getString(R.string.base_storage_path)
                    val newFileName = baseDirectory + document.path
                    val toOpen = File(sd, newFileName)
                    val sharedFileUri = FileProvider.getUriForFile(
                        context,
                        "com.babanomania.pdfscanner.provider",
                        toOpen
                    )
                    intent.setDataAndType(sharedFileUri, "application/pdf")
                    val pm = context.packageManager
                    if (intent.resolveActivity(pm) != null) {
                        context.startActivity(intent)
                    }
                }
            }, { document, root ->
                mActionMode =
                    (viewGroup.context as AppCompatActivity).startSupportActionMode(actionModeCallback)!!
                selectItem(document, root)
            })
    }

    override fun onBindViewHolder(viewHolder: FLViewHolder2, i: Int) {
        viewHolder.setDocument(documentList[i], selectedItems.contains(documentList[i]))
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    private fun selectItem(item: Document, root: View) {
        if (multiSelect) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                root.setBackgroundColor(Color.WHITE)
            } else {
                selectedItems.add(item)
                root.setBackgroundResource(R.color.colorPrimaryLight)
            }
        }
    }
}