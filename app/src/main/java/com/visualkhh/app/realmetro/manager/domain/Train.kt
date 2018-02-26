package com.visualkhh.app.realmetro.manager.domain

data class Train(val id: String = "", val name: String = "none", val type: TYPE = TYPE.NORMAL){
    enum class TYPE { NORMAL, EXPRESS, BROKEN }
}