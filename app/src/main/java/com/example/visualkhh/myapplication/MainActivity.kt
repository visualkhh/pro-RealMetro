package com.example.visualkhh.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.visualkhh.AbstractAsyncActivity
import com.example.visualkhh.myapplication.domain.Line
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import com.example.visualkhh.myapplication.R.mipmap.ic_launcher


class MainActivity : AbstractAsyncActivity(),LineEvent {
    override fun complete(line: Line) {
        metro.invalidate()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        MetroManager.eventCallBack = this
        MetroManager.startTracking()


        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle("항목중에 하나를 선택하세요.")

        // List Adapter 생성
        val adapter = ArrayAdapter<Line>(this,android.R.layout.select_dialog_singlechoice)
        adapter.addAll(MetroManager.lineIds)

        // 버튼 생성
        alertBuilder.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }

        // Adapter 셋팅
        alertBuilder.setAdapter(adapter) { dialog, id ->
            // AlertDialog 안에 있는 AlertDialog
            val strName = adapter.getItem(id)
            MetroManager.queue.push(strName)
//            val innBuilder = AlertDialog.Builder(this)
//            innBuilder.setMessage(strName.name)
//            innBuilder.setTitle("당신이 선택한 것은 ")
//            innBuilder.setPositiveButton("확인") { dialog, which -> dialog.dismiss() }
//            innBuilder.show()
        }









        button1.setOnClickListener {
            alertBuilder.show()
            //            MetroManager.queuePut("1001")
        }
        button2.setOnClickListener {
            //            MetroManager.queuePut("1002")
//            metro.defaultSetting()
        }
        button3.setOnClickListener {
            metro.draws.clear()
            for ((k, v) in MetroManager.getStation()) {
                metro.draws.addAll(v)
                metro.invalidate()
            }
        }


    }

//    private fun refreshSubwayResults(response: Subway?) {
//        if (response == null) {
//            return
//        }
//        Log.d("subWay", response.toString())
//    }


//
//    private inner class SubwayHttpRequestTask : AsyncTask<LinkedHashMap<SubwayId, Subway?>, Void, Void>() {
//        override fun onPreExecute() {
//        }
//        override fun doInBackground(vararg params: Subway): Void {
//            var rSubway : Subway = Subway();
//            try {
//                // The URL for making the GET request
//                //https://github.com/kittinunf/Fuel
////                val uri = UriComponentsBuilder.newInstance().scheme("http").host("bus.go.kr").path("getXml2.jsp").queryParam("subwayId",params[0].id).build().toUri()
////                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).response {
////                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms").responseString();
////                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).responseObject(jacksonDeserializerOf<HttpBinUserAgentModel>()) { _, _, result ->
////                    Log.d("--","asdasd")
////                }
//
////                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).response { request, response, result ->
////                    result.fold(success = { json ->
////                        Log.d("qdp success", json.toString())
////                    }, failure = { error ->
////                        Log.e("qdp error", error.toString())
////                    })
////                    Log.d("-0-0","asdasd")
////                }
//                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms",listOf("subwayId" to params[0].id)).responseString(Charset.forName("EUC-KR")) { request, response, result ->
//                    val (d, e) = result
//                    val data = d
//                    val error = e
//
//                    val request = request
//                    val response = response
//                    val gson = Gson()
//                    rSubway = gson.fromJson(data, Subway::class.java)
//                    Log.d("Request", data.toString())
//                }
////                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).responseObject(jacksonDeserializerOf<Subway>()) { _, _, result ->
////                }
////                Fuel.get("/user-agent")
////                        .responseObject(jacksonDeserializerOf<HttpBinUserAgentModel>()) { _, _, result ->
////                            assertThat(result.component1(), instanceOf(HttpBinUserAgentModel::class.java))
////                            assertThat(result.component1()?.userAgent, not(""))
////                            assertThat(result.component2(), instanceOf(FuelError::class.java))
////                        }
//
//
////                Fuel.post("http://m.bus.go.kr/mBus/subway/getStatnByRoute.bms", listOf("subwayId" to params[0].id)).responseObject<Subway>{ _, _, result ->
////                Log.d("--","asdasd");
////            }
//
//
//            } catch (e: Exception) {
//                Log.e(AbstractAsyncActivity.TAG, e.message, e)
//            }
//            return rSubway
//        }
//        override fun onPostExecute(result :Void) {
//        }
//    }


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


    //Deserializer
//    class HttpBinHeadersDeserializer : ResponseDeserializable<Subway> {
//
//        override fun deserialize(content: String): Subway {
//            val json = JSONObject(content)
//            val headers = json.getJSONObject("headers")
//            val results = headers.keys().asSequence().associate { Pair(it, headers.getString(it)) }
//            val model = Subway()
//            model.headers = results
//            return model
//        }
//
//    }

}
