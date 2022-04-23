package yzh.lifediary.adapter


import android.annotation.SuppressLint
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.RecyclerView
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken


import yzh.lifediary.R
import yzh.lifediary.entity.*
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.loadIcon
import yzh.lifediary.util.startActivity
import yzh.lifediary.view.TAG
import yzh.lifediary.view.info.PersonalActivity
import java.io.IOException

class SearchUserAdapter (val fragment:Fragment) : RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {
    var list: List<UserAndIsFollowOV>? = null


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item: ConstraintLayout = view.findViewById(R.id.gz_item)
        val controlTV: TextView = view.findViewById(R.id.gz_control_tv)
        val iconIV: ImageView = view.findViewById(R.id.gz_icon_iv)
        val nameTV: TextView = view.findViewById(R.id.gz_name_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_user_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.controlTV.setOnClickListener { nowView->
            nowView.isClickable = false
            list!![viewHolder.adapterPosition].let {
                if (it.follow) {
                        OkHttpClient.getOkHttpCline().newCall(
                                Request.Builder().baseUrl(Constant.BASE_URL).url("follow/d").post(
                                        RequestBody.Builder().addParam("userId", it.id.toString())
                                            .build()).build()
                            ).enqueue(object  :Callback{
                                override fun onFailure(call: Call?, e: IOException?) {
                                    nowView.isClickable=true
                                }

                                override fun onResponse(call: Call?, response: Response?) {
                                    val messageResponse = Gson.getGson().fromJson<MessageResponse>(
                                        response?.body,
                                        object : TypeToken<MessageResponse>() {}.type
                                    )
                                    if (messageResponse.code == 0) {
                                        action(nowView, it)
                                    } else {
                                        Constant.showToast(messageResponse.message)
                                    }

                                    nowView.isClickable=true
                                }
                            })
                } else {
                    OkHttpClient.getOkHttpCline().newCall(
                        Request.Builder().baseUrl(Constant.BASE_URL).url("follow/up").post(
                            RequestBody.Builder().addParam("userId", it.id.toString())
                                .build()).build()
                    ).enqueue(object  :Callback{
                        override fun onFailure(call: Call?, e: IOException?) {
                            nowView.isClickable=true
                        }

                        override fun onResponse(call: Call?, response: Response?) {
                            val messageResponse = Gson.getGson().fromJson<MessageResponse>(
                                response?.body,
                                object : TypeToken<MessageResponse>() {}.type
                            )
                            if (messageResponse.code == 0) {
                                action(nowView, it)
                            } else {
                                Constant.showToast(messageResponse.message)
                            }
                            nowView.isClickable=true
                        }
                    })
                }

            }
        }
        viewHolder.item.setOnClickListener {
           fragment.startActivity(PersonalActivity::class.java){
               val userAndIsFollowOV = list!![viewHolder.adapterPosition]
               putExtra("user", User(id = userAndIsFollowOV.id, username = userAndIsFollowOV.name, iconPath = userAndIsFollowOV.iconPath))
           }
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position).apply {
            this?.let {
                holder.iconIV.loadIcon(Constant.BASE_URL + "/" + iconPath)
                holder.nameTV.text = name
                updateControlTV(holder.controlTV, it.follow)
            }

        }
    }

    override fun getItemCount() = if (list == null) 0 else list!!.size

    private fun updateControlTV(textView: TextView, isFollow:Boolean) {
        Log.d(TAG, "updateControlTV: $isFollow")
        textView.apply {
            text = if (isFollow) {
                setBackgroundResource(R.drawable.ygz_drawable)
                "已关注"
            } else {
                setBackgroundResource(R.drawable.wgz_drawable)
                "关注"
            }
        }

    }

    private fun  action(controlView: View, it:UserAndIsFollowOV){
        it.follow = !it.follow
        updateControlTV(controlView as TextView, it.follow)

    }
}