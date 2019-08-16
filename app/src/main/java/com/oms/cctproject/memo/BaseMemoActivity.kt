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

abstract class BaseMemoActivity : AppCompatActivity(),View.OnClickListener {
    //app更新的接口
    var url =
        "https://raw.githubusercontent.com/FantasyEngineer/DocumentCenter/master/newfile.txt?time=" + System.currentTimeMillis()
    var memoAdatpter: MemoAdatpter? = null
    var allList: MutableList<ExpenseModel>? = null
    var comeSum = 0.0
    var outSum = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request()
        requestNewVersion()
    }

    /**
     * 请求新版本
     */
    private fun requestNewVersion() {
        OkGo.get<String>(url)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    var versionModel =
                        Gson().fromJson<VersionModel>(response?.body().toString(), VersionModel::class.java)
                    if (versionModel.version != getVerName(this@BaseMemoActivity)) {
                        showUpdataDialog(versionModel)
                    }

                }
            })
    }

    /**
     * 展示更新弹窗
     */
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

    /**
     * 获取版本号
     */
    public fun getVerName(context: Context): String {
        var verName = ""
        try {
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace();
        }
        return verName;
    }


    /**
     * 请求权限
     */
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


    fun dealMonth(month: Int): String {
        when (month) {
            in 1..9 -> return "0$month"
            else -> return month.toString()
        }
    }

    override fun onClick(p0: View?) {

    }


}