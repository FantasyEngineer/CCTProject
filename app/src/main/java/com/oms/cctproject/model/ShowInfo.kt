package com.oms.cctproject.model

class ShowInfo {
    var day: String? = null  //第几天
    var content: String? = null  //产量等详情
    var taskInfo: String? = null //任务相关，用于弹窗使用

    var titalPrice: String? = null //当天剩余cct数
    var taskList: ArrayList<TaskInfo>? = null

    constructor(
        day: String?,
        content: String?,
        taskInfo: String?,
        titalPrice: String?,
        taskList: ArrayList<TaskInfo>?
    ) {
        this.day = day
        this.content = content
        this.taskInfo = taskInfo
        this.titalPrice = titalPrice
        this.taskList = taskList
    }
}