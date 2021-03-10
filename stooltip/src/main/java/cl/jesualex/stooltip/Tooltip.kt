package cl.jesualex.stooltip

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
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
class Tooltip private constructor(
    private var activity: Activity?,
    private var refView: View?,
    private val closeOnRefDetach: Boolean
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
    internal var refAttachListener: View.OnAttachStateChangeListener? = null

    init {
        tooltipView?.setOnClickListener {
            tooltipClickListener?.onClick(tooltipView, this)

            if (clickToHide) {
               close()
            }
        }
    }

    internal fun getTextView(): TextView{
        return tooltipView!!.childView!!.getTextView()
    }

    internal fun getStartImageView(): ImageView{
        return tooltipView!!.childView!!.getStartImageView()
    }

    internal fun getEndImageView(): ImageView{
        return tooltipView!!.childView!!.getEndImageView()
    }

    @JvmOverloads internal fun show(duration: Long = 0, text: String? = null): Tooltip {
        overlay?.addView(
            tooltipView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        refView?.post {
            if(closeOnRefDetach && refAttachListener == null) {
                refAttachListener = object : View.OnAttachStateChangeListener {
                    override fun onViewDetachedFromWindow(v: View?) {
                        closeNow()
                        refView?.removeOnAttachStateChangeListener(this)
                    }

                    override fun onViewAttachedToWindow(v: View?) {}
                }

                refView?.addOnAttachStateChangeListener(refAttachListener)
            }

            tooltipView?.childView?.attach()
            text?.let { getTextView().text = it }

            val tooltipParent = refView?.parent as ViewGroup
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

    fun close(){
        if(animOut == 0){
            closeNow()
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
        overlay?.post {
            targetGhostView?.setOnClickListener(null)
            (targetGhostView?.parent as? ViewGroup)?.removeView(targetGhostView)
            targetGhostView = null

            tooltipView?.setOnClickListener(null)
            tooltipView?.clear()
            tooltipView = null

            (overlay?.parent as? ViewGroup)?.removeView(overlay)
            overlay = null

            displayListener?.onDisplay(tooltipView, false)

            refAttachListener?.let {
                refView?.removeOnAttachStateChangeListener(it)
                refAttachListener = null
            }

            activity = null
            refView = null
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
            closeOnRefViewDetach: Boolean = true
        ): TooltipBuilder {
            getActivity(refView.context)!!.let {
                return TooltipBuilder(Tooltip(it, refView, closeOnRefViewDetach))
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