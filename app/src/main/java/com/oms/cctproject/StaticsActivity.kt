package com.oms.cctproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.oms.cctproject.adapter.ViewpagerFragmentAdapter
import com.oms.cctproject.fragment.MemoStatisticsFragment
import com.oms.cctproject.fragment.PieExChartFragment
import com.oms.cctproject.fragment.PieInChartFragment
import kotlinx.android.synthetic.main.activity_statics.*
import java.util.*

class StaticsActivity : AppCompatActivity(), OnChartValueSelectedListener {
    var arrayList = ArrayList<Fragment>()

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null)
            return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statics)

        arrayList.add(PieExChartFragment())
        arrayList.add(PieInChartFragment())

        viewPager.adapter = ViewpagerFragmentAdapter(supportFragmentManager, arrayList)


        back.setOnClickListener { finish() }

        btnShowExPie.setOnClickListener {
            viewPager.setCurrentItem(0, true)
        }
        btnShowInPie.setOnClickListener {
            viewPager.setCurrentItem(1, true)
        }
    }

}
