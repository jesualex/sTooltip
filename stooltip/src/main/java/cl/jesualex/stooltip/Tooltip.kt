package cl.jesualex.stooltip

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout

/**
 * Created by jesualex on 2019-04-25.
 */
class Tooltip private constructor(private val activity: Activity, private val refView: View, private val rootView: View?){
    internal val tooltipView: TooltipView = TooltipView(activity)
    internal val overlay: FrameLayout = FrameLayout(activity)
    internal var clickToHide: Boolean = true
    internal var displayListener: DisplayListener? = null
    internal var tooltipClickListener: View.OnClickListener? = null
    internal var refViewClickListener: View.OnClickListener? = null
    internal var animIn = 0
    internal var animOut = 0

    init {
        findScrollParent(refView)?.let { scrollParent ->
            scrollParent.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {
                _, scrollX, scrollY, oldScrollX, oldScrollY ->

                tooltipView.translationY -= (scrollY - oldScrollY)
                tooltipView.translationX -= (scrollX - oldScrollX)
            })
        }

        tooltipView.setOnClickListener {
            tooltipClickListener?.onClick(tooltipView)

            if (clickToHide) {
               close()
            }
        }
    }

    private fun findScrollParent(view: View): NestedScrollView? {
        val parent = view.parent

        return parent as? NestedScrollView ?: if (parent is View) {
            findScrollParent(parent as View)
        } else {
            null
        }
    }

    @JvmOverloads fun show(duration: Long = 0): Tooltip{
        closeNow()

        val decorView = if (rootView != null)
            rootView as ViewGroup
        else
            activity.window.decorView as ViewGroup

        val rect = Rect()
        refView.getGlobalVisibleRect(rect)

        val location = IntArray(2)
        refView.getLocationOnScreen(location)
        rect.left = location[0]

        tooltipView.setup(rect, decorView.width, decorView.height)
        decorView.addView(overlay, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

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

        return this
    }

    fun close(){
        if(animOut == 0){
            closeNow()
        }else{
            val animation = AnimationUtils.loadAnimation(tooltipView.context, animOut)

            animation.setAnimationListener(object :Animation.AnimationListener{
                override fun onAnimationRepeat(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    closeNow()
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
        targetGhostView.setOnClickListener { refViewClickListener?.onClick(targetGhostView)}
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