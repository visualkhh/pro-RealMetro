package com.example.visualkhh.myapplication.domain

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import com.example.visualkhh.myapplication.view.MetroDrawable
import com.example.visualkhh.myapplication.view.MetroViewScaleMinMax
import com.example.visualkhh.myapplication.view.MetroViewSize
import kotlin.math.ln

/**
 * Created by visualkhh on 2018. 2. 16..
 */
class Station(val id: String,val lat:Float, val lng: Float,
                   var upTrain: Train? = null, var downTrain: Train? = null,
                   val name: String= "none",
                   val color: String = "#000000",
                   val type: TYPE = TYPE.NORMAL) : MetroDrawable{
    enum class TYPE { NORMAL, BROKEN }

    override fun draw(minMax: MetroViewScaleMinMax, movePoint: PointF, canvas: Canvas) {


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
        val cminX = 0
        val cminY = 0
        val cmaxX = canvas.width
        val cmaxY = canvas.height

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
        val paint = Paint()
        paint.color = Color.parseColor(color)
        paint.strokeWidth = 5f
        canvas.drawCircle(movePoint.x + catX, movePoint.y + catY, 5f, paint)
//        Log.d("onDraw", "size: w:"+canvas.width+" h:"+canvas.height+ "  --> "+lat+" "+ lng)



    }

    override fun getX() = lng //경도 그리니치 천문대로부터 서쪽으로 180도  동쪽으로 -180도

    override fun getY() = lat //위도  지구 내부가운데에서부터 북방부로 90도   남반부로 -90
}