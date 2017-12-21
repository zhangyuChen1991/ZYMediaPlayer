package com.zyc.player.ui.widget.base

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.MediaController
import com.zyc.player.util.ToastUtil
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.misc.IMediaDataSource

/**
 * Created by ZhangYu on 2017/9/16.
 */
abstract class BaseVideoView : FrameLayout, MediaController.MediaPlayerControl {

    protected val MSG_UPDATE_PROGRESS = 0x19;
    protected val MSG_PLAY_COMPLETE = 0x20;

    lateinit var iMediaPlayer: IMediaPlayer;
    private lateinit var mContext: Context;
    private lateinit var mSurfaceView: SurfaceView
    val TAG = "BaseVideoView"
    lateinit var videoHandler: VideoHandler

    inner class VideoHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                MSG_UPDATE_PROGRESS -> {
                    var currentPosition = iMediaPlayer.currentPosition
                    updateProgress(currentPosition)
                    videoHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 1000)
                }
                MSG_PLAY_COMPLETE ->{
                    updateProgress(iMediaPlayer.duration)
                }
            }
        }
    }

    abstract fun updateProgress(currentPosition: Long)

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
        videoHandler = VideoHandler();

        mSurfaceView = SurfaceView(mContext)
        addView(mSurfaceView)

        mSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                Log.d(TAG, "surfaceChanged")

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                Log.d(TAG, "surfaceDestroyed")
                iMediaPlayer.setDisplay(null)
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                Log.d(TAG, "surfaceCreated")
                iMediaPlayer.setDisplay(holder)
            }
        })

        iMediaPlayer.setOnPreparedListener(object : IMediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: IMediaPlayer) {
                start()
            }
        })

        iMediaPlayer.setOnCompletionListener(object :IMediaPlayer.OnCompletionListener{
            override fun onCompletion(mp: IMediaPlayer){
                onPlayCompletion(mp)
            }
        })

        iMediaPlayer.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
            override fun onError(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
                Log.d(TAG, "Error: " + what + "," + extra)
                ToastUtil.showToastShort("Error: " + what + "," + extra)
                return true
            }
        })

        initResources()
        initView()
    }

    open fun onPlayCompletion(mp: IMediaPlayer){
        videoHandler.sendEmptyMessage(MSG_PLAY_COMPLETE)
        videoHandler.removeMessages(MSG_UPDATE_PROGRESS)
    }

    abstract fun initView()

    abstract fun initResources()

    fun play(context: Context, uri: Uri) {
        iMediaPlayer.setDataSource(context, uri)
        iMediaPlayer.setScreenOnWhilePlaying(true)
        iMediaPlayer.prepareAsync()
    }

    fun play(mediaDataSource: IMediaDataSource) {
        iMediaPlayer.setDataSource(mediaDataSource)
        iMediaPlayer.setScreenOnWhilePlaying(true)
        iMediaPlayer.prepareAsync()
    }

    override fun isPlaying(): Boolean {
        return iMediaPlayer.isPlaying!!
    }

    override fun canSeekForward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDuration(): Int {
        return iMediaPlayer.duration!!.toInt()
    }

    override fun pause() {
        iMediaPlayer.pause()

    }

    override fun getBufferPercentage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int) {
        iMediaPlayer.seekTo(pos.toLong())
    }

    override fun getCurrentPosition(): Int {
        return iMediaPlayer.currentPosition!!.toInt()
    }

    override fun canSeekBackward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        iMediaPlayer.start()
        videoHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS)
        setMaxProgress(duration)

    }

    abstract fun setMaxProgress(duration: Int)

    fun stop() {
        iMediaPlayer.stop();
        videoHandler.removeMessages(MSG_UPDATE_PROGRESS)
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onResume() {
        iMediaPlayer.start()
    }

    fun onPause() {
        pause()
    }

    fun onDestory() {
        stop()
        iMediaPlayer.release()
    }

}