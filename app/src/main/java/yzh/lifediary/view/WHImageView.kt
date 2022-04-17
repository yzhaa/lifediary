package yzh.lifediary.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class WHImageView : AppCompatImageView {
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?) : super(context!!)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 设置View宽高的测量值
        var wms = widthMeasureSpec
        var hms = heightMeasureSpec
        setMeasuredDimension(
            getDefaultSize(0, wms),
            getDefaultSize(0, hms)
        )
        // 只有setMeasuredDimension调用之后，才能使用getMeasuredWidth()和getMeasuredHeight()来获取视图测量出的宽高，以此之前调用这两个方法得到的值都会是0
        val childWidthSize: Int = measuredWidth

        // 高度和宽度一样
        wms = MeasureSpec.makeMeasureSpec(
            childWidthSize, MeasureSpec.EXACTLY
        )
        hms = wms
        super.onMeasure(wms, hms)
    }
}

