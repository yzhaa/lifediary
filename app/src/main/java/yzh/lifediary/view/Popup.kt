package yzh.lifediary.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.donkingliang.imageselector.utils.ImageSelector
import com.yzh.permissonx.PermissionX
import razerdp.basepopup.BasePopupWindow
import yzh.lifediary.R

//Activity ->FragmentActivity->AppcompatActivity

class Popup(val fragment: Fragment) : BasePopupWindow(fragment) {

    //这里的content 跟Activity应该是同一个东西
    init {
        (fragment as DiaryFragment).setCallback(this::callback)
        setContentView(R.layout.popwindow)
        val textView = findViewById<TextView>(R.id.g_tv)
        textView.setOnClickListener {
            if (check()) {
                PermissionX.request(
                    fragment,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ) { allGranted, deniedList ->
                    if (allGranted) {
                        open()
                        dismiss()
                    }
                }
            } else {
                open()
                dismiss()
            }
        }


    }


    private fun check(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ) != PackageManager.PERMISSION_GRANTED

    }

    private fun open() {
        ImageSelector.builder()
            .useCamera(true) // 设置是否使用拍照
            .setSingle(false)  //设置是否单选
            .canPreview(false) //是否可以预览图片，默认为true
            .setMaxSelectCount(5) // 图片的最大选择数量，小于等于0时，不限数量。
            .start(fragment, 1) // 打开相册
    }


    private fun callback(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (data != null) {
                //获取选择器返回的数据 wang 7/23
                val images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                /**是否是来自于相机拍照的图片，
                 * 只有本次调用相机拍出来的照片，返回时才为true。当为true时，图片返回的结果有且只有一张图片。
                 */
                val isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
                if (images != null) {
                    for (i in images) {
                        Log.d(TAG, "onActivityResult: $i")
                    }
                    fragment.startActivity(
                        Intent(context, UpDiaryActivity::class.java)
                            .putStringArrayListExtra("images", images)
                    )
                }
            }
        }
    }


}