package com.oms.cctproject.highcacu

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.oms.cctproject.R
import com.oms.cctproject.adapter.AddTaskAdapter
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.model.TaskInfo
import com.oms.cctproject.util.KeyBrodUtil
import com.oms.cctproject.util.PrefUtil
import kotlinx.android.synthetic.main.activity_input_high_caclu.*
import kotlinx.android.synthetic.main.activity_input_high_caclu.calcu
import kotlinx.android.synthetic.main.activity_input_high_caclu.etCCTnum
import kotlinx.android.synthetic.main.activity_input_high_caclu.etWeiwang

class InputHighCacluActivity : AppCompatActivity() {
    var adapter: AddTaskAdapter? = null

    var list: ArrayList<TaskInfo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_high_caclu)

        scrollView.setOnTouchListener { _: View, _: MotionEvent ->
            KeyBrodUtil.closeKeybord(etCCTnum, this)
            return@setOnTouchListener false
        }
        //------------------------初始化页面----------------------------------------------
//        val listType = object : TypeToken<List<TaskInfo>>() {}.type
        //创建一个只有个低级任务的列表，作为默认列表
//        var defaultlist: ArrayList<TaskInfo> = ArrayList()
//        defaultlist.add(TaskInfo.creatLowTask())
//        list = Gson().fromJson(PrefUtil.getString("list", Gson().toJson(defaultlist)), listType)

        etCCTnum.setText(PrefUtil.getString("cctnum", "1000"))
        //------------------------初始化页面结束------------------------------------------


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddTaskAdapter(R.layout.layout_addtask)
        recyclerView.adapter = adapter
        adapter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        adapter!!.setListener(object : ClickListener {
            override fun modify(position: Int) {
                var task = adapter?.data?.get(position)

                var dialog: AlertDialog.Builder =
                    AlertDialog.Builder(this@InputHighCacluActivity)
                var v = layoutInflater.inflate(R.layout.dialog_task_input, null)
                var spiname = v.findViewById<Spinner>(R.id.spiName)
                spiname.setSelection(
                    when (task?.name) {
                        TaskInfo.prohName -> 0
                        TaskInfo.highName -> 1
                        TaskInfo.midName -> 2
                        TaskInfo.lowName -> 3
                        else -> 0
                    }
                )

                var spinum = v.findViewById<Spinner>(R.id.spiNum)
                spinum.setSelection(
                    30 - task?.surplusTime!!
                )

                dialog.setNegativeButton("确认") { _: DialogInterface, _: Int ->
                    task?.name = spiname.selectedItem.toString()
                    task?.surplusTime = spinum.selectedItem.toString().toInt()
//                    task?.outputNumDaily = getOutputNumDaily(spiname.selectedItem.toString())
//                    task?.buyNeed = getBuyNeed(spiname.selectedItem.toString())
                    adapter!!.notifyItemChanged(position)
                }
                dialog.setView(v)
                dialog.create().show()

            }

            override fun copy(position: Int) {
                var task = adapter?.data?.get(position)
                if (task != null) {
                    list.add(position + 1, task)
                }
                adapter!!.notifyItemInserted(position + 1)
            }

            override fun delete(position: Int) {
                adapter!!.remove(position)
            }
        })
        adapter?.setNewData(list)

        //添加任务
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

        calcu.setOnClickListener {
            var intent = Intent(this, HighcacluActivity::class.java)
            intent.putExtra("num", etCCTnum.text.toString())
            intent.putExtra("weiwang", etWeiwang.text.toString())
            intent.putExtra("list", Gson().toJson(list))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("12312", "resume")
    }

    override fun onDestroy() {
        super.onDestroy()
//        PrefUtil.putString("list", Gson().toJson(list))
        PrefUtil.putString("cctnum", etCCTnum.text.toString())

    }


}
