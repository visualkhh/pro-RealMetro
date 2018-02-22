package com.visualkhh.app.realmetro.domain.subway.bug_go_kr


//https://blog.frankel.ch/comparing-lombok-and-kotlin/
class BusGoLine {

    constructor()
    constructor(error: BusGoError = BusGoError(), resultList: List<BusGoStation> = ArrayList()) {
//        this.id = id
        this.error = error
//        this.name = name
        this.resultList = resultList
    }

//    var id: String = ""
//    var name: String = ""

    var error: BusGoError = BusGoError();

    var resultList: List<BusGoStation> = ArrayList()
}
