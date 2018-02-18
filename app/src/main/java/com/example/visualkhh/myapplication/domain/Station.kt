package com.example.visualkhh.myapplication.domain

import android.graphics.*
import com.example.visualkhh.myapplication.view.MetroDrawable
import com.example.visualkhh.myapplication.view.domain.MetroViewScaleMinMax
import android.text.TextPaint



/**
 * Created by visualkhh on 2018. 2. 16..
 */
class Station(val id: String,val lat:Float, val lng: Float,
                   var upTrain: Train? = null, var downTrain: Train? = null,
                   var name: String= "none",
                   var color: String = "#000000",
                   var type: TYPE = TYPE.NORMAL) : MetroDrawable{
    enum class TYPE { NORMAL, BROKEN, HIDDEN }


    override fun draw(minMax: MetroViewScaleMinMax, canvas: Canvas) {
        if(type==TYPE.HIDDEN){
            return
        }


        val minX = 0
        val minY = 0
        val maxX = minMax.maxX - minMax.minX
        val maxY = minMax.maxY - minMax.minY

        val atX = lng - minMax.minX
        val atY = lat - minMax.minY   //위도는 아래로 내려가면 갈수록 0에 가까워지기때문에 뒤집기 해줘야한다
//        val atY = minMax.maxY - (lat - minMax.minY)   //위도는 아래로 내려가면 갈수록 0에 가까워지기때문에 뒤집기 해줘야한다


        /*
        전체값에서 일부값은 몇 퍼센트? 계산법 공식
        일부값 ÷ 전체값 X 100
        예제) 300에서 105는 몇퍼센트?
        답: 35%
         */
        val atXPer = (atX / maxX) * 100
        val atYPer = (atY / maxY) * 100


        ////////////canvase
        val cmaxX = canvas.width
        val cmaxY = canvas.height
//        val cmaxX = ((canvas.width * zoom) / 100)
//        val cmaxY = ((canvas.height * zoom) / 100)



        ///
//        movePoint.x = ((movePoint.x * zoom) / 100)
//        movePoint.y = ((movePoint.y * zoom) / 100)
//        movePoint.x = (movePoint.x + (canvas.width - cmaxX))/2
//        movePoint.y = (movePoint.y + (canvas.height - cmaxY))/2



//        Log.d("Station", "w:"+canvas.width+" h:"+canvas.height+" x:"+cmaxX+"  y:"+cmaxY+" zoom : "+zoom+" mx:"+movePoint.x+" my:"+movePoint.y)
        /*
        전체값의 몇 퍼센트는 얼마? 계산법 공식
        전체값 X 퍼센트 ÷ 100
        예제) 300의 35퍼센트는 얼마?
        답) 105
         */
        val catX = (cmaxX * atXPer) / 100
//        val catY = (cmaxY * atYPer) / 100
        val catY = cmaxY - (cmaxY * atYPer) / 100 //위도는 아래로 내려가면 갈수록 0에 가까워지기때문에 뒤집기 해줘야한다



//        canvas.drawColor(Color.WHITE)
//        val paint = Paint()
//        paint.color = Color.parseColor(color)
//        paint.strokeWidth = (5f * zoom) / 100
//        canvas.drawCircle(movePoint.x + catX, movePoint.y + catY, (5f * zoom) / 100, paint)
        var paint = Paint()
        paint.color = Color.parseColor(color)
//        paint.strokeWidth = 3f
        canvas.drawCircle(catX, catY, 4f, paint)


        val textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = 3f
//        textPaint.color = Color.parseColor(color)
        textPaint.color = Color.BLACK
        canvas.drawText((upTrain?.let { "▴" }?:run { "" })+name+(downTrain?.let { "▼" }?:run { "" }),catX-5,catY,textPaint)




        upTrain?.let {
            var paint = Paint()
            paint.color = Color.RED
            canvas.drawCircle(catX-2, catY, 1f, paint)
        }
        downTrain?.let {
            var paint = Paint()
            paint.color = Color.BLUE
            canvas.drawCircle(catX+2, catY, 1f, paint)
        }


//        canvas.drawCircle(drag.width() + catX, drag.height() - catY, (5f * zoom) / 100, paint)

//        var marginX = 0
//        var marginY = 0
//        lastDownPoint = firstDownPoint?.let{it}?:lastDownPoint
//        lastDownPoint?.let {
////            marginX = (firstDownPoint.x - Math.min(drag.left, drag.right)).toInt()
////            marginY = (firstDownPoint.y - Math.min(drag.top, drag.bottom)).toInt()
//            lastDownPoint.x = it.x
//            lastDownPoint.y = it.y
//            if(drag.left < it.x){
//                marginX = -drag.width()
//            }
//            if(drag.top < it.y){
//                marginY = -drag.height()
//            }
//            if(drag.right > it.x){
//                marginX = +drag.width()
//            }
//            if(drag.bottom > it.y){
//                marginY = +drag.height()
//            }
//        }
//        canvas.drawCircle(marginX+catX, marginY+catY, (5f * zoom) / 100, paint)
//        Log.d("onDraw", "size: w:"+canvas.width+" h:"+canvas.height+ "  --> "+lat+" "+ lng)
    }
    override fun getX() = lng //경도 그리니치 천문대로부터 서쪽으로 180도  동쪽으로 -180도
    override fun getY() = lat //위도  지구 내부가운데에서부터 북방부로 90도   남반부로 -90
}