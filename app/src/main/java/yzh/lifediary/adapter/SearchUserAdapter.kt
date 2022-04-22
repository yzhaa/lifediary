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


import yzh.lifediary.R
import yzh.lifediary.entity.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.loadIcon
import yzh.lifediary.view.info.PersonalActivity

import java.util.*

class SearchUserAdapter(val activity: Activity) : RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {
    var list: MutableList<User>? = null

    var removeList: MutableList<Int> = LinkedList()
    var addList: MutableList<Int> = LinkedList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item: ConstraintLayout = view.findViewById(R.id.gz_item)
        val controlTV: TextView = view.findViewById(R.id.gz_control_tv)
        val iconIV: ImageView = view.findViewById(R.id.gz_icon_iv)
        val nameTV: TextView = view.findViewById(R.id.gz_name_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gz_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.controlTV.setOnClickListener {
            list!![viewHolder.adapterPosition].let {
                if (!removeList.contains(it.id)) {
                    removeList.add(it.id)
                } else {
                    removeList.remove(it.id)
                }
                updateControlTV(viewHolder.controlTV, it.id)
            }
        }
        viewHolder.item.setOnClickListener {
            activity.startActivity(
                Intent(activity, PersonalActivity::class.java).putExtra(
                    "user", list!![viewHolder.adapterPosition]
                )
            )
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position).apply {
            this?.let {
                holder.iconIV.loadIcon(Constant.BASE_URL + "/" + iconPath)
                holder.nameTV.text = username
                updateControlTV(holder.controlTV, it.id)
            }

        }
    }

    override fun getItemCount() = if (list == null) 0 else list!!.size

    private fun updateControlTV(textView: TextView, id: Int) {
        textView.apply {
            text = if (!removeList.contains(id)) {
                setBackgroundResource(R.drawable.ygz_drawable)
                "已关注"
            } else {
                setBackgroundResource(R.drawable.wgz_drawable)
                "关注"
            }
        }

    }
}