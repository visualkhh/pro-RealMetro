package com.example.visualkhh.myapplication.domain


//https://blog.frankel.ch/comparing-lombok-and-kotlin/
class Subway {

    constructor()
    constructor(error: Error = Error(), id: String ="", name: String ="", resultList: List<Station> = ArrayList()) {
        this.id = id
        this.error = error
        this.name = name
        this.resultList = resultList
    }

    var id: String = ""
    var name: String = ""

    var error: Error = Error();

    var resultList: List<Station> = ArrayList()
}
