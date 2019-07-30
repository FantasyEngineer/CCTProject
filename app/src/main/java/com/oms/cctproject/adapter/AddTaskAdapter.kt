package com.oms.cctproject.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.oms.cctproject.R
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.model.TaskInfo

class AddTaskAdapter(layoutResId: Int) :
    BaseQuickAdapter<TaskInfo, BaseViewHolder>(layoutResId) {

    private var listener: ClickListener? = null
    fun setListener(lis: ClickListener) {
        this.listener = lis
    }

    override fun convert(helper: BaseViewHolder?, item: TaskInfo?) {
        helper!!.getView<TextView>(R.id.taskName).text = item?.name
        helper!!.getView<TextView>(R.id.taskTime).text = item?.surplusTime.toString()
        helper.getView<TextView>(R.id.delete).setOnClickListener {
            listener?.delete(helper.position)
        }
        helper.getView<TextView>(R.id.modify).setOnClickListener {
            listener?.modify(helper.position)
        }
        helper.getView<TextView>(R.id.copy).setOnClickListener {
            listener?.copy(helper.position)
        }
    }


}