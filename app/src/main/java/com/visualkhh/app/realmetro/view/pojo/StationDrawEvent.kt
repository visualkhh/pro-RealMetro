package com.visualkhh.app.realmetro.view.pojo

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import com.visualkhh.app.realmetro.manager.domain.Station


class StationDrawEvent(val station: Station) : DrawEventCallBack {
    val stationCircleSize = 12f
    val trainSize = 5f
    val stationTextSize = 5f
    override fun onDraw(canvas: Canvas, x: Float, y: Float, paint: SerializablePaint) {

        val scale = paint.scale

        val dpiStationCircleSize = pxToDp(stationCircleSize * scale)

//        paint.color = Color.parseColor(station.color)

        var stationPaint = Paint()
        stationPaint.color = Color.parseColor(station.color)
        stationPaint.style = Paint.Style.FILL
        stationPaint.strokeJoin = Paint.Join.ROUND
        stationPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawCircle(x, y, dpiStationCircleSize, stationPaint)

        station.upTrain?.let {
            var subPaint = Paint()
            subPaint.color = Color.RED
            subPaint.setStrokeWidth(0f)
            canvas.drawCircle(x + (dpiStationCircleSize / 2), y,  pxToDp(trainSize * scale), subPaint)
        }
        station.downTrain?.let {
            var subPaint = Paint()
            subPaint.color = Color.BLUE
            subPaint.setStrokeWidth(0f)
            canvas.drawCircle(x - (dpiStationCircleSize / 2), y,  pxToDp(trainSize * scale), subPaint)
        }

        ////////////text
        val textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = pxToDp(stationTextSize * paint.scale)
        textPaint.color = Color.BLACK
        val (wh,hh) = textToWidthHeigt(station.name, textPaint)
        canvas.drawText(station.name, x-wh, y-hh, textPaint)

    }

    fun textToWidthHeigt(text: String, paint: Paint): Pair<Int, Float> {
        val width = Math.ceil(paint.measureText(station.name).toDouble()).toInt()
        val textBounds = Rect()
        paint.getTextBounds(station.name, 0, 1, textBounds)
        val height = textBounds.height().toFloat()
        return Pair(width/2, height/2)
    }

    fun dpToPx(dp: Float): Float {
        return (dp * Resources.getSystem().getDisplayMetrics().density)

    }

    fun pxToDp(px: Float): Float {
        return (px / Resources.getSystem().getDisplayMetrics().density)
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