package yzh.lifediary

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import org.json.JSONObject


class MyApplication:Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val TOKEN = "XFWt8wmf2wbMrGKs" // 填入你申请到的令牌值
    }

    override fun onCreate() {
        super.onCreate()
        context =applicationContext

    }
}