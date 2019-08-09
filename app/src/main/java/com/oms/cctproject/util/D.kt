package com.oms.touchpoint.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object D {
    private var toast: Toast? = null
    private var context: Context? = null
    /*使用lateinit*/
    private var handler: Handler? = null

    fun init(context: Context) {
        this.context = context
        handler = Handler()
    }

    fun showShort(message: CharSequence) {
        show(message, Toast.LENGTH_SHORT)
    }

    private fun show(msg: CharSequence, length: Int) {
        show(Runnable {
            hideToast()
            toast = Toast.makeText(context, msg, length)
            toast!!.show()
        })
    }

    private fun hideToast() {
        if (null != toast)
            toast!!.cancel()
    }

    private fun show(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else if (handler != null) {
            handler!!.post(runnable)
        }
    }
}
