package cl.jesualex.stooltip

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.widget.AppCompatImageView
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout

/**
 * Created by jesualex on 2019-04-29.
 */
class TargetView(context: Context): AppCompatImageView(context) {
    private val position = IntArray(4)
    private val size = IntArray(2)

    private fun setLayoutLikeAsView(target: View) {
        target.getLocationOnScreen(position)
        position[2] = position[0] + target.width
        position[3] = position[1] + target.height
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(position[0], position[1], position[2], position[3])
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(size[0], size[1])
    }

    fun setTarget(target: View?) {
        if (target == null) return
        setLayoutLikeAsView(target)
        val bitmap = getBitmapFromView(target) ?: return

        setImageBitmap(bitmap)
        size[0] = bitmap.width
        size[1] = bitmap.height
    }

    fun createDrawableFromView(activity: Activity, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    fun loadBitmapFromView(v: View): Bitmap {
        if (v.measuredHeight <= 0) {
            val specWidth = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED)
            v.measure(specWidth, specWidth)
            val questionWidth = v.measuredWidth

            val specHeight = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED)
            v.measure(specHeight, specHeight)
            val questionHeight = v.measuredHeight

            v.measure(questionWidth, questionHeight)

            val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.layout(0, 0, v.measuredWidth, v.measuredHeight)
            v.draw(c)
            return b
        }

        val b = Bitmap.createBitmap(v.layoutParams.width, v.layoutParams.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)


        return b
    }

    fun getBitmapFromView(view: View): Bitmap? {
        val width = view.width
        val height = view.height

        if (width == 0 || height == 0) {
            return null
        }

        val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }
}