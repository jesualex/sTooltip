package cl.jesualex.stooltip

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

    private var screenBorderMargin = 0
    private var arrowHeight = 0
    private var arrowWidth = 0
    private var arrowSourceMargin = 0
    private var arrowTargetMargin = 0
    private var color = 0xff1F7C82.toInt()
    private var bubblePath: Path? = null
    private var align = Align.CENTER
    private var screenWidth = 0
    private var screenHeight = 0
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
        val p = res.getDimensionPixelSize(R.dimen.tooltipPadding)

        paddingL = p
        paddingT = p
        paddingR = p
        paddingB = p

        corner = res.getDimensionPixelSize(R.dimen.tooltipCorner)
        arrowHeight = res.getDimensionPixelSize(R.dimen.tooltipArrowH)
        arrowWidth = res.getDimensionPixelSize(R.dimen.tooltipArrowW)
        shadowPadding = res.getDimensionPixelSize(R.dimen.tooltipShadowPadding).toFloat()
        screenBorderMargin = res.getDimensionPixelSize(R.dimen.tooltipScreenBorderMargin)

        bubblePaint.color = color
        bubblePaint.style = Paint.Style.FILL

        setLayerType(View.LAYER_TYPE_SOFTWARE, bubblePaint)

        setShadow(res.getDimensionPixelSize(R.dimen.tooltipShadowW).toFloat())
        setPosition()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wSpec = MeasureSpec.getSize(widthMeasureSpec)
        val hSpec = MeasureSpec.getSize(heightMeasureSpec)
        val wCalculate = calculateWidth(rect, screenWidth, wSpec)
        val hCalculate = calculateHeight(rect, screenHeight, hSpec)

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(wCalculate, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(hCalculate, MeasureSpec.AT_MOST)
        )
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
        var topLeftDiameter = topLeftDiameter
        var topRightDiameter = topRightDiameter
        var bottomRightDiameter = bottomRightDiameter
        var bottomLeftDiameter = bottomLeftDiameter
        val path = Path()

        topLeftDiameter = if (topLeftDiameter < 0) 0f else topLeftDiameter
        topRightDiameter = if (topRightDiameter < 0) 0f else topRightDiameter
        bottomLeftDiameter = if (bottomLeftDiameter < 0) 0f else bottomLeftDiameter
        bottomRightDiameter = if (bottomRightDiameter < 0) 0f else bottomRightDiameter

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

        path.moveTo(left + topLeftDiameter / 2f, top)
        //LEFT, TOP

        if (position == Position.BOTTOM) {
            path.lineTo(arrowSourceX - arrowWidth, top)
            path.lineTo(arrowTargetX, shadowRect.top)
            path.lineTo(arrowSourceX + arrowWidth, top)
        }

        path.lineTo(right - topRightDiameter / 2f, top)
        path.quadTo(right, top, right, top + topRightDiameter / 2)
        //RIGHT, TOP

        if (position == Position.LEFT) {
            path.lineTo(right, arrowSourceY - arrowWidth)
            path.lineTo(shadowRect.right, arrowTargetY)
            path.lineTo(right, arrowSourceY + arrowWidth)
        }

        path.lineTo(right, bottom - bottomRightDiameter / 2)
        path.quadTo(right, bottom, right - bottomRightDiameter / 2, bottom)
        //RIGHT, BOTTOM

        if (position == Position.TOP) {
            path.lineTo(arrowSourceX + arrowWidth, bottom)
            path.lineTo(arrowTargetX, shadowRect.bottom)
            path.lineTo(arrowSourceX - arrowWidth, bottom)
        }

        path.lineTo(left + bottomLeftDiameter / 2, bottom)
        path.quadTo(left, bottom, left, bottom - bottomLeftDiameter / 2)
        //LEFT, BOTTOM

        if (position == Position.RIGHT) {
            path.lineTo(left, arrowSourceY + arrowWidth)
            path.lineTo(shadowRect.left, arrowTargetY)
            path.lineTo(left, arrowSourceY - arrowWidth)
        }

        path.lineTo(left, top + topLeftDiameter / 2)
        path.quadTo(left, top, left + topLeftDiameter / 2, top)
        path.close()

        return path
    }

    private fun getAlignOffset(myLength: Int, hisLength: Int): Int {
        return when (align) {
            Align.END -> hisLength - myLength
            Align.CENTER -> (hisLength - myLength) / 2
            else -> 0
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

            y = rect.top + getAlignOffset(height, rect.height())
        } else {
            y = if (position == Position.BOTTOM) {
                rect.bottom + distanceWithView
            } else { // top
                rect.top - height - distanceWithView
            }

            x = rect.left + getAlignOffset(width, rect.width())
        }

        translationX = x.toFloat()
        translationY = y.toFloat()
    }

    fun setup(viewRect: Rect, screenWidth: Int, screenHeight: Int) {
        setPosition()
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
        this.rect = Rect(viewRect)
    }

    private fun calculateWidth(rect: Rect, screenWidth: Int, width: Int): Int {
        return  if (position == Position.LEFT && width > rect.left) {
            rect.left - screenBorderMargin - distanceWithView
        } else if (position == Position.RIGHT && rect.right + width > screenWidth) {
            screenWidth - rect.right - screenBorderMargin - distanceWithView
        } else if ((position == Position.TOP || position == Position.BOTTOM) && width > screenWidth) {
            screenWidth - (screenBorderMargin * 2)
        }else {
            width
        }
    }

    private fun calculateHeight(rect: Rect, screenHeight: Int, height: Int): Int {
        return  if (position == Position.TOP && height > rect.top) {
            rect.top - screenBorderMargin - distanceWithView
        } else if (position == Position.BOTTOM && rect.bottom + height > screenHeight) {
            screenHeight - rect.bottom - screenBorderMargin - distanceWithView
        } else if ((position == Position.LEFT || position == Position.RIGHT) && height > screenHeight - (screenBorderMargin * 2)) {
            screenHeight - (screenBorderMargin * 2)
        }else {
            height
        }
    }

    fun setAlign(align: Align) {
        this.align = align
        postInvalidate()
    }

    fun setColor(color: Int) {
        this.color = color
        bubblePaint.color = color
        postInvalidate()
    }
}
