package com.oms.cctproject

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.SeekBar
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.TypeChartValueModel
import com.oms.cctproject.util.Manager
import com.oms.touchpoint.widget.D
import kotlinx.android.synthetic.main.activity_statics.*
import java.util.ArrayList

class StaticsActivity : AppCompatActivity(), OnChartValueSelectedListener {
    private var typelist: ArrayList<TypeChartValueModel>? = null
    private var tf: Typeface? = null

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null)
            return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statics)
        //获取数据
        typelist = getTypeChartValueList(Manager.getManager().allList!!)
        back.setOnClickListener { finish() }

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
        chart1.centerText = "月度支出扇形图展示"

        chart1.rotationAngle = 0f
        // enable rotation of the chart1 by touch
        chart1.isRotationEnabled = true
        chart1.isHighlightPerTapEnabled = true

        // chart1.setUnit(" €");
        // chart1.setDrawUnitsInchart1(true);

        // add a selection listener
        chart1.setOnChartValueSelectedListener(this)

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
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
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

        val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
        s.setSpan(RelativeSizeSpan(1.5f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
        s.setSpan(RelativeSizeSpan(.65f), 14, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 14, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 14, s.length, 0)
        return s
    }


    var type0: TypeChartValueModel? = null
    var type1: TypeChartValueModel? = null
    var type2: TypeChartValueModel? = null
    var type3: TypeChartValueModel? = null
    var type4: TypeChartValueModel? = null
    var type5: TypeChartValueModel? = null
    var type6: TypeChartValueModel? = null
    var type7: TypeChartValueModel? = null
    var type8: TypeChartValueModel? = null
    var type9: TypeChartValueModel? = null
    var type10: TypeChartValueModel? = null
    var type11: TypeChartValueModel? = null
    var type12: TypeChartValueModel? = null
    var type13: TypeChartValueModel? = null

    /**
     * 获取每种类型对应的总价，用于图表展示
     */
    private fun getTypeChartValueList(list: MutableList<ExpenseModel>): ArrayList<TypeChartValueModel> {
        list.forEach {
            //只显示支出
            if (!it.isIncome) {
                when (it.type) {
                    Manager.getManager().typelist[0] -> {
                        type0 = if (type0 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[0],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[0],
                                type0!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[1] -> {
                        type1 = if (type1 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[1],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[1],
                                type1!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[2] -> {
                        type2 = if (type2 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[2],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[2],
                                type2!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[3] -> {
                        type3 = if (type3 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[3],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[3],
                                type3!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[4] -> {
                        type4 = if (type4 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[4],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[4],
                                type4!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[5] -> {
                        type5 = if (type5 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[5],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[5],
                                type5!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[6] -> {
                        type6 = if (type6 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[6],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[6],
                                type6!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[7] -> {
                        type7 = if (type7 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[7],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[7],
                                type7!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[8] -> {
                        type8 = if (type8 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[8],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[8],
                                type8!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[9] -> {
                        type9 = if (type9 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[9],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[9],
                                type9!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[10] -> {
                        type10 = if (type10 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[10],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[10],
                                type10!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[11] -> {
                        type11 = if (type11 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[11],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[11],
                                type11!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[12] -> {
                        type12 = if (type12 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[12],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[12],
                                type12!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                    Manager.getManager().typelist[13] -> {
                        type13 = if (type13 == null) {
                            TypeChartValueModel(
                                Manager.getManager().typelist[13],
                                it.price.toFloat()
                            )
                        } else {
                            TypeChartValueModel(
                                Manager.getManager().typelist[13],
                                type13!!.typeValueSum + it.price.toFloat()
                            )
                        }
                    }
                }

            }
        }
        var list = ArrayList<TypeChartValueModel>()
        if (type0 != null)
            list.add(type0!!)
        if (type1 != null)
            list.add(type1!!)
        if (type2 != null)
            list.add(type2!!)
        if (type3 != null)
            list.add(type3!!)
        if (type4 != null)
            list.add(type4!!)
        if (type5 != null)
            list.add(type5!!)
        if (type6 != null)
            list.add(type6!!)
        if (type7 != null)
            list.add(type7!!)
        if (type8 != null)
            list.add(type8!!)
        if (type9 != null)
            list.add(type9!!)
        if (type10 != null)
            list.add(type10!!)
        if (type11 != null)
            list.add(type11!!)
        if (type12 != null)
            list.add(type12!!)
        if (type13 != null)
            list.add(type13!!)
        return list
    }
}
