package com.oms.cctproject.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.utils.ColorTemplate
import com.oms.cctproject.R
import com.oms.cctproject.model.DayChartValueModel
import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.TypeChartValueModel
import com.oms.cctproject.util.DateUtil
import com.oms.cctproject.util.Manager
import org.litepal.LitePal
import java.util.*


class PieExChartFragment : Fragment() {
    private var typelist: ArrayList<TypeChartValueModel>? = null
    private var tf: Typeface? = null
    private lateinit var chart1: PieChart
    private lateinit var chart2: BarChart
    private var time: String? = null
    private var searchtext: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pie, container, false)
        chart1 = view.findViewById(R.id.chart1)
        chart2 = view.findViewById(R.id.chart2)
        initView()
        initView2()
        return view
    }

    private fun initView() {
        time = Manager.getManager().searchTime.toString()
        searchtext = time!!.split("年")[0] + "-" + time!!.split("年")[1].split("月")[0]
        typelist = getList()
        //获取数据
//        typelist = getTypeChartValueList(Manager.getManager().allList!!)
        chart1.setUsePercentValues(true)

        chart1.description.isEnabled = false
        chart1.setExtraOffsets(5f, 10f, 5f, 5f)

        chart1.dragDecelerationFrictionCoef = 0.95f

        tf = Typeface.createFromAsset(activity?.assets, "OpenSans-Regular.ttf")

        chart1.setCenterTextTypeface(Typeface.createFromAsset(activity?.assets, "OpenSans-Light.ttf"))
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

        // add a lot of colors

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
        data.setValueFormatter(PercentFormatter())
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

    var allList = ArrayList<MutableList<ExpenseModel>>()

    //获取支出每项总额的list
    private fun getList(): ArrayList<TypeChartValueModel> {
        var typeChartValueModels = ArrayList<TypeChartValueModel>()
        //获取所有种类的支出的数据
        Manager.getManager().typelist.forEach {
            var list = LitePal.where(
                "date like ? and type like ?",
                "%$searchtext%",
                "%$it%"
            ).find(ExpenseModel::class.java)
            if (list.size != 0) {
                allList.add(list)
            }
        }

        allList.forEach {
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
        for (i in 1..DateUtil.getMonthDays(searchtext?.split("-")?.get(1)?.toInt()!!)) {
            var date: String = searchtext + "-" + String.format("%02d", i)
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


        val start = 1f

        val values = ArrayList<BarEntry>()

        dayChartValueModels.forEach {
            values.add(BarEntry(it.day.toFloat(), it.dayValueSum))
        }

//        var i = start.toInt()
//        while (i < start + count) {
//            val `val` = (Math.random() * (range + 1)).toFloat()
//
//            //            if (Math.random() * 100 < 25) {
//            //                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
//            //            } else {
//            Log.d(this.javaClass.toString(), i.toString() + "")
//            values.add(BarEntry(i.toFloat(), `val`))
//            i++
//            //            }
//        }

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

            val startColor1 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_orange_light)
            val startColor2 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_blue_light)
            val startColor3 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_orange_light)
            val startColor4 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_green_light)
            val startColor5 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_red_light)
            val endColor1 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_blue_dark)
            val endColor2 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_purple)
            val endColor3 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_green_dark)
            val endColor4 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_red_dark)
            val endColor5 = ContextCompat.getColor(activity?.applicationContext!!, android.R.color.holo_orange_dark)

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