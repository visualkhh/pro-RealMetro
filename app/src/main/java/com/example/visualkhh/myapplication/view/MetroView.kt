package com.example.visualkhh.myapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.util.Log
import java.util.ArrayList
import android.graphics.*


class MetroView :View, View.OnTouchListener{
    var defaultSize = MetroViewSize()
    var draws = ArrayList<MetroDrawable>()
//    var draws = ArrayList<Class<out MetroDrawable>>()

    var zoom: Float = 100f
    var downPoint: PointF? = null
    var doubleDownRect: Rect = Rect(0,0,0,0)
    var movePoint: PointF = PointF(0f,0f)
    var upMovePoint: PointF = PointF(0f,0f)

    constructor(context: Context?) : super(context){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private fun init() {
        setOnTouchListener(this);
        setFocusableInTouchMode(true);  // 이벤트가 계속해서 발생하기 위해
        defaultSize = MetroViewSize(width, height, 0)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        var minMax = MetroViewScaleMinMax(Float.MAX_VALUE,Float.MIN_VALUE,Float.MAX_VALUE,Float.MIN_VALUE)
        draws.forEach{
            minMax.minY = Math.min(it.getY(), minMax.minY)
            minMax.maxY = Math.max(it.getY(), minMax.maxY)
            minMax.minX = Math.min(it.getX(), minMax.minX)
            minMax.maxX = Math.max(it.getX(), minMax.maxX)
        }
        draws.forEach{it.draw(minMax, movePoint, canvas)}


        val paint = Paint()
        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL
        canvas.drawRect(doubleDownRect, paint)


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
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {


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
                movePoint.x = upMovePoint.x + (center.x - downPoint!!.x)
                movePoint.y = upMovePoint.y + (center.y - downPoint!!.y)



                if(event.pointerCount>1){
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


                    /*
                        전체값에서 일부값은 몇 퍼센트? 계산법 공식
                        일부값 ÷ 전체값 X 100
                        예제) 300에서 105는 몇퍼센트?
                        답: 35%
                     */
                    val dat = doubleDownRect.width() * doubleDownRect.height()
                    val at = atDoubleDownRect.width() * atDoubleDownRect.height()
                    zoom = (at.toFloat() / dat.toFloat()).toFloat() * 100f

                }


                var rr = ""
                for (i in 0 until event.pointerCount) {
                    rr += ", "+i+"(id:"+event.getPointerId(i)+", x:"+ event.getX(i)+", y:"+event.getY(i)
                }
                Log.d("phoro", rr+" 손가락으로 움직이는 중 "+downPoint?.x + "  "+downPoint?.y+"  zoom:"+zoom)
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
//                downPoint = PointF(centerX, centerY)

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
//                if(event.pointerCount>1){
//                    upPoint1 = PointF(event.getX(0),event.getY(0))
//                    upPoint2 = PointF(event.getX(1),event.getY(1))
//                }
            }
            MotionEvent.ACTION_UP -> {
                downPoint = null
                upMovePoint.x = movePoint.x
                upMovePoint.y = movePoint.y
                Log.d("phoro", "손가락 땠음")
            }
        }
        return r;
    }
}