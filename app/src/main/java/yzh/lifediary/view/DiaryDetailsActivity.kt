package yzh.lifediary.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_diary_details.*
import yzh.lifediary.R
import yzh.lifediary.entity.DiaryDetails
import yzh.lifediary.entity.DiaryImage
import yzh.lifediary.entity.DiaryDetailsResponse
import yzh.lifediary.entity.DiaryItem
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import java.io.IOException


class DiaryDetailsActivity : AppCompatActivity() {
    lateinit var item: DiaryItem
    var diaryDetails: DiaryDetails? = null

    lateinit var adapter: BannerImageAdapter<DiaryImage>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_details)
        item = intent.getSerializableExtra("item") as DiaryItem
        init()
    }


    private fun init() {
        OkHttpClient.getOkHttpCline()
            .newCall(Request.Builder().baseUrl(Constant.BASE_URL).url("diary/d/${item.id}").build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {

                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call?, response: Response?) {
                    val detailsResponse = Gson.getGson()
                        .fromJson<DiaryDetailsResponse>(response?.body,
                            object : TypeToken<DiaryDetailsResponse>() {}.type
                        )
                    diaryDetails = detailsResponse.data
                    update()

                }
            })

        username_tv.text = item.username
        Glide.with(this).load(Constant.BASE_URL + "/" + item.userIcon).into(user_icon_iv)
        if (item.isLike) gz_iv.setBackgroundResource(R.drawable.yiguanzhu)
        else gz_iv.setBackgroundResource(R.drawable.iconweiguanzhu)
        adapter = object : BannerImageAdapter<DiaryImage>(null) {
            override fun onBindView(
                holder: BannerImageHolder?,
                data: DiaryImage,
                position: Int,
                size: Int
            ) {
                Glide.with(holder!!.itemView)
                    .load(Constant.BASE_URL + "/" + data.path)
                    .into(holder.imageView.apply { scaleType = ImageView.ScaleType.FIT_CENTER })
            }
        }
        banner.addBannerLifecycleObserver(this)//添加生命周期观察者
            .setAdapter(adapter).indicator = CircleIndicator(this);

        if (item.isLike) like_iv.setImageResource(R.drawable.dianzan)


    }

    private fun update() {

        diaryDetails?.let {
            diary_title_tv.text = it.diary.title
            diary_content_tv.text = it.diary.content
            diary_time_tv.text = it.diary.date
            adapter.setDatas(it.images)
            adapter.notifyDataSetChanged()
        }


    }
}