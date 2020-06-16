package cl.jesualex.stooltip

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView

/**
 * Created by jesualex on 2019-04-29.
 */
class TargetView(context: Context): AppCompatImageView(context) {
    private val position = IntArray(4)
    private val size = IntArray(2)

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(position[0], position[1], position[2], position[3])
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(size[0], MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(size[1], MeasureSpec.EXACTLY)
        )
    }

    fun setTarget(target: View?) {
        if (target == null) return

        val innerPos = IntArray(2)

        target.getLocationOnScreen(innerPos)
        position[0] = innerPos[0]
        position[1] = innerPos[1]
        position[2] = innerPos[0] + target.width
        position[3] = innerPos[1] + target.height

        val bitmap = getBitmapFromView(target) ?: return

        setImageBitmap(bitmap)
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