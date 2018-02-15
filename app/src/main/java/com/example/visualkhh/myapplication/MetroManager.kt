package com.example.visualkhh.myapplication

import android.util.Log
import com.example.visualkhh.myapplication.domain.geometry.Location
import com.example.visualkhh.myapplication.domain.geometry.google.Geocode
import com.example.visualkhh.myapplication.domain.subway.bug_go_kr.Subway
import com.example.visualkhh.myapplication.domain.subway.SubwayId
import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import java.nio.charset.Charset
import java.util.concurrent.LinkedBlockingDeque

object  MetroManager {
    val subWayIds = listOf<SubwayId>(
        SubwayId(id = "1001", name = "1호선"),
        SubwayId(id = "1002", name = "2호선"),
        SubwayId(id = "1003", name = "3호선"),
        SubwayId(id = "1004", name = "4호선"),
        SubwayId(id = "1005", name = "5호선"),
        SubwayId(id = "1006", name = "6호선"),
        SubwayId(id = "1007", name = "7호선"),
        SubwayId(id = "1008", name = "8호선"),
        SubwayId(id = "1009", name = "9호선"),
        SubwayId(id = "1091", name = "우이신설경의전철"),
        SubwayId(id = "1075", name = "분당선"),
        SubwayId(id = "1065", name = "공항철도"),
        SubwayId(id = "1069", name = "인천1호선"),
        SubwayId(id = "1069", name = "인천1호선"),
        SubwayId(id = "1077", name = "신붕당선"),
        SubwayId(id = "1063", name = "경의중앙"),
        SubwayId(id = "1067", name = "경춘선"),
        SubwayId(id = "1071", name = "수인선"),
        SubwayId(id = "1079", name = "용인에버라인"),
        SubwayId(id = "1081", name = "의정부전철")
    )
    val subWays : LinkedHashMap<SubwayId, Subway> = LinkedHashMap()
    val locations : LinkedHashMap<SubwayId, Location> = LinkedHashMap()
    val threadSize = 2
    val queue = LinkedBlockingDeque<SubwayId>(100)
//    init{
//        subWayIds.forEach{
//            subWays.put(it, null)
//            locations.put(it, null)
//        }
//    }


    fun queuePut(id: String){
        queue.put(getSubwayId(id))
    }


    fun startTracking(){
        for (i in 1.. threadSize){
            Thread {
                while(true) {
                    var subwayId = queue.take()
                    Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to subwayId.id)).responseString(Charset.forName("EUC-KR")) { request, response, result ->
                        val (data, error) = result
                        if (null == error) {
                            val rSubway = Gson().fromJson(data, Subway::class.java)
                            subWays.put(subwayId, rSubway)
                            Log.d("Request", data.toString())

//                            if(null==locations.get(subwayId)){
//                                startLocation(subwayId, rSubway)
//                            }

                        }
                    }
                    Thread.sleep(2000)
                }
            }.start()
        }
    }

    fun startLocation(subwayId: SubwayId, subway: Subway) {
        for (it in subway.resultList){
            Thread {
                val params = listOf("address" to it.statnNm+"역")
                Fuel.get("https://maps.googleapis.com/maps/api/geocode/json", params).responseString(Charset.forName("UTF-8")) { request, response, result ->
                    val (data, error) = result
                        if (null == error) {
                            val rGeocoder = Gson().fromJson(data, Geocode::class.java)
                            locations.put(subwayId, rGeocoder.getLocation())
                            Log.d("Request", data.toString())
                        }
                }
            }.start()


            break
            Thread.sleep(1000)
        }
    }


    fun getSubwayId(id: String): SubwayId?{
        return subWayIds.find { it.id.equals(id) }
    }

}