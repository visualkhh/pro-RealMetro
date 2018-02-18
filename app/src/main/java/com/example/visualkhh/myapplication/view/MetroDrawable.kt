package com.example.visualkhh.myapplication.view

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect


interface MetroDrawable {
    fun draw(minMax: MetroViewScaleMinMax, drag:Rect, zoom: Float=100f, canvas: Canvas)
    fun getX(): Float
    fun getY(): Float
}