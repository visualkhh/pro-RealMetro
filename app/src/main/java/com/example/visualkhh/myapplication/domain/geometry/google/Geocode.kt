package com.example.visualkhh.myapplication.domain.geometry.google

import com.example.visualkhh.myapplication.domain.geometry.Location

data class Geocode(val status: String, val results : List<Result>) {


//    fun getLocation():Location{
//        var r: Location? = null
//        if(null!=results && results.size>0) {
//            r = Location(results.get(0).geometry.lat, results.get(0).geometry.lng)
//        }
//        return r!!
//    }
}