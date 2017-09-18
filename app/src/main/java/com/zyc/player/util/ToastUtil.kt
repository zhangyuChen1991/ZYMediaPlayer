package com.zyc.player.util

import android.widget.Toast
import com.zyc.player.base.BaseApplication

/**
 * Created by ZhangYu on 2017/9/18.
 */
class ToastUtil {
    //companion object 静态变量和方法
    companion object {

        var mToast: Toast? = null
        internal fun showToastShort(text: String) {
            mToast?.cancel()
            mToast = Toast.makeText(BaseApplication.mContext, text, Toast.LENGTH_SHORT)
            mToast?.show()
        }
    }
}