package com.example.visualkhh.myapplication.domain

class Error {
    constructor(errorCode: String = "", errorMessage: String = "") {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
    }

    var errorCode: String = ""
    var errorMessage: String = ""
}