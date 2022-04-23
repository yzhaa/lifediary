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
import yzh.lifediary.entity.UserAndIsFollowOV

import yzh.lifediary.okhttp.*



class SearchUserFragment : Fragment() {
     val adapter=SearchUserAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        val recyclerView=view.findViewById<RecyclerView>(R.id.search_user_rv)
        recyclerView.adapter=adapter
        return view
    }



@SuppressLint("NotifyDataSetChanged")
fun  update(users:List<UserAndIsFollowOV>){
        adapter.list=users
        adapter.notifyDataSetChanged()
    }


}