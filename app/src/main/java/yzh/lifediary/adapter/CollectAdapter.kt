package yzh.lifediary.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
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


class CollectAdapter(val activity:Activity): RecyclerView.Adapter<CollectAdapter.ViewHolder>() {
    var list: MutableList<DiaryItem>? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item: MaterialCardView=view.findViewById(R.id.sc_item)
        val cancelTV:TextView= view.findViewById(R.id.sc_cancel_tv)
        val mainIV: ImageView = view.findViewById(R.id.sc_main_iv)
        val titleTV: TextView = view.findViewById(R.id.sc_title_tv)
        val likeCount: TextView =view.findViewById(R.id.sc_lc_tv)
        val nameTV: TextView =view.findViewById(R.id.sc_name_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sc_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.cancelTV.setOnClickListener {
            it.isClickable = false
                OkHttpClient.getOkHttpCline()
                    .newCall(
                        Request.Builder().baseUrl(Constant.BASE_URL).url("like/d")
                            .post(RequestBody.Builder().addParam("diaryId",
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
            activity.startActivity(Intent(activity, DiaryDetailsActivity::class.java).putExtra("item", list?.get(viewHolder.adapterPosition)))
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position).apply {
            this?.let {
                holder.mainIV.loadPic(Constant.BASE_URL + "/" + mainPic)
                holder.nameTV.text= username
                holder.likeCount.text=likeCount.toString()
                holder.titleTV.text=title
            }

        }
    }

    override fun getItemCount() = if (list==null) 0 else list!!.size

}