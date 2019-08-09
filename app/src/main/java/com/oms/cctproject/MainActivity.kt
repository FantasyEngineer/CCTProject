package com.oms.cctproject

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.callback.Callback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.oms.cctproject.highcacu.InputHighCacluActivity
import com.oms.cctproject.memo.MemoHomeActivity
import com.oms.cctproject.util.FileIOUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observer
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.tablemanager.Connector
import java.io.File

class MainActivity : AppCompatActivity() {

    var db: SQLiteDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Connector.getDatabase()


        calcu.setOnClickListener {
            var intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("num", etCCTnum.text.toString())
            intent.putExtra("day", etDay.text.toString())
            intent.putExtra("weiwang", etWeiwang.text.toString())
            startActivity(intent)
        }

        goHighCaclu.setOnClickListener {
            var intent = Intent(this, InputHighCacluActivity::class.java)
            startActivity(intent)
        }
        goMemo.setOnClickListener {
            var intent = Intent(this, MemoHomeActivity::class.java)
            startActivity(intent)
        }

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

        request()
    }

    private fun request() {
        OkGo.get<String>("https://raw.githubusercontent.com/FantasyEngineer/DocumentCenter/master/newfile.txt")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response.toString()
                }
            })
    }
}
