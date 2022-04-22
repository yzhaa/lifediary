package yzh.lifediary.view.info.change


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_save_info.*
import kotlinx.android.synthetic.main.title.*

import yzh.lifediary.R


import yzh.lifediary.entity.UserResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.TaskExecutor
import yzh.lifediary.view.TAG
import java.io.IOException

class ChangeInfoActivity: AppCompatActivity()  {
    val fromAlbum = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_info)
        init()

    }

    override fun onStart() {
        super.onStart()
        update()
    }

    private fun  update(){
        change_name_tv.text=Constant.user.username
        Glide.with(this).load(Constant.BASE_URL+"/"+Constant.user.iconPath).into(change_icon_iv)
    }
    private fun  init(){
        title_tv.text = "个人信息"
        right_btn.visibility= View.INVISIBLE
        update()
        change_icon_item.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // 指定只显示照片
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)
        }
        change_name_item.setOnClickListener {
            startActivity(Intent(this, ChangeNameActivity::class.java))
        }
        change_password_item.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的照片显示
                        noticeChange(uri)
                    }
                }
            }
        }

    }
    private fun noticeChange(uri: Uri) {
        val bitmap =contentResolver.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)

        }
        OkHttpClient.getOkHttpCline().newCall(
            Request.Builder().baseUrl(Constant.BASE_URL).url("modify/icon")
                .post(
                    RequestBody.Builder().type(RequestBody.ImageType)
                        .writeBitmap(listOf(Constant.bitmapCompress(bitmap!!)))
                        .build()
                ).build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d(TAG, "onFailure: " + e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                val ur = Gson.getGson().fromJson<UserResponse>(response?.body,
                    object : TypeToken<UserResponse>() {}.type
                )
                Log.d(TAG, "onResponse: " + ur.data)
                Constant.user = ur.data

                TaskExecutor.writeUserToFile(this@ChangeInfoActivity, Constant.user)
            }
        })
    }



}