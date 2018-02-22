package com.example.visualkhh.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import com.example.visualkhh.myapplication.domain.Station
import com.example.visualkhh.myapplication.view.pojo.SerializablePaint
import android.R.attr.y
import android.R.attr.x
import com.example.visualkhh.myapplication.view.util.LogUtil
import com.example.visualkhh.myapplication.view.util.PaintUtil.measureText






class StationDrawEvent(val station: Station) : DrawEventCallBack{
    override fun onDraw(canvas: Canvas, x: Float, y: Float, paint: SerializablePaint) {
        val stationSize = 3f*paint.scale

        paint.color = Color.parseColor(station.color)
        canvas.drawCircle(x, y, stationSize, paint)

        station.upTrain?.let {
            var subPaint = Paint()
            subPaint.color = Color.RED
            subPaint.setStrokeWidth(0f)
            canvas.drawCircle(x - (stationSize/2), y, (1f* paint.scale), subPaint)
        }
        station.downTrain?.let {
            var subPaint = Paint()
            subPaint.color = Color.BLUE
            subPaint.setStrokeWidth(0f)
            canvas.drawCircle(x + (stationSize/2), y, (1f* paint.scale), subPaint)
        }

        ////////////text
        val textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = 3f * paint.scale
        textPaint.color = Color.BLACK

        val width = Math.ceil(textPaint.measureText(station.name).toDouble()).toInt()
//        val width = textPaint.measureText(station.name, 0, 1)
        // use height from getTextBounds()
        val textBounds = Rect()
        textPaint.getTextBounds(station.name, 0, 1, textBounds)
        val height = textBounds.height().toFloat()
        val wh = width/2
        val hh = height/2


        LogUtil.d("draw (x:"+x+" y:"+y+") w:"+width+" h:"+height+"   wh:"+wh+" hh:"+hh)
        canvas.drawText(station.name,x-wh,y-hh,textPaint)
//        canvas.drawText(station.name,x,y,textPaint)

    }


//    fun getTextWidth(text: String, paint: Paint): Int {
//        val bounds = Rect()
//        paint.getTextBounds(text, 0, end, bounds)
//        return bounds.left + bounds.width()
//    }
//
//    fun getTextHeight(text: String, paint: Paint): Int {
//        val bounds = Rect()
//        paint.getTextBounds(text, 0, end, bounds)
//        return bounds.bottom + bounds.height()
//    }
}