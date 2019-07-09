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

    private fun getBitmapFromView(view: View): Bitmap? {
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