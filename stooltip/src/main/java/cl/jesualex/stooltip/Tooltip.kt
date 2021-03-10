package cl.jesualex.stooltip

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes

/**
 * Created by jesualex on 2019-04-25.
 */
class Tooltip private constructor(
    private var activity: Activity?,
    private var refView: View?,
    closeOnParentDetach: Boolean
){
    internal var tooltipView: TooltipView? = TooltipView(activity!!)
    internal var overlay: FrameLayout? = FrameLayout(activity!!)
    internal var clickToHide: Boolean = true
    internal var displayListener: DisplayListener? = null
    internal var tooltipClickListener: TooltipClickListener? = null
    internal var refViewClickListener: TooltipClickListener? = null
    internal var animIn = 0
    internal var animOut = 0
    internal var targetGhostView: TargetView? = null

    init {
        tooltipView?.setOnClickListener {
            tooltipClickListener?.onClick(tooltipView, this)

            if (clickToHide) {
               close()
            }
        }

        if(false) {
            refView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    closeNow()
                    refView?.removeOnAttachStateChangeListener(this)
                }

                override fun onViewAttachedToWindow(v: View?) {}
            })
        }
    }

    internal fun getTextView(): TextView?{
        return tooltipView?.childView?.getTextView()
    }

    internal fun getStartImageView(): ImageView?{
        return tooltipView?.childView?.getStartImageView()
    }

    internal fun getEndImageView(): ImageView?{
        return tooltipView?.childView?.getEndImageView()
    }

    @JvmOverloads fun show(duration: Long = 0, text: String? = null): Tooltip{
        refView?.post {
            closeNow()

            tooltipView?.childView?.attach()
            text?.let { getTextView()?.text = it }

            val tooltipParent = activity!!.window.decorView as ViewGroup
            val rect = Rect()

            refView?.getGlobalVisibleRect(rect)

            tooltipView?.setup(rect, tooltipParent)

            val lP = ViewGroup.LayoutParams(
                    tooltipParent.width,
                    tooltipParent.height
            )

            tooltipParent.addView(overlay, lP)

            if(animIn == 0){
                displayListener?.onDisplay(tooltipView, true)
            }else{
                val animation = AnimationUtils.loadAnimation(tooltipView!!.context, animIn)

                animation.setAnimationListener(object :Animation.AnimationListener{
                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        displayListener?.onDisplay(tooltipView, true)
                    }

                    override fun onAnimationStart(p0: Animation?) {}
                })

                tooltipView?.startAnimation(animation)
            }

            if (duration > 0) {
                tooltipView?.postDelayed({ close() }, duration)
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
            overlay?.post { closeNow() }
        }else{
            val animation = AnimationUtils.loadAnimation(tooltipView?.context, animOut)

            animation.setAnimationListener(object :Animation.AnimationListener{
                override fun onAnimationRepeat(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    overlay?.post { closeNow() }
                }

                override fun onAnimationStart(p0: Animation?) {}
            })

            tooltipView?.startAnimation(animation)
        }
    }

    fun closeNow(){
        val parent = overlay?.parent

        if (parent != null && parent is ViewGroup) {
            overlay?.post {
                parent.removeView(overlay)
                displayListener?.onDisplay(tooltipView, false)
            }

            activity = null
            refView = null
            targetGhostView?.setOnClickListener(null)
            (targetGhostView?.parent as? ViewGroup)?.removeView(targetGhostView)
            targetGhostView = null

            (tooltipView?.parent as? ViewGroup)?.removeView(tooltipView)

            tooltipView?.clear()

            tooltipView = null
            overlay = null
        }
    }

    fun isShown() = overlay?.parent != null

    internal fun initTargetClone() {
        targetGhostView = TargetView(activity!!)

        targetGhostView?.setTarget(refView)
        overlay?.addView(targetGhostView)
        targetGhostView?.setOnClickListener { refViewClickListener?.onClick(targetGhostView, this)}
    }

    internal fun setOverlayListener(overlayClickListener: TooltipClickListener){
        overlay?.setOnClickListener { overlayClickListener.onClick(it, this) }
    }

    fun moveTooltip(x: Int, y:Int){
        overlay!!.translationY -= y
        overlay!!.translationX -= x
    }

    companion object{
        @JvmStatic @JvmOverloads fun on(
            refView: View,
            closeOnParentDetach: Boolean = true
        ): TooltipBuilder {
            getActivity(refView.context)!!.let {
                return TooltipBuilder(Tooltip(it, refView, closeOnParentDetach))
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