package yzh.lifediary.view.info

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_follow.*
import kotlinx.android.synthetic.main.title.*
import yzh.lifediary.R
import yzh.lifediary.adapter.FollowAdapter
import yzh.lifediary.entity.FollowListResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.TaskExecutor
import java.io.IOException

class FollowActivity : AppCompatActivity() {

    lateinit var adapter:FollowAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow)
        init()


    }

    private  fun  init(){
        title_tv.text="关注"
        right_btn.visibility= View.INVISIBLE
        adapter = FollowAdapter(this)
        gz_rv.adapter=adapter
        left_btn.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adapter.removeList.size > 0) {
            TaskExecutor.execute{
                OkHttpClient.getOkHttpCline()
                    .newCall(
                        Request.Builder().baseUrl(Constant.BASE_URL).url("follow/ds")
                            .post(
                                RequestBody.Builder().addParam("userIds", adapter.removeList.toString())
                                    .build()
                            )
                            .build()
                    ).execute()
            }
        }

    }

    private fun getData(){
        OkHttpClient.getOkHttpCline()
            .newCall(Request.Builder().baseUrl(Constant.BASE_URL).url("follow").build())
            .enqueue(object :
                Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Constant.showToast("失败")

                }

                //应该使用更加精确的函数，告知哪部分发生变化
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call?, response: Response?) {
                    adapter.list = Gson.getGson().fromJson<FollowListResponse>(
                        response?.body,
                        object : TypeToken<FollowListResponse>() {}.type
                    ).data
                    Log.d("CollectTest", "onResponse: ${adapter.list}")
                    adapter.notifyDataSetChanged()

                }
            })
    }
}