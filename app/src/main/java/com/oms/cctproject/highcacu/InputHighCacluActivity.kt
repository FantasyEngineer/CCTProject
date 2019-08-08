package com.oms.cctproject.highcacu

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oms.cctproject.R
import com.oms.cctproject.adapter.AddTaskAdapter
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.model.TaskInfo
import com.oms.cctproject.model.TaskInfo.Companion.getBuyNeed
import com.oms.cctproject.model.TaskInfo.Companion.getOutputNumDaily
import com.oms.cctproject.util.FileIOUtils
import com.oms.cctproject.util.KeyBrodUtil
import com.oms.cctproject.util.LinearManager
import com.oms.cctproject.util.PrefUtil
import kotlinx.android.synthetic.main.activity_input_high_caclu.*
import kotlinx.android.synthetic.main.activity_input_high_caclu.calcu
import kotlinx.android.synthetic.main.activity_input_high_caclu.etCCTnum
import kotlinx.android.synthetic.main.activity_input_high_caclu.etWeiwang
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class InputHighCacluActivity : AppCompatActivity(), ClickListener {
    var adapter: AddTaskAdapter? = null
    var isGet: Boolean = false
    var path: String = Environment.getExternalStorageDirectory().absolutePath + "/user1"
    var pathcct: String = Environment.getExternalStorageDirectory().absolutePath + "/usercctnum"


    var list: ArrayList<TaskInfo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_high_caclu)

        scrollView.setOnTouchListener { _: View, _: MotionEvent ->
            KeyBrodUtil.closeKeybord(etCCTnum, this)
            return@setOnTouchListener false
        }

        /*是否领取过*/
        isReceive.setOnCheckedChangeListener { _, b ->
            isGet = b
        }
        //------------------------初始化页面----------------------------------------------
        val listType = object : TypeToken<List<TaskInfo>>() {}.type
//        创建一个只有个低级任务的列表，作为默认列表
        var defaultlist: ArrayList<TaskInfo> = ArrayList()
        defaultlist.add(TaskInfo.creatLowTask())
        list = Gson().fromJson(PrefUtil.getString("list", Gson().toJson(defaultlist)), listType)

        etCCTnum.setText(PrefUtil.getString("cctnum", "1000"))
        //------------------------初始化页面结束------------------------------------------

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddTaskAdapter(R.layout.layout_addtask)
        recyclerView.adapter = adapter
        adapter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        adapter!!.setListener(this)
        adapter?.setNewData(list)

        //添加任务,弹出提示框
        addTask.setOnClickListener {
            var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            var v = layoutInflater.inflate(R.layout.dialog_task_input, null)
            var spiname = v.findViewById<Spinner>(R.id.spiName)
            var spinum = v.findViewById<Spinner>(R.id.spiNum)

            dialog.setNegativeButton("确认") { _: DialogInterface, _: Int ->
                when (spiname.selectedItem) {
                    TaskInfo.prohName -> list.add(TaskInfo.creatProTask(spinum.selectedItem.toString().toInt()))
                    TaskInfo.highName -> list.add(TaskInfo.creatHighTask(spinum.selectedItem.toString().toInt()))
                    TaskInfo.midName -> list.add(TaskInfo.creatMidTask(spinum.selectedItem.toString().toInt()))
                    TaskInfo.lowName -> list.add(TaskInfo.creatLowTask(spinum.selectedItem.toString().toInt()))
                }
                adapter!!.notifyItemChanged(list.size - 1)
            }
            dialog.setView(v)
            dialog.create().show()
        }

        /*计算*/
        calcu.setOnClickListener {
            var intent = Intent(this, HighcacluActivity::class.java)
            intent.putExtra("num", etCCTnum.text.toString())
            intent.putExtra("weiwang", etWeiwang.text.toString())
            intent.putExtra("list", Gson().toJson(list))
            intent.putExtra("isget", isGet)
            startActivity(intent)
        }

        /*保存任务*/
        save.setOnClickListener {
            if (list.size == 0) {
                Toast.makeText(this, "列表为空，无法保存", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            FileIOUtils.writeFileFromString(File(path), Gson().toJson(list), false)
            FileIOUtils.writeFileFromString(File(pathcct), etCCTnum.text.toString(), false)
        }

        /*恢复任务列表*/
        resave.setOnClickListener {
            var defaultlist: ArrayList<TaskInfo> = ArrayList()
            defaultlist.add(TaskInfo.creatLowTask())
            list = Gson().fromJson(FileIOUtils.readFile2String(File(path)), listType)
            adapter!!.setNewData(list)
            etCCTnum.setText(FileIOUtils.readFile2String(File(pathcct)))
        }
    }


    override fun onStop() {
        super.onStop()
        PrefUtil.putString("list", Gson().toJson(adapter?.data))
        PrefUtil.putString("cctnum", etCCTnum.text.toString())
    }

    /**
     * 任务删除
     */
    override fun delete(position: Int) {
        adapter!!.remove(position)

    }

    /**
     * 任务复制
     */
    override fun copy(position: Int) {
        var task = adapter?.data?.get(position)
        var name: String = task?.name.toString()
        var surplus: Int = task?.surplusTime!!
        var output: Double = task?.outputNumDaily
        var buyneed: Int = task?.buyNeed
        var tasknew = TaskInfo(name, surplus, buyneed, output)
        if (tasknew != null) {
            list.add(position + 1, tasknew)
        }
        adapter!!.notifyDataSetChanged()
        recyclerView.smoothScrollToPosition(list.size)
    }

    /**
     * 任务修改
     */
    override fun modify(position: Int) {
        var task = adapter?.data?.get(position)
        var dialog: AlertDialog.Builder =
            AlertDialog.Builder(this@InputHighCacluActivity)
        var v = layoutInflater.inflate(R.layout.dialog_task_input, null)
        var spiname = v.findViewById<Spinner>(R.id.spiName)
        var spinum = v.findViewById<Spinner>(R.id.spiNum)
        spiname.setSelection(
            when (task?.name) {
                TaskInfo.prohName -> 0
                TaskInfo.highName -> 1
                TaskInfo.midName -> 2
                TaskInfo.lowName -> 3
                else -> 0
            }
        )
        spinum.setSelection(
            30 - task?.surplusTime!!
        )

        dialog.setNegativeButton("确认") { _: DialogInterface, _: Int ->
            adapter?.data?.get(position)?.name = spiname.selectedItem.toString()
            adapter?.data?.get(position)?.surplusTime = spinum.selectedItem.toString().toInt()
            adapter?.data?.get(position)?.outputNumDaily = getOutputNumDaily(spiname.selectedItem.toString())
            adapter?.data?.get(position)?.buyNeed = getBuyNeed(spiname.selectedItem.toString())
            adapter!!.notifyDataSetChanged()
        }
        dialog.setView(v)
        dialog.create().show()
    }

}
