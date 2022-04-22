package yzh.lifediary.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken
import yzh.lifediary.MyApplication
import yzh.lifediary.R
import yzh.lifediary.entity.MessageResponse
import yzh.lifediary.entity.User
import yzh.lifediary.okhttp.*
import yzh.lifediary.view.LoginActivity
import yzh.lifediary.view.TAG
import java.io.*


object Constant {

    val BASE_URL = "http://192.168.1.169:8081"
    val LoginInfo = "loginInfo"
    val userFile = "userInfo"

    val TEXT_MIMETYPE = "text/plain"
    val Image_MIMETYPE = "image/*"

    lateinit var user: User

    var options = RequestOptions() // 既缓存原始图片，又缓存转化后的图片
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).skipMemoryCache(false)

    fun isExpire(func: (Boolean) -> Unit) {
        val file: File = MyApplication.context.getFileStreamPath(userFile)
        OkHttpClient.getOkHttpCline().newCall(
            Request.Builder().baseUrl(BASE_URL).url("isExpire").build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d(TAG, "onFailure: " + e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                Log.d(TAG, "onResponse: " + response?.body.toString())
                val message = Gson.getGson().fromJson<MessageResponse>(
                    response?.body,
                    object : TypeToken<MessageResponse>() {}.type
                )

                func(message.message == "没有过期" && file.exists())

            }
        })
    }

    fun showToast(string: String) {
        Toast.makeText(MyApplication.context, string, Toast.LENGTH_SHORT).show()
    }


    fun loginOut(context: Context) {
        TaskExecutor.execute {
            val file: File = context.getFileStreamPath(userFile)
            if (file.exists()) file.delete()
            val edit =
                MyApplication.context.getSharedPreferences(LoginInfo, Context.MODE_PRIVATE).edit()
            edit.clear()
            edit.apply()
            context.startActivity(Intent(context, LoginActivity::class.java))
        }

    }

    fun convertDpToPixel(activity: Activity, dp: Float): Int {
        val displayMetrics = activity.resources.displayMetrics;
        return (dp * displayMetrics.density).toInt()
    }


    fun bitmapCompress(string: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(string)
        return bitmapCompress(bitmap)

    }

    fun bitmapCompress(bitmap: Bitmap): Bitmap {
        val mc = bitmap.byteCount / 1024 / 1024
        var newBitmap = bitmap
        val baos = ByteArrayOutputStream()
        val quality: Int = when {
            mc < 1 -> 100
            mc < 3 -> 75
            mc < 5-> 50
            else -> 25
        }
        if (quality != 100) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            val bytes = baos.toByteArray()
            newBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            baos.close()
        }
        return newBitmap
    }

    fun createTemp(bitmap: Bitmap): File {
        val file =
            File(MyApplication.context.cacheDir, System.currentTimeMillis().toString() + ".jpeg");
        try {
            val bos = BufferedOutputStream(FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (e: IOException) {
            e.printStackTrace();
        }
        return file
    }

    fun deleteTemp(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }



}
fun ImageView.loadIcon(url:String ){
    Glide.with(this).load(url).placeholder(R.drawable.icon_palceholder).apply(Constant.options).into(this)
}
fun ImageView.loadPic(url:String ){
    Glide.with(this).load(url).placeholder(R.drawable.mainpic_placeholder).apply(Constant.options).into(this)
}

fun  Fragment.startActivity(activity:Class<*> ,func: Intent.() -> Unit){
    val intent = Intent(this.requireContext(),activity)
    intent.func()
    this.startActivity(intent)
}

fun  Activity.startActivity(activity: Class<*> ,func: Intent.() -> Unit){
    val intent = Intent(this,activity)
    intent.func()
    this.startActivity(intent)
}