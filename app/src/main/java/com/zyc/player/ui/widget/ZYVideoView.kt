package com.zyc.player.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.MediaController
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Created by ZhangYu on 2017/9/16.
 */
class ZYVideoView : FrameLayout, MediaController.MediaPlayerControl {


    private var iMediaPlayer: IMediaPlayer? = null;
    private var mContext: Context? = null;
    private var mSurfaceView: SurfaceView? = null

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
        iMediaPlayer?.setDisplay(mSurfaceView?.holder)


        mSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })


    }

    override fun isPlaying(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canSeekForward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDuration(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBufferPercentage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canSeekBackward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}