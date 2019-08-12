package com.oms.cctproject.memo

import android.app.Dialog
import android.content.DialogInterface
import android.icu.lang.UCharacter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oms.cctproject.R
import com.oms.cctproject.adapter.TypeSeclectAdapter
import com.oms.cctproject.listener.SingleClickListener
import com.oms.cctproject.model.TaskInfo
import com.oms.touchpoint.widget.D
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_note.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        type.setOnClickListener {
            var dialog: Dialog = Dialog(this)
            var v = layoutInflater.inflate(R.layout.dialog_type_select, null)
            var recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            var typeSeclectAdapter = TypeSeclectAdapter(R.layout.item_type_textview)
            recyclerView.adapter = typeSeclectAdapter
            val typelist = ArrayList<String>()
            typelist.add("餐饮零食")
            typelist.add("公交地铁")
            typelist.add("网上购物")
            typelist.add("火车动车")
            typelist.add("门票游玩")
            typelist.add("支付欠款")
            typelist.add("水电网费")
            typeSeclectAdapter.listener = object : SingleClickListener {
                override fun onclick(position: Int) {
                    type.text = typelist[position]
                    (dialog).dismiss()
                }
            }
            typeSeclectAdapter.setNewData(typelist)

            dialog.setContentView(v)
            dialog.show()

        }



        date.setText(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))




        confirm.setOnClickListener {
            if (TextUtils.isEmpty(price.text.toString())) {
                Toast.makeText(this, "金额不能为空", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(type.text.toString())) {
                Toast.makeText(this, "类型不能为空", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(note.text.toString())) {
                Toast.makeText(this, "备注不能为空", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var bundle = Bundle()
            bundle.putString("date", date.text.toString())
            bundle.putString("price", price.text.toString())
            bundle.putString("type", type.text.toString())
            bundle.putString("note", note.text.toString())
            bundle.putBoolean("isincome", false)
            intent.putExtra("bundle", bundle)
            setResult(1, intent)
            finish()
        }
    }
}
