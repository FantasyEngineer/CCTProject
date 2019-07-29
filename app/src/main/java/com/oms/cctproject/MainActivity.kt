package com.oms.cctproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oms.cctproject.highcacu.InputHighCacluActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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

    }
}
