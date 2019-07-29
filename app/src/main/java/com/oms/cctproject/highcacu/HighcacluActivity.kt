package com.oms.cctproject.highcacu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.oms.cctproject.R
import com.oms.cctproject.model.ShowInfo
import com.oms.cctproject.model.TaskInfo
import com.oms.cctproject.adapter.Myadapter
import kotlinx.android.synthetic.main.activity_second.*

class HighcacluActivity : AppCompatActivity(), BaseQuickAdapter.OnItemClickListener {
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        var showInfo = adapter?.getItem(position) as ShowInfo
        var dialog = AlertDialog.Builder(this)
        var v = layoutInflater.inflate(R.layout.activity_highcaclu, null)
        dialog.setView(v)
        v.findViewById<TextView>(R.id.dialog_tv_content).text = showInfo.taskInfo
        dialog.create().show()
    }

    val proNum = 2
    val highNum = 4
    val midNum = 8
    val lowNum = 16
    val prohName = "专家级任务"
    val highName = "高级任务"
    val midName = "中级任务"
    val lowName = "初级任务"
    //总投入的cct币数量
    var titalNum = 1000.00
    //总投入的天数
    private var titalDay1 = 90
    //经过的天数
    private var titalDay = 90
    private var weiWang: Double = 0.0
    //这里是任务详情
    var adapter: Myadapter? = null
    var day: String? = null
    var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highcaclu)
        //向下
        downBottom.setOnClickListener {
            recyclerView.scrollToPosition(adapter?.itemCount!!.minus(1))
        }
        //向上
        uptop.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        Toast.makeText(this, "如果当天的收益足够买一个目标任务，那么一定先买任务再答题，切记切记", Toast.LENGTH_LONG).show()

        titalNum = this.intent.getStringExtra("num").toDouble()
        titalDay1 = this.intent.getStringExtra("day").toInt()
        weiWang = this.intent.getStringExtra("weiwang").toDouble()
        titalDay = titalDay1

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()//添加数据和删除数据的动画效果
        adapter = Myadapter(R.layout.item)
        recyclerView.adapter = adapter
        caclu()
        adapter!!.onItemClickListener = this
    }

    //总的任务tasklist
    var taskList: ArrayList<TaskInfo> = ArrayList()

    private fun caclu() {
        //展示list
        var list = ArrayList<ShowInfo>()
        tv_title.text = "总投入币数量$titalNum,总投资时间${titalDay1}天"
        //默认赠送一个初级任务
        taskList.add(TaskInfo(lowName, 30, 10, 0.4))
        while (titalDay >= 0) {
            //遍历区分高中低级任务，计算每天的产出
            var proTaskNum = 0
            var highTaskNum: Int = 0
            var midTaskNum: Int = 0
            var lowTaskNum: Int = 0
            var outputproTask = 0.0
            var outputhighTask: Double = 0.0//高级任务产出
            var outputmidTask: Double = 0.0//中级任务产出
            var outputlowTask: Double = 0.0//低级任务产出
            var taskInfo = ""

            var timeNum = titalDay1 - titalDay
            day = ("第${timeNum}天")

            taskList.forEach {
                when (it.name) {
                    prohName -> {
                        proTaskNum++
                        //每天对应等级的任务日产量
                        outputproTask += it.outputNumDaily
                    }
                    highName -> {
                        highTaskNum++
                        outputhighTask += it.outputNumDaily
                    }
                    midName -> {
                        midTaskNum++
                        outputmidTask += it.outputNumDaily

                    }
                    lowName -> {
                        lowTaskNum++
                        outputlowTask += it.outputNumDaily

                    }
                }
                //产量总额
                titalNum += it.outputNumDaily
                //每个任务的完成次数要减一
                it.surplusTime--
            }
            content = ("专家级任务个数为$proTaskNum，日产量$outputproTask\n")
            content = ("高级任务个数为$highTaskNum，日产量$outputhighTask\n")
            content += ("中级任务个数为$midTaskNum，日产量$outputmidTask\n")
            content += ("低级任务个数为$lowTaskNum，日产量$outputlowTask\n")
            //威望值判定每天产币
            titalNum += weiWang / 0.05 * 0.0183
            content += ("威望值日产量${weiWang / 0.05 * 0.0183}\n")
            content += ("币值总量$titalNum\n")

            //判定任务失效,时间归零，删除任务
            var iterator = taskList.iterator()
            while (iterator.hasNext()) {
                var integer = iterator.next()
                if (integer.surplusTime == 0) {
                    iterator.remove()
                    content += ("${integer.name}过期，删除\n")
                    when (integer.name) {
                        prohName -> {
                            proTaskNum--
                        }
                        highName -> {
                            highTaskNum--
                        }
                        midName -> {
                            midTaskNum--
                        }
                        lowName -> {
                            lowTaskNum--
                        }
                    }
                }
            }
            //购买任务
            if (proTaskNum < proNum) {
                while (titalNum >= 10000) {
                    //增加一个专家级任务
                    titalNum -= 10000//币值扣除
                    var taskInfo = TaskInfo(prohName, 30, 10000, 450.0)
                    //当天完成回答,额外获得，次数减一
                    titalNum += 450.0
                    taskInfo.surplusTime--
                    taskList.add(taskInfo)
                    //专家级数量+1，自循环判定
                    proTaskNum++
                    content += ("购买专家级任务,扣除10000币，专家级任务实际数量为$proTaskNum\n任务完成前购买额外获得450.0\n")
                }
            }

            //当高级任务数量小于4的时候
            if (highTaskNum <= highNum && (midTaskNum == 0 || midTaskNum >= 8)) {
                //当总钱数大于1000的时候购买，购买之后总币数减少1000
                while (titalNum >= 1000) {
                    if (highTaskNum < highNum) {
                        taskList.add(TaskInfo(highName, 29, 1000, 42.666666666))
                        titalNum -= 1000
                        titalNum += 42.666666666
                        //先答题
                        highTaskNum++

                        content += ("购买高级任务,扣除1000币，高级任务实际数量为$highTaskNum\n任务完成前购买额外获得42.666666666\n")
                    } else {
                        content += ("高级购买完成\n")
                        break
                    }
                }
            }

            //当中级任务数量小于8的时候
            if (midTaskNum < midNum) {
                //当总币数大于100的时候购买，购买之后总币数减少100
                while (titalNum >= 100) {
                    if (midTaskNum < midNum) {
                        taskList.add(TaskInfo(midName, 29, 100, 4.16666666))
                        titalNum -= 100
                        titalNum += 4.16666666
                        midTaskNum++
                        content += ("购买中级任务,扣除100币,中级任务实际数量为$midTaskNum\n任务完成前购买额外获得4.16666666\n")
                    } else {
                        content += ("中级购买完成\n")
                        break
                    }
                }
            }

            //当中级任务满的时候并且低级任务小于16的时候
            if (midTaskNum >= midNum && lowTaskNum <= lowNum) {
                //当总币数大于10的时候购买，购买之后总币数减少10
                while (titalNum >= 10) {
                    if (midTaskNum >= midNum && lowTaskNum < lowNum) {
                        taskList.add(TaskInfo(lowName, 29, 10, 0.4))
                        titalNum += 4.16666666
                        titalNum -= 0.4
                        lowTaskNum++
                        content += ("购买初级任务,扣除10币,初级任务实际数量为$lowTaskNum\n任务完成前购买额外获得0.4\n")
                    } else {
                        content += ("初级购买完成\n")
                        break
                    }
                }
            }

            content += ("币值结余$titalNum\n")

            //判定一下每天的任务的时间
            taskList.forEach {
                taskInfo += "${it.name}，任务剩余时长${it.surplusTime}天\n"
            }

            //天数减一
            titalDay--
            //将当前的详情展示出来，并且将任务list同步到adapter中，用于弹窗展示
            list.add(ShowInfo(day, content, taskInfo))
        }
        adapter?.setNewData(list)
    }

    /**
     * log 输出
     */
    fun log(msg: String, vararg tags: String) {
        Log.i(if (tags.isNotEmpty()) tags[0] else "log-i", msg)
    }

    private fun creatProTask(): TaskInfo {
        return TaskInfo(prohName, 30, 10000, 450.0)
    }

    private fun creatHighTask(): TaskInfo {
        return TaskInfo(highName, 30, 1000, 42.666666666)
    }

    private fun creatMidTask(): TaskInfo {
        return TaskInfo(midName, 30, 100, 4.16666666)
    }

    private fun creatLowTask(): TaskInfo {
        return TaskInfo(lowName, 30, 10, 0.4)
    }

}
