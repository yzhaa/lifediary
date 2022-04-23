package yzh.lifediary.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.fragment_diary.*

import yzh.lifediary.R
import yzh.lifediary.adapter.DiaryAdapter
import yzh.lifediary.entity.ItemResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.view.TAG
import java.io.IOException


//onCreateView 得自己find，因为view还没有返回去，如果使用kotlinx.android.synthetic.main.fragment_diary.* 会出错
//设置这个是为了isUpdate是为了反正选取图片，触发onStart 也会更新,然后顺便插入新记录的时候，设置update 为true 获取新数据
var isInsert = false //全局的变量。不会销毁

//这个变量是看看是否从InfoFragment 跳转过来的，如果是的话，就重绘，否则recycleview出现重影
var isRepaint = false
var isUpdateInMain = false //点赞，关注，之后更新

class DiaryFragment() : Fragment() {


    //Fragment传了this，难道调用不是调用DiaryFragment()的onActivityResult嘛？
    private val popupWindow by lazy { Popup(this) }
    lateinit var adapter: DiaryAdapter
    private lateinit var callback: (Int, Int, Intent?) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_diary, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        (recyclerView.layoutManager as StaggeredGridLayoutManager).apply {
            spanCount = 2
//            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }

        adapter = DiaryAdapter(this)
        recyclerView.adapter = adapter
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.sr)
        swipeRefreshLayout.setOnRefreshListener {
            getData(false)
        }
        val btn = view.findViewById<FloatingActionButton>(R.id.addBtn)
        btn.setOnClickListener {
            popupWindow.setPopupGravity(Gravity.BOTTOM).showPopupWindow()

        }
        isUpdateInMain = true
        return view
    }

    //同一个Activity 下的fragment 切换的时候，并不一定执行这个方法
    override fun onStart() {
        super.onStart()
        if (isInsert) {
            sr.isRefreshing = true
            getData(true)
            Log.d(TAG, "onStart: 插入了")
            isInsert = false
        }
        if (isUpdateInMain) {
            sr.isRefreshing = true
            getData(false)
            Log.d(TAG, "onStart: 更新了")
            isUpdateInMain = false
        }


        //这样可能导致每次调用onStart（不可见变可见），都会重新设置
//        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        recyclerView.layoutManager = layoutManager
//        val adapter = DiaryAdapter()
//        recyclerView.adapter = adapter

    }


    //会重新绘制
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (isRepaint) {
            isRepaint = false
            Log.d(TAG, "onResume: 重新绘制了")
            adapter.notifyDataSetChanged()
        }

    }


    //用什么 context（Activity，Fragment）启动的Activity，就会回调什么的onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callback(requestCode, resultCode, data)

    }

    fun setCallback(callback: (Int, Int, Intent?) -> Unit) {
        this.callback = callback

    }

    private fun getData(isAdd: Boolean) {
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
                    adapter.list = Gson.getGson().fromJson<ItemResponse>(
                        response?.body,
                        object : TypeToken<ItemResponse>() {}.type
                    ).data
                    if (isAdd) {
                        adapter.notifyItemInserted(adapter.itemCount - 1)
                        Log.d(TAG, "onResponse: 插入")
                    } else {
                        adapter.notifyDataSetChanged()
                        Log.d(TAG, "onResponse: ")
                    }
                    sr.isRefreshing = false
                }
            })
    }

}