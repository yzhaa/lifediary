package yzh.lifediary.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle

import androidx.viewpager2.adapter.FragmentStateAdapter
import yzh.lifediary.view.info.InfoFragment

import yzh.lifediary.view.main.MainFragment


class MainViewAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
   private  val  mListFragment = listOf(MainFragment(), InfoFragment())

    override fun getItemCount(): Int {
        return mListFragment.size
    }

    override fun createFragment(position: Int): Fragment {
      return mListFragment[position]

    }

}