package com.babanomania.pdfscanner.fileView

import android.graphics.Color
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.RecyclerView
import com.babanomania.pdfscanner.R
import com.babanomania.pdfscanner.databinding.FileItemViewBinding
import com.babanomania.pdfscanner.persistance.Document

class FLViewHolder2(
    private val binding: FileItemViewBinding,
    private val onClickListener: (Document, View) -> Unit,
    private val longClickListener: (Document, View) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {

    private val categoryImageMap: Map<String, Int> = mapOf(
        "Others" to R.drawable.ic_category_others,
        "Shopping" to R.drawable.ic_category_shopping,
        "Vehicle" to R.drawable.ic_category_vehicle,
        "Medical" to R.drawable.ic_category_medical,
        "Legal" to R.drawable.ic_category_legal,
        "Housing" to R.drawable.ic_category_housing,
        "Books" to R.drawable.ic_category_books,
        "Food" to R.drawable.ic_category_food,
        "Banking" to R.drawable.ic_category_banking,
        "Receipts" to R.drawable.ic_category_receipt,
        "Manuals" to R.drawable.ic_category_manuals,
        "Travel" to R.drawable.ic_category_travel,
        "Notes" to R.drawable.ic_category_notes,
        "ID" to R.drawable.ic_category_id,
    )



    fun setDocument(document: Document, selected: Boolean) {
        with(binding) {
            fileName.text = document.name
            timeLabel.text = document.scanned
            categoryLabel.text = document.category
            if (document.pageCount > 1) {
                pageCount.visibility = View.VISIBLE
                pageCount.text = document.pageCount.toString() + " Pages"
            } else {
                pageCount.visibility = View.GONE
            }
            root.setBackgroundColor(if (selected) Color.LTGRAY else Color.WHITE)

            ivDocIcon.setImageResource(
                categoryImageMap[document.category] ?: R.drawable.ic_category_others
            )

            root.setOnClickListener {
                onClickListener(document, root)
            }
            root.setOnLongClickListener {
                longClickListener(document, root)
                true
            }

//            relativeLayout.setOnClickListener { v ->
//                if (adapter.multiSelect) {
//                    selectItem(document)
//                    if (adapter.selectedItems.size == 0) {
//                        adapter.mActionMode!!.finish()
//                    } else {
//                        adapter.mActionMode!!.invalidate()
//                    }
//                } else {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    val sd = Environment.getExternalStorageDirectory()
//                    val baseDirectory = v.context.getString(R.string.base_storage_path)
//                    val newFileName = baseDirectory + document.path
//                    val toOpen = File(sd, newFileName)
//                    val sharedFileUri = FileProvider.getUriForFile(
//                        v.context,
//                        "com.babanomania.pdfscanner.provider",
//                        toOpen
//                    )
//                    intent.setDataAndType(sharedFileUri, "application/pdf")
//                    val pm = v.context.packageManager
//                    if (intent.resolveActivity(pm) != null) {
//                        v.context.startActivity(intent)
//                    }
//                }
//            }
//            itemView.setOnLongClickListener { view ->
//                adapter.mActionMode =
//                    (view.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
//                selectItem(document)
//                true
//            }
        }
    }

}