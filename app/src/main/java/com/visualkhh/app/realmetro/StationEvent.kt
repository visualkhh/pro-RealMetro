package com.visualkhh.app.realmetro

import com.visualkhh.app.realmetro.domain.Line
import com.visualkhh.app.realmetro.domain.Station

/**
 * Created by visualkhh on 2018. 2. 19..
 */
interface StationEvent {
    fun complete(station: Map<Line, List<Station>>, stationMinX: Float, stationMaxX: Float, stationMinY: Float, stationMaxY: Float)

}