package yzh.lifediary.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment


import yzh.lifediary.MyApplication
import yzh.lifediary.R
import yzh.lifediary.entity.User
import yzh.lifediary.util.Constant
import yzh.lifediary.util.loadIcon
import yzh.lifediary.view.info.change.ChangeInfoActivity

import yzh.lifediary.view.info.CollectActivity
import yzh.lifediary.view.info.FollowActivity
import yzh.lifediary.view.info.PersonalActivity




class InfoAdapter(var user: User, val fragment: Fragment) : BaseAdapter() {
    //    private var icon =BitmapFactory.decodeFile(user.icon)
    private val LOGIN_OUT = 4
    private val ARTICLE = 3
    private val FOLLOW = 1
    private val COLLECT = 2

    inner class UserInfoCM(val icon: ImageView, val id: TextView, val name: TextView)
    inner class ItemCM(val icon: ImageView, val label: TextView)

    private val lisItemIcon =
        listOf(R.drawable.guanzhu, R.drawable.shoucang, R.drawable.jilu, R.drawable.tuichu)
    private val listItemLabel = listOf("关注", "收藏", "个人文章", "退出")



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
           view.setOnClickListener {
               fragment.startActivity(Intent(fragment.requireContext(), ChangeInfoActivity::class.java))
           }
            userInfoCM.icon.loadIcon(Constant.BASE_URL + "/" + user.iconPath)
        } else {
            if (convertView == null || convertView.tag !is ItemCM) {
                view =
                    LayoutInflater.from(MyApplication.context).inflate(R.layout.card, parent, false)
                view.tag = ItemCM(view.findViewById(R.id.iv_card), view.findViewById(R.id.tv_card))
            } else view = convertView
            val itemCM = view.tag as ItemCM
            view.setOnClickListener {
                when (position) {
                    LOGIN_OUT -> {
                        fragment.activity?.apply {
                            Constant.loginOut(this)
                        }
                    }
                    COLLECT -> {
                        fragment.startActivity(
                            Intent(
                                fragment.requireContext(),
                                CollectActivity::class.java
                            )
                        )
                    }
                    ARTICLE -> {
                        fragment.startActivity(
                            Intent(
                                fragment.requireContext(),
                                PersonalActivity::class.java
                            ).putExtra("user", Constant.user)
                        )
                    }
                    FOLLOW -> {
                        fragment.startActivity(
                            Intent(
                                fragment.requireContext(),
                                FollowActivity::class.java
                            )
                        )
                    }


                }
            }
            itemCM.icon.setImageResource(lisItemIcon[position - 1])
            itemCM.label.text = listItemLabel[position - 1]
        }
        return view
    }




}