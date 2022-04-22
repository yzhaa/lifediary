package yzh.lifediary.view.info

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.title.*
import yzh.lifediary.R
import yzh.lifediary.adapter.CollectAdapter
import yzh.lifediary.entity.ItemResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import java.io.IOException

class CollectActivity : AppCompatActivity() {
    lateinit var adapter: CollectAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)
        init()


    }

    private  fun  init(){
        title_tv.text="收藏"
        right_btn.visibility= View.INVISIBLE
        adapter = CollectAdapter(this)
        sc_rv.adapter=adapter
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun getData(){
        OkHttpClient.getOkHttpCline()
            .newCall(Request.Builder().baseUrl(Constant.BASE_URL).url("diary/collect").build())
            .enqueue(object :
                Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Constant.showToast("失败")

                }

                //应该使用更加精确的函数，告知哪部分发生变化
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call?, response: Response?) {
                    adapter.list = Gson.getGson().fromJson<ItemResponse>(
                        response?.body,
                        object : TypeToken<ItemResponse>() {}.type
                    ).data
                    Log.d("CollectTest", "onResponse: ${adapter.list}")
                    adapter.notifyDataSetChanged()

                }
            })
    }
}