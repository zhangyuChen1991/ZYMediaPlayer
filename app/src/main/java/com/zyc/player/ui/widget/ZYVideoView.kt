package com.zyc.player.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.MediaController
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Created by ZhangYu on 2017/9/16.
 */
class ZYVideoView : FrameLayout, MediaController.MediaPlayerControl {


    var iMediaPlayer: IMediaPlayer? = null;
    private var mContext: Context? = null;
    private var mSurfaceView: SurfaceView? = null
    val TAG = "ZYVideoView"

    constructor(ctx: Context) : super(ctx) {
        initVideoView(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        initVideoView(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        initVideoView(ctx)
    }

    private fun initVideoView(ctx: Context) {
        mContext = ctx.applicationContext
        iMediaPlayer = IjkMediaPlayer()

        mSurfaceView = SurfaceView(mContext)
        addView(mSurfaceView)

        mSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                Log.d(TAG, "surfaceChanged")

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                Log.d(TAG, "surfaceDestroyed")
                iMediaPlayer!!.setDisplay(null)
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                Log.d(TAG, "surfaceCreated")
                iMediaPlayer!!.setDisplay(holder)
            }
        })

    }


    override fun isPlaying(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return iMediaPlayer?.isPlaying!!
    }

    override fun canSeekForward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDuration(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return iMediaPlayer?.duration!!.toInt()
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        iMediaPlayer?.pause()
    }

    override fun getBufferPercentage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        iMediaPlayer?.seekTo(pos.toLong())
    }

    override fun getCurrentPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return iMediaPlayer?.currentPosition!!.toInt()
    }

    override fun canSeekBackward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        iMediaPlayer?.start()
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}