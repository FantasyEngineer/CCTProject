package com.oms.cctproject.memo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.oms.cctproject.R
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

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
