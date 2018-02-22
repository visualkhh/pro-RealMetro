package com.example.visualkhh.myapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.util.Log
import java.util.ArrayList
import android.graphics.*
import android.view.ScaleGestureDetector
import com.example.visualkhh.myapplication.view.domain.MetroViewScaleMinMax
import com.example.visualkhh.myapplication.view.domain.MetroViewSize


class MetroViewTemp :View, View.OnTouchListener{
    private var scaleDetector: ScaleGestureDetector
    private var scaleFactor = 1f
    //These two variables keep track of the X and Y coordinate of the finger when it first
    //touches the screen
    private var startX = 0f
    private var startY = 0f

    //These two variables keep track of the amount we need to translate the canvas along the X
    //and the Y coordinate
    private var translateX = 0f
    private var translateY = 0f

    //These two variables keep track of the amount we translated the X and Y coordinates, the last time we
    //panned.
    private var previousTranslateX = 0f
    private var previousTranslateY = 0f
    /////////


    ////////////
    var defaultSize = MetroViewSize()
    var draws = ArrayList<MetroDrawable>()
//    var draws = ArrayList<Class<out MetroDrawable>>()

    var zoom: Float = 100f
    var upZoom: Float = 100f
    var downPoint: PointF? = null
    var doubleDownRect: Rect = Rect(0,0,0,0)
    var dragRect: Rect = Rect(0,0,0,0)

    var movePoint: PointF = PointF(0f,0f)
    var upMovePoint: PointF = PointF(0f,0f)


    init {
        scaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    constructor(context: Context?) : super(context){
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context)
    }

    private fun init(context: Context?) {
        setOnTouchListener(this);
        setFocusableInTouchMode(true);  // 이벤트가 계속해서 발생하기 위해
        defaultSize = MetroViewSize(width, height, 0)
    }



    override fun onDraw(canvas: Canvas) {


        ////// translate and sacle
        canvas.save()

        //We're going to scale the X and Y coordinates by the same amount
        canvas.scale(scaleFactor, scaleFactor)

        //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
        if (translateX * -1 < 0) {
            translateX = 0f
        } else if (translateX * -1 > (scaleFactor - 1) * layoutParams.width) {
            translateX = (1 - scaleFactor) * layoutParams.width
        }//This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
        //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
        //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
        //as doing -1 * (scaleFactor - 1) * displayWidth

        if (translateY * -1 < 0) {
            translateY = 0f
        } else if (translateY * -1 > (scaleFactor - 1) * layoutParams.height) {
            translateY = (1 - scaleFactor) * layoutParams.height
        }//We do the exact same thing for the bottom bound, except in this case we use the height of the display

        //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level
        //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
        canvas.translate(translateX / scaleFactor, translateY / scaleFactor)

        //////////////////







        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawRect(Rect(0,0,width,height), paint)


        var minMax = MetroViewScaleMinMax(Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE)
        draws.forEach{
            minMax.minY = Math.min(it.getY(), minMax.minY)
            minMax.maxY = Math.max(it.getY(), minMax.maxY)
            minMax.minX = Math.min(it.getX(), minMax.minX)
            minMax.maxX = Math.max(it.getX(), minMax.maxX)
        }
//        draws.forEach{it.draw(minMax, movePoint, firstDownPoint, dragRect, zoom, canvas)}
        draws.forEach{it.draw(minMax, canvas,null)}

//        canvas.rotate(11f)
//        canvas.skew(-0.1f,0f)
//        canvas.translate(60f,0f)
//        canvas.save()
//        canvas.translate(250f,250f)
//        canvas.scale(250f,250f)
//        canvas.restore()
        /////////
        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL
        canvas.drawRect(dragRect, paint)




//        if (pointList.size() < 2) return
//        for (i in 1 until pointList.size()) {
//            if (pointList.get(i).draw) {
//                paint.setColor(pointList.get(i).mStrokeColor)
//                paint.setStrokeWidth(pointList.get(i).mStrokeWidth)
//                canvas.drawLine(pointList.get(i - 1).x,
//                        pointList.get(i - 1).y, pointList.get(i).x,
//                        pointList.get(i).y, paint)
//            }
//        }

        canvas.restore()
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        scaleDetector!!.onTouchEvent(event)

        val center = PointF(width/2f,height/2f)
        if(event.pointerCount==1){
            center.x = event.x
            center.y = event.y
        }else if(event.pointerCount>=2){
            val minX = Math.min(event.getX(0),event.getX(1))
            val maxX = Math.max(event.getX(0),event.getX(1))
            val minY = Math.min(event.getY(0),event.getY(1))
            val maxY = Math.max(event.getY(0),event.getY(1))
            val centerX = minX + ((maxX - minX) / 2)
            val centerY = minY + ((maxY - minY) / 2)
            center.x = centerX
            center.y = centerY
        }






        var r: Boolean = false

        when(event.getAction() and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                downPoint = PointF(event.x, event.y)
                Log.d("phoro", " 손가락으로 터치했음")
                invalidate()         // 그림 다시 그리기
                r=true
            }

            MotionEvent.ACTION_MOVE -> {

                downPoint = downPoint?.let { it } ?: PointF(event.x, event.y)
                dragRect.set(
                        Math.min(downPoint!!.x, event.x).toInt(),
                        Math.min(downPoint!!.y, event.y).toInt(),
                        Math.max(downPoint!!.x, event.x).toInt(),
                        Math.max(downPoint!!.y, event.y).toInt()
                )
                movePoint.x = upMovePoint.x + (center.x - downPoint!!.x)
                movePoint.y = upMovePoint.y + (center.y - downPoint!!.y)





                if(event.pointerCount>1){

                    downPoint = PointF(event.x, event.y)

                    val point1 = PointF(event.getX(0),event.getY(0))
                    val point2 = PointF(event.getX(1),event.getY(1))

                    /*
                    피타고라스 정의
                    피타고라스 정리는 위 그림에 적혀있는 것처럼 a^2 + b^2 = c^2이다. 여기서 a와 b는 가로와 세로 변의 길이를 의미하고 c는 직각삼각형의 빗변, 혹은 가장 긴 변의 길이를 의미한다.
                     */
                    val atDoubleDownRect = Rect(
                            Math.min(event.getX(0),event.getX(1)).toInt(),
                            Math.min(event.getY(0),event.getY(1)).toInt(),
                            Math.max(event.getX(0),event.getX(1)).toInt(),
                            Math.max(event.getY(0),event.getY(1)).toInt()
                    )

//                    var dat = Math.pow(doubleDownRect.width().toDouble(),2.0) + Math.pow(doubleDownRect.height().toDouble(),2.0)
//                    val at = Math.pow(atDoubleDownRect.width().toDouble(),2.0) + Math.pow(atDoubleDownRect.height().toDouble(),2.0)
//                    val ch = ((at.toFloat() / dat.toFloat()).toFloat() * 100f) - 100f
//                    var czoom = zoom + ch
//                    if(czoom<=1000 && czoom>=10){
//                        zoom = czoom
//                    }

                    /*
                        전체값에서 일부값은 몇 퍼센트? 계산법 공식
                        일부값 ÷ 전체값 X 100
                        예제) 300에서 105는 몇퍼센트?
                        답: 35%
                     */
                    val dat = doubleDownRect.width() * doubleDownRect.height()
                    val at = atDoubleDownRect.width() * atDoubleDownRect.height()
                    val ch = ((at.toFloat() / dat.toFloat()).toFloat() * 100f) - 100f
//                    var czoom = zoom + (ch / 2)

                    /*
                    전체값의 몇 퍼센트는 얼마? 계산법 공식
                    전체값 X 퍼센트 ÷ 100
                    예제) 300의 35퍼센트는 얼마?
                    답) 105
                     */
                    var czoom = upZoom + ((zoom * ch) / 100)

                    if(czoom<=1000 && czoom>=10){
                        zoom = czoom
                    }


                    Log.d("MetroView", "ddRect w:"+doubleDownRect.width()+" h:"+doubleDownRect.height()+ " z:"+zoom+"  ch:"+ch )

                }


//                var rr = ""
//                for (i in 0 until event.pointerCount) {
//                    rr += ", "+i+"(id:"+event.getPointerId(i)+", x:"+ event.getX(i)+", y:"+event.getY(i)
//                }
//                Log.d("phoro", rr+" 손가락으로 움직이는 중 "+firstDownPoint?.x + "  "+firstDownPoint?.y+"  zoom:"+zoom)
                invalidate()
                r=true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                var r = ""
                for (i in 0 until event.pointerCount) {
                    r += ", "+i+"(id:"+event.getPointerId(i)+", x:"+ event.getX(i)+", y:"+event.getY(i)
                }
//                val minX = Math.min(event.getX(0),event.getX(1))
//                val maxX = Math.max(event.getX(0),event.getX(1))
//                val minY = Math.min(event.getY(0),event.getY(1))
//                val maxY = Math.max(event.getY(0),event.getY(1))
//
//                val centerX = minX + ((maxX - minX) / 2)
//                val centerY = minY + ((maxY - minY) / 2)
//                firstDownPoint = PointF(centerX, centerY)

                downPoint = PointF(center.x, center.y)
                doubleDownRect.set(
                        Math.min(event.getX(0),event.getX(1)).toInt(),
                        Math.min(event.getY(0),event.getY(1)).toInt(),
                        Math.max(event.getX(0),event.getX(1)).toInt(),
                        Math.max(event.getY(0),event.getY(1)).toInt()
                )
                Log.d("phoro", r+" 멀티 터")
            }
            MotionEvent.ACTION_POINTER_UP -> {
                downPoint = null
                upMovePoint.x = movePoint.x
                upMovePoint.y = movePoint.y
                upZoom = zoom
//                if(event.pointerCount>1){
//                    upPoint1 = PointF(event.getX(0),event.getY(0))
//                    upPoint2 = PointF(event.getX(1),event.getY(1))
//                }
            }
            MotionEvent.ACTION_UP -> {
                downPoint = null
                upMovePoint.x = movePoint.x
                upMovePoint.y = movePoint.y
                upZoom = zoom
                Log.d("phoro", "손가락 땠음")
            }
        }
        return r;
    }



    fun defaultSetting(){
        zoom = 100f
        upZoom = 100f
        downPoint = null
        doubleDownRect = Rect(0,0,0,0)
        movePoint = PointF(0f,0f)
        upMovePoint = PointF(0f,0f)
        invalidate()
    }




    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM))
            return true
        }
    }

    companion object {
        //These two constants specify the minimum and maximum zoom
        private val MIN_ZOOM = 1f
        private val MAX_ZOOM = 5f

        //These constants specify the mode that we're in
        private val NONE = 0
        private val DRAG = 1
        private val ZOOM = 2
    }
}