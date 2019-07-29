package com.oms.cctproject.model

import android.util.Log

data class TaskInfo(
    val name: String,//任务名称
    var surplusTime: Int,//任务生效天数
    val buyNeed: Int,//购买任务需要的币的数量
    val outputNumDaily: Double,//每天产出币数量
    val flag: Long = System.currentTimeMillis()
)

