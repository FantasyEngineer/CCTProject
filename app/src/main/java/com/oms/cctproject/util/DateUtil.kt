package com.oms.cctproject.util

class DateUtil {

    companion object {
        fun getMonthDays(month: Int): Int {
            when (month) {
                1, 3, 5, 7, 8, 10, 11 -> return 31
                else -> return 30
            }
        }

    }
}