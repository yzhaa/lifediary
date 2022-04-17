package yzh.lifediary.NetWork

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import yzh.lifediary.util.Constant

const val LOGIN_OUT="LOGIN_OUT_NOW"
class LoginOutBroadCast :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"身份信息过期",Toast.LENGTH_SHORT).show()
        Constant.loginOut(context!!)
    }
}