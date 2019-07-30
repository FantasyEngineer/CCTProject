package com.oms.cctproject.highcacu

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.oms.cctproject.R
import com.oms.cctproject.SecondActivity
import com.oms.cctproject.adapter.AddTaskAdapter
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.model.TaskInfo
import kotlinx.android.synthetic.main.activity_input_high_caclu.*
import kotlinx.android.synthetic.main.activity_input_high_caclu.calcu
import kotlinx.android.synthetic.main.activity_input_high_caclu.etCCTnum
import kotlinx.android.synthetic.main.activity_input_high_caclu.etDay
import kotlinx.android.synthetic.main.activity_input_high_caclu.etWeiwang

class InputHighCacluActivity : AppCompatActivity() {
    var adapter: AddTaskAdapter? = null

    var list: ArrayList<TaskInfo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_high_caclu)


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
            intent.putExtra("day", etDay.text.toString())
            intent.putExtra("weiwang", etWeiwang.text.toString())
            intent.putExtra("list", Gson().toJson(list))
            startActivity(intent)
        }
    }

}
