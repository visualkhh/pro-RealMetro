package com.visualkhh.app.realmetro.domain.geometry.google

data class Geocode(val status: String, val results : List<Result>) {


//    fun getLocation():Location{
//        var r: Location? = null
//        if(null!=results && results.size>0) {
//            r = Location(results.get(0).geometry.lat, results.get(0).geometry.lng)
//        }
//        return r!!
//    }
}