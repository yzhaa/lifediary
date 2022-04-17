package yzh.lifediary.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_save_info.*

import yzh.lifediary.R
import yzh.lifediary.entity.User
import yzh.lifediary.entity.UserResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.TaskExecutor
import java.io.IOException

class ChangeInfoActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_info)
        saveInfo_btn.setOnClickListener {
            if (tv_password_save.text.toString() != "" && tv_username_save.text.toString() != "") {
                OkHttpClient.getOkHttpCline().newCall(
                    Request.Builder().baseUrl(Constant.BASE_URL).url("modify/info")
                        .post(
                            RequestBody.Builder().addParam("username",tv_username_save.text.toString()).addParam("password",tv_password_save.text.toString()).build()).build()
                ).enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        Log.d(TAG, "onFailure: " + e.toString())
                        Toast.makeText(this@ChangeInfoActivity,"修改失败",Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val ur = Gson.getGson().fromJson<UserResponse>(response?.body,
                            object : TypeToken<UserResponse>() {}.type)
                        Log.d(TAG, "onResponse: " + ur.data)
                        Constant.user=ur.data
                        //通知InfoFragment
                        TaskExecutor.writeUserToFile(this@ChangeInfoActivity, Constant.user)
                        finish()
                    }
                })
            }


        }

    }
}