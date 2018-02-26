package com.visualkhh.app.realmetro.manager.domain.bug_go_kr

class BusGoError {
    constructor(errorCode: String = "", errorMessage: String = "") {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
    }

    var errorCode: String = ""
    var errorMessage: String = ""
}