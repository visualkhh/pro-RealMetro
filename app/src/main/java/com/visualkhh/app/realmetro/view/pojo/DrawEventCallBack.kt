package com.visualkhh.app.realmetro.view.pojo

import android.graphics.Canvas
import com.visualkhh.app.realmetro.view.pojo.SerializablePaint

/**
 * Created by visualkhh on 2018. 2. 19..
 */
interface DrawEventCallBack {
    fun onDraw(canvas: Canvas, x: Float, y: Float, paint: SerializablePaint)

}