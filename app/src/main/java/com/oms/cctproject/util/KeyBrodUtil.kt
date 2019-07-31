package com.oms.cctproject.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 */

object KeyBrodUtil {
    /**
     * 打开软键盘
     *
     * @param mEditText
     * @param mContext
     */
    fun openKeybord(mEditText: EditText, mContext: Context) {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    /**
     * 关闭软键盘
     */
    fun closeKeybord(mEditText: EditText, mContext: Context) {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }

    /**
     * 判断当前软键盘是否打开
     *
     * @param activity
     * @return
     */
    fun isSoftInputShow(activity: Activity): Boolean {

        // 虚拟键盘隐藏 判断view是否为空
        val view = activity.window.peekDecorView()
        if (view != null) {
            // 隐藏虚拟键盘
            val inputmanger = activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //       inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);

            return inputmanger.isActive && activity.window.currentFocus != null
        }
        return false
    }
}
