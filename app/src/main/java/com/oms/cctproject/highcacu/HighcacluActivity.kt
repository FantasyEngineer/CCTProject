package com.oms.cctproject.highcacu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oms.cctproject.R
import com.oms.cctproject.model.ShowInfo
import com.oms.cctproject.model.TaskInfo
import com.oms.cctproject.adapter.Myadapter
import com.oms.cctproject.model.TaskInfo.Companion.highName
import com.oms.cctproject.model.TaskInfo.Companion.lowName
import com.oms.cctproject.model.TaskInfo.Companion.midName
import com.oms.cctproject.model.TaskInfo.Companion.prohName
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_second.recyclerView
import java.util.*
import kotlin.collections.ArrayList

class HighcacluActivity : AppCompatActivity(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.RequestLoadMoreListener {
    override fun onLoadMoreRequested() {
        //---------------------------刷新轮旬-------------------------------------------------
        handler.postDelayed({
            endIndex += 50
            caclu(endIndex)
        }, 1000)

    }


    //---------------------------recyclerView每项点击-------------------------------------------------
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        var showInfo = adapter?.getItem(position) as ShowInfo
        var dialog = AlertDialog.Builder(this)
        var v = layoutInflater.inflate(R.layout.activity_dialog, null)
        dialog.setView(v)
        v.findViewById<TextView>(R.id.dialog_tv_content).text = showInfo.taskInfo
        dialog.create().show()
    }

    private val proNum = 1
    private val highNum = 4
    private val midNum = 8
    private val lowNum = 16
    //总投入的cct币数量
    var titalNum = 1000.00
    //总投入的天数
    private var titalDay1 = 10000
    //经过的天数
    private var titalDay = 10000
    private var weiWang: Double = 0.0
    //这里是任务详情
    var adapter: Myadapter? = null
    var day: String? = null
    var content: String? = null
    var handler: Handler = Handler()
    var baseList: ArrayList<TaskInfo>? = null
    var endIndex: Int = 50
    var isGet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highcaclu)

        //---------------------------------页面数据初始化-----------------------------------------------------
        Toast.makeText(this, "如果当天的收益足够买一个目标任务，那么一定先买任务再答题，切记切记", Toast.LENGTH_LONG).show()
        titalNum = this.intent.getStringExtra("num").toDouble()
        weiWang = this.intent.getStringExtra("weiwang").toDouble()
        isGet = this.intent.getBooleanExtra("isget", false)

        var liststr = this.intent.getStringExtra("list")
        val listType = object : TypeToken<List<TaskInfo>>() {}.type
        //获取前一个页面预设的任务
        baseList = Gson().fromJson(liststr, listType)
        //将任务添加到taskList
        taskList.addAll(this!!.baseList!!)
        tv_title.text = "总投入币数量$titalNum"

        //---------------------------------RecyclerView初始化-------------------------------------------------
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()//添加数据和删除数据的动画效果
        adapter = Myadapter(R.layout.item)
        recyclerView.adapter = adapter

        //---------------------------------监听---------------------------------------------------------------
        adapter!!.onItemClickListener = this
        adapter!!.setOnLoadMoreListener(this)
        //向下
        downBottom.setOnClickListener {
            recyclerView.scrollToPosition(adapter?.itemCount!!.minus(1))
        }
        //向上
        uptop.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        //---------------------------------首次计算50个--------------------------------------------------------
        caclu(endIndex)

    }

    //总的任务tasklist
    var taskList: ArrayList<TaskInfo> = ArrayList()
    //展示list
    var list = ArrayList<ShowInfo>()
    //临时金额，不参与答题之后的任务奖励，单纯为了计算本次的结余是否足够买任务
    var temporaryVariable: Double = 0.0

    private fun caclu(time: Int) {
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
            var cale = Calendar.getInstance()
            cale.add(Calendar.DAY_OF_MONTH, timeNum)
            var year = cale.get(Calendar.YEAR)
            var month = cale.get(Calendar.MONTH) + 1
            var data = cale.get(Calendar.DAY_OF_MONTH)

            day = ("${year}年${month}月${data}日，第${timeNum}天,资金$titalNum")
            temporaryVariable = titalNum
            //当日是否领取过任务cct奖励，如果领取，这里不进行任何处理
            if (timeNum == 0 && isGet) {
                taskList.forEach {
                    when (it.name) {
                        prohName -> {
                            proTaskNum++
                        }
                        highName -> {
                            highTaskNum++
                        }
                        midName -> {
                            midTaskNum++
                        }
                        lowName -> {
                            lowTaskNum++

                        }
                    }
                }
                content = ("当天已经领取过cct奖励，币值总量$titalNum\n")
                //购买任务
                if (proTaskNum < proNum) {
                    while (titalNum >= 10000) {
                        if (proTaskNum < proNum) {
                            //增加一个专家级任务
                            var taskInfo = TaskInfo.creatProTask()
                            taskList.add(taskInfo)
                            titalNum -= taskInfo.buyNeed
                            //专家级数量+1，自循环判定
                            proTaskNum++
                            content += ("购买专家级任务,扣除10000币，专家级任务实际数量为$proTaskNum\n任务完成前购买额外获得450.0\n")
                        } else {
                            content += ("专家级任务购买完成\n")
                            break
                        }
                    }
                }

                //当高级任务数量小于4的时候/*(midTaskNum == 0 || midTaskNum >= 8)*/
                if (highTaskNum <= highNum) {
                    //当总钱数大于1000的时候购买，购买之后总币数减少1000
                    while (titalNum >= 1000) {
                        if (highTaskNum < highNum) {
                            var taskInfo = TaskInfo.creatHighTask()
                            taskList.add(taskInfo)
                            titalNum -= taskInfo.buyNeed
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
                            var taskInfo = TaskInfo.creatMidTask()
                            taskList.add(taskInfo)
                            titalNum -= taskInfo.buyNeed
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
                            var taskInfo = TaskInfo.creatLowTask()
                            taskList.add(taskInfo)
                            titalNum -= taskInfo.buyNeed
                            lowTaskNum++
                            content += ("购买初级任务,扣除10币,初级任务实际数量为$lowTaskNum\n任务完成前购买额外获得0.4\n")
                        } else {
                            content += ("初级购买完成\n")
                            break
                        }
                    }
                }

                content += ("币值结余$titalNum\n")
            } else {
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
                content += ("高级任务个数为$highTaskNum，日产量$outputhighTask\n")
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
                    while (temporaryVariable >= 10000) {
                        if (proTaskNum < proNum) {
                            //增加一个专家级任务
                            var taskInfo = TaskInfo.creatProTask()
                            taskDeal(taskInfo)
                            temporaryVariable -= taskInfo.buyNeed
                            //专家级数量+1，自循环判定
                            proTaskNum++
                            content += ("购买专家级任务,扣除10000币，专家级任务实际数量为$proTaskNum\n任务完成前购买额外获得450.0\n")
                        } else {
                            content += ("专家级任务购买完成\n")
                            break
                        }
                    }
                } else {
                    content += ("专家级任务购买完成\n")
                }

                //当高级任务数量小于4的时候/*(midTaskNum == 0 || midTaskNum >= 8)*/
                if (highTaskNum <= highNum) {
                    //当总钱数大于1000的时候购买，购买之后总币数减少1000
                    while (temporaryVariable >= 1000) {
                        if (highTaskNum < highNum) {
                            var taskInfo = TaskInfo.creatHighTask()
                            taskDeal(taskInfo)
                            temporaryVariable -= taskInfo.buyNeed

                            //先答题
                            highTaskNum++
                            content += ("购买高级任务,扣除1000币，高级任务实际数量为$highTaskNum\n任务完成前购买额外获得42.666666666\n")
                        } else {
                            content += ("高级购买完成\n")
                            break
                        }
                    }
                } else {
                    content += ("高级购买完成\n")
                }

                //当中级任务数量小于8的时候
                if (midTaskNum < midNum) {
                    //当总币数大于100的时候购买，购买之后总币数减少100
                    while (temporaryVariable >= 100) {
                        if (midTaskNum < midNum) {
                            var taskInfo = TaskInfo.creatMidTask()
                            taskDeal(taskInfo)
                            temporaryVariable -= taskInfo.buyNeed

                            midTaskNum++
                            content += ("购买中级任务,扣除100币,中级任务实际数量为$midTaskNum\n任务完成前购买额外获得4.16666666\n")
                        } else {
                            content += ("中级购买完成\n")
                            break
                        }
                    }
                } else {
                    content += ("中级购买完成\n")
                }

                //当中级任务满的时候并且低级任务小于16的时候
                if (midTaskNum >= midNum && lowTaskNum <= lowNum) {
                    //当总币数大于10的时候购买，购买之后总币数减少10
                    while (temporaryVariable >= 10) {
                        if (midTaskNum >= midNum && lowTaskNum < lowNum) {
                            var taskInfo = TaskInfo.creatLowTask()
                            taskDeal(taskInfo)
                            temporaryVariable -= taskInfo.buyNeed

                            lowTaskNum++
                            content += ("购买初级任务,扣除10币,初级任务实际数量为$lowTaskNum\n任务完成前购买额外获得0.4\n")
                        } else {
                            content += ("初级购买完成\n")
                            break
                        }
                    }
                }

                content += ("币值结余$titalNum\n")
            }
            //判定一下每天的任务的时间
            taskList.forEach {
                taskInfo += "${it.name}，任务剩余时长${it.surplusTime}天\n"
            }
            //天数减一
            titalDay--
            //将当前的详情展示出来，并且将任务list同步到adapter中，用于弹窗展示
            list.add(ShowInfo(day, content, taskInfo, titalNum.toString(), taskList))
            //跳出循环
            if (timeNum > time) {
                break
            }


        }
        adapter?.setNewData(list)
    }

    private fun taskDeal(taskInfo: TaskInfo) {
        taskList.add(taskInfo)
        titalNum -= taskInfo.buyNeed
        //如果没有领取过任务，才可以累加
        titalNum += taskInfo.outputNumDaily
        taskInfo.surplusTime--
    }

}