package yzh.lifediary.view.search


import android.annotation.SuppressLint
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import yzh.lifediary.R
import yzh.lifediary.adapter.DiaryAdapter
import yzh.lifediary.adapter.SearchDiaryAdapter
import yzh.lifediary.entity.DiaryItem



class SearchDiaryFragment: Fragment() {
   val adapter: SearchDiaryAdapter= SearchDiaryAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search_diary, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    fun  update(diarys:List<DiaryItem>){
        adapter.list=diarys
        adapter.notifyDataSetChanged()

    }





}