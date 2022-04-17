package yzh.lifediary.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import yzh.lifediary.view.DiaryFragment
import yzh.lifediary.view.InfoFragment
import yzh.lifediary.view.MainFragment
import yzh.lifediary.weather.MainViewFragment

class DiaryTypeAdapter(val fragment: Fragment): FragmentStateAdapter(fragment) {
    private  val  mListFragment = listOf(DiaryFragment(),MainViewFragment())

    override fun getItemCount(): Int {
        return mListFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return mListFragment[position]

    }

}