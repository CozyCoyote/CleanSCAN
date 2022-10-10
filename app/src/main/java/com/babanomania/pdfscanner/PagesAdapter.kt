package com.babanomania.pdfscanner

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.babanomania.pdfscanner.databinding.AddMoreImgBinding
import com.babanomania.pdfscanner.databinding.EachFileImgBinding
import com.scanlibrary.getScaledBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class PagesAdapter(
    private val docs: List<String>,
    private val addMoreListener: () -> Unit,
    private val deleteListener: (String) -> Unit,
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val PAGE_ITEM = 0
        const val ADD_ITEM = 1
    }

    override fun getItemCount() = docs.size + 1

    override fun getItemViewType(position: Int) = when (position) {
        docs.size -> ADD_ITEM
        else -> PAGE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ADD_ITEM -> AddMoreViewHolder(
                binding = AddMoreImgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                clickListener = addMoreListener
            )
            PAGE_ITEM -> PageViewHolder(
                binding = EachFileImgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                deleteListener = deleteListener
            )
            else -> {
                throw IndexOutOfBoundsException()
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is PageViewHolder -> holder.bind(docs[position])
            else -> {
            }
        }
    }

}

internal class PageViewHolder(
    private val binding: EachFileImgBinding,
    private val deleteListener: (String) -> Unit
) : ViewHolder(binding.root) {

    fun bind(page: String) {

        binding.btnDelete.setOnClickListener {
            deleteListener(page)
        }
        GlobalScope.launch(Dispatchers.Main) {
            val uri = Uri.fromFile(File(itemView.context.cacheDir, page))
            val bitmap = getScaledBitmap(
                itemView.context,
                uri,
                with(itemView.context.resources) {
                    getDimensionPixelSize(R.dimen.page_thumbnail_width) to getDimensionPixelSize(R.dimen.page_thumbnail_height)
                })
            binding.eachFileScreenshot.setImageBitmap(bitmap)
        }
    }

}

internal class AddMoreViewHolder(binding: AddMoreImgBinding, clickListener: () -> Unit) :
    ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            clickListener()
        }
    }

}
