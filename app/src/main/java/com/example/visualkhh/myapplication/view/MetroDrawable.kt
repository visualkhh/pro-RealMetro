package com.example.visualkhh.myapplication.view

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF


interface MetroDrawable {
    fun draw(minMax: MetroViewScaleMinMax, movePoint:PointF, zoom: Float=100f, canvas: Canvas)
    fun getX(): Float
    fun getY(): Float
}