package com.example.visualkhh.myapplication

import android.graphics.Canvas
import com.example.visualkhh.myapplication.domain.Line
import com.example.visualkhh.myapplication.view.pojo.SerializablePaint

/**
 * Created by visualkhh on 2018. 2. 19..
 */
interface DrawEventCallBack {
    fun onDraw(canvas: Canvas, x: Float, y: Float, paint: SerializablePaint)

}