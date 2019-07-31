package com.oms.cctproject.model

import android.util.Log

data class TaskInfo(
    var name: String,//任务名称
    var surplusTime: Int,//任务生效天数
    var buyNeed: Int,//购买任务需要的币的数量
    var outputNumDaily: Double,//每天产出币数量
    val flag: Long = System.currentTimeMillis()
) {
    companion object {
        val prohName = "专家级任务"
        val highName = "高级任务"
        val midName = "中级任务"
        val lowName = "初级任务"
        fun creatProTask(): TaskInfo {
            return TaskInfo(prohName, 30, 10000, 450.0)
        }

        fun creatProTask(num: Int): TaskInfo {
            return TaskInfo(prohName, num, 10000, 450.0)
        }


        fun creatHighTask(): TaskInfo {
            return TaskInfo(highName, 30, 1000, 42.666666666)
        }

        fun creatHighTask(num: Int): TaskInfo {
            return TaskInfo(highName, num, 1000, 42.666666666)
        }


        fun creatMidTask(): TaskInfo {
            return TaskInfo(midName, 30, 100, 4.16666666)
        }

        fun creatMidTask(num: Int): TaskInfo {
            return TaskInfo(midName, num, 100, 4.16666666)
        }

        fun creatLowTask(): TaskInfo {
            return TaskInfo(lowName, 30, 10, 0.4)
        }

        fun creatLowTask(num: Int): TaskInfo {
            return TaskInfo(lowName, num, 10, 0.4)
        }

        //根据名字获取到产出
        fun getOutputNumDaily(name: String): Double {
            return when (name) {
                prohName -> 450.0
                highName -> 42.666666666
                midName -> 4.16666666
                lowName -> 0.4
                else -> 0.0
            }
        }

        //根据名字获取到所需
        fun getBuyNeed(name: String): Int {
            return when (name) {
                prohName -> 10000
                highName -> 1000
                midName -> 100
                lowName -> 10
                else -> 0
            }
        }


    }


}

