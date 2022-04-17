package yzh.lifediary.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import yzh.lifediary.R
import yzh.lifediary.entity.DiaryItem
import yzh.lifediary.util.Constant
import yzh.lifediary.view.DiaryDetailsActivity

class DiaryAdapter(val fragment: Fragment) : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {
    var list: List<DiaryItem>? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userIcon: ImageView = view.findViewById(R.id.user_icon_iv)
        val userName: TextView = view.findViewById(R.id.username_tv)
        val likeCount: TextView =view.findViewById(R.id.share_tv)
        val title: TextView=view.findViewById(R.id.title_tv)
        val titleImage: ImageView = view.findViewById(R.id.title_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            fragment.startActivity(Intent(fragment.activity,DiaryDetailsActivity::class.java).putExtra("item", list?.get(viewHolder.adapterPosition)))
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        list?.get(position).apply {
            this?.let {
                Glide.with(fragment)
                    .load(Constant.BASE_URL + "/" + userIcon)
                    .into(holder.userIcon)
                holder.userName.text= username
                holder.likeCount.text=likeCount.toString()
                holder.title.text=title
                Glide.with(fragment)
                    .load(Constant.BASE_URL + "/" + mainPic)
                    .into(holder.titleImage)

            }

        }



    }

    override fun getItemCount() = if (list==null) 0 else list!!.size

}