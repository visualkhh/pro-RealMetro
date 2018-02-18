package com.example.visualkhh.myapplication.view

import android.graphics.Canvas
import com.example.visualkhh.myapplication.view.domain.MetroViewScaleMinMax


interface MetroDrawable {
    fun draw(minMax: MetroViewScaleMinMax, canvas: Canvas)
    fun getX(): Float
    fun getY(): Float
}