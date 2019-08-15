package com.oms.cctproject.model

import android.util.Log

data class DayChartValueModel(
    var day: String,//哪天
    var dayValueSum: Float = 0.0f  //哪天的总消费
)