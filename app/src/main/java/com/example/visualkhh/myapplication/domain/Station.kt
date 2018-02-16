package com.example.visualkhh.myapplication.domain

/**
 * Created by visualkhh on 2018. 2. 16..
 */
data class Station(val id: String,val lat:Double, val lng: Double,
                   var upTrain: Train? = null, var downTrain: Train? = null,
                   val name: String= "none",
                   val color: String = "#000000",
                   val type: TYPE = TYPE.NORMAL) {

    enum class TYPE { NORMAL, BROKEN }

}