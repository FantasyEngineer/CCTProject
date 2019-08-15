package com.oms.cctproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewpagerFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    lateinit var fm: FragmentManager
    lateinit var list: ArrayList<Fragment>

    constructor(fm: FragmentManager, list: ArrayList<Fragment>) : this(fm) {
        this.fm = fm
        this.list = list
    }

    override fun getItem(position: Int): Fragment? {
        return list.get(position)
    }

    override fun getCount(): Int {
        return list.size
    }
}
