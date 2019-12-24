package com.ndp.server.controller

import com.ndp.server.bean.Chart
import com.ndp.server.bean.ChartsJson
import com.ndp.server.bean.ResponseEnvelope
import com.ndp.server.utils.DatabaseHandler
import com.ndp.server.utils.Miscellaneous
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class StatisticController {

    @GetMapping("/statistic/charts")
    fun getCharts(): ResponseEntity<*>? {

        val portChart = Miscellaneous.parseChartMap(DatabaseHandler.getPortData())
        val serviceChart = Miscellaneous.parseChartMap(DatabaseHandler.getServiceData())

        val data = ChartsJson(portChart, serviceChart)
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @PostMapping("/statistic/search")
    fun getSearchCharts(
            @RequestParam("type") type: String,
            @RequestParam("keyword") keyword: String
    ): ResponseEntity<*>? {
        when (type) {
            "port" -> {
                val data = Miscellaneous.parseChartMap(DatabaseHandler.getServiceData(port = Integer.parseInt(keyword)))
                return ResponseEntity(ResponseEnvelope(
                        HttpStatus.OK.value(),
                        "OK",
                        data
                ), HttpStatus.OK)
            }
            "service" -> {
                val data = Miscellaneous.parseChartMap(DatabaseHandler.getPortData(service = keyword))
                return ResponseEntity(ResponseEnvelope(
                        HttpStatus.OK.value(),
                        "OK",
                        data
                ), HttpStatus.OK)
            }
            "ip" -> {
                val portChart = Miscellaneous.parseChartMap(DatabaseHandler.getPortData(ipRange = keyword))
                val serviceChart = Miscellaneous.parseChartMap(DatabaseHandler.getServiceData(ipRange = keyword))

                val data = ChartsJson(portChart, serviceChart)
                return ResponseEntity(ResponseEnvelope(
                        HttpStatus.OK.value(),
                        "OK",
                        data
                ), HttpStatus.OK)
            }
            else -> return ResponseEntity(ResponseEnvelope<Any?>(
                    HttpStatus.BAD_REQUEST.value(),
                    "No type was specified"
            ), HttpStatus.BAD_REQUEST)
        }
    }
}