package com.babanomania.pdfscanner.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.babanomania.pdfscanner.R
import com.babanomania.pdfscanner.databinding.LayoutIntroBinding

class IntroViewPagerAdapter(
    private val mContext: Context,
    private val mListScreen: List<IntroItem>,
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen = LayoutIntroBinding.inflate(inflater, container, false)
        layoutScreen.introTitle.text = mListScreen[position].title
        layoutScreen.introDescription.text = mListScreen[position].description
        layoutScreen.introImg.setImageResource(mListScreen[position].screenImg)
        container.addView(layoutScreen.root)
        return layoutScreen.root
    }

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}