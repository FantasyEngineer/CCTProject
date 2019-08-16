package com.oms.cctproject.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oms.cctproject.R
import com.oms.cctproject.adapter.TypeSeclectAdapter
import com.oms.cctproject.listener.PupClickListener
import com.oms.cctproject.listener.SingleClickListener
import com.oms.cctproject.listener.TypePupClickListener
import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.TypeChartValueModel
import com.oms.cctproject.util.wheelview.WheelView
import com.oms.cctproject.util.wheelview.adapter.NumericWheelAdapter
import com.oms.touchpoint.widget.D
import kotlinx.android.synthetic.main.activity_memo_home.*
import kotlinx.android.synthetic.main.activity_note.*

class Manager private constructor() {
    var pupClickListener: PupClickListener? = null
        get() = field
        set(value) {
            field = value
        }
    var typePupClickListener: TypePupClickListener? = null
        get() = field
        set(value) {
            field = value
        }
    var record: String? = null
    var allList: MutableList<ExpenseModel>? = null
    var searchTime: String? = null
        get() = field
        set(value) {
            field = value
        }

    var typelist = mutableListOf(
        "餐饮消费", "网络购物", "交通出行", "水果零食", "通讯费用",
        "美容健身", "家居日用", "娱乐社交", "学习办公", "烟酒消费",
        "房租水电", "购房贷款", "购房贷款", "服饰鞋品"
    )
    var incomeTypelist = mutableListOf(
        "工作薪水", "兼职所得", "链信卖币", "其他所得"
    )

    fun savaRecord(text: String) {
        this.record = text
    }

    fun setAlllist(allList: MutableList<ExpenseModel>?) {
        this.allList = allList
    }


    companion object {
        private var manager: Manager? = null

        fun getManager(): Manager {
            if (manager == null) {
                manager = Manager()
            }
            return manager as Manager
        }
    }


    /**
     * 展示分类筛选的pupwindow
     */
    fun showPupTypeSelect(context: Activity, locationView: View): PopupWindow {
        makeWindowDark(context)
        var typealllist = mutableListOf("全部展示")
        typealllist.addAll(typelist)
        typealllist.addAll(incomeTypelist)
        var inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.layout_select_type, null)
        var pupWindow =
            PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false)

        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        var typeSeclectAdapter = TypeSeclectAdapter(R.layout.item_type_textview)
        recyclerView.adapter = typeSeclectAdapter
        typeSeclectAdapter.setNewData(typealllist)
        typeSeclectAdapter.listener = object : SingleClickListener {
            override fun onclick(position: Int) {
                typePupClickListener?.type(typeSeclectAdapter.data[position])
                pupWindow.dismiss()
            }
        }
        pupWindow.isOutsideTouchable = true
        pupWindow.showAsDropDown(locationView)
        pupWindow.setOnDismissListener {
            makeWindowLight(context)
        }
        return pupWindow
    }


    //选择时间弹窗
    fun showDatePop(activity: Activity, loacationView: View, monthView: TextView) {
        makeWindowDark(activity)
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        var screenWidth = metrics.widthPixels
        val inflater = LayoutInflater.from(activity)
        val popupWindowView = inflater.inflate(R.layout.pop_date_select, null)
        val tv_ok = popupWindowView.findViewById(R.id.btn_ok) as Button
        var wl_year = popupWindowView.findViewById(R.id.wl_year) as WheelView
        var wl_month = popupWindowView.findViewById(R.id.wl_month) as WheelView
        val tv_cancel_time = popupWindowView.findViewById(R.id.btn_cancel) as Button
        var popupWindow = PopupWindow(popupWindowView, screenWidth * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true

        val numericWheelAdapterStart1 = NumericWheelAdapter(activity, 2010, 2020)
        numericWheelAdapterStart1.setLabel("")
        wl_year?.viewAdapter = numericWheelAdapterStart1
        numericWheelAdapterStart1.textColor = R.color.black
        numericWheelAdapterStart1.textSize = 20
        wl_year.isCyclic = false//是否可循环滑动

        val numericWheelAdapterStart2 = NumericWheelAdapter(activity, 1, 12, "%02d")
        numericWheelAdapterStart2.setLabel("")
        wl_month?.viewAdapter = numericWheelAdapterStart2
        numericWheelAdapterStart2.textColor = R.color.black
        numericWheelAdapterStart2.textSize = 20
        wl_month.isCyclic = true
        //设置默认
        wl_year.currentItem = monthView.text.substring(0, 4).toInt() - 2010
        wl_month.currentItem = monthView.text.substring(5, 7).toInt() - 1


        popupWindow.showAtLocation(loacationView, Gravity.CENTER, 0, 0)
        tv_ok.setOnClickListener {
            var currentSelectYear = wl_year.currentItem + 2010//年
            var currentSelectMonth = wl_month.currentItem + 1//月
            pupClickListener?.click(currentSelectYear, currentSelectMonth)
            popupWindow.dismiss()
        }
        tv_cancel_time.setOnClickListener { popupWindow.dismiss() }
        popupWindow.setOnDismissListener {
            makeWindowLight(activity)
        }
    }


    /**
     * 让屏幕变暗
     */
    protected fun makeWindowDark(activity: Activity) {
        val window = activity.window
        val lp = window.attributes
        lp.alpha = 0.5f
        window.attributes = lp
    }

    /**
     * 让屏幕变亮
     */
    protected fun makeWindowLight(activity: Activity) {
        val window = activity.window
        val lp = window.attributes
        lp.alpha = 1f
        window.attributes = lp
    }

}
