package com.visualkhh.app.realmetro.manager

import android.util.Log
import com.visualkhh.app.realmetro.manager.domain.geometry.naver.NaverSubwayProvider
import com.visualkhh.app.realmetro.manager.domain.bug_go_kr.BusGoLine
import com.visualkhh.app.realmetro.manager.domain.Line
import com.visualkhh.app.realmetro.manager.domain.Station
import com.visualkhh.app.realmetro.manager.domain.Train
import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.visualkhh.app.realmetro.view.pojo.StationEvent
import java.nio.charset.Charset
import java.util.concurrent.LinkedBlockingDeque

object  MetroManager {
    val lineIds = listOf<Line>(
            Line(id = "1001", name = "1호선"),
            Line(id = "1002", name = "2호선"),
            Line(id = "1003", name = "3호선"),
            Line(id = "1004", name = "4호선"),
            Line(id = "1005", name = "5호선"),
            Line(id = "1006", name = "6호선"),
            Line(id = "1007", name = "7호선"),
            Line(id = "1008", name = "8호선"),
            Line(id = "1009", name = "9호선"),
            Line(id = "1067", name = "경춘선"),
            Line(id = "1063", name = "경의중앙"),
            Line(id = "1091", name = "우이신설경의전철"),
            Line(id = "1075", name = "분당선"),
            Line(id = "1065", name = "공항철도"),
            Line(id = "1069", name = "인천1호선"),
            Line(id = "1069", name = "인천2호선"),
            Line(id = "1077", name = "신붕당선"),
            Line(id = "1071", name = "수인선"),
            Line(id = "1079", name = "용인에버라인"),
            Line(id = "1081", name = "의정부전철")
    )

    val lines: LinkedHashMap<Line, List<Station>> = LinkedHashMap()
//    var minLat: Float = Float.MAX_VALUE
//    var maxLat: Float = Float.MIN_VALUE
//    var minLng: Float = Float.MAX_VALUE
//    var maxLng: Float = Float.MIN_VALUE

    val busGolines: LinkedHashMap<Line, BusGoLine> = LinkedHashMap()
    var subwayProvider : NaverSubwayProvider? = null
    val threadSize = 2
    val queue = LinkedBlockingDeque<Line>(100)
    var stationCallBack: StationEvent? = null



    var minLat = Float.MAX_VALUE
    var maxLat = Float.MIN_VALUE
    var minLng = Float.MAX_VALUE
    var maxLng = Float.MIN_VALUE

    fun queuePut(id: String){
        queue.put(getSubwayId(id))
    }


    fun startTracking(){
        for (i in 1..threadSize){
            Thread {
                while(true) {
                    //blocking
                    var subwayId = queue.take()
                    Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to subwayId.id)).responseString(Charset.forName("EUC-KR")) { request, response, result ->
                        val (data, error) = result
                        if (null == error) {
                            val r = Gson().fromJson(data, BusGoLine::class.java)
                            busGolines.put(subwayId, r)
                            Log.d("Request", data.toString())


                            val stations = lines.filter { it.key.name.equals(subwayId.name) }.flatMap { it.value }
                            val unStations = lines.filter { !it.key.name.equals(subwayId.name) }.flatMap { it.value }
                            stations.forEach{
                                it.type= Station.TYPE.NORMAL
                                it.upTrain = null
                                it.downTrain = null
                            }
                            unStations.forEach{
                                it.type= Station.TYPE.HIDDEN
                                it.upTrain = null
                                it.downTrain = null
                            }


                            ///////////
                            var minLat = Float.MAX_VALUE
                            var maxLat = Float.MIN_VALUE
                            var minLng = Float.MAX_VALUE
                            var maxLng = Float.MIN_VALUE
                            val rrr: LinkedHashMap<Line, List<Station>> = LinkedHashMap()
                            val rs = ArrayList<Station>()
                            r.resultList.forEach { train ->
                                stations.filter { it.name.equals(train.statnNm+"역") }.forEach{
                                    if("Y".equals(train.existYn1)) it.upTrain = Train() else it.upTrain = null
                                    if("Y".equals(train.existYn2)) it.downTrain = Train() else it.downTrain = null
                                    rs.add(it)
                                    minLat = Math.min(it.lat, minLat)
                                    maxLat = Math.max(it.lat, maxLat)
                                    minLng = Math.min(it.lng, minLng)
                                    maxLng = Math.max(it.lng, maxLng)
                                }
                            }

                            rrr.put(subwayId,rs)
                            stationCallBack?.complete(rrr, minLng, maxLng, minLat, maxLat)


                        }
                    }
                    Thread.sleep(2000)
                }
            }.start()
        }
    }

    fun reloadStation(){
        Fuel.get("https://map.naver.com/external/SubwayProvide.xml?requestFile=metaData.json&readPath=1000&version=5.4").responseString(Charset.forName("UTF-8")) { request, response, result ->
            val (data, error) = result
                if (null == error) {
                    Log.d("Request", data.toString())
                    var personList: List<NaverSubwayProvider> = Gson().fromJson(data, object : TypeToken<List<NaverSubwayProvider>>() {}.type)
                    subwayProvider = personList.get(0)
                    /////////parsing

                    subwayProvider!!.subwayTotalLineSection.forEach { lineIt ->
                        val line = Line(lineIt.stationCode, color = lineIt.color)
                        val stations = ArrayList<Station>()
                        val list = subwayProvider!!.realInfo.filter { line.id==it.logicalLine.code }.forEach { stationIt ->
                            val station = Station(stationIt.id, stationIt.latitude.toFloat(), stationIt.longitude.toFloat(), name = stationIt.name, color = lineIt.color)
                            line.name = stationIt.logicalLine.name
                            minLat = Math.min(station.lat, minLat)
                            maxLat = Math.max(station.lat, maxLat)
                            minLng = Math.min(station.lng, minLng)
                            maxLng = Math.max(station.lng, maxLng)
//                            if("2".equals(stationIt.logicalLine.code))
                            stations.add(station)
                        }

//                        stations.sortWith(compareBy ({ it.lat }, {it.lng}))
//                        stations.sortWith(compareBy (Station::lat, Station::lng))
                        lines.put(line, stations)
                    }

                    stationCallBack?.complete(lines, minLng, maxLng, minLat, maxLat)
                }
        }
    }





//    fun startLocationGoogle(subwayId: SubwayId, subway: Subway) {
//        for (it in subway.resultList){
//            Thread {
//                val params = listOf("address" to it.statnNm+"역")
//                Fuel.get("https://maps.googleapis.com/maps/api/geocode/json", params).responseString(Charset.forName("UTF-8")) { request, response, result ->
//                    val (data, error) = result
//                        if (null == error) {
//                            val rGeocoder = Gson().fromJson(data, Geocode::class.java)
//                            locations.put(subwayId, rGeocoder.getLocation())
//                            Log.d("Request", data.toString())
//                        }
//                }
//            }.start()
//
//
//            break
//            Thread.sleep(1000)
//        }
//    }


    fun getSubwayId(id: String): Line?{
        return lineIds.find { it.id.equals(id) }
    }
    fun getSubway(id: String): BusGoLine? {
        return busGolines.get(lineIds.find { it.id.equals(id) })
    }

//    fun draw(subwayIdStr: String) :List<Location>{
//        val subway = getSubway(subwayIdStr)!!
//
//        var r = ArrayList<Location>()
//
//        for(i in subway.resultList){
//
//            subwayProvider!!.realInfo.find { it.name.startsWith(i.statnNm) }.let {
//                Log.d("ww", "it "+it!!.name+" "+it!!.latitude+"      "+it!!.longitude)
//                r.add(Location(it!!.name, it!!.latitude.toDouble(), it!!.longitude.toDouble()))
//            }
//        }
//        return r
//    }

}