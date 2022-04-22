package yzh.lifediary.view.info

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import yzh.lifediary.R
import yzh.lifediary.adapter.InfoAdapter

import yzh.lifediary.util.Constant
import yzh.lifediary.view.main.isRepaint

class InfoFragment : Fragment() {
    lateinit var adapter: InfoAdapter
    lateinit var intent: Intent
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        adapter = InfoAdapter(Constant.user, this@InfoFragment)
        (view as (ListView)).adapter = adapter
        return view

    }

    override fun onStart() {
        super.onStart()
        adapter.user=Constant.user
        adapter.notifyDataSetChanged()


    }


    //要在这里通知 是否Diary是否要重新绘制，因为onStart（）方法并不一定触发
    override fun onPause() {
        super.onPause()
        isRepaint = true
    }
}