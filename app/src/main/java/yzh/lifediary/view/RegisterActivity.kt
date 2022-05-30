package yzh.lifediary.view

import android.os.Bundle
import android.util.Log
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken


import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.register_btn
import yzh.lifediary.R
import yzh.lifediary.entity.UserResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant

import java.io.IOException

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
    }

    private fun init() {
        initBase()
        back_btn.setOnClickListener {
            finish()
        }
        register_btn.setOnClickListener {
            it.isClickable = false
            if (qr_password_et.text.isNotEmpty() && password_et.text.isNotEmpty() && account_et.text.isNotEmpty() && name_et.text.isNotEmpty()) {
                if (qr_password_et.text.toString() == password_et.text.toString()) {
                    OkHttpClient.getOkHttpCline().newCall(
                        Request.Builder().baseUrl(Constant.BASE_URL).url("register").saveCookie()
                            .post(
                                RequestBody.Builder()
                                    .addParam("account", account_et.text.toString())
                                    .addParam("password", password_et.text.toString())
                                    .addParam("username", name_et.text.toString()).build()
                            ).build()
                    ).enqueue(object : Callback {
                        override fun onFailure(call: Call?, e: IOException?) {
                            Log.d(TAG, "onResponse: ${e.toString()}")
                            it.isClickable = true
                            done()
                        }

                        override fun onResponse(call: Call?, response: Response?) {
                            Log.d(TAG, "onResponse: ${response?.body}")
                            val userResponse = Gson.getGson().fromJson<UserResponse>(
                                response?.body,
                                object : TypeToken<UserResponse>() {}.type
                            )
                            Log.d(TAG, "onResponse: $userResponse")
                            if (userResponse.code == 0) {
                                Constant.showToast("注册成功")
                                finish()
                            } else {
                                Constant.showToast(userResponse.message)
                            }
                            it.isClickable = true
                            done()
                        }
                    })
                    load()
                } else {
                    Constant.showToast("两次输入密码不一致")
                    it.isClickable = true
                }
            } else {
                Constant.showToast("请输入完整")
                it.isClickable = true
            }

        }

    }


    private fun done() {
        hide {
            register_main.alpha = 1f
            register_main.isClickable = true
        }
    }

    private fun load() {
        show {
            register_main.alpha = 0.5f
            register_main.isClickable = false
        }
    }


}