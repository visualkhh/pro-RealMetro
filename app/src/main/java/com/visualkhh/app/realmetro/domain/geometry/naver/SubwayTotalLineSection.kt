package com.visualkhh.app.realmetro.domain.geometry.naver

data class SubwayTotalLineSection(val stationCode: String,
                                  val totalLines: String,
                                  val decos: String){
    val color: String get(){
//        val regex = Regex(".*stroke=\"(.*?)\".*")
//        val regex = ".*stroke=\"(.*?)\".*".toRegex()
//        val match = regex.matchEntire(totalLines)
//        if (match != null) {
//            val (precision) = match.destructured
//            return precision
//        }

        var rval = "#555555"
        ".*stroke=\"(.*?)\".*".toRegex().matchEntire(totalLines)?.let {val (rrval)=it.destructured; rval=rrval}
        return rval

    }
}