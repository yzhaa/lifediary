package yzh.lifediary.view

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_info.*
import yzh.lifediary.R
import yzh.lifediary.adapter.InfoAdapter
import yzh.lifediary.adapter.fromAlbum
import yzh.lifediary.entity.User
import yzh.lifediary.util.Constant

class InfoFragment : Fragment() {
    lateinit var adapter: InfoAdapter
    lateinit var intent: Intent
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        adapter = InfoAdapter(Constant.user, this@InfoFragment)
        (view as (ListView)).adapter = adapter
        return view

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的照片显示
                        adapter.noticeChange(uri)
                    }
                }
            }
        }

    }


}