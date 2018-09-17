package uz.mukhammadakbar.image.viewer.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

class ShadowLayout : RelativeLayout {

    private var mShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mShadowDepth: Float = 0.toFloat()
    private var mShadowBitmap: Bitmap? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }

    constructor(context: Context) : super(context) { init() }

    private fun init() {
        mShadowPaint.color = Color.BLACK
        mShadowPaint.style = Style.FILL
        setWillNotDraw(false)
        mShadowBitmap = Bitmap.createBitmap(sShadowRect.width(),
                sShadowRect.height(), Bitmap.Config.ARGB_8888)
        val c = Canvas(mShadowBitmap)
        mShadowPaint.maskFilter = BlurMaskFilter(BLUR_RADIUS.toFloat(), Blur.NORMAL)
        c.translate(BLUR_RADIUS.toFloat(), BLUR_RADIUS.toFloat())
        c.drawRoundRect(sShadowRectF, sShadowRectF.width() / 40,
                sShadowRectF.height() / 40, mShadowPaint)
    }

    fun setShadowDepth(depth: Float) {
        if (depth != mShadowDepth) {
            mShadowDepth = depth
            mShadowPaint.alpha = (100 + 150 * (1 - mShadowDepth)).toInt()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.VISIBLE || child.alpha == 0f) {
                continue
            }
            val depthFactor = (80 * mShadowDepth).toInt()
            canvas.save()
            canvas.translate((child.left + depthFactor).toFloat(),
                    (child.top + depthFactor).toFloat())
            canvas.concat(child.matrix)
            tempShadowRectF.right = child.width.toFloat()
            tempShadowRectF.bottom = child.height.toFloat()
            canvas.drawBitmap(mShadowBitmap, sShadowRect, tempShadowRectF, mShadowPaint)
            canvas.restore()
        }
    }

    companion object {
        internal const val BLUR_RADIUS = 200
        internal val sShadowRectF = RectF(0f, 0f, 200f, 200f)
        internal val sShadowRect = Rect(0, 0, 200 + 2 * BLUR_RADIUS, 200 + 2 * BLUR_RADIUS)
        internal var tempShadowRectF = RectF(0f, 0f, 0f, 0f)
    }
}