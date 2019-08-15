package com.oms.cctproject.fragment

import android.content.Context
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
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.oms.cctproject.R
import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.TypeChartValueModel
import com.oms.cctproject.util.Manager
import com.oms.touchpoint.widget.D
import kotlinx.android.synthetic.main.fragment_memostatistics.*
import org.litepal.LitePal
import java.lang.reflect.Type
import java.util.ArrayList


class PieInChartFragment : Fragment() {
    private var typelist: ArrayList<TypeChartValueModel>? = null
    private var tf: Typeface? = null
    private lateinit var chart1: PieChart
    private var time: String? = null
    private var searchtext: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pie_income, container, false)
        chart1 = view.findViewById(R.id.chart1)
        initView()
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
        val s = SpannableString("$time\n收入比例展示")
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
        Manager.getManager().incomeTypelist.forEach {
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

}