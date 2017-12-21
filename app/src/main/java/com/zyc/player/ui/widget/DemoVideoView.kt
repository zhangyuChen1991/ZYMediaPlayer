package com.zyc.player.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.zyc.player.R
import com.zyc.player.base.BaseApplication.Companion.mContext
import com.zyc.player.ui.widget.base.BaseVideoView
import tv.danmaku.ijk.media.player.IMediaPlayer


/**
 * Created by ZhangYu on 2017/12/21.
 */

class DemoVideoView : BaseVideoView {

    override fun updateProgress(currentPosition: Long) {
        Log.d(TAG, "updateProgress()  currentPosition = " + currentPosition)
        mSeekBar.setProgress(currentPosition.toInt())
    }

    override fun setMaxProgress(duration: Int) {
        Log.d(TAG, "setMaxProgress()  duration = " + duration)

        mSeekBar.max = duration
    }

    lateinit var mSeekBar: SeekBar

    override fun initView() {
        View.inflate(mContext, R.layout.demo_video_view, this)
        mSeekBar = findViewById(R.id.dvv_seekBar) as SeekBar

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                iMediaPlayer.seekTo(seekBar?.progress!!.toLong())

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "onProgressChanged()  progress = " + progress)

            }
        })
    }

    override fun initResources() {

    }

    constructor(ctx: Context) : super(ctx) {
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
    }
}
