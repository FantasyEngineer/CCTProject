package com.oms.cctproject.memo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oms.cctproject.R
import com.oms.cctproject.adapter.MemoAdatpter
import com.oms.cctproject.model.ExpenseModel
import kotlinx.android.synthetic.main.activity_memo_home.*
import kotlinx.android.synthetic.main.activity_memo_home.recyclerView
import org.litepal.LitePal
import org.litepal.extension.findAll
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat

class MemoHomeActivity : AppCompatActivity() {
    var path: String = Environment.getExternalStorageDirectory().absolutePath + "/memo.txt"
    private var memoAdatpter: MemoAdatpter? = null
    var allList: MutableList<ExpenseModel>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_home)
        //如果没有该文件就创建
//        if (!File(path).exists()) {
//            var expenseModel = ExpenseModel(12.90, "餐饮美食", "测试数据", false)
//            list.add(expenseModel)
//            FileIOUtils.writeFileFromString(File(path), Gson().toJson(list), false)
//        }

        /*获取数据*/
//        val listType = object : TypeToken<List<ExpenseModel>>() {}.type
//        var str = FileIOUtils.readFile2String(File(path))
//        if (str != null || str != "") {
//            list = (Gson().fromJson(str, listType))
//        }
        /*查询数据库中所有的数据*/
        allList = LitePal.findAll<ExpenseModel>()

        /*查找8月的*/
//        var l = LitePal.where("date like ?", "%" + "2019-08" + "%").find(ExpenseModel::class.java)

        /*初始化recycleview*/
        recyclerView.layoutManager = LinearLayoutManager(this)
        memoAdatpter = MemoAdatpter(R.layout.item_memo)
        recyclerView.adapter = memoAdatpter
        memoAdatpter!!.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT)
        memoAdatpter!!.setNewData(allList)

        //记一笔
        note.setOnClickListener {
            startActivityForResult(Intent(this, NoteActivity().javaClass), 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis())),
        if (requestCode == 1 && resultCode == 1) {
            var bundle = data?.getBundleExtra("bundle")
            var expenseModel = ExpenseModel(
                bundle?.getString("date")!!,
                bundle?.getString("price")!!,
                bundle?.getString("type")!!,
                bundle?.getString("note")!!,
                bundle?.getBoolean("isincome", false)!!
            )
            /*保存到数据库*/
            expenseModel.save()
            //插入到list中，界面更新
            allList?.add(0, expenseModel)
            memoAdatpter!!.notifyItemChanged(0)
        }
    }

}