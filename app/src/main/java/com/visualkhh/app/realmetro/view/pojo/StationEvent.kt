package com.visualkhh.app.realmetro.view.pojo

import com.visualkhh.app.realmetro.manager.domain.Line
import com.visualkhh.app.realmetro.manager.domain.Station

/**
 * Created by visualkhh on 2018. 2. 19..
 */
interface StationEvent {
    fun complete(station: Map<Line, List<Station>>, stationMinX: Float, stationMaxX: Float, stationMinY: Float, stationMaxY: Float)

}