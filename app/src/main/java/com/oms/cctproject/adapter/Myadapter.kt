package com.oms.cctproject.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.oms.cctproject.R
import com.oms.cctproject.model.ShowInfo

class Myadapter(layoutResId: Int) :
    BaseQuickAdapter<ShowInfo, BaseViewHolder>(layoutResId) {
    override fun convert(helper: BaseViewHolder?, item: ShowInfo?) {
        helper!!.getView<TextView>(R.id.tv_day).text = item?.day
        helper!!.getView<TextView>(R.id.tv_content).text = item?.content
    }

}