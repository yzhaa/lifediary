package yzh.lifediary.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_diary_details.*
import yzh.lifediary.R
import yzh.lifediary.entity.*
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.TaskExecutor
import yzh.lifediary.util.loadIcon
import yzh.lifediary.util.loadPic
import yzh.lifediary.view.info.PersonalActivity
import java.io.IOException


class DiaryDetailsActivity : AppCompatActivity() {
    lateinit var item: DiaryItem
    var diaryDetails: DiaryDetails? = null
    var isFollow = false
    var isSelf = false

    //用来分享的
    lateinit var mainBitmap: Bitmap


    lateinit var adapter: BannerImageAdapter<DiaryImage>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_details)
        item = intent.getSerializableExtra("item") as DiaryItem
        isSelf = Constant.user.id == item.userId
        init()
    }


    private fun share() {

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = Constant.Image_MIMETYPE
        intent.putExtra(Intent.EXTRA_TEXT, "Email body over here")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Email subject over here")
        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过文件提供器选择性得将内容的URL共享给外部
            FileProvider.getUriForFile(
                this,
                "yzh.lifediary.fileprovider",
                Constant.createTemp(mainBitmap)
            )
        } else {
            //android 7以下可以直接获得
            Uri.fromFile(Constant.createTemp(mainBitmap))
        }
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        startActivity(Intent.createChooser(intent, "选择分享应用"))

    }

//    private fun  wxShare(){
//
//        wechat.setOnClickListener {
//            Log.d("share", "nativeshare 微信分享 ")
//            if (weChatShareUtil.isSupportWX) {
//                val desc = "百度"
//                var result = true
//                val url = "http://www.baidu.com"
//                val title = "百度"
//                val bitmap: Bitmap
//                result = weChatShareUtil.shareUrl(
//                    url,
//                    title,
//                    bitmap,
//                    desc,
//                    SendMessageToWX.Req.WXSceneSession
//                )
//            } else {
//                Toast.makeText(this@MineWebViewActivity, "手机上微信版本不支持分享到朋友圈", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//        val wxcircle: Button = view.findViewById(R.id.bottom_share_wxcircle)
//        wxcircle.setOnClickListener(View.OnClickListener {
//            Log.d("share", "nativeshare 微信朋友圈分享 ")
//            if (weChatShareUtil.isSupportWX) {
//                val desc = "百度"
//                var result = true
//                val url = "http://www.baidu.com"
//                val title = "百度"
//                val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_logo)
//                result = weChatShareUtil.shareUrl(
//                    url,
//                    title,
//                    bitmap,
//                    desc,
//                    SendMessageToWX.Req.WXSceneTimeline
//                )
//            } else {
//                Toast.makeText(this@MineWebViewActivity, "手机上微信版本不支持分享到朋友圈", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        })
//        shareDialog.setContentView(view)
//        shareDialog.show()
//
//
//    }

    private fun init() {
        if (isSelf) gz_iv.visibility = View.INVISIBLE
        TaskExecutor.execute {
            mainBitmap =
                Glide.with(this).asBitmap().load(Constant.BASE_URL + "/" + item.userIcon).submit()
                    .get()
        }
        getData()
        listener()
        username_tv.text = item.username
        user_icon_iv.setOnClickListener {
            startActivity(
                Intent(this, PersonalActivity::class.java).putExtra(
                    "user",
                    User(item.userId, username = item.username, iconPath = item.userIcon)
                )
            )
        }
        user_icon_iv.loadIcon(Constant.BASE_URL + "/" + item.userIcon)
        adapter = object : BannerImageAdapter<DiaryImage>(null) {
            override fun onBindView(
                holder: BannerImageHolder?,
                data: DiaryImage,
                position: Int,
                size: Int
            ) {
                holder!!.imageView.apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    loadPic(Constant.BASE_URL + "/" + data.path)
                }
            }
        }

        banner.addBannerLifecycleObserver(this)//添加生命周期观察者
            .setAdapter(adapter).indicator = CircleIndicator(this);
        likeUpdate()

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

    private fun likeUpdate() {
        like_iv.setImageResource(if (item.isLike) R.drawable.dianzan else R.drawable.wdianzan)
    }

    private fun followUpdate() {
        if (isFollow) gz_iv.setBackgroundResource(R.drawable.yiguanzhu)
        else gz_iv.setBackgroundResource(R.drawable.iconweiguanzhu)
    }

    private fun listener() {
        like_iv.setOnClickListener {
            like_iv.isClickable = false
            val s = if (item.isLike) "d" else "up"
            OkHttpClient.getOkHttpCline()
                .newCall(
                    Request.Builder().baseUrl(Constant.BASE_URL).url("like/$s")
                        .post(RequestBody.Builder().addParam("diaryId", item.id.toString()).build())
                        .build()
                )
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        like_iv.isClickable = true
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val messageResponse = Gson.getGson()
                            .fromJson<MessageResponse>(
                                response?.body,
                                object : TypeToken<MessageResponse>() {}.type
                            )
                        if (messageResponse.code == 0) {
                            item.isLike = !item.isLike
                            likeUpdate()
                            isUpdate=true
                        }
                        like_iv.isClickable = true
                    }
                })
        }
        if (!isSelf) {
            gz_iv.setOnClickListener {
                like_iv.isClickable = false
                val s = if (isFollow) "d" else "up"
                OkHttpClient.getOkHttpCline()
                    .newCall(
                        Request.Builder().baseUrl(Constant.BASE_URL).url("follow/$s")
                            .post(
                                RequestBody.Builder().addParam("userId", item.userId.toString())
                                    .build()
                            )
                            .build()
                    )
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call?, e: IOException?) {
                            like_iv.isClickable = true
                        }

                        override fun onResponse(call: Call?, response: Response?) {
                            val messageResponse = Gson.getGson()
                                .fromJson<MessageResponse>(
                                    response?.body,
                                    object : TypeToken<MessageResponse>() {}.type
                                )
                            if (messageResponse.code == 0) {
                                isFollow = !isFollow
                                followUpdate()
                                isUpdate=true
                            }
                            like_iv.isClickable = true
                        }
                    })
            }
        }


        back_iv.setOnClickListener {
            finish()
        }
        like_count_tv.setOnClickListener {
            share()
        }
    }

    private fun getData() {
        OkHttpClient.getOkHttpCline()
            .newCall(Request.Builder().baseUrl(Constant.BASE_URL).url("diary/d/${item.id}").build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {

                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call?, response: Response?) {
                    val detailsResponse = Gson.getGson()
                        .fromJson<DiaryDetailsResponse>(
                            response?.body,
                            object : TypeToken<DiaryDetailsResponse>() {}.type
                        )
                    diaryDetails = detailsResponse.data
                    update()

                }
            })

        if (!isSelf) {
            OkHttpClient.getOkHttpCline()
                .newCall(
                    Request.Builder().baseUrl(Constant.BASE_URL).url("follow/get/${item.userId}")
                        .build()
                )
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {

                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val followResponse = Gson.getGson()
                            .fromJson<FollowResponse>(
                                response?.body,
                                object : TypeToken<FollowResponse>() {}.type
                            )

                        isFollow = followResponse.data != null
                        followUpdate()

                    }
                })
        }

    }


}