package com.scanlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import kotlin.math.abs

/**
 * Created by jhansi on 28/03/15.
 */
class PolygonView : FrameLayout {
    private lateinit var paint: Paint
    private lateinit var pointer1: ImageView
    private lateinit var pointer2: ImageView
    private lateinit var pointer3: ImageView
    private lateinit var pointer4: ImageView
    private lateinit var midPointer13: ImageView
    private lateinit var midPointer12: ImageView
    private lateinit var midPointer34: ImageView
    private lateinit var midPointer24: ImageView

    private var listener: ((Boolean) -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        pointer1 = getImageView(0, 0)
        pointer2 = getImageView(width, 0)
        pointer3 = getImageView(0, height)
        pointer4 = getImageView(width, height)
        midPointer13 = getImageView(0, height / 2)
        midPointer13.setOnTouchListener(MidPointTouchListenerImpl(pointer1, pointer3))
        midPointer12 = getImageView(0, width / 2)
        midPointer12.setOnTouchListener(MidPointTouchListenerImpl(pointer1, pointer2))
        midPointer34 = getImageView(0, height / 2)
        midPointer34.setOnTouchListener(MidPointTouchListenerImpl(pointer3, pointer4))
        midPointer24 = getImageView(0, height / 2)
        midPointer24.setOnTouchListener(MidPointTouchListenerImpl(pointer2, pointer4))
        addView(pointer1)
        addView(pointer2)
        addView(midPointer13)
        addView(midPointer12)
        addView(midPointer34)
        addView(midPointer24)
        addView(pointer3)
        addView(pointer4)
        initPaint()
    }

    override fun attachViewToParent(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.attachViewToParent(child, index, params)
    }

    @SuppressLint("ResourceType")
    private fun initPaint() {
        paint = Paint()
        paint.color = getCustomColor(R.attr.selection_indicator_color)
        paint.strokeWidth = 2f
        paint.isAntiAlias = true
    }

    var points: Map<Int, PointF>
        get() {
            val points = listOf(
                PointF(pointer1.x, pointer1.y),
                PointF(pointer2.x, pointer2.y),
                PointF(pointer3.x, pointer3.y),
                PointF(pointer4.x, pointer4.y)
            )
            return getOrderedPoints(points)
        }
        set(pointFMap) {
            if (pointFMap.size == 4) {
                setPointsCoordinates(pointFMap)
            }
        }

    private fun getOrderedPoints(points: List<PointF>): Map<Int, PointF> {
        val centerPoint = PointF()
        val size = points.size
        for (pointF in points) {
            centerPoint.x += pointF.x / size
            centerPoint.y += pointF.y / size
        }
        val orderedPoints: MutableMap<Int, PointF> = HashMap()
        for (pointF in points) {
            var index = -1
            if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                index = 0
            } else if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                index = 1
            } else if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                index = 2
            } else if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                index = 3
            }
            orderedPoints[index] = pointF
        }
        return orderedPoints
    }

    private fun setPointsCoordinates(pointFMap: Map<Int, PointF>) {
        pointer1.x = pointFMap[0]!!.x
        pointer1.y = pointFMap[0]!!.y
        pointer2.x = pointFMap[1]!!.x
        pointer2.y = pointFMap[1]!!.y
        pointer3.x = pointFMap[2]!!.x
        pointer3.y = pointFMap[2]!!.y
        pointer4.x = pointFMap[3]!!.x
        pointer4.y = pointFMap[3]!!.y
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(
            pointer1.x + pointer1.width / 2,
            pointer1.y + pointer1.height / 2,
            pointer3.x + pointer3.width / 2,
            pointer3.y + pointer3.height / 2,
            paint
        )
        canvas.drawLine(
            pointer1.x + pointer1.width / 2,
            pointer1.y + pointer1.height / 2,
            pointer2.x + pointer2.width / 2,
            pointer2.y + pointer2.height / 2,
            paint
        )
        canvas.drawLine(
            pointer2.x + pointer2.width / 2,
            pointer2.y + pointer2.height / 2,
            pointer4.x + pointer4.width / 2,
            pointer4.y + pointer4.height / 2,
            paint
        )
        canvas.drawLine(
            pointer3.x + pointer3.width / 2,
            pointer3.y + pointer3.height / 2,
            pointer4.x + pointer4.width / 2,
            pointer4.y + pointer4.height / 2,
            paint
        )
        midPointer13.x = pointer3.x - (pointer3.x - pointer1.x) / 2
        midPointer13.y = pointer3.y - (pointer3.y - pointer1.y) / 2
        midPointer24.x = pointer4.x - (pointer4.x - pointer2.x) / 2
        midPointer24.y = pointer4.y - (pointer4.y - pointer2.y) / 2
        midPointer34.x = pointer4.x - (pointer4.x - pointer3.x) / 2
        midPointer34.y = pointer4.y - (pointer4.y - pointer3.y) / 2
        midPointer12.x = pointer2.x - (pointer2.x - pointer1.x) / 2
        midPointer12.y = pointer2.y - (pointer2.y - pointer1.y) / 2
    }

    private fun getImageView(x: Int, y: Int): ImageView = ImageView(context).apply {
        this.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        this.setImageResource(R.drawable.circle)
        this.x = x.toFloat()
        this.y = y.toFloat()
        setOnTouchListener(TouchListenerImpl())
    }

    private inner class MidPointTouchListenerImpl(
        private val mainPointer1: ImageView,
        private val mainPointer2: ImageView
    ) : OnTouchListener {

        private val downPt = PointF() // Record Mouse Position When Pressed Down
        private var startPt = PointF() // Record Start Position of 'img'

        @SuppressLint("ResourceType")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val mv = PointF(event.x - downPt.x, event.y - downPt.y)
                    if (abs(mainPointer1.x - mainPointer2.x) > abs(
                            mainPointer1.y - mainPointer2.y
                        )
                    ) {
                        if (mainPointer2.y + mv.y + v.height < height && mainPointer2.y + mv.y > 0) {
                            v.x = (startPt.y + mv.y).toInt().toFloat()
                            startPt = PointF(v.x, v.y)
                            mainPointer2.y = (mainPointer2.y + mv.y).toInt().toFloat()
                        }
                        if (mainPointer1.y + mv.y + v.height < height && mainPointer1.y + mv.y > 0) {
                            v.x = (startPt.y + mv.y).toInt().toFloat()
                            startPt = PointF(v.x, v.y)
                            mainPointer1.y = (mainPointer1.y + mv.y).toInt().toFloat()
                        }
                    } else {
                        if (mainPointer2.x + mv.x + v.width < width && mainPointer2.x + mv.x > 0) {
                            v.x = (startPt.x + mv.x).toInt().toFloat()
                            startPt = PointF(v.x, v.y)
                            mainPointer2.x = (mainPointer2.x + mv.x).toInt().toFloat()
                        }
                        if (mainPointer1.x + mv.x + v.width < width && mainPointer1.x + mv.x > 0) {
                            v.x = (startPt.x + mv.x).toInt().toFloat()
                            startPt = PointF(v.x, v.y)
                            mainPointer1.x = (mainPointer1.x + mv.x).toInt().toFloat()
                        }
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    downPt.x = event.x
                    downPt.y = event.y
                    startPt = PointF(v.x, v.y)
                }
                MotionEvent.ACTION_UP -> {
                    val isValid = isValidShape(points)
                    listener?.invoke(isValid)
                    paint.color = getCustomColor(
                        if (isValid) R.attr.selection_indicator_color else R.attr.selection_indicator_color_invalid
                    )
                }
                else -> {}
            }
            invalidate()
            return true
        }
    }

    fun isValidShape(pointFMap: Map<Int, PointF>): Boolean {
        return pointFMap.size == 4
    }

    fun setValidPolygonListener(listener: ((Boolean) -> Unit)?) {
        this.listener = listener
    }

    private inner class TouchListenerImpl : OnTouchListener {

        private val downPt = PointF() // Record Mouse Position When Pressed Down
        private var startPt = PointF() // Record Start Position of 'img'

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val mv = PointF(event.x - downPt.x, event.y - downPt.y)
                    if (startPt.x + mv.x + v.width < width && startPt.y + mv.y + v.height < height && startPt.x + mv.x > 0 && startPt.y + mv.y > 0) {
                        v.x = (startPt.x + mv.x).toInt().toFloat()
                        v.y = (startPt.y + mv.y).toInt().toFloat()
                        startPt = PointF(v.x, v.y)
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    downPt.x = event.x
                    downPt.y = event.y
                    startPt = PointF(v.x, v.y)
                }
                MotionEvent.ACTION_UP -> {
                    val isValid = isValidShape(points)
                    listener?.invoke(isValid)
                    paint.color = getCustomColor(
                        if (isValid) R.attr.selection_indicator_color else R.attr.selection_indicator_color_invalid
                    )
                }
                else -> {}
            }
            invalidate()
            return true
        }
    }

    fun getCustomColor(@AttrRes id: Int): Int {
        return context.obtainStyledAttributes(intArrayOf(id)).use {
            it.getColor(0, Color.MAGENTA)
        }
    }

}
