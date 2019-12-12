package cl.jesualex.stooltip

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.annotation.AnimRes
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by jesualex on 2019-04-29.
 */
class TooltipBuilder(private val tooltip: Tooltip){
    @JvmOverloads fun show(duration: Long = 0): Tooltip{
        tooltip.overlay.addView(tooltip.tooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return tooltip.show(duration)
    }

    fun drawableLeft(@DrawableRes left: Int): TooltipBuilder {
        tooltip
            .getTextView()
            ?.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0)
        return this
    }

    fun drawableRight(@DrawableRes right: Int): TooltipBuilder {
        tooltip
            .getTextView()
            ?.setCompoundDrawablesWithIntrinsicBounds(0, 0, right, 0)
        return this
    }

    fun drawableTop(@DrawableRes top: Int): TooltipBuilder {
        tooltip
            .getTextView()
            ?.setCompoundDrawablesWithIntrinsicBounds(0, top, 0, 0)
        return this
    }

    fun drawableBottom(@DrawableRes bottom: Int): TooltipBuilder {
        tooltip
            .getTextView()
            ?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, bottom)
        return this
    }

    fun text(text: String): TooltipBuilder {
        tooltip.getTextView()?.text = text

        return this
    }

    fun text(text: Spanned): TooltipBuilder {
        tooltip.getTextView()?.text = text

        return this
    }

    fun text(@StringRes text: Int): TooltipBuilder {
        tooltip.getTextView()?.setText(text)

        return this
    }

    fun textColor(@ColorInt textColor: Int): TooltipBuilder {
        tooltip.getTextView()?.setTextColor(textColor)
        return this
    }

    fun textTypeFace(typeface: Typeface): TooltipBuilder {
        tooltip.getTextView()?.typeface = typeface
        return this
    }

    fun textSize(unit: Int, textSize: Float): TooltipBuilder {
        tooltip.getTextView()?.setTextSize(unit, textSize)
        return this
    }

    fun textSize(textSize: Float): TooltipBuilder {
        tooltip.getTextView()?.textSize = textSize
        return this
    }

    fun textGravity(textGravity: Int): TooltipBuilder {
        tooltip.getTextView()?.gravity = textGravity
        return this
    }

    fun icon(@DrawableRes iconRes: Int): TooltipBuilder {
        tooltip.getImageView()?.let {
            it.setImageResource(iconRes)
            it.visibility = View.VISIBLE
        }
        return this
    }

    fun icon(icon: Drawable): TooltipBuilder {
        tooltip.getImageView()?.let {
            it.setImageDrawable(icon)
            it.visibility = View.VISIBLE
        }
        return this
    }

    fun icon(icon: Bitmap): TooltipBuilder {
        tooltip.getImageView()?.let {
            it.setImageBitmap(icon)
            it.visibility = View.VISIBLE
        }
        return this
    }

    fun iconMargin(left: Int, top: Int, right: Int, bottom: Int): TooltipBuilder {
        tooltip.getImageView()?.let {
            val lp = it.layoutParams

            if(lp is FrameLayout.LayoutParams){
                lp.setMargins(left, top, right, bottom)
                it.layoutParams = lp
            }
        }

        return this
    }

    fun iconSize(h: Int, w: Int): TooltipBuilder {
        tooltip.getImageView()?.let {
            val lp = it.layoutParams
            lp.height = h
            lp.width = w
            it.layoutParams = lp
        }

        return this
    }

    fun color(@ColorInt color: Int): TooltipBuilder {
        tooltip.tooltipView.setColor(color)
        return this
    }

    fun padding(top: Int, right: Int, bottom: Int, left: Int): TooltipBuilder {
        tooltip.tooltipView.paddingT = top
        tooltip.tooltipView.paddingB = bottom
        tooltip.tooltipView.paddingL = left
        tooltip.tooltipView.paddingR = right

        return this
    }

    fun position(position: Position): TooltipBuilder {
        tooltip.tooltipView.position = position
        return this
    }

    fun corner(corner: Int): TooltipBuilder {
        tooltip.tooltipView.corner = corner
        return this
    }

    fun clickToHide(clickToHide: Boolean): TooltipBuilder {
        tooltip.clickToHide = clickToHide
        return this
    }

    fun distanceWithView(distance: Int): TooltipBuilder {
        tooltip.tooltipView.distanceWithView = distance
        return this
    }

    fun border(color: Int, width: Float): TooltipBuilder {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.color = color
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = width
        tooltip.tooltipView.borderPaint = borderPaint
        return this
    }

    fun displayListener(listener: DisplayListener): TooltipBuilder {
        tooltip.displayListener = listener
        return this
    }

    fun tooltipClickListener(listener: TooltipClickListener): TooltipBuilder {
        tooltip.tooltipClickListener = listener
        return this
    }

    fun refViewClickListener(listener: TooltipClickListener): TooltipBuilder {
        tooltip.refViewClickListener = listener
        return this
    }

    @JvmOverloads fun overlay(@ColorInt color: Int, listener: TooltipClickListener? = null): TooltipBuilder {
        tooltip.overlay.setBackgroundColor(color)
        tooltip.initTargetClone()
        listener?.let { tooltip.setOverlayListener(listener) }
        return this
    }

    /**
    * Set [r] to 0 for disableShadow
    */
    @JvmOverloads fun shadow(r: Float, @ColorInt color: Int = 0xffaaaaaa.toInt()): TooltipBuilder {
        tooltip.tooltipView.setShadow(r, color)
        return this
    }

    fun shadowPadding(padding: Float): TooltipBuilder {
        tooltip.tooltipView.shadowPadding = padding
        return this
    }

    @JvmOverloads fun animation(@AnimRes animIn: Int,@AnimRes animOut: Int = animIn): TooltipBuilder {
        tooltip.animIn = animIn
        tooltip.animOut = animOut
        return this
    }

    fun minWidth(minWidth: Int): TooltipBuilder {
        tooltip.tooltipView.minWidth =  minWidth
        return this
    }

    fun minHeight(minHeight: Int): TooltipBuilder {
        tooltip.tooltipView.minHeight =  minHeight
        return this
    }

    fun arrowSize(h: Int, w: Int): TooltipBuilder {
        tooltip.tooltipView.arrowHeight = h
        tooltip.tooltipView.arrowWidth = w

        return this
    }

    fun borderMargin(margin: Int): TooltipBuilder {
        tooltip.tooltipView.lMargin = margin

        return this
    }
}