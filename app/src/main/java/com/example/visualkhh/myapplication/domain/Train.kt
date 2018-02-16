package com.example.visualkhh.myapplication.domain

data class Train(val id: String, val name: String = "none", val type: TYPE = TYPE.NORMAL){
    enum class TYPE { NORMAL, EXPRESS, BROKEN }
}