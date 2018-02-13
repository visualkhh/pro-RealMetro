package com.example.visualkhh.myapplication

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.example.visualkhh.AbstractAsyncActivity
import com.example.visualkhh.myapplication.domain.Subway
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import com.github.kittinunf.fuel.jackson.responseObject


class MainActivity : AbstractAsyncActivity () {


    val subWays : LinkedHashMap<String, Subway> = LinkedHashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
//        var at = Subway(); at.id="1001"; at.name="1호선";
//        subWays.put("1001", at)
        subWays.put("1001", Subway(id = "1001", name = "1호선"))
//        subWays.put("1002", Subway(id = "1002", name = "2호선"))
//        subWays.put("1003", Subway(id = "1003", name = "3호선"))
//        subWays.put("1004", Subway(id = "1004", name = "4호선"))
//        subWays.put("1005", Subway(id = "1005", name = "5호선"))
//        subWays.put("1006", Subway(id = "1006", name = "6호선"))
//        subWays.put("1007", Subway(id = "1007", name = "7호선"))
//        subWays.put("1008", Subway(id = "1008", name = "8호선"))
//        subWays.put("1009", Subway(id = "1009", name = "9호선"))
//        subWays.put("1091", Subway(id = "1091", name = "우이신설경의전철"))
//        subWays.put("1075", Subway(id = "1075", name = "분당선"))
//        subWays.put("1065", Subway(id = "1065", name = "공항철도"))
//        subWays.put("1069", Subway(id = "1069", name = "인천1호선"))
//        subWays.put("1069", Subway(id = "1069", name = "인천1호선"))
//        subWays.put("1077", Subway(id = "1077", name = "신붕당선"))
//        subWays.put("1063", Subway(id = "1063", name = "경의중앙"))
//        subWays.put("1067", Subway(id = "1067", name = "경춘선"))
//        subWays.put("1071", Subway(id = "1071", name = "수인선"))
//        subWays.put("1079", Subway(id = "1079", name = "용인에버라인"))
//        subWays.put("1081", Subway(id = "1081", name = "의정부전철"))

        subWays.forEach{entry ->
             SubwayHttpRequestTask().execute(entry.value)
            Thread.sleep(1000);

        }

//        HttpRequestTask().execute()
    }


    // ***************************************
    // Private methods
    // ***************************************
    private fun refreshSubwayResults(response: Subway?) {
        if (response == null) {
            return
        }
        Log.d("subWay", response.toString())
    }


    // ***************************************
    // Private classes
    // ***************************************
    data class HttpBinUserAgentModel(var userAgent: String = "")
    private inner class SubwayHttpRequestTask : AsyncTask<Subway, Void, Subway>() {
        override fun onPreExecute() {
            showLoadingProgressDialog()
        }
        override fun doInBackground(vararg params: Subway): Subway {
            try {
                // The URL for making the GET request
                //https://github.com/kittinunf/Fuel
//                val uri = UriComponentsBuilder.newInstance().scheme("http").host("bus.go.kr").path("getXml2.jsp").queryParam("subwayId",params[0].id).build().toUri()
//                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).response {
//                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms").responseString();
//                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).responseObject(jacksonDeserializerOf<HttpBinUserAgentModel>()) { _, _, result ->
//                    Log.d("--","asdasd")
//                }

//                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).response { request, response, result ->
//                    result.fold(success = { json ->
//                        Log.d("qdp success", json.toString())
//                    }, failure = { error ->
//                        Log.e("qdp error", error.toString())
//                    })
//                    Log.d("-0-0","asdasd")
//                }
                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).responseObject<Subway>{_,_,result->
                    Log.e("qdp error", result.toString())
                };

//                Fuel.get("/user-agent")
//                        .responseObject(jacksonDeserializerOf<HttpBinUserAgentModel>()) { _, _, result ->
//                            assertThat(result.component1(), instanceOf(HttpBinUserAgentModel::class.java))
//                            assertThat(result.component1()?.userAgent, not(""))
//                            assertThat(result.component2(), instanceOf(FuelError::class.java))
//                        }


//                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).responseObject<Subway>{ _, _, result ->
//                Log.d("--","asdasd");
//            }


            } catch (e: Exception) {
                Log.e(AbstractAsyncActivity.TAG, e.message, e)
            }
            return Subway()
        }
        override fun onPostExecute(result: Subway) {
            dismissProgressDialog()
            refreshSubwayResults(result)
        }
    }







//    private inner class HttpRequestTask : AsyncTask<Void, Void, ResponseEntity<String>>() {
//        override fun onPreExecute() {
//            showLoadingProgressDialog()
//        }
//        override fun doInBackground(vararg params: Void): ResponseEntity<String>? {
//            try {
//                // The URL for making the GET request
//                val url = "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyDVp8_CSKw7KG9K429u8RK3qNHtvv1n-DM"
//
//
//                // Set the Accept header for "application/json"
//                val requestHeaders = HttpHeaders()
//                val acceptableMediaTypes = ArrayList<MediaType>()
//                acceptableMediaTypes.add(MediaType.APPLICATION_JSON)
//                requestHeaders.accept = acceptableMediaTypes
//
//                // Populate the headers in an HttpEntity object to use for the request
//                val requestEntity = HttpEntity<Any>(requestHeaders)
//
//                // Create a new RestTemplate instance
//                val restTemplate = RestTemplate()
//                restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
//
//                // Perform the HTTP GET request
//                val response : ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.GET, requestEntity,String::class.java)
//                return response;
//            } catch (e: Exception) {
//                Log.e(AbstractAsyncActivity.TAG, e.message, e)
//            }
//            return null
//        }
//        override fun onPostExecute(result: ResponseEntity<String>) {
//            dismissProgressDialog()
//            refreshResults(result)
//        }
//    }

}
