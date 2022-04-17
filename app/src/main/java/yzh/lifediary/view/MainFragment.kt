package yzh.lifediary.view


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator


import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.title.*

import yzh.lifediary.R
import yzh.lifediary.adapter.DiaryTypeAdapter


class MainFragment : Fragment() {
    var isFirst = true
    val tabName = listOf("记录", "天气")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        //只保证第一次调用都时候执行即可
        if (isFirst) {
            initView()
            isFirst = false
        }

    }

    private fun initView() {
        title_tv.text = "主页"
        right_btn.setBackgroundResource(R.drawable.sousuo)
//        要这样设置，不要   left_btn.isVisible=false
        left_btn.visibility=View.INVISIBLE

        val adapter = DiaryTypeAdapter(this)
        fm_vp.adapter = adapter

        val mediator = TabLayoutMediator(fm_tl, fm_vp) { tab, position -> //这里可以自定义TabView
            tab.text = tabName[position]
        }


        //要执行这一句才是真正将两者绑定起来
        //要执行这一句才是真正将两者绑定起来
        mediator.attach()

    }


}


