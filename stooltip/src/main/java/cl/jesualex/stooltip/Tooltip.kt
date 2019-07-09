package cl.jesualex.stooltip

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.support.annotation.StringRes
import android.support.v4.widget.NestedScrollView
import android.text.Spannable
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by jesualex on 2019-04-25.
 */
class Tooltip private constructor(private val activity: Activity, private val refView: View, private val rootView: View?){
    internal val tooltipView: TooltipView = TooltipView(activity)
    internal val overlay: FrameLayout = FrameLayout(activity)
    internal var clickToHide: Boolean = true
    internal var displayListener: DisplayListener? = null
    internal var tooltipClickListener: TooltipClickListener? = null
    internal var refViewClickListener: TooltipClickListener? = null
    internal var animIn = 0
    internal var animOut = 0

    init {
        if(rootView?.let{return@let findScrollParent(it)} == null){
            findScrollParent(refView)?.let { scrollParent ->
                scrollParent.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {
                    _, scrollX, scrollY, oldScrollX, oldScrollY ->

                    tooltipView.translationY -= (scrollY - oldScrollY)
                    tooltipView.translationX -= (scrollX - oldScrollX)
                })
            }
        }

        tooltipView.setOnClickListener {
            tooltipClickListener?.onClick(tooltipView, this)

            if (clickToHide) {
               close()
            }
        }

        refView.addOnAttachStateChangeListener( object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                closeNow()
                v?.removeOnAttachStateChangeListener(this)
            }

            override fun onViewAttachedToWindow(v: View?) {}
        })
    }

    private fun findScrollParent(view: View): NestedScrollView? {
        val parent = view.parent

        return parent as? NestedScrollView ?: if (parent is View) {
            findScrollParent(parent as View)
        } else {
            null
        }
    }

    internal fun getTextView(): TextView?{
        return when (val childView = tooltipView.childView) {
            is ChildView -> childView.getTextView()
            is TextView -> childView
            else -> null
        }
    }

    internal fun getImageView(): ImageView?{
        return when (val childView = tooltipView.childView) {
            is ChildView -> childView.getImageView()
            is ImageView -> childView
            else -> null
        }
    }

    @JvmOverloads fun show(duration: Long = 0, text: String? = null): Tooltip{
        refView.post {
            closeNow()

            text?.let { getTextView()?.text = it }

            val decorView = if (rootView != null)
                rootView as ViewGroup
            else
                activity.window.decorView as ViewGroup

            val rect = Rect()
            val decorRect = Rect()
            refView.getGlobalVisibleRect(rect)
            decorView.getGlobalVisibleRect(decorRect)

            tooltipView.setup(rect, decorRect)

            val lP = ViewGroup.LayoutParams(
                    decorView.width,
                    decorView.height
            )

            decorView.addView(overlay, lP)

            if(animIn == 0){
                displayListener?.onDisplay(tooltipView, true)
            }else{
                val animation = AnimationUtils.loadAnimation(tooltipView.context, animIn)

                animation.setAnimationListener(object :Animation.AnimationListener{
                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        displayListener?.onDisplay(tooltipView, true)
                    }

                    override fun onAnimationStart(p0: Animation?) {}
                })

                tooltipView.startAnimation(animation)
            }

            if (duration > 0) {
                tooltipView.postDelayed({ close() }, duration)
            }
        }

        return this
    }

    fun show(duration: Long, @StringRes text: Int): Tooltip{
        getTextView()?.setText(text)
        return show(duration)
    }

    fun show(duration: Long, text: Spanned): Tooltip{
        getTextView()?.text = text
        return show(duration)
    }

    fun show(@StringRes text: Int): Tooltip{
        getTextView()?.setText(text)
        return show()
    }

    fun show(text: Spanned): Tooltip{
        getTextView()?.text = text
        return show()
    }

    fun show(text: String): Tooltip{
        getTextView()?.text = text
        return show()
    }

    fun close(){
        if(animOut == 0){
            overlay.post { closeNow() }
        }else{
            val animation = AnimationUtils.loadAnimation(tooltipView.context, animOut)

            animation.setAnimationListener(object :Animation.AnimationListener{
                override fun onAnimationRepeat(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    overlay.post { closeNow() }
                }

                override fun onAnimationStart(p0: Animation?) {}
            })

            tooltipView.startAnimation(animation)
        }
    }

    fun closeNow(){
        val parent = overlay.parent

        if (parent != null && parent is ViewGroup) {
            parent.removeView(overlay)
            displayListener?.onDisplay(tooltipView, false)
        }
    }

    internal fun initTargetClone() {
        val targetGhostView = TargetView(activity)
        targetGhostView.setTarget(refView)
        overlay.addView(targetGhostView)
        targetGhostView.setOnClickListener { refViewClickListener?.onClick(targetGhostView, this)}
    }

    internal fun setOverlayListener(overlayClickListener: TooltipClickListener){
        overlay.setOnClickListener { overlayClickListener.onClick(it, this) }
    }

    companion object{
        @JvmStatic @JvmOverloads fun on(refView: View, rootView: View? = null): TooltipBuilder {
            return getActivity(refView.context)!!.let {
                return TooltipBuilder(Tooltip(it, refView, rootView))
            }
        }

        @JvmStatic private fun getActivity(context: Context): Activity? {
            if (context is Activity) {
                return context
            } else if (context is ContextWrapper) {
                return getActivity(context.baseContext)
            }

            return null
        }
    }
}