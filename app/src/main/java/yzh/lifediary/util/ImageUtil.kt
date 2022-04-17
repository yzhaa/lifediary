package yzh.lifediary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import yzh.lifediary.okhttp.*
import yzh.lifediary.view.TAG
import java.io.*

object ImageUtil {

    fun setBitmap(context: Context, path: String, image: ImageView) {
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), path
        )
        if (file.exists()) {
            image.setImageBitmap(BitmapFactory.decodeStream(FileInputStream(file)))
        } else {
            Log.d(TAG, Constant.BASE_URL + path)
            OkHttpClient.getOkHttpCline()
                .newCall(Request.Builder().baseUrl(Constant.BASE_URL).url(path).build())
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {

                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        response?.body?.encodeToByteArray()?.apply {
                            Log.d(TAG, response.body)
                            image.setImageBitmap(BitmapFactory.decodeByteArray(this, 0, this.size))
                        }
                    }
                })
        }

    }

    /* Checks if external storage is available for read and write */
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /* Checks if external storage is available to at least read */
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    private fun getPrivateAlbumStorageDir(context: Context, albumName: String): File {
        // Get the directory for the app's private pictures directory.
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        return file
    }


}