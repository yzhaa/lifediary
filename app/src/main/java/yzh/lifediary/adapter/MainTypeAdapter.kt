package yzh.lifediary.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import yzh.lifediary.view.main.DiaryFragment
import yzh.lifediary.view.main.weather.WeatherFragment

class MainTypeAdapter(val fragment: Fragment): FragmentStateAdapter(fragment) {
    private  val  mListFragment = listOf(DiaryFragment(),WeatherFragment())

    override fun getItemCount(): Int {
        return mListFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return mListFragment[position]

    }

}