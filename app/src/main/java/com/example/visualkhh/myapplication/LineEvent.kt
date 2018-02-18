package com.example.visualkhh.myapplication

import com.example.visualkhh.myapplication.domain.Line

/**
 * Created by visualkhh on 2018. 2. 19..
 */
interface LineEvent {
    fun complete(line: Line)

}