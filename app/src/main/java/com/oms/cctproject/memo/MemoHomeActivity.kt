package com.oms.cctproject.memo

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.oms.cctproject.R
import com.oms.cctproject.StaticsActivity
import com.oms.cctproject.adapter.MemoAdatpter
import com.oms.cctproject.highcacu.InputHighCacluActivity
import com.oms.cctproject.listener.ClickListener
import com.oms.cctproject.model.ExpenseModel
import com.oms.cctproject.model.VersionModel
import com.oms.cctproject.util.Manager
import com.oms.cctproject.util.PrefUtil
import com.oms.cctproject.util.wheelview.WheelView
import com.oms.cctproject.util.wheelview.adapter.NumericWheelAdapter
import com.oms.touchpoint.widget.D
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_memo_home.*
import org.litepal.LitePal
import java.util.*

class MemoHomeActivity : AppCompatActivity() {
    //    var path: String = Environment.getExternalStorageDirectory().absolutePath + "/memo.txt"
    private var memoAdatpter: MemoAdatpter? = null
    var allList: MutableList<ExpenseModel>? = null
    var comeSum = 0.0
    var outSum = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_home)
        var mon = Calendar.getInstance().get(Calendar.MONTH) + 1
        month.text = Calendar.getInstance().get(Calendar.YEAR).toString() + "年" + dealMonth(mon) + "月"
        var searchtext = Calendar.getInstance().get(Calendar.YEAR).toString() + "-" + dealMonth(mon)
        /*查询数据库中所有的数据*/
//        allList = LitePal.findAll()
        showListByWord(searchtext)
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
                //总收入要减去
                if (memoAdatpter!!.getItem(position)?.isIncome!!) {
                    comeSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
                } else {
                    outSum -= memoAdatpter!!.getItem(position)?.price?.toDouble()!!
                }
                memoAdatpter!!.remove(position)
                this@MemoHomeActivity.runOnUiThread {
                    expenses.text = "总支出  ￥$outSum"
                    income.text = "总收入  ￥$comeSum"
                }
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

        pupSelect.setOnClickListener {
            showDatePop()
            makeWindowDark()
        }

        tv_statistic.setOnClickListener {
            Manager.getManager().setAlllist(memoAdatpter!!.data)
            Manager.getManager().searchTime = month.text.toString()
            var intent = Intent(this, StaticsActivity::class.java)
            startActivity(intent)
        }


        request()
        requestNewVersion()
    }

    private fun showListByWord(searchtext: String) {
        comeSum = 0.0
        outSum = 0.0
        allList = LitePal.where("date like ?", "%$searchtext%").find(ExpenseModel::class.java)
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
            memoAdatpter!!.notifyDataSetChanged()

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


    //选择时间弹窗
    private fun showDatePop() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        var screenWidth = metrics.widthPixels
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupWindowView = inflater.inflate(R.layout.pop_date_select, null)
        val tv_ok = popupWindowView.findViewById(R.id.btn_ok) as Button
        var wl_year = popupWindowView.findViewById(R.id.wl_year) as WheelView
        var wl_month = popupWindowView.findViewById(R.id.wl_month) as WheelView
        val tv_cancel_time = popupWindowView.findViewById(R.id.btn_cancel) as Button
        var popupWindow = PopupWindow(popupWindowView, screenWidth * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        initWheelView(popupWindowView)
        popupWindow.showAtLocation(main, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener { makeWindowLight() }
        tv_ok.setOnClickListener {
            var currentSelectYear = wl_year.currentItem + 2010//年
            var currentSelectMonth = wl_month.currentItem + 1//月
//-----------------------------------处理数据------------------------------------------------------------------------------
            month.text = "${currentSelectYear}年" + String.format("%02d", currentSelectMonth) + "月"
            var searchtext = currentSelectYear.toString() + "-" + String.format("%02d", currentSelectMonth)
            showListByWord(searchtext)
            memoAdatpter?.setNewData(allList)
//-----------------------------------处理数据------------------------------------------------------------------------------
            popupWindow.dismiss()
        }
        tv_cancel_time.setOnClickListener { popupWindow.dismiss() }
    }

    private fun initWheelView(view: View) {
        var wl_year = view.findViewById(R.id.wl_year) as WheelView
        var wl_month = view.findViewById(R.id.wl_month) as WheelView

        val numericWheelAdapterStart1 = NumericWheelAdapter(this, 2010, 2020)
        numericWheelAdapterStart1.setLabel("年")
        wl_year?.viewAdapter = numericWheelAdapterStart1
        numericWheelAdapterStart1.textColor = R.color.black
        numericWheelAdapterStart1.textSize = 20
        wl_year.isCyclic = false//是否可循环滑动

        val numericWheelAdapterStart2 = NumericWheelAdapter(this, 1, 12, "%02d")
        numericWheelAdapterStart2.setLabel("月")
        wl_month?.viewAdapter = numericWheelAdapterStart2
        numericWheelAdapterStart2.textColor = R.color.black
        numericWheelAdapterStart2.textSize = 20
        wl_month.isCyclic = true
        //设置默认
        wl_year.currentItem = month.text.substring(0, 4).toInt() - 2010
        wl_month.currentItem = month.text.substring(5, 7).toInt() - 1
    }

    /**
     * 让屏幕变暗
     */
    private fun makeWindowDark() {
        val window = window
        val lp = window.attributes
        lp.alpha = 0.5f
        window.attributes = lp
    }

    /**
     * 让屏幕变亮
     */
    private fun makeWindowLight() {
        val window = window
        val lp = window.attributes
        lp.alpha = 1f
        window.attributes = lp
    }


    /**
     * 请求新版本
     */
    private fun requestNewVersion() {
        OkGo.get<String>("https://raw.githubusercontent.com/FantasyEngineer/DocumentCenter/master/newfile.txt?time=" + System.currentTimeMillis())
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    var versionModel =
                        Gson().fromJson<VersionModel>(response?.body().toString(), VersionModel::class.java)
                    if (versionModel.version != getVerName(this@MemoHomeActivity)) {
                        showUpdataDialog(versionModel)
                    }

                }
            })
    }

    private fun showUpdataDialog(versionModel: VersionModel) {
        var alerdialog = AlertDialog.Builder(this)
        alerdialog.setMessage(versionModel.content)
        alerdialog.setPositiveButton("更新") { _, _ ->
            D.showShort("正在下载${versionModel.version}版本")
            var uri = Uri.parse(versionModel.url)
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }
        alerdialog.create().show()

    }

    public fun getVerName(context: Context): String {
        var verName = ""
        try {
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace();
        }
        return verName;
    }


}