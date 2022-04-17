package yzh.lifediary.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.card.view.*

import yzh.lifediary.R

class Card (context: Context,attributeSet: AttributeSet?,defStyleAttr:Int):LinearLayout(context,attributeSet,defStyleAttr) {
    constructor(context: Context,attributeSet: AttributeSet?):this(context,attributeSet,0)

    constructor(context: Context):this(context,null)

    init {

        LayoutInflater.from(context).inflate(R.layout.card,this)
        val ta=context.obtainStyledAttributes(attributeSet,R.styleable.Card)
        ta.getText(R.styleable.Card_text).apply { tv_card.text=this }
        ta.getResourceId(R.styleable.Card_src,0).apply { iv_card.setImageResource(this) }
        ta.recycle()



    }

}