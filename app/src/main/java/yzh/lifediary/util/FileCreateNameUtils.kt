package yzh.lifediary.util

import android.annotation.SuppressLint
import yzh.lifediary.util.FileCreateNameUtils
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

object FileCreateNameUtils {
    const val numberChar = "0123456789"
    const val PNG=".png"

    /***
     * 文件名生成工具类
     */
    fun toCreateName(): String {
        return nowDatetoString + generateNum(5)
    }

    /***
     * 生成日期字符串 yyyyMMddHHmm
     *
     * @author MRC
     * @date 2019年4月16日下午2:19:37
     * @return
     */
    val nowDatetoString: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyyMMddHHmm")
            return formatter.format(currentTime)
        }

    /***
     * 生成随机数
     * @author MRC
     * @date 2019年4月16日下午2:21:06
     * @param len
     * @return
     */
    fun generateNum(len: Int): String {
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until len) {
            sb.append(numberChar[random.nextInt(numberChar.length)])
        }
        return sb.toString()
    }
}