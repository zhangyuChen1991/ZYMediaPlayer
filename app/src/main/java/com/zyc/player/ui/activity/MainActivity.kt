package com.zyc.player.ui.activity

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import com.zyc.player.R
import com.zyc.player.media.FileMediaDataSource
import com.zyc.player.ui.widget.ZYVideoView
import com.zyc.player.util.FileUtils
import com.zyc.player.util.ToastUtil
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.io.File

class MainActivity : AppCompatActivity() {

    var mZYVideoView: ZYVideoView? = null
    var mBtnPlay: Button? = null
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mZYVideoView = findViewById(R.id.video_view) as ZYVideoView?
        mBtnPlay = findViewById(R.id.btn_play) as Button

        mBtnPlay!!.setOnClickListener {
//            val dataSource = FileMediaDataSource(File(getTargetFilePath()))
//            mZYVideoView?.play(dataSource)

            var uri : Uri = Uri.fromFile(getTargetFile())
            mZYVideoView?.play(this@MainActivity,uri)
        }


    }

    private fun getTargetFile(): File? {
        var targetFilePath = getTargetFilePath()
        var targetFile = FileUtils.getFileByPath(targetFilePath!!)
        return targetFile
    }

    private fun getTargetFilePath(): String? {
        var exFile = Environment.getExternalStorageDirectory();
        var exFilePath = exFile.absolutePath
        Log.d(TAG, "exFilePath:" + exFilePath)

        var targetFilePath = exFilePath + "/相机/video_20170918_164602.mp4"
        return targetFilePath
    }
}
