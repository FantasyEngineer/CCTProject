package com.oms.cctproject.util

import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.TypeChartValueModel

class Manager private constructor() {
    var record: String? = null
    var allList: MutableList<ExpenseModel>? = null

    var typelist = mutableListOf(
        "餐饮消费", "网络购物", "交通出行", "水果零食", "通讯费用",
        "美容健身", "家居日用", "娱乐社交", "学习办公", "烟酒消费",
        "房租水电", "购房贷款", "购房贷款", "服饰鞋品"
    )
    var incomeTypelist = mutableListOf(
        "工作薪水", "兼职", "链信卖币", "智慧晶卖币", "其他"
    )


    fun savaRecord(text: String) {
        this.record = text
    }

    fun setAlllist(allList: MutableList<ExpenseModel>?) {
        this.allList = allList
    }


    companion object {
        private var manager: Manager? = null

        fun getManager(): Manager {
            if (manager == null) {
                manager = Manager()
            }
            return manager as Manager
        }
    }


}
