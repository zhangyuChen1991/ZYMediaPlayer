/*
 * 文件名: DeviceTools.java
 * 版    权：深圳市快播科技有限公司
 * 描    述: 
 * 创建人: 胡启明
 * 创建时间:2012-7-16
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.zyc.player.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.content.res.Resources
import android.location.LocationManager
import android.media.AudioManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.TextView

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration
import java.util.LinkedHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

object DeviceUtils {
    var TAG = "DeviceTools"
    val INVALID_MAC_VALUES = arrayOf("000000000000", "00:00:00:00:00:00", "020000000000", "02:00:00:00:00:00", "ffffffffffff", "ff:ff:ff:ff:ff:ff", "FFFFFFFFFFFF", "FF:FF:FF:FF:FF:FF")
    val OPERATOR_CHINA_MOBILE = 1
    val OPERATOR_CHINA_UNICOM = 2
    val OPERATOR_CHINA_TELECOM = 3
    val OPERATOR_UNKNOWN = 4
    val CPU_ARM = "Arm"
    val CPU_INTEL = "Intel"
    val CPU_ARM_64_PREFIX = "AArch64"

    private val CMD = "/system/bin/cat"
    private val MAX_FREQUENCY = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"

    /**
     * 获取内存大小
     *
     * @return
     */
    // TODO: handle exception
    val ramInfo: Long
        get() {
            var total: Long = 0
            var fr: FileReader? = null
            var br: BufferedReader? = null

            try {
                fr = FileReader("/proc/meminfo")
                br = BufferedReader(fr)
                var line = br.readLine()
                line = line.replace(" ".toRegex(), "").toLowerCase()
                val i = line.indexOf("memtotal:")
                val j = line.indexOf("kb")
                if (i > -1 && j > -1) {
                    line = line.substring(i + "memtotal:".length, j)
                    total = java.lang.Long.valueOf(line)!! / 1024
                }
            } catch (e: Exception) {
            } finally {
                try {
                    if (br != null) {
                        br.close()
                        br = null
                    }

                    if (fr != null) {
                        fr.close()
                        fr = null
                    }
                } catch (e: Exception) {

                }

            }

            return total
        }

    val localIpAddress: String?
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            return inetAddress.hostAddress.toString()
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

            return null
        }

    val sdkInt: Int
        get() = android.os.Build.VERSION.SDK_INT

    val osVersion: String
        get() = android.os.Build.VERSION.RELEASE

    val model: String
        get() = android.os.Build.MODEL

    val brand: String
        get() = android.os.Build.BRAND

    /**
     * [获取设备Rom大小]<br></br>
     * 功能详细描述
     *
     * @return
     */
    val romInfo: Long
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()
            var size = totalBlocks * blockSize
            size /= 1024
            size /= 1024
            return size
        }

    /**
     * [获取CPU频率]<br></br>
     * 功能详细描述
     *
     * @return
     */
    // Log.d(TAG, "getMaxCpuFreq begin");
    // Log.d(TAG, "getMaxCpuFreq end: " + result);
    val maxCpuFreq: Int
        get() {
            var result = 0
            val cmd: ProcessBuilder
            try {
                val args = arrayOf(CMD, MAX_FREQUENCY)
                cmd = ProcessBuilder(*args)
                val process = cmd.start()
                val `in` = process.inputStream
                val buff = ByteArray(256)
                val sb = StringBuilder()
                while (`in`.read(buff) != -1) {
                    sb.append(String(buff))
                }
                `in`.close()
                var str: String? = sb.toString()
                if (str != null) {
                    str = str.trim { it <= ' ' }
                    result = Integer.valueOf(str)!!
                }
                process.destroy()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return result
        }

    private val mArmArchitecture = arrayOf("", -1, "")

    /**
     * [获取cpu类型和架构]
     *
     * @return
     */
    // 64位cpu是armv8
    // 64位cpu；
    val cpuArchitecture: Array<Any>?
        get() {
            if (mArmArchitecture[1] as Int != -1) {
                return mArmArchitecture
            }
            try {
                val `is` = FileInputStream("/proc/cpuinfo")
                val ir = InputStreamReader(`is`)
                val br = BufferedReader(ir)
                try {
                    val nameProcessor = "Processor"
                    val nameFeatures = "Features"
                    val nameModel = "model name"
                    val nameCpuFamily = "cpu family"
                    while (true) {
                        val line = br.readLine()
                        var pair: Array<String>? = null
                        Log.i(TAG, line ?: "null")
                        if (line == null) {
                            break
                        }
                        pair = line.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (pair.size != 2)
                            continue
                        val key = pair[0].trim { it <= ' ' }
                        val `val` = pair[1].trim { it <= ' ' }
                        if (key.startsWith(nameProcessor)) {
                            var n = ""
                            Log.i(TAG, "val:" + `val`)
                            if (`val`.contains(CPU_ARM_64_PREFIX)) {
                                mArmArchitecture[0] = CPU_ARM
                                mArmArchitecture[1] = 8
                                continue
                            }
                            for (i in `val`.indexOf("ARMv") + 4 until `val`.length) {
                                val temp = `val`[i] + ""
                                if (temp.matches("\\d".toRegex())) {
                                    n += temp
                                } else {
                                    break
                                }
                            }
                            Log.i(TAG, "n:" + n)
                            mArmArchitecture[0] = CPU_ARM
                            mArmArchitecture[1] = Integer.parseInt(n)
                            continue
                        }

                        if (key.compareTo(nameFeatures, ignoreCase = true) == 0) {
                            if (`val`.contains("neon")) {
                                mArmArchitecture[2] = "neon"
                            }
                            continue
                        }

                        if (key.compareTo(nameModel, ignoreCase = true) == 0) {
                            if (`val`.contains(CPU_INTEL)) {
                                mArmArchitecture[0] = CPU_INTEL
                                mArmArchitecture[2] = "atom"
                            }
                            continue
                        }

                        if (key.compareTo(nameCpuFamily, ignoreCase = true) == 0) {
                            mArmArchitecture[1] = Integer.parseInt(`val`)
                            continue
                        }
                    }
                } finally {
                    br.close()
                    ir.close()
                    `is`.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return mArmArchitecture
        }

    val cpuArchString: String?
        get() {
            val info = cpuArchitecture
            if (info == null || info.size != 3 || info[1] as Int == -1) {
                return null
            }
            val sb = StringBuilder()
            sb.append(info[0])
            sb.append(info[1])
            sb.append(info[2])
            return sb.toString()
        }

    private fun getMemoryClass(context: Context): Int {
        try {
            return (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
        } catch (e: Exception) {
            return 0
        }

    }

    /**
     * 获取相对于内存的空间的大小
     *
     * @param context
     * @param rate    比率，如0.02是内存大小的百分之二
     * @return
     */
    fun getMemCacheSize(context: Context, rate: Float): Int {
        val totalMemory = (getMemoryClass(context) * 1024 * 1024).toLong()
        // 使用内存的2%作为缓存
        var cacheSize = Math.round(rate * totalMemory).toInt()
        val maxSize = (3 * 1024 * 1024).toInt()
        val minSize = 500 * 1024
        Log.d(TAG, "getCacheSize totalMem:" + totalMemory + " cacheSize:" + cacheSize + " max:" + maxSize + " min:"
                + minSize)
        if (cacheSize > maxSize) {
            cacheSize = maxSize
        } else if (cacheSize < minSize) {
            cacheSize = minSize
        }
        Log.d(TAG, "final cacheSize:" + cacheSize)
        return cacheSize
    }

    fun isGpsOpen(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 获得手机是不是设置了GPS开启状态
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun getLocalIp(context: Context): String {
        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wi = wm.connectionInfo
        val ipAdd = wi.ipAddress

        return (ipAdd and 0xFF).toString() + "." + (ipAdd shr 8 and 0xFF) + "." + (ipAdd shr 16 and 0xFF) + "." + (ipAdd shr 24 and 0xFF)
    }

    private fun getLocalMacAddressBySdk(context: Context): String? {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifi != null) {
            var info: WifiInfo? = null
            try {
                info = wifi.connectionInfo
            } catch (exception: Exception) {
                Log.d(TAG, "Exception:" + exception)
            }

            if (info != null) {
                return info.macAddress
            }
        }
        return null
    }


    fun checkMac(mac: String): Boolean {
        if (TextUtils.isEmpty(mac)) {
            return false
        }
        val p = Pattern.compile("^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$")
        val matcher = p.matcher(mac)
        if (matcher.find()) {
            for (invalid in INVALID_MAC_VALUES) {
                if (invalid == mac) {
                    return false
                }
            }
            return true
        }
        return false
    }

    fun getOperator(context: Context): Int {
        val tel = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tel != null) {
            val simOperator = tel.simOperator
            if ("46000" == simOperator || "46002" == simOperator || "46007" == simOperator) {
                return OPERATOR_CHINA_MOBILE
            } else if ("46001" == simOperator) {
                return OPERATOR_CHINA_UNICOM
            } else if ("46003" == simOperator) {
                return OPERATOR_CHINA_TELECOM
            }
        }

        return OPERATOR_UNKNOWN
    }

    fun getAppVersionCode(context: Context): Int {
        var verCode = 0
        try {
            verCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }

        return verCode
    }

    fun getAppVersionName(context: Context): String {
        var version = ""
        try {
            version = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }

    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    fun isScreenOriatationLandScape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * [获取是否是平板]<br></br>
     * 功能详细描述 定义960dp*720dp的设备为平板，其他为手机；
     *
     * @param context
     * @return
     */
    fun isPad(context: Context): Boolean {
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val den = context.resources.displayMetrics.density

        var w = if (width > height) width else height
        var h = if (width < height) width else height

        w = (w / den).toInt()
        h = (h / den).toInt()

        // Log.d(TAG, "width: " + width + " height: " + height + " den: " +
        // den);
        return w > 960 && h > 720
    }

    fun getDeviceType(context: Context): Int {
        // 1:手机 2:平板
        return if (isPad(context)) 2 else 1
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun hasGoogleMapApi(context: Context): Boolean {
        var obj: Any? = null
        try {
            obj = Class.forName("com.google.android.maps.MapActivity")
        } catch (e: Exception) {
        }

        // Log.d(TAG, " obj: " + obj);
        return obj != null
    }

    /**
     * 判断是否开启了自动亮度调节
     *
     * @return
     */
    fun isAutoBrightness(activity: Activity): Boolean {
        var automicBrightness = false
        try {
            automicBrightness = Settings.System.getInt(activity.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }

        return automicBrightness
    }

    /**
     * 停止自动亮度调节
     *
     * @param activity
     */
    fun stopAutoBrightness(activity: Activity) {
        Settings.System.putInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
    }

    /**
     * 开启亮度自动调节
     *
     * @param activity
     */
    fun startAutoBrightness(activity: Activity) {
        Settings.System.putInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
    }

    /**
     * 设置屏幕亮度
     *
     * @param activity
     * @param brightness
     */
    fun setBrightness(activity: Activity, brightness: Float) {
        val lp = activity.window.attributes
        lp.screenBrightness = brightness
        activity.window.attributes = lp
    }

    /**
     * 获取屏幕亮度
     */
    fun getBrightness(activity: Activity): Float {
        var curBrightnessValue = 0.0f
        try {
            val b = Settings.System.getInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
            // 需要转换成百分比
            curBrightnessValue = b / 255
        } catch (e: SettingNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return curBrightnessValue
    }

    /**
     * 保持屏幕常亮
     *
     * @param activity
     */
    fun setKeepScreenOn(activity: Activity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * 取消屏幕常亮
     *
     * @param activity
     */
    fun cancelKeepScreenOn(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    fun collectDeviceInfo(context: Context?): Map<String, String>? {
        if (context == null) {
            return null
        }
        val infoMap = LinkedHashMap<String, String>()
        try {
            val pm = context.packageManager// 获得包管理器
            // 获取当前APP包的Version
            val pi = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                infoMap.put("versionName", versionName)
                infoMap.put("versionCode", versionCode)
            }
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }

        // 获取设备信息
        val fields = Build::class.java.declaredFields// 反射机制
        for (field in fields) {
            try {
                field.isAccessible = true
                infoMap.put(field.name, field.get("").toString())
                // Log.d(TAG, field.getName() + ":" + field.get(""));
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
        return infoMap
    }

    /*public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyMgr.getLine1Number();
    }*/

    fun hasSimCard(context: Context): Boolean {
        val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simState = mTelephonyManager.simState
        return if (simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            false
        } else {
            true
        }
    }


    /**
     * px = dp * (dpi / 160)
     *
     * @param ctx
     * @param dip
     * @return
     */
    fun dipToPX(ctx: Context, dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.resources.displayMetrics).toInt()
    }

    /**
     * sp*ppi/160  =px
     *
     * @param ctx
     * @param sp
     * @return
     */
    fun spToPX(ctx: Context, sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, ctx.resources.displayMetrics).toInt()
    }

    fun hasNavigationBar(context: Context): Boolean {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        val hasMenuKey = sdkInt >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && ViewConfiguration.get(context)
                .hasPermanentMenuKey()
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

        return if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            true
        } else false
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }


    fun showNavigationBar(view: View?) {
        if (view != null && sdkInt >= Build.VERSION_CODES.HONEYCOMB && checkDeviceHasNavigationBar(view.context)) {

            /*view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);*/
            /*int mShowFlags =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;*/

            /*int old = view.getSystemUiVisibility();
            int uiOptions =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;*/

            view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    fun hideNavigationBar(view: View?) {
        if (view != null && sdkInt >= Build.VERSION_CODES.HONEYCOMB && checkDeviceHasNavigationBar(view.context)) {

            /*int mHideFlags = *//*View.SYSTEM_UI_FLAG_IMMERSIVE
                            |*//* *//*View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|*//*View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            view.setSystemUiVisibility(mHideFlags);*/

            //view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            /*int old = view.getSystemUiVisibility();*/

            val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            view.systemUiVisibility = uiOptions
        }
    }

    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var statusBarHeight = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c!!.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field!!.get(obj).toString())
            statusBarHeight = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return statusBarHeight
    }

    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
        }

        return hasNavigationBar
    }


    fun getCurrentSysVlumnValue(context: Context): Int {

        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
        val current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)

        return 100 * current / max
    }
}
