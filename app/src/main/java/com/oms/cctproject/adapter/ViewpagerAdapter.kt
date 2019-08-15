package com.oms.cctproject.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class ViewpagerAdapter : PagerAdapter() {
    var viewlist: ArrayList<View>? = null

    public fun ViewpagerAdapter(viewlist: ArrayList<View>) {

        this.viewlist = viewlist
    }


    override fun isViewFromObject(view: View, view1: Any): Boolean {
        return view == view1
    }

    override fun getCount(): Int {
        return viewlist?.size!!
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(viewlist?.get(position))
        return viewlist?.get(position)!!
    }
}