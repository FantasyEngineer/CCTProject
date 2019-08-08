package com.oms.cctproject.util

import android.content.Context
import android.os.Parcel
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

class LinearManager(context: Context?) :
    LinearLayoutManager(context) {

    private var isScrollEnabled = false

    fun setScrollEnabled(flag: Boolean) {
        this.isScrollEnabled = flag
    }


    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }
}
