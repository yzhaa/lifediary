package yzh.lifediary.view.main


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator


import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.title.*

import yzh.lifediary.R
import yzh.lifediary.adapter.MainTypeAdapter
import yzh.lifediary.util.startActivity
import yzh.lifediary.view.search.SearchActivity


class MainFragment : Fragment() {
    var isFirst = true
    val tabName = listOf("日记", "天气")

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

        val adapter = MainTypeAdapter(this)
        fm_vp.adapter = adapter

        val mediator = TabLayoutMediator(fm_tl, fm_vp) { tab, position -> //这里可以自定义TabView
            tab.text = tabName[position]
        }
        right_btn.setOnClickListener {
            startActivity(SearchActivity::class.java) {}
        }

        //要执行这一句才是真正将两者绑定起来
        //要执行这一句才是真正将两者绑定起来
        mediator.attach()

    }


}


