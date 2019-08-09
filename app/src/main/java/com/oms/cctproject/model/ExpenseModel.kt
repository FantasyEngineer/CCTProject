package com.oms.cctproject.model

import org.litepal.crud.LitePalSupport
import java.util.*

/**
 * 消费实体
 */
data class ExpenseModel(
    var date: String,
    var price: String,
    var type: String,
    var note: String,
    var isIncome: Boolean
) : LitePalSupport() {
    val id: Long = 0
}