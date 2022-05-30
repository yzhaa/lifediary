package yzh.lifediary.view.main


import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import kotlinx.android.synthetic.main.activity_up_diary.*
import kotlinx.android.synthetic.main.title.*
import yzh.lifediary.R
import yzh.lifediary.adapter.UpAdapter
import yzh.lifediary.entity.MessageResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.SpacesItemDecoration
import yzh.lifediary.util.TaskExecutor
import yzh.lifediary.view.BaseActivity
import java.io.IOException


class UpDiaryActivity : BaseActivity() {
    private val gridLayoutManager by lazy {
        GridLayoutManager(this, 3)
    }
    private val upAdapter by lazy { UpAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_up_diary)
        init()
    }


    private fun init() {
        initBase()
        title_tv.text = ""
        right_btn.setBackgroundResource(R.drawable.tijiaoqueren)

        val space = Constant.convertDpToPixel(this, 10f)
        up_cv.layoutManager = gridLayoutManager
        up_cv.addItemDecoration(SpacesItemDecoration(space))
        up_cv.setPadding(space, 0, 0, 0)
        upAdapter.list = intent.getStringArrayListExtra("images")
        up_cv.adapter = upAdapter

        left_btn.setOnClickListener {
            finish()
        }
        right_btn.setOnClickListener {
            if (up_title_et.text.toString().isNotBlank() && up_content_et.toString()
                    .isNotBlank() && upAdapter.list!!.size > 0
            ) {
                val list = mutableListOf<Bitmap>()
                right_btn.isClickable = false
                TaskExecutor.execute {
                    for (fileName in upAdapter.list!!) {
                        list.add(Constant.bitmapCompress(fileName))
                    }
                    OkHttpClient.getOkHttpCline().newCall(
                        Request.Builder().baseUrl(Constant.BASE_URL).url("diary/up")
                            .post(
                                RequestBody.Builder().type(RequestBody.ImageType).writeBitmap(list)
                                    .addParam("title", up_title_et.text.toString())
                                    .addParam("content", up_content_et.text.toString())
                                    .build()
                            ).build()
                    ).enqueue(object : Callback {
                        override fun onFailure(call: Call?, e: IOException?) {
                            right_btn.isClickable = true
                            done()
                        }

                        override fun onResponse(call: Call?, response: Response?) {

                            val diaryResponse = Gson.getGson()
                                .fromJson<MessageResponse>(
                                    response?.body,
                                    object : TypeToken<MessageResponse>() {}.type
                                )
                            if (diaryResponse.code == 0) {
                                right_btn.isClickable = true
                                isInsert = true
                                Constant.showToast("提交成功")
                                finish()
                            }
                            done()

                        }
                    })
                }
                load()
            }
        }
    }

    private fun load() {
        show {
            up_main.alpha = 0.5f
            up_main.isClickable = false
        }
    }

    private fun done() {
        hide {
            up_main.alpha = 1f
            up_main.isClickable = true
        }
    }
}