package com.ndp.server.controller

import com.ndp.server.bean.ResponseEnvelope
import com.ndp.server.bean.TaskJson
import com.ndp.server.utils.DatabaseHandler
import com.ndp.server.utils.Miscellaneous
import com.ndp.server.utils.TaskGenerator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController {

    @PostMapping("/task")
    fun addTask(
            @RequestParam("image-name") imageName: String,
            @RequestParam("tag") tag: String,
            @RequestParam("task-name") taskName: String,
            @RequestParam("priority") priority: String,
            @RequestParam("params[]") params: Array<String>
    ): ResponseEntity<*>? {
        println("image-name: $imageName")
        println("tag: $tag")
        println("task-name: $taskName")
        println("priority: $priority")
        for (k in params.indices) {
            println("param" + k + "：" + params[k])
        }

        val imageID = DatabaseHandler.selectImageID(imageName, tag)
        if (imageID == -1)
            return ResponseEntity(ResponseEnvelope<Any?>(
                    HttpStatus.BAD_REQUEST.value(),
                    "No such image & tag"
            ), HttpStatus.BAD_REQUEST)
        val tid = DatabaseHandler.selectMaxTid() + 1
        when (imageName) {
            "dnssecure" -> TaskGenerator.dnssecure(
                    tid,
                    imageID,
                    taskName,
                    priority,
                    params
            )
            "ip-test" -> TaskGenerator.ipTest(
                    tid,
                    imageID,
                    taskName,
                    priority,
                    params
            )
            "port-scan" -> TaskGenerator.portScan(
                    tid,
                    imageID,
                    taskName,
                    priority,
                    params
            )
            "page-crawl" -> TaskGenerator.pageCrawl(
                    tid,
                    imageID,
                    taskName,
                    priority,
                    params
            )
            "url-crawl" -> TaskGenerator.urlCrawl(
                    tid,
                    imageID,
                    taskName,
                    priority,
                    params
            )
        }
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Create task successfully."
        ), HttpStatus.OK)
    }

    @GetMapping("/task/running")
    fun getRunningTask(): ResponseEntity<*>? {
        val tids = DatabaseHandler.selectRunningTopTask()
        val data = ArrayList<TaskJson>()
        for (t in tids) {
            data.add(DatabaseHandler.selectTaskInfo(t))
        }
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/task/finished")
    fun getFinishedTask(): ResponseEntity<*>? {
        val data = Miscellaneous.parseChartMap(DatabaseHandler.getFinishedTask())
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/task/unfinished")
    fun getUnfinishedTask(): ResponseEntity<*>? {
        val data = Miscellaneous.parseChartMap(DatabaseHandler.getUnfinishedTask())
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/task/available/ipTestA")
    fun getIPTestAAvailable(): ResponseEntity<*>? {
        val data = DatabaseHandler.selectIPTestAAvailable()
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/task/available/portScan")
    fun getPortScanAvailable(): ResponseEntity<*>? {
        val data = DatabaseHandler.selectPortScanAvailable()
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/task/available/dnssecure")
    fun getDnssecureAvailable(): ResponseEntity<*>? {
        val data = DatabaseHandler.selectDnssecureAvailable()
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/task/available/urlCrawl")
    fun getURLCrawlAvailable(): ResponseEntity<*>? {
        val data = DatabaseHandler.selectURLCrawlAvailable()
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/task/available/pageCrawl")
    fun getPageCrawlAvailable(): ResponseEntity<*>? {
        val data = DatabaseHandler.selectPageCrawlAvailable()
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }


}