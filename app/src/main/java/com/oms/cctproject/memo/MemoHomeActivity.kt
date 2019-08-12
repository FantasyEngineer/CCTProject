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
import com.gyf.barlibrary.ImmersionBar
import com.oms.cctproject.R
import com.oms.cctproject.adapter.MemoAdatpter
import com.oms.cctproject.highcacu.InputHighCacluActivity
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.model.ExpenseModel
import com.oms.touchpoint.widget.D
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_memo_home.*
import kotlinx.android.synthetic.main.activity_memo_home.recyclerView
import org.litepal.LitePal
import org.litepal.extension.findAll
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class MemoHomeActivity : AppCompatActivity() {
    var path: String = Environment.getExternalStorageDirectory().absolutePath + "/memo.txt"
    private var memoAdatpter: MemoAdatpter? = null
    var allList: MutableList<ExpenseModel>? = null
    var comeSum = 0.0
    var outSum = 0.0
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
        var mon = Calendar.getInstance().get(Calendar.MONTH) + 1
        month.text = mon.toString() + "月"
        /*查询数据库中所有的数据*/
//        allList = LitePal.findAll()
        allList = LitePal.where("date like ?", "%" + "2019-"+dealMonth(mon) + "%").find(ExpenseModel::class.java)
        allList?.reverse()
        allList?.forEach {
            if (it.isIncome) {
                comeSum += it.price.toDouble()
            } else {
                outSum += it.price.toDouble()
            }
        }
        expenses.text = "总支出  ￥$outSum"
        income.text = "总收入  ￥$comeSum"

        /*查找8月的*/
//        var l = LitePal.where("date like ?", "%" + "2019-08" + "%").find(ExpenseModel::class.java)

        /*初始化recycleview*/
        recyclerView.layoutManager = LinearLayoutManager(this)
        memoAdatpter = MemoAdatpter(R.layout.item_memo)
        recyclerView.adapter = memoAdatpter
        memoAdatpter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        memoAdatpter!!.setNewData(allList)
        memoAdatpter?.bindToRecyclerView(recyclerView)
        memoAdatpter?.setEmptyView(R.layout.empty_view)
        memoAdatpter?.listener = object : ClickListener {
            override fun copy(position: Int) {

            }

            override fun delete(position: Int) {
                LitePal.delete(ExpenseModel::class.java, memoAdatpter!!.getItem(position)?.id!!)
                memoAdatpter!!.remove(position)
                //总收入要减去
                if (memoAdatpter!!.getItem(position)?.isIncome!!) {
                    comeSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
                } else {
                    outSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
                }
                expenses.text = "总支出  ￥$outSum"
                income.text = "总收入  ￥$comeSum"
            }


            override fun modify(position: Int) {
                D.showShort("尚未开放")
            }

        }

        //记一笔
        note.setOnClickListener {
            startActivityForResult(Intent(this, NoteActivity().javaClass), 1)
        }

        //cct计算
        cctcaclu.setOnClickListener {
            var intent = Intent(this, InputHighCacluActivity::class.java)
            startActivity(intent)
        }


        request()
    }

    private fun request() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        ).subscribe {
            if (it) {
            } else {
                finish()
            }
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
            D.showShort("是否保存成功：" + expenseModel.save().toString())
            //插入到list中，界面更新
            allList?.add(0, expenseModel)
            memoAdatpter!!.notifyItemChanged(0)

            if (expenseModel.isIncome) {
                comeSum += expenseModel.price.toDouble()
            } else {
                outSum += expenseModel.price.toDouble()
            }
            expenses.text = "总支出  ￥$outSum"
            income.text = "总收入  ￥$comeSum"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(false)
    }

    fun dealMonth(month: Int): String {
        when (month) {
            in 1..9 -> return "0$month"
            else -> return month.toString()
        }
    }

}