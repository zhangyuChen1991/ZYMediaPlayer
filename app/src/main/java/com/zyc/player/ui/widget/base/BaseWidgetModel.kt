package com.zyc.player.ui.widget.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.zyc.player.R


/**
 * Created by ZhangYu on 2017/12/21.
 */

abstract class BaseWidgetModel : LinearLayout {

    lateinit var mContext: Context
    constructor(ctx: Context) : super(ctx) {
        init(ctx, null)
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        init(ctx, attrs)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        init(ctx, attrs)
    }

    fun init(ctx: Context, attrs: AttributeSet?){
        mContext = ctx
        initResources()
        initView()
    }

    abstract fun initView()

    abstract fun initResources()
}
