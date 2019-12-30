package com.ndp.server.controller

import com.ndp.server.bean.ResponseEnvelope
import com.ndp.server.bean.TaskJson
import com.ndp.server.utils.DatabaseHandler
import com.ndp.server.utils.Miscellaneous
import com.ndp.server.utils.Settings
import com.ndp.server.utils.TaskGenerator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class TaskController {
    private val ipCount = Integer.parseInt(Settings.setting["ipCount"] as String)
    private val country = Settings.setting["ipCountry"] as String
    private val ports = Settings.setting["portScanPort"] as String
    private val dnsServer = Settings.setting["dnsServer"] as String

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

    @PostMapping("/task/batchAdd/ipTestGeo")
    fun batchAddIPTestGeoTask(
            @RequestParam("num") num: String
    ): ResponseEntity<*>? {
        val limit = Integer.parseInt(num)
        val target = DatabaseHandler.getIPByCountry(country, ipCount, limit)
        Settings.setting["ipCount"] = (ipCount + limit).toString()
        Settings.save()
        val imageID = DatabaseHandler.selectNewImageID("ip-test")
        val tid = DatabaseHandler.selectMaxTid()
        for (t in target) {
            TaskGenerator.ipTest(
                    tid,
                    imageID,
                    "batch-add-ip-test-geo",
                    "5",
                    arrayOf(t)
            )
        }
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Create task successfully."
        ), HttpStatus.OK)
    }

    @PostMapping("/task/batchAdd/ipTestA")
    fun batchAddIPTestATask(
            @RequestParam("num") num: String
    ): ResponseEntity<*>? {
        val limit = Integer.parseInt(num)
        val target = DatabaseHandler.getIPByDomainIP(limit)
        val imageID = DatabaseHandler.selectNewImageID("ip-test")
        val tid = DatabaseHandler.selectMaxTid()
        for (t in target) {
            TaskGenerator.ipTest(
                    tid,
                    imageID,
                    "batch-add-ip-test-a",
                    "5",
                    arrayOf(t)
            )
        }
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Create task successfully."
        ), HttpStatus.OK)
    }

    @PostMapping("/task/batchAdd/portScan")
    fun batchAddPortScanTask(
            @RequestParam("num") num: String
    ): ResponseEntity<*>? {
        val limit = Integer.parseInt(num)
        val ips = DatabaseHandler.getIPByPortScanFlag(limit).joinToString(",")
        val imageID = DatabaseHandler.selectNewImageID("port-scan")
        val tid = DatabaseHandler.selectMaxTid()
        TaskGenerator.portScan(
                tid,
                imageID,
                "batch-add-port-scan",
                "5",
                arrayOf(ips, ports)
        )
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Create task successfully."
        ), HttpStatus.OK)
    }

    @PostMapping("/task/batchAdd/dnssecure")
    fun batchAddDnssecureTask(
            @RequestParam("num") num: String
    ): ResponseEntity<*>? {
        val limit = Integer.parseInt(num)
        val urls = DatabaseHandler.getUrlByDnssecureFlag(limit)
        val imageID = DatabaseHandler.selectNewImageID("dnssecure")
        val tid = DatabaseHandler.selectMaxTid()
        TaskGenerator.dnssecure(
                tid,
                imageID,
                "batch-add-dnssecure",
                "5",
                arrayOf(urls.joinToString("+"), dnsServer)
        )
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Create task successfully."
        ), HttpStatus.OK)
    }

    @PostMapping("/task/batchAdd/urlCrawl")
    fun batchAddURLCrawlTask(
            @RequestParam("num") num: String
    ): ResponseEntity<*>? {
        val limit = Integer.parseInt(num)
        val urls = DatabaseHandler.getUrlByUrlFlag(limit).joinToString(",")
        val imageID = DatabaseHandler.selectNewImageID("url-crawl")
        val tid = DatabaseHandler.selectMaxTid()
        TaskGenerator.urlCrawl(
                tid,
                imageID,
                "batch-add-url-crawl",
                "5",
                arrayOf(urls)
        )
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Create task successfully."
        ), HttpStatus.OK)
    }

    @PostMapping("/task/batchAdd/pageCrawl")
    fun batchAddPageCrawlTask(
            @RequestParam("num") num: String
    ): ResponseEntity<*>? {
        val limit = Integer.parseInt(num)
        val urls = DatabaseHandler.getUrlByUrlFlag(limit).joinToString(",")
        val imageID = DatabaseHandler.selectNewImageID("page-crawl")
        val tid = DatabaseHandler.selectMaxTid()
        TaskGenerator.pageCrawl(
                tid,
                imageID,
                "batch-add-page-crawl",
                "5",
                arrayOf(urls)
        )
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Create task successfully."
        ), HttpStatus.OK)
    }
}