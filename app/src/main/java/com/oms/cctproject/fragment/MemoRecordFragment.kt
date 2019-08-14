//package com.oms.cctproject.fragment
//
//import android.content.Context
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.util.DisplayMetrics
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.LinearLayout
//import android.widget.PopupWindow
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.chad.library.adapter.base.BaseQuickAdapter
//import com.oms.cctproject.R
//import com.oms.cctproject.adapter.MemoAdatpter
//import com.oms.cctproject.listener.ClickListener
//import com.oms.cctproject.memo.MemoHomeActivity
//import com.oms.cctproject.model.ExpenseModel
//import com.oms.cctproject.util.Manager
//import com.oms.cctproject.util.wheelview.WheelView
//import com.oms.cctproject.util.wheelview.adapter.NumericWheelAdapter
//import com.oms.touchpoint.widget.D
//import org.litepal.LitePal
//import java.util.*
//
//
//class MemoRecordFragment : Fragment() {
//
//    private var memoAdatpter: MemoAdatpter? = null
//    var allList: MutableList<ExpenseModel>? = null
//    var comeSum = 0.0
//    var outSum = 0.0
//    private var month: TextView? = null
//    private var expenses: TextView? = null
//    private var income: TextView? = null
//    private var pupSelect: LinearLayout? = null
//    private var main: LinearLayout? = null
//    private var recyclerView: RecyclerView? = null
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_memorecord, container, false)
//        month = view.findViewById<TextView>(R.id.month)
//        expenses = view.findViewById<TextView>(R.id.expenses)
//        income = view.findViewById<TextView>(R.id.income)
//        pupSelect = view.findViewById<LinearLayout>(R.id.pupSelect)
//        main = view.findViewById<LinearLayout>(R.id.main)
//        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
//        initView()
//        return view
//    }
//
//    private fun initView() {
//        if (Manager.getManager().record == null) {
//            var mon = Calendar.getInstance().get(Calendar.MONTH) + 1
//            month?.text = Calendar.getInstance().get(Calendar.YEAR).toString() + "年" + dealMonth(mon) + "月"
//        } else {
//            month?.text = Manager.getManager().record
//        }
//        var searchtext = month?.text?.split("年")!![0] + "-" + month?.text?.split("年")!![1].split("月")!![0]
//        /*查询数据库中所有的数据*/
////        allList = LitePal.findAll()
//        showListByWord(searchtext)
//        /*查找8月的*/
////        var l = LitePal.where("date like ?", "%" + "2019-08" + "%").find(ExpenseModel::class.java)
//        /*初始化recycleview*/
//        recyclerView?.layoutManager = LinearLayoutManager(activity)
//        memoAdatpter = MemoAdatpter(R.layout.item_memo)
//        recyclerView?.adapter = memoAdatpter
//        memoAdatpter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
//        memoAdatpter!!.setNewData(allList)
//        memoAdatpter?.bindToRecyclerView(recyclerView)
//        memoAdatpter?.setEmptyView(R.layout.empty_view)
//        memoAdatpter?.listener = object : ClickListener {
//            override fun copy(position: Int) {
//
//            }
//
//            override fun delete(position: Int) {
//                LitePal.delete(ExpenseModel::class.java, memoAdatpter!!.getItem(position)?.id!!)
//                //总收入要减去
//                if (memoAdatpter!!.getItem(position)?.isIncome!!) {
//                    comeSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
//                } else {
//                    outSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
//                }
//                memoAdatpter!!.remove(position)
//                activity?.runOnUiThread {
//                    expenses?.text = "总支出  ￥$outSum"
//                    income?.text = "总收入  ￥$comeSum"
//                }
//            }
//
//
//            override fun modify(position: Int) {
//                D.showShort("尚未开放")
//            }
//
//        }
//
//        pupSelect?.setOnClickListener {
//            showDatePop()
//            (activity as MemoHomeActivity).makeWindowDark()
//        }
//    }
//
//
//    //选择时间弹窗
//    private fun showDatePop() {
//        val metrics = DisplayMetrics()
//        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
//        var screenWidth = metrics.widthPixels
//        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val popupWindowView = inflater.inflate(R.layout.pop_date_select, null)
//        val tv_ok = popupWindowView.findViewById(R.id.btn_ok) as Button
//        var wl_year = popupWindowView.findViewById(R.id.wl_year) as WheelView
//        var wl_month = popupWindowView.findViewById(R.id.wl_month) as WheelView
//        val tv_cancel_time = popupWindowView.findViewById(R.id.btn_cancel) as Button
//        var popupWindow = PopupWindow(popupWindowView, screenWidth * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT, true)
//        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        popupWindow.isOutsideTouchable = true
//        initWheelView(popupWindowView)
//        popupWindow.showAtLocation(main, Gravity.CENTER, 0, 0)
//        popupWindow.setOnDismissListener { (activity as MemoHomeActivity).makeWindowLight() }
//        tv_ok.setOnClickListener {
//            var currentSelectYear = wl_year.currentItem + 2010//年
//            var currentSelectMonth = wl_month.currentItem + 1//月
////-----------------------------------处理数据------------------------------------------------------------------------------
//            month?.text = "${currentSelectYear}年" + String.format("%02d", currentSelectMonth) + "月"
//            var searchtext = currentSelectYear.toString() + "-" + String.format("%02d", currentSelectMonth)
//            showListByWord(searchtext)
//            memoAdatpter?.setNewData(allList)
////-----------------------------------处理数据------------------------------------------------------------------------------
//            popupWindow.dismiss()
//        }
//        tv_cancel_time.setOnClickListener { popupWindow.dismiss() }
//    }
//
//    private fun initWheelView(view: View) {
//        var wl_year = view.findViewById(R.id.wl_year) as WheelView
//        var wl_month = view.findViewById(R.id.wl_month) as WheelView
//
//        val numericWheelAdapterStart1 = NumericWheelAdapter(activity, 2010, 2020)
//        numericWheelAdapterStart1.setLabel("年")
//        wl_year?.viewAdapter = numericWheelAdapterStart1
//        numericWheelAdapterStart1.textColor = R.color.black
//        numericWheelAdapterStart1.textSize = 20
//        wl_year.isCyclic = false//是否可循环滑动
//
//        val numericWheelAdapterStart2 = NumericWheelAdapter(activity, 1, 12, "%02d")
//        numericWheelAdapterStart2.setLabel("月")
//        wl_month?.viewAdapter = numericWheelAdapterStart2
//        numericWheelAdapterStart2.textColor = R.color.black
//        numericWheelAdapterStart2.textSize = 20
//        wl_month.isCyclic = true
//        //设置默认
//        wl_year.currentItem = month?.text?.substring(0, 4)?.toInt()!! - 2010
//        wl_month.currentItem = month?.text?.substring(5, 7)?.toInt()!! - 1
//    }
//
//    private fun showListByWord(searchtext: String) {
//        comeSum = 0.0
//        outSum = 0.0
//        allList = LitePal.where("date like ?", "%$searchtext%").find(ExpenseModel::class.java)
//        allList?.reverse()
//        allList?.forEach {
//            if (it.isIncome) {
//                comeSum += it.price.toDouble()
//            } else {
//                outSum += it.price.toDouble()
//            }
//        }
//        expenses?.text = "总支出  ￥$outSum"
//        income?.text = "总收入  ￥$comeSum"
//    }
//
//
//    fun dealMonth(month: Int): String {
//        when (month) {
//            in 1..9 -> return "0$month"
//            else -> return month.toString()
//        }
//    }
//
//
//    override fun onDestroy() {
//        Manager.getManager().savaRecord(month?.text.toString())
//        super.onDestroy()
//    }
//
//}