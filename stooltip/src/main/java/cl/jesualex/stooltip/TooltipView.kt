package cl.jesualex.stooltip

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

/**
 * Created by jesualex on 2019-04-25.
 */
class TooltipView : FrameLayout {
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF(0f, 0f, 0f, 0f)

    private var arrowSourceMargin = 0
    private var arrowTargetMargin = 0
    private var color = 0xff1F7C82.toInt()
    private var bubblePath: Path? = null
    private var hasInverted = false
    private lateinit var screenRect: Rect
    private lateinit var rect: Rect

    internal lateinit var childView: View
    internal var borderPaint: Paint? = null
    internal var corner = 0
    internal var paddingT = 0
    internal var paddingB = 0
    internal var paddingR = 0
    internal var paddingL = 0
    internal var shadowPadding = 0f
    internal var distanceWithView = 0
    internal var position = Position.BOTTOM
    internal var minHeight = 0
    internal var minWidth = 0
    internal var lMargin = 0
    internal var arrowHeight = 0
    internal var arrowWidth = 0

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

    fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int){
        setWillNotDraw(false)

        this.childView = ChildView(context, attrs, defStyleAttr)
        (childView as ChildView).getTextView().setTextColor(Color.WHITE)
        addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val res = context.resources
        val p = res.getDimensionPixelSize(R.dimen.padding)

        paddingL = p
        paddingT = p
        paddingR = p
        paddingB = p

        corner = res.getDimensionPixelSize(R.dimen.corner)
        arrowHeight = res.getDimensionPixelSize(R.dimen.arrowH)
        arrowWidth = res.getDimensionPixelSize(R.dimen.arrowW)
        shadowPadding = res.getDimensionPixelSize(R.dimen.shadowPadding).toFloat()
        lMargin = res.getDimensionPixelSize(R.dimen.screenBorderMargin)
        minWidth = res.getDimensionPixelSize(R.dimen.minWidth)
        minHeight = res.getDimensionPixelSize(R.dimen.minHeight)

        bubblePaint.color = color
        bubblePaint.style = Paint.Style.FILL

        setLayerType(View.LAYER_TYPE_SOFTWARE, bubblePaint)

        setShadow(res.getDimensionPixelSize(R.dimen.shadowW).toFloat())
        setPosition()
    }

    @SuppressLint("WrongCall")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wSpec = MeasureSpec.getSize(widthMeasureSpec)
        val hSpec = MeasureSpec.getSize(heightMeasureSpec)
        val wCalculate = calculateWidth(wSpec)
        val hCalculate = calculateHeight(hSpec)
        val margin = distanceWithView + lMargin

        if(!hasInverted && (wCalculate < minWidth + margin || hCalculate < minHeight + margin)){
            invertCurrentPosition()
            hasInverted = true
            onMeasure(widthMeasureSpec, heightMeasureSpec)
        }else{
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(wCalculate, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(hCalculate, MeasureSpec.AT_MOST)
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupPosition(rect, w, h)

        rectF.left = shadowPadding
        rectF.top = shadowPadding
        rectF.right = (w - shadowPadding * 2)
        rectF.bottom = (h - shadowPadding * 2)

        bubblePath = drawBubble(
                rect,
                rectF,
                corner.toFloat(),
                corner.toFloat(),
                corner.toFloat(),
                corner.toFloat()
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bubblePath?.let { bubblePath ->
            canvas?.drawPath(bubblePath, bubblePaint)

            borderPaint?.let{ canvas?.drawPath(bubblePath, it) }
        }
    }

    @JvmOverloads fun setShadow(r: Float, @ColorInt color: Int = 0xffaaaaaa.toInt()) {
        bubblePaint.setShadowLayer(r, 0f, 0f, if(r == 0f) Color.TRANSPARENT else color)
    }

    private fun invertCurrentPosition(){
        position = when (position) {
            Position.TOP -> Position.BOTTOM
            Position.BOTTOM -> Position.TOP
            Position.LEFT -> Position.RIGHT
            Position.RIGHT -> Position.LEFT
        }

        setPosition()
    }

    private fun setPosition() {
        when (position) {
            Position.TOP -> setPadding(paddingL, paddingT, paddingR, paddingB + arrowHeight)
            Position.BOTTOM -> setPadding(paddingL, paddingT + arrowHeight, paddingR, paddingB)
            Position.LEFT -> setPadding(paddingL, paddingT, paddingR + arrowHeight, paddingB)
            Position.RIGHT -> setPadding(paddingL + arrowHeight, paddingT, paddingR, paddingB)
        }

        postInvalidate()
    }

    private fun drawBubble(rect: Rect, shadowRect: RectF, topLeftDiameter: Float, topRightDiameter: Float, bottomRightDiameter: Float, bottomLeftDiameter: Float): Path {
        val path = Path()

        val topLeftD = if (topLeftDiameter < 0) 0f else topLeftDiameter
        val topRightD = if (topRightDiameter < 0) 0f else topRightDiameter
        val bottomLeftD = if (bottomLeftDiameter < 0) 0f else bottomLeftDiameter
        val bottomRightD = if (bottomRightDiameter < 0) 0f else bottomRightDiameter

        val spacingLeft = (if (this.position == Position.RIGHT) arrowHeight else 0).toFloat()
        val spacingTop = (if (this.position == Position.BOTTOM) arrowHeight else 0).toFloat()
        val spacingRight = (if (this.position == Position.LEFT) arrowHeight else 0).toFloat()
        val spacingBottom = (if (this.position == Position.TOP) arrowHeight else 0).toFloat()

        val left = spacingLeft + shadowRect.left
        val top = spacingTop + shadowRect.top
        val right = shadowRect.right - spacingRight
        val bottom = shadowRect.bottom - spacingBottom
        val centerX = (rect.centerX()) - x

        val arrowSourceX = if (Arrays.asList(Position.TOP, Position.BOTTOM).contains(this.position))
            centerX + arrowSourceMargin
        else
            centerX
        val arrowTargetX = if (Arrays.asList(Position.TOP, Position.BOTTOM).contains(this.position))
            centerX + arrowTargetMargin
        else
            centerX
        val arrowSourceY = if (Arrays.asList(Position.RIGHT, Position.LEFT).contains(this.position))
            bottom / 2f - arrowSourceMargin
        else
            bottom / 2f
        val arrowTargetY = if (Arrays.asList(Position.RIGHT, Position.LEFT).contains(this.position))
            bottom / 2f - arrowTargetMargin
        else
            bottom / 2f

        path.moveTo(left + topLeftD / 2f, top)
        //LEFT, TOP

        if (position == Position.BOTTOM) {
            path.lineTo(arrowSourceX - arrowWidth, top)
            path.lineTo(arrowTargetX, shadowRect.top)
            path.lineTo(arrowSourceX + arrowWidth, top)
        }

        path.lineTo(right - topRightD / 2f, top)
        path.quadTo(right, top, right, top + topRightD / 2)
        //RIGHT, TOP

        if (position == Position.LEFT) {
            path.lineTo(right, arrowSourceY - arrowWidth)
            path.lineTo(shadowRect.right, arrowTargetY)
            path.lineTo(right, arrowSourceY + arrowWidth)
        }

        path.lineTo(right, bottom - bottomRightD / 2)
        path.quadTo(right, bottom, right - bottomRightD / 2, bottom)
        //RIGHT, BOTTOM

        if (position == Position.TOP) {
            path.lineTo(arrowSourceX + arrowWidth, bottom)
            path.lineTo(arrowTargetX, shadowRect.bottom)
            path.lineTo(arrowSourceX - arrowWidth, bottom)
        }

        path.lineTo(left + bottomLeftD / 2, bottom)
        path.quadTo(left, bottom, left, bottom - bottomLeftD / 2)
        //LEFT, BOTTOM

        if (position == Position.RIGHT) {
            path.lineTo(left, arrowSourceY + arrowWidth)
            path.lineTo(shadowRect.left, arrowTargetY)
            path.lineTo(left, arrowSourceY - arrowWidth)
        }

        path.lineTo(left, top + topLeftD / 2)
        path.quadTo(left, top, left + topLeftD / 2, top)
        path.close()

        return path
    }

    private fun getOffset(myLength: Int, hisLength: Int): Int {
        return (hisLength - myLength) / 2
    }

    private fun calculatePosition(offset: Int, size: Int, begin: Int, maxVal: Int): Int{
        val pos = begin + offset

        return if(pos < 0 || pos + size > maxVal){
            pos - (pos + size - maxVal)
        }else{
            pos
        }
    }

    private fun setupPosition(rect: Rect, width: Int, height: Int) {
        val x: Int
        val y: Int

        if (position == Position.LEFT || position == Position.RIGHT) {
            x = if (position == Position.LEFT) {
                rect.left - width - distanceWithView
            } else {
                rect.right + distanceWithView
            }

            y = calculatePosition(
                    getOffset(height, rect.height()),
                    height,
                    if(rect.top < + lMargin) lMargin else rect.top,
                    calculateHeight(screenRect.height()) + lMargin
            )
        } else {
            y = if (position == Position.BOTTOM) {
                rect.bottom + distanceWithView
            } else { // top
                rect.top - height - distanceWithView
            }

            x = calculatePosition(
                    getOffset(width, rect.width()),
                    width,
                    if(rect.left < lMargin) lMargin else rect.left,
                    calculateWidth(screenRect.width()) + lMargin
            )
        }

        translationX = (x - screenRect.left).toFloat()
        translationY = (y - screenRect.top).toFloat()
    }

    fun setup(viewRect: Rect, screenRect: Rect) {
        setPosition()
        this.screenRect = screenRect
        this.rect = viewRect
    }

    private fun calculateWidth(width: Int): Int {
        return  if (position == Position.LEFT &&
                width > rect.left - lMargin - distanceWithView
        ) {
            rect.left - lMargin - distanceWithView
        } else if (position == Position.RIGHT && rect.right + screenRect.left + width >
                screenRect.width() - rect.right + screenRect.left - lMargin - distanceWithView
        ) {
            screenRect.width() - rect.right + screenRect.left - lMargin - distanceWithView
        } else if ((position == Position.TOP || position == Position.BOTTOM)
                && width > screenRect.width() - (lMargin * 2)
        ) {
            screenRect.width() - (lMargin * 2)
        }else {
            width
        }
    }

    private fun calculateHeight(height: Int): Int {
        return  if (position == Position.TOP &&
                height > rect.top - lMargin - distanceWithView
        ) {
            rect.top - lMargin - distanceWithView
        } else if (position == Position.BOTTOM && rect.bottom + screenRect.top + height >
                screenRect.height() - rect.bottom + screenRect.top - lMargin - distanceWithView
        ) {
            screenRect.height() - rect.bottom + screenRect.top - lMargin - distanceWithView
        } else if ((position == Position.LEFT || position == Position.RIGHT) &&
                height > screenRect.height() - (lMargin * 2)
        ) {
            screenRect.height() - (lMargin * 2)
        }else {
            height
        }
    }

    fun setColor(color: Int) {
        this.color = color
        bubblePaint.color = color
        postInvalidate()
    }
}
