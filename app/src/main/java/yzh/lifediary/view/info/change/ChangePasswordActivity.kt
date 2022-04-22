package yzh.lifediary.view.info.change

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_change_name.*
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.save_title.*
import yzh.lifediary.R
import yzh.lifediary.entity.UserResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.TaskExecutor
import yzh.lifediary.view.TAG
import java.io.IOException

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        title_tv.text = "更改密码"
        init()
    }

    private fun init() {
        right_btn.setOnClickListener {
            if (old_pw_et.text.isNotEmpty() && new_pw_et.text.isNotEmpty() && new_qrpw_et.text.isNotEmpty()) {
                if (new_pw_et.text.toString() != new_qrpw_et.text.toString()) {
                    Constant.showToast("两次密码不一致")
                    return@setOnClickListener
                }
                OkHttpClient.getOkHttpCline().newCall(
                    Request.Builder().baseUrl(Constant.BASE_URL).url("modify/password").post(
                        RequestBody.Builder()
                            .addParam("oldpw", old_pw_et.text.toString())
                            .addParam("newpw", new_pw_et.text.toString()).build()
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
                            TaskExecutor.writeUserToFile(this@ChangePasswordActivity, Constant.user)
                            finish()
                        } else {
                            Constant.showToast(ur.message)
                        }
                    }
                })
            } else {
                Constant.showToast("请输出完整")
            }
        }

    }
}