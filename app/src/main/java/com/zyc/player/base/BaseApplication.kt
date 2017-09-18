package com.zyc.player.base

import android.app.Application
import android.content.Context

/**
 * Created by ZhangYu on 2017/9/18.
 */
class BaseApplication : Application() {
    internal companion object {
        var mContext : Context? = null
    }
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
}