package yzh.lifediary.adapter


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import yzh.lifediary.R
import yzh.lifediary.entity.DiaryItem
import yzh.lifediary.entity.MessageResponse

import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.loadPic
import yzh.lifediary.view.main.DiaryDetailsActivity
import java.io.IOException


class  PersonalAdapter(val activity:Activity): RecyclerView.Adapter<PersonalAdapter.ViewHolder>() {
    var list: MutableList<DiaryItem>? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item: ConstraintLayout=view.findViewById(R.id.gr_item)
        val deleteTV:TextView= view.findViewById(R.id.gr_delete_tv)
        val timeTV: TextView =view.findViewById(R.id.gr_time_tv)
        val mainIV: ImageView = view.findViewById(R.id.gr_main_iv)
        val titleTV: TextView = view.findViewById(R.id.gr_title_tv)
        val contentTV: TextView =view.findViewById(R.id.gr_content_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gr_article, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.deleteTV.setOnClickListener {
            it.isClickable = false
            OkHttpClient.getOkHttpCline()
                .newCall(
                    Request.Builder().baseUrl(Constant.BASE_URL).url("diary/delete")
                        .post(RequestBody.Builder().addParam("id",
                            list!![viewHolder.adapterPosition].id.toString() ).build())
                        .build()
                )
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        it.isClickable = true
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val messageResponse = Gson.getGson()
                            .fromJson<MessageResponse>(
                                response?.body,
                                object : TypeToken<MessageResponse>() {}.type
                            )
                        if (messageResponse.code == 0) {
                            list!!.removeAt(viewHolder.adapterPosition)
                            notifyItemRemoved(viewHolder.adapterPosition)
                        }
                        it.isClickable = true
                    }
                })
        }
        viewHolder.item.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
                    DiaryDetailsActivity::class.java
                ).putExtra("item", list?.get(viewHolder.adapterPosition))
            )

        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position).apply {
            this?.let {
                holder.mainIV.loadPic(Constant.BASE_URL + "/" + mainPic)
                holder.timeTV.text= with(date){
                    val index=  date.indexOf("-")
                  date.subSequence(0,index).toString()+"\r\n"+date.subSequence(index+1,date.length).toString()
                }
                holder.contentTV.text=content
                holder.titleTV.text=title
            }

        }
    }

    override fun getItemCount() = if (list==null) 0 else list!!.size

}