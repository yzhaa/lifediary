package yzh.lifediary.adapter


import android.app.Activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.batchat.preview.PreviewTools
import yzh.lifediary.R
import yzh.lifediary.util.loadPic


class UpAdapter(val activity: Activity) : RecyclerView.Adapter<UpAdapter.ViewHolder>() {

    var list: ArrayList<String>? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //这个只能是false
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.up_image_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.image.setOnClickListener {
            list?.let { it1 ->
                PreviewTools.startImagePreview(
                    activity,
                    it1, viewHolder.itemView, viewHolder.adapterPosition
                )
            }

        }
        return viewHolder

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        list?.get(position).apply {
            this?.let {
                holder.image.loadPic(this)

            }

        }


    }

    override fun getItemCount() = if (list == null) 0 else list!!.size
}