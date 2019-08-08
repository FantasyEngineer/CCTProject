package com.oms.cctproject.model

/**
 * 消费实体
 */
class ExpenseModel {
    var data: String? = null//消费时间
    var price: Double? = null  //消费金额
    var type: String? = null //类型
    var note: String? = null //备忘
    var isIncome: Boolean = false //是收入吗？ 默认是支出

    constructor(
        data: String?,
        price: Double?,
        type: String?,
        note: String?,
        isIncome: Boolean
    ) {
        this.data = data
        this.price = price
        this.type = type
        this.note = note
        this.isIncome = isIncome
    }

    constructor(
        data: String?,
        price: Double?,
        type: String?,
        note: String?
    ) {
        this.data = data
        this.price = price
        this.type = type
        this.note = type
    }
}