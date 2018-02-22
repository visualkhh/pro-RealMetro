package com.example.visualkhh.myapplication.view

import android.graphics.Canvas
import android.graphics.Matrix
import com.example.visualkhh.myapplication.view.domain.MetroViewScaleMinMax


interface MetroDrawable {
    fun draw(minMax: MetroViewScaleMinMax, canvas: Canvas, metrix:  Matrix?)
    fun getX(): Float
    fun getY(): Float
}