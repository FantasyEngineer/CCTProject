package com.oms.cctproject.memo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.lang.UCharacter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oms.cctproject.R
import com.oms.cctproject.adapter.TypeSeclectAdapter
import com.oms.cctproject.listener.SingleClickListener
import com.oms.cctproject.model.TaskInfo
import com.oms.cctproject.util.wheelview.WheelView
import com.oms.cctproject.util.wheelview.adapter.NumericWheelAdapter
import com.oms.touchpoint.widget.D
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.activity_note.note
import kotlinx.android.synthetic.main.pop_date_full_select.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteActivity : AppCompatActivity(), View.OnClickListener {

    var isincome = false
    var typelist = mutableListOf(
        "餐饮消费", "网络购物", "交通出行", "水果零食", "通讯费用",
        "美容健身", "家居日用", "娱乐社交", "学习办公", "烟酒消费",
        "房租水电", "购房贷款", "购房贷款", "服饰鞋品"
    )
    private var incomeTypelist = mutableListOf(
        "工作薪水", "兼      职", "链信卖币", "智慧晶卖币", "其      他"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        date.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        type.setOnClickListener(this)
        expenses_flag.setOnClickListener(this)
        income_flag.setOnClickListener(this)
        confirm.setOnClickListener(this)
        date.setOnClickListener(this)
        back.setOnClickListener { finish() }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.expenses_flag -> {
                isincome = false
                expenses_flag.setBackgroundColor(resources.getColor(R.color.black))
                expenses_flag.setTextColor(resources.getColor(R.color.white))
                income_flag.setBackgroundColor(resources.getColor(R.color.white))
                income_flag.setTextColor(resources.getColor(R.color.black))
            }
            R.id.income_flag -> {
                isincome = true
                income_flag.setBackgroundColor(resources.getColor(R.color.black))
                income_flag.setTextColor(resources.getColor(R.color.white))
                expenses_flag.setBackgroundColor(resources.getColor(R.color.white))
                expenses_flag.setTextColor(resources.getColor(R.color.black))
            }
            R.id.type -> {
                var dialog: Dialog = Dialog(this)
                var v = layoutInflater.inflate(R.layout.dialog_type_select, null)
                var recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
                var typeSeclectAdapter = TypeSeclectAdapter(R.layout.item_type_textview)
                recyclerView.adapter = typeSeclectAdapter
                typeSeclectAdapter.listener = object : SingleClickListener {
                    override fun onclick(position: Int) {
                        if (isincome) {
                            type.text = incomeTypelist[position]
                        } else {
                            type.text = typelist[position]
                        }
                        (dialog).dismiss()
                    }
                }
                if (isincome) {
                    typeSeclectAdapter.setNewData(incomeTypelist)
                } else {
                    typeSeclectAdapter.setNewData(typelist)

                }
                dialog.setContentView(v)
                dialog.show()
            }
            R.id.confirm -> {
                if (TextUtils.isEmpty(price.text.toString())) {
                    Toast.makeText(this, "金额不能为空", Toast.LENGTH_LONG).show()
                    return
                }
                if (TextUtils.isEmpty(type.text.toString())) {
                    Toast.makeText(this, "类型不能为空", Toast.LENGTH_LONG).show()
                    return
                }
                if (TextUtils.isEmpty(note.text.toString())) {
                    Toast.makeText(this, "备注不能为空", Toast.LENGTH_LONG).show()
                    return
                }

                var bundle = Bundle()
                bundle.putString("date", date.text.toString())
                bundle.putString("price", price.text.toString())
                bundle.putString("type", type.text.toString())
                bundle.putString("note", note.text.toString())
                bundle.putBoolean("isincome", isincome)
                intent.putExtra("bundle", bundle)
                setResult(1, intent)
                finish()
            }
            R.id.date -> {
                showDatePop()
            }
        }
    }


    //选择时间弹窗
    @SuppressLint("SetTextI18n")
    private fun showDatePop() {
        makeWindowDark()
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        var screenWidth = metrics.widthPixels
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupWindowView = inflater.inflate(R.layout.pop_date_full_select, null)
        val tv_ok = popupWindowView.findViewById(R.id.btn_ok) as TextView
        var wl_year = popupWindowView.findViewById(R.id.wl_year) as WheelView
        var wl_month = popupWindowView.findViewById(R.id.wl_month) as WheelView
        var wl_day = popupWindowView.findViewById(R.id.wl_day) as WheelView
        var wl_hour = popupWindowView.findViewById(R.id.wl_hour) as WheelView
        var wl_min = popupWindowView.findViewById(R.id.wl_min) as WheelView
        var wl_sec = popupWindowView.findViewById(R.id.wl_sec) as WheelView
        var popupWindow = PopupWindow(popupWindowView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        initWheelView(wl_year, wl_month, wl_day, wl_hour, wl_min, wl_sec)
        popupWindow.showAtLocation(main, Gravity.BOTTOM, 0, 0)
        popupWindow.setOnDismissListener { makeWindowLight() }
        tv_ok.setOnClickListener {
            var currentSelectYear = wl_year.currentItem + 2010//年
            var currentSelectMonth = wl_month.currentItem + 1//月
            var currentSelectDay = wl_day.currentItem + 1//日
            var currentSelectHour = wl_hour.currentItem + 1//日
            var currentSelectMin = wl_min.currentItem + 1//日
            var currentSelectSec = wl_sec.currentItem + 1//日

//-----------------------------------处理数据------------------------------------------------------------------------------
            date.text =
                "$currentSelectYear-" + dealMonth(currentSelectMonth) + "-" + dealMonth(currentSelectDay) + " " + dealMonth(
                    currentSelectHour
                ) + ":" + dealMonth(currentSelectMin) + ":" + dealMonth(currentSelectSec)
//-----------------------------------处理数据------------------------------------------------------------------------------
            popupWindow.dismiss()
        }
//        tv_cancel_time.setOnClickListener { popupWindow.dismiss() }
    }

    private fun initWheelView(vararg view: WheelView) {
        var wl_year = view[0]
        var wl_month = view[1]
        var wl_day = view[2]
        var wl_hour = view[3]
        var wl_min = view[4]
        var wl_sec = view[5]

        val numericWheelAdapterStart1 = NumericWheelAdapter(this, 2010, 2020)
        numericWheelAdapterStart1.setLabel("年")
        wl_year?.viewAdapter = numericWheelAdapterStart1
        numericWheelAdapterStart1.textColor = R.color.black
        numericWheelAdapterStart1.textSize = 18
        wl_year.isCyclic = true//是否可循环滑动

        val numericWheelAdapterStart2 = NumericWheelAdapter(this, 1, 12, "%02d")
        numericWheelAdapterStart2.setLabel("月")
        wl_month?.viewAdapter = numericWheelAdapterStart2
        numericWheelAdapterStart2.textColor = R.color.black
        numericWheelAdapterStart2.textSize = 18
        wl_month.isCyclic = true

        val numericWheelAdapterStart3 = NumericWheelAdapter(this, 1, 31, "%02d")
        numericWheelAdapterStart3.setLabel("日")
        wl_day?.viewAdapter = numericWheelAdapterStart3
        numericWheelAdapterStart3.textColor = R.color.black
        numericWheelAdapterStart3.textSize = 18
        wl_day.isCyclic = true

        val numericWheelAdapterStart4 = NumericWheelAdapter(this, 1, 24, "%02d")
        numericWheelAdapterStart4.setLabel("时")
        wl_hour?.viewAdapter = numericWheelAdapterStart4
        numericWheelAdapterStart4.textColor = R.color.black
        numericWheelAdapterStart4.textSize = 18
        wl_hour.isCyclic = true

        val numericWheelAdapterStart5 = NumericWheelAdapter(this, 1, 60, "%02d")
        numericWheelAdapterStart5.setLabel("分")
        wl_min?.viewAdapter = numericWheelAdapterStart5
        numericWheelAdapterStart5.textColor = R.color.black
        numericWheelAdapterStart5.textSize = 18
        wl_min.isCyclic = true

        val numericWheelAdapterStart6 = NumericWheelAdapter(this, 1, 60, "%02d")
        numericWheelAdapterStart6.setLabel("秒")
        wl_sec?.viewAdapter = numericWheelAdapterStart6
        numericWheelAdapterStart6.textColor = R.color.black
        numericWheelAdapterStart6.textSize = 18
        wl_sec.isCyclic = true
        //设置默认
        wl_year.currentItem = date.text.split("-")[0].toInt() - 2010
        wl_month.currentItem = date.text.split("-")[1].toInt() - 1
        wl_day.currentItem = date.text.split("-")[2].split(" ")[0].toInt() - 1
        wl_hour.currentItem = date.text.split("-")[2].split(" ")[1].split(":")[0].toInt() - 1
        wl_min.currentItem = date.text.split("-")[2].split(" ")[1].split(":")[1].toInt() - 1
        wl_sec.currentItem = date.text.split("-")[2].split(" ")[1].split(":")[2].toInt() - 1

    }

    /**
     * 让屏幕变暗
     */
    private fun makeWindowDark() {
        val window = window
        val lp = window.attributes
        lp.alpha = 0.5f
        window.attributes = lp
    }

    /**
     * 让屏幕变亮
     */
    private fun makeWindowLight() {
        val window = window
        val lp = window.attributes
        lp.alpha = 1f
        window.attributes = lp
    }


    fun dealMonth(month: Int): String {
        when (month) {
            in 1..9 -> return "0$month"
            else -> return month.toString()
        }
    }
}
