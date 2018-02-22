package com.example.visualkhh.myapplication

import com.example.visualkhh.myapplication.domain.Line
import com.example.visualkhh.myapplication.domain.Station

/**
 * Created by visualkhh on 2018. 2. 19..
 */
interface StationEvent {
    fun complete(station: Map<Line, List<Station>>, stationMinX: Float, stationMaxX: Float, stationMinY: Float, stationMaxY: Float)

}