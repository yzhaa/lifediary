package yzh.lifediary.view.info.change

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_change_name.*
import kotlinx.android.synthetic.main.activity_save_info.*
import kotlinx.android.synthetic.main.save_title.*
import yzh.lifediary.R
import yzh.lifediary.entity.UserResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.TaskExecutor
import yzh.lifediary.view.TAG
import java.io.IOException

class ChangeNameActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_name)
        title_tv.text = "更改名字"
        name_et.setText(Constant.user.username)
        right_btn.isEnabled = false
        init()
    }

    private fun init() {
        name_et.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                right_btn.isEnabled = true
            }
        }
        right_btn.setOnClickListener {
            if (name_et.text.isNotEmpty()) {
                OkHttpClient.getOkHttpCline().newCall(
                    Request.Builder().baseUrl(Constant.BASE_URL).url("modify/username").post(
                        RequestBody.Builder().addParam("username", name_et.text.toString()).build()
                    ).build()
                ).enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        Log.d(TAG, "onFailure: " + e.toString())
                        Constant.showToast("修改失败")
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val ur = Gson.getGson().fromJson<UserResponse>(
                            response?.body,
                            object : TypeToken<UserResponse>() {}.type
                        )
                        if (ur.code == 0) {
                            Constant.user = ur.data
                            //通知InfoFragment
                            TaskExecutor.writeUserToFile(this@ChangeNameActivity, Constant.user)
                            finish()
                        } else {
                            Constant.showToast(ur.message)
                        }


                    }
                })
            }
        }

    }
}