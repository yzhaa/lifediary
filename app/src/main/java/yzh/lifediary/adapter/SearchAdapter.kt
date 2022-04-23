package yzh.lifediary.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import yzh.lifediary.entity.SearchResult
import yzh.lifediary.view.search.SearchDiaryFragment
import yzh.lifediary.view.search.SearchUserFragment

class SearchAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecycle) {
    private  val  mListFragment = listOf(SearchDiaryFragment(), SearchUserFragment())

    override fun getItemCount(): Int {
        return mListFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return mListFragment[position]

    }

    fun update(searchResult: SearchResult){
        ( mListFragment[0] as  SearchDiaryFragment).update(searchResult.diarys)
        (mListFragment[1] as SearchUserFragment).update(searchResult.userFollowOVS)

    }

}