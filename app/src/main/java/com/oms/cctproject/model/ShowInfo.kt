package com.oms.cctproject.model

class ShowInfo {
    var day: String? = null
    var content: String? = null
    var taskInfo: String? = null

    constructor(day: String?, content: String?, taskInfo: String?) {
        this.day = day
        this.content = content
        this.taskInfo = taskInfo
    }
}