package yzh.lifediary.view.info

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken

import kotlinx.android.synthetic.main.activity_personal.*
import kotlinx.android.synthetic.main.gz_item.view.*

import yzh.lifediary.R

import yzh.lifediary.adapter.PersonalAdapter
import yzh.lifediary.entity.ItemResponse
import yzh.lifediary.entity.User
import yzh.lifediary.entity.UserAndIsFollowOV

import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.loadIcon
import java.io.IOException

class PersonalActivity : AppCompatActivity() {
    lateinit var user:User
    lateinit var adapter: PersonalAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        user=intent.getSerializableExtra("user") as User
        init()
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init() {

        adapter = PersonalAdapter(this)
        gr_rv.adapter = adapter
        collapsingToolbar.title = user.username
        gr_icon.loadIcon(Constant.BASE_URL + "/" + user.iconPath)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun getData() {
        OkHttpClient.getOkHttpCline()
            .newCall(Request.Builder().baseUrl(Constant.BASE_URL).url("diary/person/${user.id}").build())
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