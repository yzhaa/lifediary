package yzh.lifediary.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import yzh.lifediary.MyApplication
import yzh.lifediary.R
import yzh.lifediary.entity.User
import yzh.lifediary.entity.UserResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.TaskExecutor
import yzh.lifediary.view.ChangeInfoActivity
import yzh.lifediary.view.TAG
import java.io.IOException

val fromAlbum = 2

class InfoAdapter(var user: User, val fragment: Fragment) : BaseAdapter() {
    //    private var icon =BitmapFactory.decodeFile(user.icon)
    private val LOGIN_OUT=4
    private val ARTICLE=3
    private val LIKE=1
    private val BOOK=2

    inner class UserInfoCM(val icon: ImageView, val id: TextView, val name: TextView)
    inner class ItemCM(val icon: ImageView, val label: TextView)

    private val lisItemIcon =
        listOf(R.drawable.guanzhu, R.drawable.shoucang, R.drawable.guanzhu, R.drawable.shoucang)
    private val listItemLabel = listOf("关注", "收藏", "个人文章", "退出")

    private lateinit var imageView: ImageView

    override fun getCount(): Int {
        return lisItemIcon.size + 1
    }

    override fun getItem(position: Int): Any {
        return position

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (position == 0) {
            if (convertView == null || convertView.tag !is UserInfoCM) {
                view = LayoutInflater.from(MyApplication.context)
                    .inflate(R.layout.userinfo, parent, false)
                view.tag = UserInfoCM(
                    view.findViewById(R.id.iv_icon),
                    view.findViewById(R.id.tv_id),
                    view.findViewById(R.id.tv_username)
                )
            } else view = convertView

            val userInfoCM = view.tag as UserInfoCM
            userInfoCM.name.text = user.username
            userInfoCM.id.text = user.account.toString()
            setOnKeyListener(userInfoCM.icon, userInfoCM.name)
            Glide.with(fragment)
                .load(Constant.BASE_URL + "/" + user.iconPath)
                .into(userInfoCM.icon)
        } else {
            if (convertView == null || convertView.tag !is ItemCM) {
                view =
                    LayoutInflater.from(MyApplication.context).inflate(R.layout.card, parent, false)
                view.tag = ItemCM(view.findViewById(R.id.iv_card), view.findViewById(R.id.tv_card))
            } else view = convertView
            val itemCM = view.tag as ItemCM
            view.setOnClickListener {
                when(position){
                    LOGIN_OUT -> {
                        fragment.activity?.apply {
                            Constant.loginOut(this)
                        }
                    }


                }
            }
            itemCM.icon.setImageResource(lisItemIcon[position - 1])
            itemCM.label.text = listItemLabel[position - 1]
        }
        return view
    }



    private fun setOnKeyListener(icon: ImageView, name: TextView) {
        imageView = icon
        icon.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // 指定只显示照片
            intent.type = "image/*"
            fragment.startActivityForResult(intent, fromAlbum)
        }
        name.setOnClickListener {
            fragment.startActivity(Intent(
                fragment.activity,
                ChangeInfoActivity::class.java
            ).apply { putExtra("adapter", user) })
        }

    }


    fun noticeChange(uri: Uri) {
        val bitmap = fragment.activity?.contentResolver?.openFileDescriptor(uri, "r")?.use {

            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)

        }
        imageView.setImageBitmap(bitmap)
        OkHttpClient.getOkHttpCline().newCall(
            Request.Builder().baseUrl(Constant.BASE_URL).url("modify/icon")
                .post(
                    RequestBody.Builder().type(RequestBody.ImageType).writeBitmap(listOf(bitmap))
                        .build()
                ).build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d(TAG, "onFailure: " + e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                val ur = Gson.getGson().fromJson<UserResponse>(response?.body,
                    object : TypeToken<UserResponse>() {}.type)
                Log.d(TAG, "onResponse: " + ur.data)
                Constant.user = ur.data
                user=Constant.user
                notifyDataSetChanged()
                TaskExecutor.writeUserToFile(fragment.requireActivity(), user)
            }
        })
    }


}