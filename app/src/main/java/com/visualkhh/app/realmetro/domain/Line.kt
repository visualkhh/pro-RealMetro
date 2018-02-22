package com.visualkhh.app.realmetro.domain

data class Line(val id: String, var name: String = "none", val color: String = "#333333"){
    override fun toString(): String {
        return "$name ($id)"
    }
}