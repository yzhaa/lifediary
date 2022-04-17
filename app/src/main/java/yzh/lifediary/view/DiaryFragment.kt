package yzh.lifediary.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.donkingliang.imageselector.utils.ImageSelector
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.fragment_diary.*

import yzh.lifediary.R
import yzh.lifediary.adapter.DiaryAdapter
import yzh.lifediary.entity.ItemResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import java.io.IOException


//onCreateView 得自己find，因为view还没有返回去，如果使用kotlinx.android.synthetic.main.fragment_diary.* 会出错
class DiaryFragment() : Fragment() {
    private val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

    //Fragment传了this，难道调用不是调用DiaryFragment()的onActivityResult嘛？
    private val popupWindow by lazy { Popup(this) }

    private lateinit var callback: (Int, Int, Intent?) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_diary, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        val adapter = DiaryAdapter(this)
        recyclerView.adapter = adapter
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.sr)
        swipeRefreshLayout.setOnRefreshListener {
            OkHttpClient.getOkHttpCline()
                .newCall(Request.Builder().baseUrl(Constant.BASE_URL).url("diary/get").build())
                .enqueue(object :
                    Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        Constant.showToast("失败")

                        sr.isRefreshing = false
                    }

                    //应该使用更加精确的函数，告知哪部分发生变化
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call?, response: Response?) {
                        adapter.list = Gson.getGson().fromJson<ItemResponse>(response?.body,
                            object : TypeToken<ItemResponse>() {}.type).data
                        adapter.notifyDataSetChanged()
                        sr.isRefreshing = false
                    }
                })
        }
        val btn = view.findViewById<FloatingActionButton>(R.id.addBtn)
        btn.setOnClickListener {
            popupWindow.setPopupGravity(Gravity.BOTTOM).showPopupWindow()

        }
        return view
    }


    override fun onStart() {
        super.onStart()
        //这样可能导致每次调用onStart（不可见变可见），都会重新设置

//        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        recyclerView.layoutManager = layoutManager
//        val adapter = DiaryAdapter()
//        recyclerView.adapter = adapter
    }


    //用什么 context（Activity，Fragment）启动的Activity，就会回调什么的onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callback(requestCode, resultCode, data)

    }

    fun setCallback(callback: (Int, Int, Intent?) -> Unit){
        this.callback = callback

    }
}