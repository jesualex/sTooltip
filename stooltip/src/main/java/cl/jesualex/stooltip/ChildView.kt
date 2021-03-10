package cl.jesualex.stooltip

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi

/**
 * Created by jesualex on 2019-04-29.
 */
class ChildView : LinearLayout {
    private var textView: TextView? = null
    private var iconStart: ImageView? = null
    private var iconEnd: ImageView? = null

    @JvmOverloads constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        textView = TextView(context, attrs, defStyleAttr)
        textView!!.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        iconStart = ImageView(context, attrs, defStyleAttr)
        iconStart!!.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        iconEnd = ImageView(context, attrs, defStyleAttr)
        iconEnd!!.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    fun getTextView(): TextView {
        return textView!!
    }

    fun getStartImageView(): ImageView {
        return iconStart!!
    }

    fun getEndImageView(): ImageView {
        return iconEnd!!
    }

    fun clear(){
        iconStart?.let { removeParent(it) }
        iconStart = null
        iconEnd?.let { removeParent(it) }
        iconEnd = null
        textView?.let { removeParent(it) }
        textView = null
        removeParent(this)
    }

    fun attach(){
        removeParent(iconStart!!)
        removeParent(iconEnd!!)
        removeParent(textView!!)

        if(iconStart!!.drawable != null){
            val iconLP = iconStart!!.layoutParams as LayoutParams

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                iconLP.marginEnd = context.resources.getDimensionPixelSize(R.dimen.iconTextMargin)
            }else{
                iconLP.rightMargin = context.resources.getDimensionPixelSize(R.dimen.iconTextMargin)
            }

            iconLP.gravity = Gravity.CENTER
            addView(iconStart, iconLP)
        }

        val textLP = textView!!.layoutParams as LayoutParams
        textLP.gravity = Gravity.CENTER
        addView(textView, textLP)

        if(iconEnd!!.drawable != null){
            val iconLP = iconEnd!!.layoutParams as LayoutParams

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                iconLP.marginStart = context.resources.getDimensionPixelSize(R.dimen.iconTextMargin)
            }else{
                iconLP.leftMargin = context.resources.getDimensionPixelSize(R.dimen.iconTextMargin)
            }

            iconLP.gravity = Gravity.CENTER
            addView(iconEnd, iconLP)
        }
    }

    private fun removeParent(view: View){
        view.parent?.let {
            if(it is ViewGroup){
                it.removeView(view)
            }
        }
    }
}