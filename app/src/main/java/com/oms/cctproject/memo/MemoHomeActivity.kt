package com.oms.cctproject.memo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oms.cctproject.R
import com.oms.cctproject.adapter.MemoAdatpter
import com.oms.cctproject.adapter.MemoAdatpter1
import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.TaskInfo
import com.oms.cctproject.util.FileIOUtils
import kotlinx.android.synthetic.main.activity_input_high_caclu.*
import kotlinx.android.synthetic.main.activity_memo_home.*
import kotlinx.android.synthetic.main.activity_memo_home.recyclerView
import java.io.File

class MemoHomeActivity : AppCompatActivity() {
    var path: String = Environment.getExternalStorageDirectory().absolutePath + "/memo.txt"
    var list: ArrayList<ExpenseModel> = ArrayList()
    private var memoAdatpter: MemoAdatpter1? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_home)
        //如果没有该文件就创建
        var expenseModel = ExpenseModel("2019-8-8 17:00", 12.90, "餐饮美食", "测试数据", false)
        list.add(expenseModel)
        FileIOUtils.writeFileFromString(File(path), Gson().toJson(list), false)


        /*获取数据*/
        val listType = object : TypeToken<List<TaskInfo>>() {}.type
        var str = FileIOUtils.readFile2String(File(path))
        if (str != null || str != "") {
            list = (Gson().fromJson(str, listType))
        }


        /*初始化recycleview*/
        recyclerView.layoutManager = LinearLayoutManager(this)
        memoAdatpter = MemoAdatpter1(R.layout.item_memo)
        recyclerView.adapter = memoAdatpter
        memoAdatpter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        memoAdatpter!!.setNewData(list)


        //记一笔
        note.setOnClickListener {
            var expenseModel = ExpenseModel("2019-8-8 17:00", 12.90, "餐饮美食", "去美食广场吃了一顿饭")
            list.add(expenseModel)
            FileIOUtils.writeFileFromString(File(path), Gson().toJson(list), false)
            memoAdatpter!!.notifyDataSetChanged()
        }

    }
}
