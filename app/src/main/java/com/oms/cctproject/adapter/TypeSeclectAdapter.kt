package com.oms.cctproject.adapter

import android.text.Html
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.oms.cctproject.R
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.listener.SingleClickListener
import com.oms.cctproject.model.ShowInfo

class TypeSeclectAdapter(layoutResId: Int) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId) {
    var listener: SingleClickListener? = null

    override fun convert(helper: BaseViewHolder?, item: String?) {
        var textView = helper?.getView<TextView>(R.id.textview)
        textView?.text = item!!
        textView?.setOnClickListener {
            listener?.onclick(helper?.position!!)
        }
    }


}