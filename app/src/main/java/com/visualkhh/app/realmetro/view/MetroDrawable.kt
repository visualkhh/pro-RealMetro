package com.visualkhh.app.realmetro.view

import android.graphics.Canvas
import android.graphics.Matrix
import com.visualkhh.app.realmetro.view.domain.MetroViewScaleMinMax


interface MetroDrawable {
    fun draw(minMax: MetroViewScaleMinMax, canvas: Canvas, metrix:  Matrix?)
    fun getX(): Float
    fun getY(): Float
}