package yzh.lifediary.view.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.material.tabs.TabLayoutMediator
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken

import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.fragment_main.fm_tl
import kotlinx.android.synthetic.main.fragment_main.fm_vp
import yzh.lifediary.R
import yzh.lifediary.adapter.SearchAdapter
import yzh.lifediary.entity.SearchResultResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import java.io.IOException


var isUpdateInSearchResult = false
class SearchResultActivity : AppCompatActivity() {
    val tabName = listOf("日记", "用户")
    var title: String? = null
    lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        title = intent.getStringExtra("title")
        initView()
        isUpdateInSearchResult = true

    }

    private fun initView() {
        search_tv.text = title
        back_iv.setOnClickListener {
            finish()
        }
//        要这样设置，不要   left_btn.isVisible=false

        adapter = SearchAdapter(supportFragmentManager, lifecycle)
        fm_vp.adapter = adapter

        val mediator = TabLayoutMediator(fm_tl, fm_vp) { tab, position -> //这里可以自定义TabView
            tab.text = tabName[position]
        }
        search_item.setOnClickListener {
            finish()
        }
        //要执行这一句才是真正将两者绑定起来
        //要执行这一句才是真正将两者绑定起来
        mediator.attach()
    }

    override fun onResume() {
        super.onResume()
        if (isUpdateInSearchResult) {
            getData()
            isUpdateInSearchResult = !isUpdateInSearchResult
        }
    }

    private fun getData() {
        OkHttpClient.getOkHttpCline()
            .newCall(
                Request.Builder().baseUrl(Constant.BASE_URL).url("search/r/$title")
                    .build()
            )
            .enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                }

                override fun onResponse(call: Call?, response: Response?) {
                    Gson.getGson().fromJson<SearchResultResponse>(
                        response?.body,
                        object : TypeToken<SearchResultResponse>() {}.type
                    ).data.apply {
                        adapter.update(this)
                    }

                }
            })
    }



}