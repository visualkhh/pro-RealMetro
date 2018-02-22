package com.visualkhh.app.realmetro.view

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.support.annotation.ColorRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.visualkhh.app.realmetro.DrawEventCallBack
import com.visualkhh.app.realmetro.view.pojo.*
import com.visualkhh.app.realmetro.view.util.BitmapUtil
import com.visualkhh.app.realmetro.view.util.Constant
import com.visualkhh.app.realmetro.view.util.LogUtil

import java.util.ArrayList

import com.visualkhh.app.realmetro.view.util.PaintUtil.getDistance
import com.visualkhh.app.realmetro.view.util.PaintUtil.measureText


/**
 * Created by lht on 16/10/17.
 */

class PaintView : View {

    private var mOnDrawListener: OnDrawListener? = null

    //View Size
    //View尺寸
    private var bInited = false
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    //Background Color
    //背景色
    private var mBgColor = Color.WHITE
    //Paint List for Stroke
    //绘制笔迹Paint列表
    private var mPaintList = ArrayList<SerializablePaint>()

    private var mLastDimensionW = -1
    private var mLastDimensionH = -1

    //Paint for Text
    //用于绘制文字
    private var mTextRectPaint: SerializablePaint? = null
    //Paint List for Stroke
    //绘制文字Paint列表
    private var mTextPaintList = ArrayList<SerializablePaint>()

    //Background Image
    //背景图
    private var mBgBitmap: Bitmap? = null
    private var mBgPadding = 0
    //Paint for Background
    //绘制背景图Paint
    private var mBgPaint: Paint? = null

    var isPaintEnable = true

    //Current Coordinate
    //当前坐标
    private var mCurrentX: Float = 0.toFloat()
    private var mCurrentY: Float = 0.toFloat()
    //Current Drawing Path
    //当前绘制路径
    private var mCurrentPath: SerializablePath? = null

    //Shape List(Path, Point and Text)
    //绘制列表(线、点和文字）
    private var mDrawShapes: ArrayList<DrawShape>? = ArrayList()
    private var bPathDrawing = false

    //Gesture
    //手势
    private var mGestureState = Constant.GestureState.NONE

    /**
     * 设置手势是否可用
     * @param gestureEnable
     */
    var isGestureEnable = true
    private var bGestureMoving = false
    private var mScaleMax = 10f
    private var mScaleMin = 0.5f

    //Center Point of Two Fingers
    //当次两指中心点
    private var mCurrentCenterX: Float = 0.toFloat()
    private var mCurrentCenterY: Float = 0.toFloat()
    //当次两指间距
    private var mCurrentLength = 0f
    //当次位移
    private var mCurrentDistanceX: Float = 0.toFloat()
    private var mCurrentDistanceY: Float = 0.toFloat()
    //当次缩放
    private var mCurrentScale: Float = 0.toFloat()

    //整体矩阵
    private var mMainMatrix = Matrix()
    private var mMainMatrixValues = FloatArray(9)
    //当次矩阵
    private var mCurrentMatrix = Matrix()

    /**
     * 获得当前笔迹
     */
    private val currentPaint: SerializablePaint
        get() = mPaintList[mPaintList.size - 1]

    /**
     * 获得当前文字笔迹
     */
    private val currentTextPaint: SerializablePaint
        get() = mTextPaintList[mTextPaintList.size - 1]

    interface OnDrawListener {
        fun afterPaintInit(viewWidth: Int, viewHeight: Int)
        fun afterEachPaint(drawShapes: ArrayList<DrawShape>?)
    }

    fun setOnDrawListener(onDrawListener: OnDrawListener) {
        mOnDrawListener = onDrawListener
    }

//    var draws = ArrayList<MetroDrawable>()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        isDrawingCacheEnabled = true

        initPaint()
    }

    private fun initPaint() {
        mBgPaint = Paint()
        mBgPaint!!.isAntiAlias = true
        mBgPaint!!.isDither = true

        val paint = SerializablePaint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND// 设置外边缘
        paint.strokeCap = Paint.Cap.ROUND// 形状

        mPaintList.add(paint)

        val textPaint = SerializablePaint(paint)
        textPaint.style = Paint.Style.FILL
        mTextPaintList.add(textPaint)

        mTextRectPaint = SerializablePaint(paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (!bInited) {
            mWidth = right - left
            mHeight = bottom - top

            resizeBitmap()

            bInited = true

            if (mOnDrawListener != null) {
                mOnDrawListener!!.afterPaintInit(mWidth, mHeight)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        // Get the superclass parcelable state
        val superState = super.onSaveInstanceState()

        return SavedState(superState, mDrawShapes, mLastDimensionW, mLastDimensionH)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        // If not instance of my state, let the superclass handle it
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

// Superclass restore state
        super.onRestoreInstanceState(state.superState)

        mDrawShapes = state.drawShapes

        mLastDimensionW = state.lastDimensionW
        mLastDimensionH = state.lastDimensionH
    }

    fun addDrawEvent(x: Float, y: Float, drawEvents: DrawEventCallBack, invalidate: Boolean = true) {
//        for(at in drawEvents) {
//            val drawEvent = DrawEvent(x, y, currentTextPaint)
//            drawEvent.drawEventCallBack = at
//            mDrawShapes!!.add(drawEvent)
//        }
        val drawEvent = DrawEvent(x, y, currentTextPaint)
        drawEvent.drawEventCallBack = drawEvents
        mDrawShapes!!.add(drawEvent)
        if(invalidate) {
            invalidate()
        }
    }
    /**
     * 添加文字
     * @param text
     * @param x
     * @param y
     */
    fun addText(text: String, x: Float, y: Float) {
        val drawText = DrawText(x, y, currentTextPaint)
        drawText.setText(text)
        mDrawShapes!!.add(drawText)
        invalidate()
    }

    /**
     * 添加文字
     * @param text
     * @param x
     * @param y
     * @param gravity
     */
    fun addText(text: String, x: Float, y: Float, gravity: Int) {
        var x = x
        var y = y
        val textRect = measureText(currentTextPaint, text)

        when (gravity) {
            Constant.TextGravity.CENTER -> {
                x = ((mWidth - textRect.width()) / 2).toFloat()
                y = ((mHeight + textRect.height()) / 2).toFloat()
            }
            Constant.TextGravity.CENTER_HORIZONTAL -> x = ((mWidth - textRect.width()) / 2).toFloat()
            Constant.TextGravity.CENTER_VERTICAL -> y = ((mHeight + textRect.height()) / 2).toFloat()
        }

        val drawText = DrawText(x, y, currentTextPaint)
        drawText.setText(text)
        mDrawShapes!!.add(drawText)
        invalidate()
    }

    /**
     * 设置缩放上限，默认为2
     * @param scaleMax
     */
    fun setScaleMax(scaleMax: Float) {
        this.mScaleMax = scaleMax
    }

    /**
     * 设置缩放下限，默认为0.5
     * @param scaleMin
     */
    fun setScaleMin(scaleMin: Float) {
        this.mScaleMin = scaleMin
    }

    /**
     * Undo
     * 撤销
     * @return is Undo still available 是否还能撤销
     */
    fun undo(): Boolean {
        if (mDrawShapes != null && mDrawShapes!!.size > 0) {
            mDrawShapes!!.removeAt(mDrawShapes!!.size - 1)
            invalidate()
        }

        if (mOnDrawListener != null) {
            mOnDrawListener!!.afterEachPaint(mDrawShapes)
        }

        return mDrawShapes != null && mDrawShapes!!.size > 0
    }

    /**
     * Clear All
     * 清除所有笔迹
     * @return is Undo still available 是否还能撤销
     */
    fun clear(invalidate: Boolean = true): Boolean {
        if (mDrawShapes != null && mDrawShapes!!.size > 0) {
            mDrawShapes!!.clear()
            if(invalidate) {
                invalidate()
            }
        }

        if (mOnDrawListener != null) {
            mOnDrawListener!!.afterEachPaint(mDrawShapes)
        }

        return mDrawShapes != null && mDrawShapes!!.size > 0
    }

    /**
     * Set background color from resource
     * @param res
     */
    fun setBgColorFromRes(@ColorRes res: Int) {
        setBgColor(context.resources.getColor(res))
    }

    /**
     * Set background color
     * 设置背景颜色
     * @param color 0xaarrggbb
     */
    fun setBgColor(color: Int) {
        mBgColor = color
    }

    /**
     * Set paint color from resource
     * @param res
     */
    fun setColorFromRes(@ColorRes res: Int) {
        setColor(context.resources.getColor(res))
    }

    /**
     * Set paint color
     * 设置画笔颜色
     * @param color 0xaarrggbb
     */
    fun setColor(color: Int) {
        val paint = SerializablePaint(currentPaint)
        paint.color = color
        mPaintList.add(paint)
    }

    /**
     * Set stroke width
     * 设置画笔宽度
     * @param width
     */
    fun setStrokeWidth(width: Int) {
        val paint = SerializablePaint(currentPaint)
        paint.strokeWidth = width.toFloat()
        mPaintList.add(paint)
    }

    /**
     * Set text color from resource
     * @param res
     */
    fun setTextColorFromRes(@ColorRes res: Int) {
        setTextColor(context.resources.getColor(res))
    }

    /**
     * Set text color
     * 设置文字颜色
     * @param color 0xaarrggbb
     */
    fun setTextColor(color: Int) {
        val paint = SerializablePaint(currentTextPaint)
        paint.color = color
        mTextPaintList.add(paint)

        mTextRectPaint!!.color = color
    }

    /**
     * Set text size
     * 设置文字大小
     * @param size
     */
    fun setTextSize(size: Int) {
        val paint = SerializablePaint(currentTextPaint)
        paint.textSize = size.toFloat()
        mTextPaintList.add(paint)
    }

    /**
     * 获取绘制结果图
     * @param isViewOnly true for just inside the view,
     * false for whole bitmap in original scale and transition
     * @return paint result 绘制结果图
     */
    fun getBitmap(isViewOnly: Boolean): Bitmap {
        val result: Bitmap
        if (isViewOnly) {
            destroyDrawingCache()
            result = drawingCache
        } else {
            result = Bitmap.createBitmap(mBgBitmap!!.width,
                    mBgBitmap!!.height, Bitmap.Config.ARGB_8888)
            val matrix = Matrix()
            val canvas = Canvas()
            canvas.setBitmap(result)

            canvas.drawColor(mBgColor)

            BitmapUtil.setBitmapPosition(mBgBitmap!!,
                    mBgBitmap!!.width, mBgBitmap!!.height, matrix)
            if (mBgBitmap != null) {
                canvas.drawBitmap(mBgBitmap!!, matrix, mBgPaint)
            }

            mMainMatrix.invert(matrix)
            for (shape in mDrawShapes!!) {
                shape.clone(1f).draw(canvas, matrix)
            }
        }
        return result
    }

    /**
     * Set background image
     * 设置背景图
     * @param bitmap
     */
    fun setBitmap(bitmap: Bitmap, padding: Int) {
        mBgBitmap = bitmap
        mBgPadding = padding
    }

    /**
     * Set background image
     * 设置背景图
     * @param bitmap
     */
    fun setBitmap(bitmap: Bitmap) {
        mBgBitmap = bitmap
    }

    fun reset(){
        mOnDrawListener = null



        bInited = false
        mWidth = 0
        mHeight = 0

        mBgColor = Color.WHITE
        mPaintList = ArrayList<SerializablePaint>()

        mLastDimensionW = -1
        mLastDimensionH = -1

        mTextRectPaint = null
        mTextPaintList = ArrayList<SerializablePaint>()

        mBgBitmap = null
        mBgPadding = 0
        mBgPaint = null
        isPaintEnable = true
        mCurrentX = 0.toFloat()
        mCurrentY = 0.toFloat()
        mCurrentPath = null

        mDrawShapes = ArrayList()
        bPathDrawing = false

        mGestureState = Constant.GestureState.NONE



        isGestureEnable = true
        bGestureMoving = false
        mScaleMax = 10f
        mScaleMin = 0.5f


        mCurrentCenterX = 0.toFloat()
        mCurrentCenterY = 0.toFloat()

        mCurrentLength = 0f

        mCurrentDistanceX = 0.toFloat()
        mCurrentDistanceY = 0.toFloat()

        mCurrentScale = 0.toFloat()
        mMainMatrix = Matrix()
        mMainMatrixValues = FloatArray(9)
        mCurrentMatrix = Matrix()
        initPaint()
//        mCurrentScale = scale
//        mMainMatrix.postScale(mCurrentScale, mCurrentScale, mCurrentCenterX, mCurrentCenterY)
//        mCurrentMatrix.setScale(mCurrentScale, mCurrentScale, mCurrentCenterX, mCurrentCenterY)
//        scaleStrokeWidth(mCurrentScale)
//        mMainMatrix.getValues(mMainMatrixValues)
//        mCurrentMatrix.reset()
//        invalidate()
    }

    private fun resizeBitmap() {
        if (mBgBitmap == null) {
            return
        }

        //将图压缩到view尺寸内
        if (mBgBitmap!!.width > mWidth - mBgPadding * 2 || mBgBitmap!!.height > mHeight - mBgPadding * 2) {
            mBgBitmap = BitmapUtil.zoomBitmap(mBgBitmap!!,
                    mWidth - mBgPadding * 2,
                    mHeight - mBgPadding * 2)
        }

        BitmapUtil.setBitmapPosition(mBgBitmap!!, mWidth, mHeight, mMainMatrix)
    }

    /**
     * 缩放所有笔迹
     */
    private fun scaleStrokeWidth(scale: Float) {
        for (paint in mPaintList) {
            paint.scale = paint.scale * scale
        }
        for (paint in mTextPaintList) {
            paint.scale = paint.scale * scale
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBgColor)

//        if (mBgBitmap != null) {
//            canvas.drawBitmap(mBgBitmap!!, mMainMatrix, mBgPaint)
//        }
//
        for (shape in mDrawShapes!!) {
            shape.draw(canvas, mCurrentMatrix)
        }

//        var minMax = MetroViewScaleMinMax(Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE)
//        draws.forEach{
//            minMax.minY = Math.min(it.getY(), minMax.minY)
//            minMax.maxY = Math.max(it.getY(), minMax.maxY)
//            minMax.minX = Math.min(it.getX(), minMax.minX)
//            minMax.maxX = Math.max(it.getX(), minMax.maxX)
//        }
//        draws.forEach{it.draw(minMax, canvas, mCurrentMatrix)}
    }

    private fun paint(drawShape: DrawShape) {
        if (!isPaintEnable) return

        mDrawShapes!!.add(drawShape)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        mGestureState = Constant.GestureState.NONE
        when (event.action and MotionEvent.ACTION_MASK) {
        //多点按下
            MotionEvent.ACTION_POINTER_DOWN -> {
                LogUtil.d("ACTION_DOUBLE_DOWN")
                if (isGestureEnable) {
                    doubleFingerDown(event)
                }
            }
        //单点按下
            MotionEvent.ACTION_DOWN -> {
                LogUtil.d("ACTION_SINGLE_DOWN")
                touchDown(x, y)
            }
        //移动
            MotionEvent.ACTION_MOVE ->
                //文字不在输入时，单点移动
                if (event.pointerCount == Constant.GestureFingers.SINGLE && !bGestureMoving) {
                    LogUtil.d("ACTION_SINGLE_MOVE")
                    touchMove(x, y)
                } else if (event.pointerCount == Constant.GestureFingers.DOUBLE && isGestureEnable) {
                    LogUtil.d("ACTION_DOUBLE_MOVE")
                    bGestureMoving = true
                    doubleFingerMove(event)
                }//文字不在输入时，多点移动
        //抬起
            MotionEvent.ACTION_UP -> {
                LogUtil.d("ACTION_UP")
                bGestureMoving = false
                touchUp(x, y)
            }
        }

        when (mGestureState) {
            Constant.GestureState.DRAG -> {
//                                setDragMode();
                mMainMatrix.postTranslate(mCurrentDistanceX, mCurrentDistanceY)
                mCurrentMatrix.setTranslate(mCurrentDistanceX, mCurrentDistanceY)
            }
            Constant.GestureState.ZOOM -> {
//                                setZoomMode();
                mMainMatrix.postScale(mCurrentScale, mCurrentScale, mCurrentCenterX, mCurrentCenterY)
                mCurrentMatrix.setScale(mCurrentScale, mCurrentScale, mCurrentCenterX, mCurrentCenterY)
                scaleStrokeWidth(mCurrentScale)
            }
            Constant.GestureState.NONE -> mCurrentMatrix.reset()
        }

        mMainMatrix.getValues(mMainMatrixValues)

        invalidate()
        return true
    }

    private fun touchDown(x: Float, y: Float) {
        mCurrentX = x
        mCurrentY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val previousX = mCurrentX
        val previousY = mCurrentY

        mCurrentX = x
        mCurrentY = y

        val dx = Math.abs(x - previousX)
        val dy = Math.abs(y - previousY)

        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            if (!bPathDrawing) {
                mCurrentPath = SerializablePath()
                mCurrentPath!!.moveTo(previousX, previousY)
                paint(DrawPath(mCurrentPath, currentPaint))
                bPathDrawing = true
            }

            //设置贝塞尔曲线的操作点为起点和终点的一半
            val cX = (mCurrentX + previousX) / 2
            val cY = (mCurrentY + previousY) / 2

            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mCurrentPath!!.quadTo(previousX, previousY, cX, cY)
        }
    }

    private fun touchUp(x: Float, y: Float) {
        //不在绘制笔迹，而是点击时
        if (!bPathDrawing && x == mCurrentX && y == mCurrentY) {
            paint(DrawPoint(x, y, currentPaint))
        }

        bPathDrawing = false

        if (mOnDrawListener != null) {
            mOnDrawListener!!.afterEachPaint(mDrawShapes)
        }
    }

    //两点按下
    private fun doubleFingerDown(event: MotionEvent) {
        mCurrentCenterX = (event.getX(0) + event.getX(1)) / 2
        mCurrentCenterY = (event.getY(0) + event.getY(1)) / 2

        mCurrentLength = getDistance(event)
    }

    //两点移动
    private fun doubleFingerMove(event: MotionEvent) {
        //当前中心点
        val curCenterX = (event.getX(0) + event.getX(1)) / 2
        val curCenterY = (event.getY(0) + event.getY(1)) / 2

        //当前两点间距离
        val curLength = getDistance(event)

        //拖动
        if (Math.abs(mCurrentLength - curLength) < 5) {
            mGestureState = Constant.GestureState.DRAG
            mCurrentDistanceX = curCenterX - mCurrentCenterX
            mCurrentDistanceY = curCenterY - mCurrentCenterY
        } else if (mCurrentLength < curLength || mCurrentLength > curLength) {
            mGestureState = Constant.GestureState.ZOOM
            mCurrentScale = curLength / mCurrentLength

            //放大缩小临界值判断
            val toScale = mMainMatrixValues[Matrix.MSCALE_X] * mCurrentScale
            if (toScale > mScaleMax || toScale < mScaleMin) {
                mCurrentScale = 1f
            }
        }//放大 || 缩小

        mCurrentCenterX = curCenterX
        mCurrentCenterY = curCenterY

        mCurrentLength = curLength
    }

    private fun setDragMode() {
        mMainMatrix.getValues(mMainMatrixValues)

        val imageLeft = mMainMatrixValues[Matrix.MTRANS_X]
        val imageRight = mMainMatrixValues[Matrix.MTRANS_X] + mBgBitmap!!.width * mMainMatrixValues[Matrix.MSCALE_X]
        val imageTop = mMainMatrixValues[Matrix.MTRANS_Y]
        val imageBtm = mMainMatrixValues[Matrix.MTRANS_Y] + mBgBitmap!!.height * mMainMatrixValues[Matrix.MSCALE_Y]

        if (imageLeft + mCurrentDistanceX >= 0 || imageRight + mCurrentDistanceX <= mWidth) {
            mCurrentDistanceX = 0f
        }

        if (imageTop + mCurrentDistanceY >= 0 || imageBtm + mCurrentDistanceY <= mHeight) {
            mCurrentDistanceY = 0f
        }
    }

    private fun setZoomMode() {
        val imageLeft = mMainMatrixValues[Matrix.MTRANS_X]
        val imageRight = mMainMatrixValues[Matrix.MTRANS_X] + mBgBitmap!!.width * mMainMatrixValues[Matrix.MSCALE_X]
        val imageTop = mMainMatrixValues[Matrix.MTRANS_Y]
        val imageBtm = mMainMatrixValues[Matrix.MTRANS_Y] + mBgBitmap!!.height * mMainMatrixValues[Matrix.MSCALE_Y]

        if (imageLeft == 0f) {
            mCurrentCenterX = 0f
        } else if (imageRight == mWidth.toFloat()) {
            mCurrentCenterX = mWidth.toFloat()
        } else {
            mCurrentCenterX = (mWidth / 2).toFloat()
        }

        if (imageTop >= 0) {
            mCurrentCenterY = 0f
        } else if (imageBtm <= mHeight) {
            mCurrentCenterY = mHeight.toFloat()
        } else {
            mCurrentCenterY = (mHeight / 2).toFloat()
        }
    }

}
