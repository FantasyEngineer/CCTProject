package com.oms.cctproject.listener

interface ClickListener {
    fun delete(position: Int)

    fun copy(position: Int)

    fun modify(position: Int)
}