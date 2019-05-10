package cl.jesualex.stooltip

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by jesualex on 2019-04-29.
 */
class ChildView : LinearLayout {
    private lateinit var textView: TextView
    private lateinit var icon: ImageView

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

    fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        textView = TextView(context, attrs, defStyleAttr)
        icon = ImageView(context, attrs, defStyleAttr)

        val iconLP = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val textLP = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            iconLP.marginEnd = context.resources.getDimensionPixelSize(R.dimen.iconRightMargin)
        }else{
            iconLP.rightMargin = context.resources.getDimensionPixelSize(R.dimen.iconRightMargin)
        }

        textLP.gravity = Gravity.CENTER
        iconLP.gravity = Gravity.CENTER
        icon.visibility = View.GONE

        addView(icon, iconLP)
        addView(textView, textLP)
    }

    fun getTextView(): TextView {
        return textView
    }

    fun getImageView(): ImageView {
        return icon
    }
}