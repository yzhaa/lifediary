package yzh.lifediary.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_follow.*
import kotlinx.android.synthetic.main.title.*
import yzh.lifediary.R
import yzh.lifediary.adapter.SearchUserAdapter
import yzh.lifediary.entity.DiaryItem
import yzh.lifediary.entity.User

import yzh.lifediary.okhttp.*



class SearchUserFragment : Fragment() {
    lateinit var adapter:SearchUserAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        val recyclerView=view.findViewById<RecyclerView>(R.id.search_user_rv)
        adapter =SearchUserAdapter(this)
        recyclerView.adapter=adapter
        return view
    }




//    override fun onDestroy() {
//        super.onDestroy()
//        if (adapter.removeList.size > 0) {
//            TaskExecutor.execute {
//                OkHttpClient.getOkHttpCline()
//                    .newCall(
//                        Request.Builder().baseUrl(Constant.BASE_URL).url("follow/ds")
//                            .post(
//                                RequestBody.Builder()
//                                    .addParam("userIds", adapter.removeList.toString())
//                                    .build()
//                            )
//                            .build()
//                    ).execute()
//            }
//        }
//
//    }
@SuppressLint("NotifyDataSetChanged")
fun  update(users:MutableList<User>){
        adapter.list=users
        adapter.notifyDataSetChanged()

    }


}