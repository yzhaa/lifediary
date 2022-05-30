package yzh.lifediary.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun initBase(){
        progressBar.visibility = View.GONE
    }

    fun show(func:()->Unit){
        progressBar.visibility = View.VISIBLE
        func()
    }
    fun hide(func: () -> Unit){
        progressBar.visibility = View.GONE
        func()
    }

}