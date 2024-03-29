package com.oms.cctproject.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.oms.cctproject.R
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.model.ExpenseModel

class MemoAdatpter(layoutResId: Int) :
    BaseQuickAdapter<ExpenseModel, BaseViewHolder>(layoutResId) {
    var listener: ClickListener? = null
        set(value) {
            field = value
        }

    override fun convert(helper: BaseViewHolder?, item: ExpenseModel?) {
        helper?.getView<TextView>(R.id.note)?.text = item?.note
        helper?.getView<TextView>(R.id.date)?.text = item?.date

        helper?.getView<TextView>(R.id.price)?.run {
            //是收入
            if (item?.isIncome!!) {
                this.text = "+" + item?.price.toString()
                this.setTextColor(resources.getColor(R.color.holo_red_dark))
            } else {//是支出
                this.text = "-" + item?.price.toString()
                this.setTextColor(resources.getColor(R.color.black))
            }
        }

        helper?.getView<TextView>(R.id.type)?.text = item?.type
        helper?.getView<TextView>(R.id.flag)?.text = item?.type?.get(0)?.toString()
        helper?.getView<TextView>(R.id.delete)?.setOnClickListener {
            listener?.delete(helper.position)
        }

        helper?.getView<TextView>(R.id.modify)?.setOnClickListener {
            listener?.modify(helper.position)
        }
    }

}