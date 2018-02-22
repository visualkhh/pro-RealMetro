package com.visualkhh.app.realmetro

import org.junit.Test



class RegexKotlinTest {

    @Test
    fun test() {
        var str = "<path d=\"M3461.258,2153.622h-601.861cvarchar(255)-54.313,0-99.11,40.732-105.514,93.315c-0.518,4.256-0.785,8.589-0.785,12.984l0,0   c0,34.404-24.591,63.101-57.124,69.54c-4.477,0.886-9.104,1.351-13.838,1.351h-318.397c-58.424,0-106.299-49.483-106.299-110.524   l-0.045-846.107c-0.267-58.976-47.859-106.567-106.299-106.298l-106.328-0.001c-39.139,0-70.866-31.728-70.866-70.866   l-0.023-425.197c0-58.708-47.592-106.299-111.299-106.299H423.937c-51.779,0.202-100.779-49.298-101.008-106.299\" fill=\"none\" stroke=\"#FF7300\" stroke-miterlimit=\"10\" stroke-width=\"12\"/>"
        val mydata = "some string with 'the data i want' inside"
//        val pattern = "stroke\\=\"(.*?)\"".toPattern()
        val pattern = "path(......)".toPattern()
        val matcher = pattern.matcher(mydata)
        if (matcher.find()) {
            println(matcher.group(1))
        }
        println(str)



        val regex = Regex(".*stroke=\"(.*?)\".*")
//        val regex = Regex("\\#(......)")
//        val string = "varchar(255)"
//        str = "show varchar(255)dfgfsdg"
//        val regex = Regex(".*varchar\\((\\d+)\\).*")
//        val regex = Regex(".*varchar\\((.+)\\).*")

        val match = regex.matchEntire(str)
        if (match != null) {
            val (precision) = match.destructured
            println(precision)
        } else {
            println("not a varchar")
        }
//        val regex = Regex("varchar\\((\\d+)\\)")
//        val string = "varchar(255)"
//
//        val match = regex.matchEntire(string)
//        if (match != null) {
//            val (precision) = match.destructured
//            println(precision)
//        } else {
//            println("not a varchar")
//        }
    }
}

