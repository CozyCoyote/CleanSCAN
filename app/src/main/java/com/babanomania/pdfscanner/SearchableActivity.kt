package com.babanomania.pdfscanner

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babanomania.pdfscanner.fileView.FLAdapter
import com.babanomania.pdfscanner.fileView.FLAdapter2
import com.babanomania.pdfscanner.persistance.DocumentViewModel
import com.babanomania.pdfscanner.utils.UIUtil

class SearchableActivity : AppCompatActivity() {

    private val viewModel: DocumentViewModel by viewModels()

    private lateinit var emptyLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        supportActionBar?.setTitle("")
        recyclerView = findViewById(R.id.rwSearch)
        UIUtil.setLightNavigationBar(recyclerView, this)
        val fileAdapter = FLAdapter(viewModel, this)
        recyclerView.setAdapter(fileAdapter)
        emptyLayout = findViewById(R.id.empty_search_list)
        viewModel.allDocuments.observe(this) { documents ->
            if (documents!!.size > 0) {
                emptyLayout.setVisibility(View.GONE)
                recyclerView.setVisibility(View.VISIBLE)
            } else {
                emptyLayout.setVisibility(View.VISIBLE)
                recyclerView.setVisibility(View.GONE)
            }
            fileAdapter.setData(documents)
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(linearLayoutManager)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        menuInflater.inflate(R.menu.searchview_menu, menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        val searchMenuItem = menu.findItem(R.id.menu_searchview)
        searchMenuItem.expandActionView()
        val searchView = searchMenuItem.actionView as SearchView?
        searchView!!.setSearchableInfo(searchableInfo)
        searchView.isIconified = false
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    doMySearch(newText)
                    return false
                }
            })
        return true
    }

    fun doMySearch(query: String) {
        recyclerView = findViewById(R.id.rwSearch)
        UIUtil.setLightNavigationBar(recyclerView, this)
        val fileAdapter = FLAdapter(viewModel, this)
        recyclerView.setAdapter(fileAdapter)
        viewModel.search("%$query%").observe(this) { documents ->
            if (documents!!.size > 0) {
                emptyLayout!!.visibility = View.GONE
                recyclerView.setVisibility(View.VISIBLE)
            } else {
                emptyLayout!!.visibility = View.VISIBLE
                recyclerView.setVisibility(View.GONE)
            }
            fileAdapter.setData(documents)
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(linearLayoutManager)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}