package yzh.lifediary.view


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import yzh.lifediary.MyApplication
import yzh.lifediary.util.Constant


import yzh.lifediary.okhttp.*
import yzh.lifediary.R
import yzh.lifediary.entity.User
import yzh.lifediary.entity.UserResponse
import yzh.lifediary.util.TaskExecutor
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream


class LoginActivity : BaseActivity() {
    //创建播放视频的控件对象

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        //加载数据
    }

    private fun init() {
        initBase()
        
        val file: File = MyApplication.context.getFileStreamPath(Constant.userFile)
        if (file.exists()) {
            ObjectInputStream(openFileInput(Constant.userFile)).readObject()
                ?.apply {
                    Constant.user = this as User
                    Log.d(TAG, "fileExists:eeee")
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }
        }



        login_btn.setOnClickListener {
            it.isClickable = false
            OkHttpClient.getOkHttpCline().newCall(
                Request.Builder().baseUrl(Constant.BASE_URL).url("login").saveCookie()
                    .post(
                        RequestBody.Builder().addParam("account", login_id_et.text.toString())
                            .addParam("password", login_pw_et.text.toString()).build()
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
                        Constant.user = userResponse.data
                        Log.d(TAG, "onCreateView++:${Constant.user} ")
                        TaskExecutor.writeUserToFile(this@LoginActivity, userResponse.data)
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    }
                    Constant.showToast(userResponse.message)
                    it.isClickable = true
                    done()
                }
            })
            load()

        }
        register_btn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun initView() {
        vv.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.view}"))
        vv.start()
        vv.setOnCompletionListener { vv.start() }
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    override fun onDestroy() {
        vv.suspend()
        super.onDestroy()
    }

    private fun load() {
        show {
            login_main.alpha = 0.5f
            login_main.isClickable = false
        }
    }

    private fun done() {
        hide {
            login_main.alpha = 1f
            login_main.isClickable=true
        }
    }
}

