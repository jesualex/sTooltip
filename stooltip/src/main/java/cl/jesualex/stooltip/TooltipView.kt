package cl.jesualex.stooltip

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat

/**
 * Created by jesualex on 2019-04-25.
 */
class TooltipView : FrameLayout {
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var bubblePath: Path? = null
    private var hasInverted = false
    private lateinit var parent: View
    private lateinit var parentRect: Rect
    private lateinit var rect: Rect

    internal var childView: ChildView? = null
    internal var borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var corner = 0
    internal var paddingT = 0
    internal var paddingB = 0
    internal var paddingE = 0
    internal var paddingS = 0
    internal var shadowPadding = 0f
    internal var distanceWithView = 0
    internal var position = Position.BOTTOM
    internal var minHeight = 0
    internal var minWidth = 0
    internal var lMargin = 0
    internal var arrowHeight = 0f
    internal var arrowWidth = 0f

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

    fun clear(){
        childView?.clear()
        childView = null
        (parent as? ViewGroup)?.removeView(this)
    }

    fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int){
        setWillNotDraw(false)

        this.childView = ChildView(context, attrs, defStyleAttr)
        childView!!.getTextView().setTextColor(Color.WHITE)
        addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val res = context.resources
        val p = res.getDimensionPixelSize(R.dimen.padding)

        paddingS = p
        paddingT = p
        paddingE = p
        paddingB = p

        corner = res.getDimensionPixelSize(R.dimen.corner)
        arrowHeight = res.getDimensionPixelSize(R.dimen.arrowH).toFloat()
        arrowWidth = res.getDimensionPixelSize(R.dimen.arrowW).toFloat()
        shadowPadding = res.getDimensionPixelSize(R.dimen.shadowPadding).toFloat()
        lMargin = res.getDimensionPixelSize(R.dimen.screenBorderMargin)
        minWidth = res.getDimensionPixelSize(R.dimen.minWidth)
        minHeight = res.getDimensionPixelSize(R.dimen.minHeight)

        bubblePaint.style = Paint.Style.FILL
        borderPaint.style = Paint.Style.STROKE

        setShadow(res.getDimensionPixelSize(R.dimen.shadowW).toFloat())
    }

    @SuppressLint("WrongCall")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wms = widthMeasureSpec
        var hms = heightMeasureSpec
        val wSpec = MeasureSpec.getSize(wms)
        val hSpec = MeasureSpec.getSize(hms)
        val wCalculate = calculateWidth(wSpec)
        val hCalculate = calculateHeight(hSpec)
        val margin = distanceWithView + lMargin + borderPaint.strokeWidth

        if(!hasInverted && (wCalculate < minWidth + margin || hCalculate < minHeight + margin)){
            invertCurrentPosition()
            hasInverted = true
        }else{
            wms = MeasureSpec.makeMeasureSpec(wCalculate, MeasureSpec.AT_MOST)
            hms = MeasureSpec.makeMeasureSpec(hCalculate, MeasureSpec.AT_MOST)
        }

        setPadding()
        super.onMeasure(wms, hms)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupPosition(rect, w, h)

        bubblePath = drawBubble(w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bubblePath?.let { bubblePath ->
            canvas?.drawPath(bubblePath, bubblePaint)
            canvas?.drawPath(bubblePath, borderPaint)
        }
    }

    @JvmOverloads fun setShadow(r: Float, @ColorInt color: Int = 0xffaaaaaa.toInt()) {
        bubblePaint.setShadowLayer(r, 0f, 0f, if(r == 0f) Color.TRANSPARENT else color)
    }

    private fun invertCurrentPosition(){
        position = when (position) {
            Position.TOP -> Position.BOTTOM
            Position.BOTTOM -> Position.TOP
            Position.START -> Position.END
            Position.END -> Position.START
        }
    }

    private fun setPadding() {
        val extraDistance = (borderPaint.strokeWidth + arrowHeight + distanceWithView).toInt()

        when (getRelativePosition()) {
            Position.TOP -> setPadding(paddingS, paddingT, paddingE, paddingB + extraDistance)
            Position.BOTTOM -> setPadding(paddingS, paddingT + extraDistance, paddingE, paddingB)
            Position.START -> setPadding(paddingS, paddingT, paddingE + extraDistance, paddingB)
            Position.END -> setPadding(paddingS + extraDistance, paddingT, paddingE, paddingB)
        }
    }

    private fun drawBubble(w: Float, h: Float): Path {
        val path = Path()
        val corner = if (corner < 0) 0f else corner.toFloat()
        val distance = distanceWithView + borderPaint.strokeWidth
        val arrowDistance = arrowHeight + distance

        val position = getRelativePosition()

        val start = if(position == Position.END) arrowDistance else distance
        val top = if(position == Position.TOP) arrowDistance else distance
        val end = if(position == Position.START) arrowDistance else distance
        val bottom = if(position == Position.BOTTOM) arrowDistance else distance

        path.moveTo(start, corner + bottom)

        if(position == Position.END){
            path.lineTo(start, (h - arrowWidth)/2)
            path.lineTo(0f, h/2)
            path.lineTo(start, (h + arrowWidth)/2)
        }

        path.lineTo(start, h - corner - top)
        path.quadTo(start, h - top, start + corner, h - top)

        if(position == Position.TOP){
            path.lineTo((w - arrowWidth)/2, h - top)
            path.lineTo(w/2, h)
            path.lineTo((w + arrowWidth)/2, h - top)
        }

        path.lineTo(w - corner - end, h - top)
        path.quadTo(w - end, h - top, w - end, h - corner - top)

        if(position == Position.START){
            path.lineTo(w - end, (h + arrowWidth)/2)
            path.lineTo(w, h/2)
            path.lineTo(w - end, (h - arrowWidth)/2)
        }

        path.lineTo(w - end, bottom + corner)
        path.quadTo(w - end, bottom, w - corner - end, bottom)

        if(position == Position.BOTTOM){
            path.lineTo((w + arrowWidth)/2, bottom)
            path.lineTo(w/2, 0f)
            path.lineTo((w - arrowWidth)/2, bottom)
        }

        path.lineTo(corner + start, bottom)
        path.quadTo(start, bottom, start, corner + bottom)
        path.close()

        return path
    }

    private fun getRelativePosition(): Position{
        val rtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL

        if(!rtl){
            return position
        }

        return when(position){
            Position.END -> Position.START
            Position.START -> Position.END
            else -> position
        }
    }

    private fun getOffset(myLength: Int, hisLength: Int): Int {
        return (hisLength - myLength) / 2
    }

    private fun calculatePosition(offset: Int, size: Int, begin: Int, maxVal: Int): Int{
        val pos = begin + offset

        return if(pos < 0 && begin + size < maxVal){
            begin
        }else if(pos < 0 || pos + size > maxVal){
            maxVal - size
        }else{
            pos
        }
    }

    private fun setupPosition(rect: Rect, width: Int, height: Int) {
        val distance = (distanceWithView + borderPaint.strokeWidth).toInt()
        val x: Int
        val y: Int

        if (position == Position.START || position == Position.END) {
            x = if (position == Position.START) {
                rect.left - width - distance
            } else {
                rect.right + distance
            }

            y = calculatePosition(
                    getOffset(height, rect.height()),
                    height,
                    if(rect.top < + lMargin) lMargin else rect.top,
                    calculateHeight(parent.height) + lMargin
            )
        } else {
            y = if (position == Position.BOTTOM) {
                rect.bottom + distance
            } else { // top
                rect.top - height - distance
            }

            x = calculatePosition(
                    getOffset(width, rect.width()),
                    width,
                    if(rect.left < lMargin) lMargin else rect.left,
                    calculateWidth(parent.width) + lMargin
            )
        }

        val rtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL

        translationX = (x - parentRect.left).toFloat() * if(rtl) -1 else 1
        translationY = (y - parentRect.top).toFloat()
    }

    fun setup(viewRect: Rect, tooltipParent: View) {
        this.parent = tooltipParent
        this.parentRect = Rect()
        parent.getGlobalVisibleRect(parentRect)
        this.rect = viewRect
    }

    private fun calculateWidth(width: Int): Int {
        val parentLP = parent.layoutParams as? MarginLayoutParams
        val maxWidth = parent.width - (parentLP?.leftMargin ?: 0) - (parentLP?.rightMargin ?: 0) -
                parent.paddingLeft - parent.paddingRight

        return  if (position == Position.START &&
                width > rect.left - lMargin - distanceWithView
        ) {
            rect.left - lMargin - distanceWithView
        } else if (position == Position.END && rect.right + parentRect.left + width >
                maxWidth - rect.right + parentRect.left - lMargin - distanceWithView
        ) {
            maxWidth - rect.right + parentRect.left - lMargin - distanceWithView
        } else if ((position == Position.TOP || position == Position.BOTTOM)
                && width > maxWidth - (lMargin * 2)
        ) {
            maxWidth - (lMargin * 2)
        }else {
            width
        }
    }

    private fun calculateHeight(height: Int): Int {
        val parentLP = parent.layoutParams as? MarginLayoutParams
        val maxHeight = parent.height - (parentLP?.topMargin ?: 0) - (parentLP?.bottomMargin ?: 0) -
                parent.paddingTop - parent.paddingBottom

        return  if (position == Position.TOP &&
                height > rect.top - lMargin - distanceWithView
        ) {
            rect.top - lMargin - distanceWithView
        } else if (position == Position.BOTTOM && rect.bottom + parentRect.top + height >
                maxHeight - rect.bottom + parentRect.top - lMargin - distanceWithView
        ) {
            maxHeight - rect.bottom + parentRect.top - lMargin - distanceWithView
        } else if ((position == Position.START || position == Position.END) &&
                height > maxHeight - (lMargin * 2)
        ) {
            maxHeight - (lMargin * 2)
        }else {
            height
        }
    }

    fun setColor(color: Int) {
        bubblePaint.color = color
        postInvalidate()
    }
}
