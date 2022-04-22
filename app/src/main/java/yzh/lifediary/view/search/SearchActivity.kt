package yzh.lifediary.view.search


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import com.yzh.myjson.Gson
import com.yzh.myjson.TypeToken

import kotlinx.android.synthetic.main.activity_search.*
import yzh.lifediary.R
import yzh.lifediary.entity.SearchResponse
import yzh.lifediary.okhttp.*
import yzh.lifediary.util.Constant
import yzh.lifediary.util.startActivity
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var adapter: ArrayAdapter<String>

    var data = ArrayList<String>(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val item = menu?.findItem(R.id.search)
        searchView = MenuItemCompat.getActionView(item) as SearchView
        initSearchView()
        return true
    }

    private fun initSearchView() {
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "请输入搜索内容"
        adapter = ArrayAdapter<String>(this, R.layout.search_item, data)
        lv.adapter = adapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
              this@SearchActivity.startActivity(SearchResultActivity::class.java){
                  putExtra("title", query)
              }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null || newText.isEmpty()) {
                    adapter.clear()
                    adapter.notifyDataSetChanged()
                    return true
                }

                OkHttpClient.getOkHttpCline()
                    .newCall(
                        Request.Builder().baseUrl(Constant.BASE_URL).url("search/m/$newText")
                            .build()
                    )
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call?, e: IOException?) {
                        }

                        override fun onResponse(call: Call?, response: Response?) {
                            Gson.getGson().fromJson<SearchResponse>(
                                response?.body,
                                object : TypeToken<SearchResponse>() {}.type
                            ).data.apply {
                                adapter.clear()
                                adapter.addAll(this)

                            }
                        }
                    })
                return true
            }
        })
    }


}