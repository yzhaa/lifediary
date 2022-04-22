package yzh.lifediary.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import yzh.lifediary.R
import yzh.lifediary.entity.DiaryItem
import yzh.lifediary.util.Constant
import yzh.lifediary.util.loadIcon
import yzh.lifediary.util.loadPic
import yzh.lifediary.util.startActivity
import yzh.lifediary.view.main.DiaryDetailsActivity

class SearchDiaryAdapter(val fragment: Fragment) : RecyclerView.Adapter<SearchDiaryAdapter.ViewHolder>() {
    var list: List<DiaryItem>? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userIcon: ImageView = view.findViewById(R.id.user_icon_iv)
        val userName: TextView = view.findViewById(R.id.username_tv)
        val likeCount: TextView =view.findViewById(R.id.like_count_tv)
        val title: TextView=view.findViewById(R.id.title_tv)
        val titleImage: ImageView = view.findViewById(R.id.mainpic_iv)
        val content :TextView=view.findViewById(R.id.content_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_diary_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            fragment.startActivity(DiaryDetailsActivity::class.java){
                putExtra("item", list?.get(viewHolder.adapterPosition))
            }
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position).apply {
            this?.let {
                holder.userIcon.loadIcon(Constant.BASE_URL + "/" + userIcon)

                holder.userName.text= username
                holder.likeCount.text=likeCount.toString()
                holder.title.text=title
                holder.titleImage.loadPic(Constant.BASE_URL + "/" + mainPic)
                holder.content.text=content

            }

        }
    }

    override fun getItemCount() = if (list==null) 0 else list!!.size

}