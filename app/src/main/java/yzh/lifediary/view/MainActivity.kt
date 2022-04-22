package yzh.lifediary.view


import android.Manifest
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yzh.permissonx.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import yzh.lifediary.NetWork.LOGIN_OUT
import yzh.lifediary.NetWork.LoginOutBroadCast
import yzh.lifediary.R
import yzh.lifediary.adapter.MainViewAdapter


const val TAG = "TestTAG"

class MainActivity : AppCompatActivity() {
    private lateinit var loginOutBroadCast: LoginOutBroadCast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        PermissionX.request(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) { allGranted, deniedList ->
            if (allGranted) {
                Toast.makeText(this, "申请成功", Toast.LENGTH_SHORT).show();
            }
        }
        loginOutBroadCast= LoginOutBroadCast()
        registerReceiver(loginOutBroadCast, IntentFilter().apply { addAction(LOGIN_OUT) })


        initView()
    }

    private fun  initView() {
        val adapter = MainViewAdapter(supportFragmentManager, lifecycle)
        viewPage.isUserInputEnabled=false
        viewPage.adapter=adapter

        tabLayout.apply {
            addTab(tabLayout.newTab().setText("主页").setIcon(R.drawable.zhuye))
            addTab(tabLayout.newTab().setText("我").setIcon(R.drawable.wo))
        }
        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.text){
                   "主页"-> viewPage.setCurrentItem(0,false)
                   "我"-> viewPage.setCurrentItem(1,false)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        //要执行这一句才是真正将两者绑定起来
        //要执行这一句才是真正将两者绑定起来



    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(loginOutBroadCast)

    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.backup -> Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show()
//            R.id.delete -> Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show()
//            R.id.settings -> Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show()
//        }
//        return true
//    }
}
