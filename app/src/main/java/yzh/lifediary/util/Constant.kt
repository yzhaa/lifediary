package yzh.lifediary.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import yzh.lifediary.MyApplication
import yzh.lifediary.entity.LoginResponse
import yzh.lifediary.entity.User
import yzh.lifediary.okhttp.*
import yzh.lifediary.view.LoginAndRegisterActivity
import yzh.lifediary.view.TAG
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

object Constant {

    val BASE_URL = "http://192.168.125.193:8081"
    val LoginInfo = "loginInfo"
    val userFile = "userInfo"

    lateinit var user: User

    fun isExpire(func: (Boolean) -> Unit) {
        val file: File = MyApplication.context.getFileStreamPath(userFile)
        OkHttpClient.getOkHttpCline().newCall(
            Request.Builder().baseUrl(BASE_URL).url("isExpire").build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d(TAG, "onFailure: " + e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                Log.d(TAG, "onResponse: " + response?.body.toString())
                val message = Gson.getGson().fromJson<LoginResponse>(
                    response?.body,
                    object : TypeToken<LoginResponse>() {}.type
                )

                func(message.message == "没有过期" && file.exists())

            }
        })
    }

    fun showToast(string: String) {
        Toast.makeText(MyApplication.context, string, Toast.LENGTH_SHORT).show()
    }


    fun loginOut(context: Context) {
        TaskExecutor.execute {
            val file: File = context.getFileStreamPath(userFile)
            if (file.exists()) file.delete()
            val edit =
                MyApplication.context.getSharedPreferences(LoginInfo, Context.MODE_PRIVATE).edit()
            edit.clear()
            edit.apply()
            context.startActivity(Intent(context, LoginAndRegisterActivity::class.java))
        }

    }

    fun convertDpToPixel(activity: Activity, dp: Float): Int {
        val displayMetrics = activity.resources.displayMetrics;
        return (dp * displayMetrics.density).toInt()
    }


    fun bitmapCompress(string: String): Bitmap {
        var bitmap = BitmapFactory.decodeFile(string)
        val mc = bitmap.byteCount / 1024 / 1024
        val baos = ByteArrayOutputStream()
        var quality = 100
        quality = when {
            mc < 2 -> 100
            mc < 5 ->
                75
            else -> 50
        }
        if (quality != 100) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            val bytes = baos.toByteArray()
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            baos.close()
        }
        return bitmap

    }
}