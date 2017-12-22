package com.zyc.player.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.zyc.player.R
import com.zyc.player.ui.widget.base.BaseVideoView
import com.zyc.player.util.DeviceUtils
import com.zyc.player.util.StringUtils


/**
 * Created by ZhangYu on 2017/12/21.
 */

class DemoVideoView : BaseVideoView, View.OnClickListener {
    lateinit var mSeekBar: SeekBar

    lateinit var mTvPlayTime: TextView
    lateinit var mIvPausePlay: ImageView
    lateinit var mBottomCtrlArea: LinearLayout
    private var mBottomCtrlAreaHide: Boolean = false

    override fun initView() {
        View.inflate(mContext, R.layout.demo_video_view, this)
        mSeekBar = findViewById(R.id.dvv_seekBar) as SeekBar
        mTvPlayTime = findViewById(R.id.dvv_text_position) as TextView
        mIvPausePlay = findViewById(R.id.img_btn_play) as ImageView
        mBottomCtrlArea = findViewById(R.id.dvv_bottom_ctrl_area) as LinearLayout

        mIvPausePlay.setOnClickListener(this)

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                iMediaPlayer.seekTo(seekBar?.progress!!.toLong())
                if(!isPlaying){
                    iMediaPlayer.start()
                }
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "onProgressChanged()  progress = " + progress)

            }
        })

        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (mbottomAnimating) return false
                if (mBottomCtrlAreaHide) {
                    showBottomArea()
                } else {
                    hideBottomArea()
                }
                return false
            }

        })
    }


    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.img_btn_play -> {
                if (isPlaying) {
                    mIvPausePlay.setImageResource(R.drawable.eq_c)
                    iMediaPlayer.pause()
                } else {
                    mIvPausePlay.setImageResource(R.drawable.er_c)
                    iMediaPlayer.start()
                }
            }
        }
    }

    override fun start() {
        super.start()
        hideBottomArea()
    }

    override fun pause() {
        super.pause()
        showBottomArea()
    }

    override fun stop() {
        super.stop()
        showBottomArea()
    }


    private var mbottomAnimating: Boolean = false

    fun showBottomArea() {
        mbottomAnimating = true
        var curTranslationY = mBottomCtrlArea.getTranslationY();
        var animator: ObjectAnimator = ObjectAnimator.ofFloat(mBottomCtrlArea, "translationY", curTranslationY, curTranslationY - mBottomCtrlArea.height.toFloat())
        animator.setDuration(300)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                mbottomAnimating = false

            }
        })
        animator.start()
        mBottomCtrlAreaHide = false
    }

    fun hideBottomArea() {
        mbottomAnimating = true
        var curTranslationY = mBottomCtrlArea.getTranslationY();
        var animator: ObjectAnimator = ObjectAnimator.ofFloat(mBottomCtrlArea, "translationY", curTranslationY, curTranslationY + mBottomCtrlArea.height.toFloat())
        animator.setDuration(300)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                mbottomAnimating = false

            }
        })
        animator.start()
        mBottomCtrlAreaHide = true
    }

    override fun resetPlayAreaSize(videoWidth: Int, videoHeight: Int) {
        var ratio = videoWidth.toFloat() / videoHeight.toFloat()
        var param: LayoutParams = mSurfaceView.layoutParams as LayoutParams
        param.width = (height * ratio).toInt()
        var screenWidth = DeviceUtils.getScreenWidth(mContext)
        if (param.width > screenWidth) {
            param.width = screenWidth
        }
        mSurfaceView.layoutParams = param
    }

    override fun initResources() {

    }

    override fun updateProgress(currentPosition: Long) {
        Log.d(TAG, "updateProgress()  currentPosition = " + currentPosition)
        mSeekBar.setProgress(currentPosition.toInt())
        mTvPlayTime.setText(StringUtils.parseDuration(currentPosition));

    }

    override fun setMaxProgress(duration: Int) {
        Log.d(TAG, "setMaxProgress()  duration = " + duration)

        mSeekBar.max = duration
    }

    constructor(ctx: Context) : super(ctx) {
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
    }
}
