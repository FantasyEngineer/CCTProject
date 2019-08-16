package com.oms.cctproject.memo

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.oms.cctproject.R
import com.oms.cctproject.StaticsActivity
import com.oms.cctproject.adapter.MemoAdatpter
import com.oms.cctproject.highcacu.InputHighCacluActivity
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.listener.PupClickListener
import com.oms.cctproject.listener.TypePupClickListener
import com.oms.cctproject.model.DayChartValueModel
import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.TypeChartValueModel
import com.oms.cctproject.model.VersionModel
import com.oms.cctproject.util.DateUtil
import com.oms.cctproject.util.Manager
import com.oms.cctproject.util.PrefUtil
import com.oms.cctproject.util.wheelview.WheelView
import com.oms.cctproject.util.wheelview.adapter.NumericWheelAdapter
import com.oms.touchpoint.widget.D
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_memo_home.*
import org.litepal.LitePal
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class MemoHomeActivity : BaseMemoActivity() {
    //    var path: String = Environment.getExternalStorageDirectory().absolutePath + "/memo.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_home)
        var mon = Calendar.getInstance().get(Calendar.MONTH) + 1
        month.text = Calendar.getInstance().get(Calendar.YEAR).toString() + "-" + dealMonth(mon)
        var searchtext = month.text.toString()
        /*查询数据库中所有的数据*/
//        allList = LitePal.findAll()
        showListByWord(searchtext)
        /*初始化recycleview*/
        recyclerView.layoutManager = LinearLayoutManager(this)
        memoAdatpter = MemoAdatpter(R.layout.item_memo)
        recyclerView.adapter = memoAdatpter
        memoAdatpter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        memoAdatpter!!.setNewData(allList)
        memoAdatpter?.bindToRecyclerView(recyclerView)
        memoAdatpter?.setEmptyView(R.layout.empty_view)
        memoAdatpter?.listener = onclickListener
        //记一笔
        note.setOnClickListener(this)
        //cct计算
        cctcaclu.setOnClickListener(this)
        pupSelect.setOnClickListener(this)
        tv_record.setOnClickListener(this)
        tv_statistics.setOnClickListener(this)
        select_type.setOnClickListener(this)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(p0: View?) {
        super.onClick(p0)
        when (p0?.id) {
            R.id.note -> startActivityForResult(Intent(this, NoteActivity().javaClass), 1)
            R.id.cctcaclu -> startActivity(Intent(this, InputHighCacluActivity::class.java))
            R.id.pupSelect -> {//时间选择
                isInitChart = true
                Manager.getManager().showDatePop(this, main, month)
                Manager.getManager().pupClickListener = object : PupClickListener {
                    override fun click(year1: Int, month1: Int) {
                        month.text = "$year1-" + String.format("%02d", month1)
                        var searchtext = month.text.toString()
                        showListByWord(searchtext)
                        memoAdatpter?.setNewData(allList)
                    }
                }
            }

            R.id.tv_record -> {
                tv_record.setBackgroundColor(getColor(R.color.stroke))
                tv_statistics.setBackgroundColor(getColor(R.color.black))
                ll_record.visibility = View.VISIBLE
                ll_statics.visibility = View.GONE
                memotitle.text = "账单"
            }
            R.id.tv_statistics -> {
                tv_record.setBackgroundColor(getColor(R.color.black))
                tv_statistics.setBackgroundColor(getColor(R.color.stroke))
                ll_record.visibility = View.GONE
                ll_statics.visibility = View.VISIBLE
                memotitle.text = "统计"
                if (isInitChart) {
                    initView()
                    initView2()
                    isInitChart = false
                }

            }
            R.id.select_type -> {
                Manager.getManager().showPupTypeSelect(this, rl_filter_layout)
                Manager.getManager().typePupClickListener = object : TypePupClickListener {
                    override fun type(type: String) {
                        if (type == "全部展示") {
                            memoAdatpter?.setNewData(allList)
                        }else{
//                            var typeList: ArrayList<ExpenseModel> = ArrayList()
//                            allList?.forEach{
//                                if(it.type==type){
//                                    typeList.add(it)
//                                }
//                            }
//                            memoAdatpter?.setNewData(typelist)
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据时间选择获取到list
     */
    private fun showListByWord(searchtext: String) {
        comeSum = 0.0
        outSum = 0.0
        allList = LitePal.where("date like ?", "%$searchtext%").find(ExpenseModel::class.java)
        allList?.reverse()
        allList?.forEach {
            if (it.isIncome) {
                comeSum += it.price.toDouble()
            } else {
                outSum += it.price.toDouble()
            }
        }
        expenses.text = "总支出  ￥$outSum"
        income.text = "总收入  ￥$comeSum"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis())),
        if (requestCode == 1 && resultCode == 1) {
            var bundle = data?.getBundleExtra("bundle")
            var expenseModel = ExpenseModel(
                bundle?.getString("date")!!,
                bundle?.getString("price")!!,
                bundle?.getString("type")!!,
                bundle?.getString("note")!!,
                bundle?.getBoolean("isincome", false)!!
            )
            /*保存到数据库*/
            D.showShort("是否保存成功：" + expenseModel.save().toString())
            //插入到list中，界面更新
            allList?.add(0, expenseModel)
            memoAdatpter!!.notifyDataSetChanged()

            if (expenseModel.isIncome) {
                comeSum += expenseModel.price.toDouble()
            } else {
                outSum += expenseModel.price.toDouble()
            }
            expenses.text = "总支出  ￥$outSum"
            income.text = "总收入  ￥$comeSum"
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    //侧滑打开的按钮的操作
    var onclickListener = object : ClickListener {
        override fun copy(position: Int) {

        }

        override fun delete(position: Int) {
            LitePal.delete(ExpenseModel::class.java, memoAdatpter!!.getItem(position)?.id!!)
            //总收入要减去
            if (memoAdatpter!!.getItem(position)?.isIncome!!) {
                comeSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
            } else {
                outSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
            }
            memoAdatpter!!.remove(position)
            this@MemoHomeActivity.runOnUiThread {
                expenses.text = "总支出  ￥$outSum"
                income.text = "总收入  ￥$comeSum"
            }
        }

        override fun modify(position: Int) {
            D.showShort("尚未开放")
        }

    }


    //------------------------------------下面是对统计做的操作---------------------------------------------------------------------
    private var typelist: ArrayList<TypeChartValueModel>? = null
    private var tf: Typeface? = null
    private var time: String? = null
    var isInitChart = true

    private fun initView() {
        time = month.text.toString()
        //获取数据
        typelist = getList()
        chart1.setUsePercentValues(true)

        chart1.description.isEnabled = false
        chart1.setExtraOffsets(5f, 10f, 5f, 5f)

        chart1.dragDecelerationFrictionCoef = 0.95f

        tf = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")

        chart1.setCenterTextTypeface(Typeface.createFromAsset(assets, "OpenSans-Light.ttf"))
        chart1.centerText = generateCenterSpannableText()

        chart1.setExtraOffsets(20f, 0f, 20f, 0f)

        chart1.isDrawHoleEnabled = true
        chart1.setHoleColor(Color.WHITE)

        chart1.setTransparentCircleColor(Color.WHITE)
        chart1.setTransparentCircleAlpha(110)

        chart1.holeRadius = 58f
        chart1.transparentCircleRadius = 61f

        chart1.setDrawCenterText(true)
//        chart1.centerText = "月度支出扇形图展示"

        chart1.rotationAngle = 0f
        // enable rotation of the chart1 by touch
        chart1.isRotationEnabled = false
        chart1.isHighlightPerTapEnabled = true

        // chart1.setUnit(" €");
        // chart1.setDrawUnitsInchart1(true);

        // add a selection listener
//        chart1.setOnChartValueSelectedListener(activity)

        chart1.animateY(1400, Easing.EaseInOutQuad)
        // chart1.spin(2000, 0, 360);

        val l = chart1.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = false

        setData()
    }


    private fun setData() {
        val entries = ArrayList<PieEntry>()
        for (i in 0 until this.typelist?.size!!) {
            entries.add(
                PieEntry(
                    typelist?.get(i)?.typeValueSum!!,
                    typelist?.get(i)?.typeName
                )
            )
        }

        val dataSet = PieDataSet(entries, "Election Results")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        val colors = ArrayList<Int>()

        for (c in ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS)
            colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS)
            colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS)
            colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS)
            colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);


        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        //dataSet.setUsingSliceColorAsValueLineColor(true);

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter() as ValueFormatter?)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        data.setValueTypeface(tf)
        chart1.setData(data)

        // undo all highlights
        chart1.highlightValues(null)

        chart1.invalidate()
    }


    private fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("$time\n支出比例展示")
        s.setSpan(RelativeSizeSpan(1.5f), 0, 8, 0)
//        s.setSpan(StyleSpan(Typeface.NORMAL), 7, s.length - 15, 0)
//        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
//        s.setSpan(RelativeSizeSpan(.65f), 14, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 7, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 7, s.length, 0)
        return s
    }


    //获取支出每项总额的list
    private fun getList(): ArrayList<TypeChartValueModel> {
        var allList1 = ArrayList<MutableList<ExpenseModel>>()

        var typeChartValueModels = ArrayList<TypeChartValueModel>()
        //获取所有种类的支出的数据
        Manager.getManager().typelist.forEach {
            var list = LitePal.where(
                "date like ? and type like ?",
                "%$time%",
                "%$it%"
            ).find(ExpenseModel::class.java)
            if (list.size != 0) {
                allList1.add(list)
            }
        }

        allList1.forEach {
            var typeChartValueModel: TypeChartValueModel? = null
            var typeNum: Float = 0.0f
            for (expenseModel in it) {
                typeNum += expenseModel.price.toFloat()
                typeChartValueModel = TypeChartValueModel(
                    expenseModel.type,
                    typeNum
                )
            }
            typeChartValueModels.add(typeChartValueModel!!)
        }
        return typeChartValueModels
    }


    //0----------------------------图表--每天的柱状图-------------------------------------------------------
    private fun initView2() {

        chart2.setDrawBarShadow(false)
        chart2.setDrawValueAboveBar(true)

        chart2.description.isEnabled = false

        // if more than 60 entries are displayed in the chart2, no values will be
        // drawn
        chart2.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        chart2.setPinchZoom(true)

        chart2.setDrawGridBackground(false)
        // chart2.setDrawYLabels(false);

        val xAxis = chart2.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTypeface(tf)
        xAxis.setDrawGridLines(false)
        xAxis.setGranularity(1f) // only intervals of 1 day
        xAxis.setLabelCount(15)

//        val custom = MyValueFormatter("￥")

        val leftAxis = chart2.getAxisLeft()
        leftAxis.setTypeface(tf)
        leftAxis.setLabelCount(8, false)
//        leftAxis.setValueFormatter(custom)
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.setSpaceTop(15f)
        leftAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)

        val rightAxis = chart2.getAxisRight()
        rightAxis.setDrawGridLines(true)
        rightAxis.setTypeface(tf)
        rightAxis.setLabelCount(8, true)
//        rightAxis.setValueFormatter(custom)
        rightAxis.setSpaceTop(15f)
        rightAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)

        val l = chart2.getLegend()
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT)
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        l.setDrawInside(false)
        l.setForm(Legend.LegendForm.SQUARE)
        l.setFormSize(9f)
        l.setTextSize(11f)
        l.setXEntrySpace(4f)

//        val mv = MarkerView(activity, null)
//        mv.setChartView(chart2) // For bounds control
//        chart2.setMarker(mv) // Set the marker to the chart

        // setting data
//        seekBarY.setProgress(50);
//        seekBarX.setProgress(12);

        // chart.setDrawLegend(false);
        setBarData()
    }


    private fun setBarData() {
        var dayChartValueModels = ArrayList<DayChartValueModel>()
        var dayallList = ArrayList<MutableList<ExpenseModel>>()
        //获取当月每天的的支出的数据
        for (i in 1..DateUtil.getMonthDays(time?.split("-")?.get(1)?.toInt()!!)) {
            var date: String = time + "-" + String.format("%02d", i)
            var list = LitePal.where(
                "date like ?",
                "%$date%"
            ).find(ExpenseModel::class.java)
            //将每天的数据都保存到每一个list中
            dayallList.add(list)
        }

        for ((index, value) in dayallList.withIndex()) {
            var dayChartValueModel: DayChartValueModel? = null
            var typeNum = 0.0f
            var day = index + 1
            if (value == null || value.size == 0) {
                dayChartValueModel = DayChartValueModel(
                    day.toString(),
                    0.0f
                )
            } else {
                for (ex in value) {
                    if (!ex.isIncome) {
                        typeNum += ex.price.toFloat()
                    }
                    dayChartValueModel = DayChartValueModel(
                        day.toString(),
                        typeNum
                    )
                }
            }
            dayChartValueModels.add(dayChartValueModel!!)
        }

        val values = ArrayList<BarEntry>()

        dayChartValueModels.forEach {
            values.add(BarEntry(it.day.toFloat(), it.dayValueSum))
        }

        val set1: BarDataSet

        if (chart2.getData() != null && chart2.getData().getDataSetCount() > 0) {
            set1 = chart2.getData().getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart2.getData().notifyDataChanged()
            chart2.notifyDataSetChanged()

        } else {
            set1 = BarDataSet(values, time)
            set1.setDrawIcons(false)
            //            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
            set1.setGradientColor(startColor, endColor);*/

            val startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light)
            val startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light)
            val startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light)
            val endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            val endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple)
            val endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark)
            val endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            val endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark)

            val gradientColors = ArrayList<GradientColor>()
            gradientColors.add(GradientColor(startColor1, endColor1))
            gradientColors.add(GradientColor(startColor2, endColor2))
            gradientColors.add(GradientColor(startColor3, endColor3))
            gradientColors.add(GradientColor(startColor4, endColor4))
            gradientColors.add(GradientColor(startColor5, endColor5))

            set1.gradientColors = gradientColors

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.setValueTypeface(tf)
            data.barWidth = 0.9f

            chart2.setData(data)
        }
    }


}