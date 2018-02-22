package com.visualkhh.app.realmetro.view.domain

data class MetroViewScaleMinMax(var minY: Float = Float.MAX_VALUE,
                                var maxY: Float = Float.MIN_VALUE,
                                var minX: Float = Float.MAX_VALUE,
                                var maxX: Float = Float.MIN_VALUE) {
}