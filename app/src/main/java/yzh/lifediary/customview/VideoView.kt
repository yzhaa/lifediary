package yzh.lifediary.customview

import android.content.Context
import android.media.MediaPlayer.OnPreparedListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.VideoView


/**
 * Created by DaQiE on 2017/2/20 0020.
 */
class CustomVideoView : VideoView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //我们重新计算高度
        val width = getDefaultSize(0, widthMeasureSpec)
        val height = getDefaultSize(0, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun setOnPreparedListener(l: OnPreparedListener) {
        super.setOnPreparedListener(l)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyDown(keyCode, event)
    }
}

