package com.ndp.server.utils

import com.ndp.server.bean.*
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.schema.Column
import me.liuwj.ktorm.schema.Table
import org.springframework.stereotype.Component

@Component
object DatabaseHandler {

    private val dbUrl = Settings.setting["dbUrl"] as String
    private val dbDriver = Settings.setting["dbDriver"] as String
    private val dbUser = Settings.setting["dbUser"] as String
    private val dbPassword = Settings.setting["dbPassword"] as String

    init {
        Database.Companion.connect(
                dbUrl,
                dbDriver,
                dbUser,
                dbPassword
        )
    }

    fun insertImage(
            imageName: String,
            tag: String,
            size: String,
            uploadTime: String,
            fileName: String
    ) {
        Image.insert {
            it.imageName to imageName
            it.tag to tag
            it.size to size
            it.uploadTime to uploadTime
            it.fileName to fileName
        }
    }

    fun selectImage(): List<String> {
        return Image.selectDistinct(Image.imageName)
                .map { it[Image.imageName]!! }
                .toCollection(ArrayList())
    }

    fun selectTags(imageName: String): List<ImageJson> {
        return Image.select(Image.tag, Image.uploadTime, Image.size)
                .where { Image.imageName eq imageName }
                .map {
                    ImageJson(
                            it[Image.imageName]!!,
                            it[Image.tag]!!,
                            it[Image.uploadTime]!!,
                            it[Image.size]!!
                    )
                }
    }

    fun selectImageID(imageName: String, tag: String): Int {
        val id = Image.select(Image.id)
                .where {
                    (Image.imageName eq imageName) and
                            (Image.tag eq tag)
                }
                .map { it[Image.id] ?: 0 }
        return if (id.isNotEmpty())
            id[0]
        else
            -1
    }

    fun selectMaxTid(): Int {
        val tid = Task.select(max(Task.tid))
                .map { it[Task.tid] ?: 0 }
        return if (tid.isNotEmpty()) tid[0] else 1
    }

    fun selectMaxID(): Int {
        val id = Task.select(max(Task.id))
                .map { it[Task.id] ?: 0 }
        return if (id.isNotEmpty()) id[0] else 1
    }

    private fun batchInsertTasks(
            tid: Int,
            taskName: String,
            imageID: Int,
            priority: Int,
            param: List<String>
    ) {
        Task.batchInsert {
            for (p in param) {
                item {
                    it.tid to tid
                    it.taskName to taskName
                    it.imageID to imageID
                    it.param to p
                    it.priority to priority
                }
            }
        }
    }

    fun batchInsertDnssecureTasks(
            tid: Int,
            taskName: String,
            imageID: Int,
            priority: Int,
            domains: List<String>,
            reServers: List<String>
    ) {
        Task.batchInsert {
            for (d in domains) {
                for (r in reServers) {
                    val param = "$tid,$d,$r,$taskName,uuid"
                    item {
                        it.tid to tid
                        it.taskName to taskName
                        it.imageID to imageID
                        it.param to param
                        it.priority to priority
                    }
                }
            }
        }
    }

    fun batchInsertIPTestTasks(
            tid: Int,
            taskName: String,
            imageID: Int,
            priority: Int,
            ips: List<String>
    ) {
        batchInsertTasks(
                tid,
                taskName,
                imageID,
                priority,
                ips
        )
    }

    fun batchInsertPortScanTasks(
            tid: Int,
            taskName: String,
            imageID: Int,
            priority: Int,
            ips: List<String>,
            ports: List<String>
    ) {
        Task.batchInsert {
            for (p in ports) {
                for (i in ips) {
                    val param = "$i;$p"
                    item {
                        it.tid to tid
                        it.taskName to taskName
                        it.imageID to imageID
                        it.param to param
                        it.priority to priority
                    }
                }
            }
        }
    }

    fun batchInsertPageCrawlTasks(
            tid: Int,
            taskName: String,
            imageID: Int,
            priority: Int,
            urls: List<String>
    ) {
        batchInsertTasks(
                tid,
                taskName,
                imageID,
                priority,
                urls
        )
    }

    fun batchInsertURLCrawlTasks(
            tid: Int,
            taskName: String,
            imageID: Int,
            priority: Int,
            urls: List<String>
    ) {
        batchInsertTasks(
                tid,
                taskName,
                imageID,
                priority,
                urls
        )
    }

    private fun getData(ipRange: String, column: Column<*>, port: Int, service: String): Map<String, Int> {
        var ipStart = 0L
        var ipEnd = 0L
        if (ipRange != "") {
            when {
                ipRange.contains("/") -> {
                    val set = ipRange.split("/")
                    ipStart = TaskGenerator.iNetString2Number(set[0])
                    ipEnd = TaskGenerator.parseIPEnd(ipStart, Integer.parseInt(set[1]))
                }
                ipRange.contains("-") -> {
                    val set = ipRange.split("-")
                    ipStart = TaskGenerator.iNetString2Number(set[0])
                    ipEnd = TaskGenerator.iNetString2Number(set[1])
                }
                else -> {
                    ipStart = TaskGenerator.iNetString2Number(ipRange)
                }
            }
        }
        val map = HashMap<String, Int>()
        val count = count(Port.id).aliased("c")

        Port.select(column, count)
                .whereWithConditions {
                    if (ipStart != 0L) {
                        it += Port.ipID greaterEq ipStart
                        it += Port.ipID lessEq ipEnd
                    } else {
                        it += Port.ipID eq ipStart
                    }
                    if (port != 0) {
                        it += Port.port eq port
                    }
                    if (service != "") {
                        it += Port.service eq service
                    }
                }
                .groupBy(Port.port)
                .orderBy(count.desc())
                .limit(0, 8)
                .forEach {
                    map[it[Port.port]!!.toString()] = it[count]!!
                }
        return map
    }

    fun getPortData(ipRange: String = "", service: String = ""): Map<String, Int> {
        return getData(ipRange, Port.port, 0, service)
    }

    fun getServiceData(ipRange: String = "", port: Int = 0): Map<String, Int> {
        return getData(ipRange, Port.service, port, "")
    }

    fun selectRunningTopTask(): List<Int> {
        val tids = ArrayList<Int>()
        Task.selectDistinct(Task.tid)
                .whereWithOrConditions {
                    it += Task.taskStatus eq 20010
                    it += Task.taskStatus eq 20020
                }
                .forEach { tids.add(it[Task.tid]!!) }
        return tids
    }

    fun selectImageInfoByID(imageID: Int): ImageJson {
        return Image.select(Image.imageName, Image.tag)
                .where { Image.id eq imageID }
                .limit(0, 1)
                .map {
                    ImageJson(
                            it[Image.imageName]!!,
                            it[Image.tag]!!,
                            "",
                            ""
                    )
                }
                .toList()[0]
    }

    fun selectTaskInfo(tid: Int): TaskJson {
        val imageID = Task.select(Task.imageID)
                .where { Task.tid eq tid }
                .limit(0, 1)
                .map { it[Task.imageID]!! }
                .toList()[0]
        val imageInfo = selectImageInfoByID(imageID)

        val taskName = Task.select(Task.taskName)
                .where { Task.tid eq tid }
                .limit(0, 1)
                .map { it[Task.taskName]!! }
                .toList()[0]

        val count = count(Task.id).aliased("c")
        val total = Task.select(count)
                .where { Task.tid eq tid }
                .map { it[count]!! }
                .toList()[0]
        val waiting = Task.select(count)
                .whereWithConditions {
                    it += Task.tid eq tid
                    it += Task.taskStatus eq 20000
                }
                .map { it[count]!! }
                .toList()[0]
        return TaskJson(
                tid,
                taskName,
                imageInfo.imageName,
                imageInfo.tag,
                total,
                total - waiting
        )
    }

    fun getTaskGroupByImage(isFinished: Boolean): Map<String, Int> {
        val idMap = HashMap<Int, Int>()
        val count = count(Task.id).aliased("c")
        Task.select(Task.imageID, count)
                .where {
                    if (isFinished)
                        Task.taskStatus notInList arrayListOf(20000, 20010, 20020)
                    else
                        Task.taskStatus inList arrayListOf(20000, 20010, 20020)
                }
                .groupBy(Task.imageID)
                .orderBy(count.desc())
                .forEach { idMap[it[Task.imageID]!!] = it[count]!! }
        val map = HashMap<String, Int>()
        for (k in idMap.keys) {
            val imageName = selectImageInfoByID(k).imageName
            map[imageName] = map.getOrDefault(imageName, 0) + idMap[k]!!
        }
        return map
    }

    fun getFinishedTask(): Map<String, Int> {
        return getTaskGroupByImage(true)
    }

    fun getUnfinishedTask(): Map<String, Int> {
        return getTaskGroupByImage(false)
    }

    fun selectAvailable(
            table: Table<Nothing>, idColumn: Column<Int>, column: Column<Int>
    ): Map<Int, Int> {
        val count = count(idColumn).aliased("c")
        val map = HashMap<Int, Int>()
        table.select(column, count)
                .groupBy(column)
                .orderBy(count.desc())
                .forEach { map[it[column]!!] = it[count]!! }
        return map
    }

    fun selectIPTestAAvailable(): AvailableJson {
        val map = selectAvailable(DomainIP, DomainIP.id, DomainIP.ipTestFlag)
        val available = map.getOrDefault(0, 0)
        val total = map.getOrDefault(1, 0) + available
        return AvailableJson(total, available)
    }

    fun selectPortScanAvailable(): AvailableJson {
        val map = selectAvailable(IP, IP.id, IP.portScanFlag)
        val available = map.getOrDefault(0, 0)
        val total = map.getOrDefault(1, 0) + available
        return AvailableJson(total, available)
    }

    fun selectDnssecureAvailable(): AvailableJson {
        val map = selectAvailable(Page, Page.id, Page.dnssecureFlag)
        val available = map.getOrDefault(0, 0)
        val total = map.getOrDefault(1, 0) + available
        return AvailableJson(total, available)
    }

    fun selectURLCrawlAvailable(): AvailableJson {
        val map = selectAvailable(Page, Page.id, Page.urlFlag)
        val available = map.getOrDefault(0, 0)
        val total = map.getOrDefault(1, 0) + available
        return AvailableJson(total, available)
    }

    fun selectPageCrawlAvailable(): AvailableJson {
        val map = selectAvailable(Page, Page.id, Page.pageFlag)
        val available = map.getOrDefault(0, 0)
        val total = map.getOrDefault(1, 0) + available
        return AvailableJson(total, available)
    }

    fun selectNewImageID(imageName: String): Int {
        return Image.select(Image.id)
                .where { Image.imageName eq imageName }
                .orderBy(Image.id.desc())
                .map { it[Image.id]!! }
                .toList()[0]
    }

    // TODO FLAG
    fun getIPByCountry(country: String, offset: Int, limit: Int): List<String> {
        return Block
                .select(Block.network, Block.geoNameID)
                .where {
                    Block.geoNameID inList
                            Location.select(Location.geoNameID).where { Location.countryISOCode eq country }
                }
                .limit(offset, limit)
                .map {
                    it[Block.network]!!
                }
                .toCollection(ArrayList())
    }

    fun getIPByPortScanFlag(limit: Int): List<String> {
        val intIPSet = ArrayList<Int>()
        val strIPSet = ArrayList<String>()
        IP.select(IP.id, IP.ip)
                .where { IP.portScanFlag eq 0 }
                .limit(0, limit)
                .forEach {
                    intIPSet.add(it[IP.id]!!)
                    strIPSet.add(it[IP.ip]!!)
                }
        IP.batchUpdate {
            for (i in intIPSet) {
                item {
                    it.portScanFlag to 1
                    where { it.id eq i }
                }
            }
        }
        return strIPSet
    }

    fun getIPByDomainIP(limit: Int): List<String> {
        val id = ArrayList<Int>()
        val ips = ArrayList<String>()
        DomainIP.select(DomainIP.id, DomainIP.ipID)
                .where { DomainIP.ipTestFlag eq 0 }
                .limit(0, limit)
                .onEach { id.add(it[DomainIP.id]!!) }
                .map { TaskGenerator.iNetNumber2String(it[DomainIP.ipID]!!) + "/24" }
                .forEach { ips.add(it) }
        DomainIP.batchUpdate {
            for (i in id) {
                item {
                    DomainIP.ipTestFlag to 1
                    where { it.id eq i }
                }
            }
        }
        return ips
    }

    private fun getUrl(flag: Column<Int>, limit: Int): List<String> {
        val urlID = ArrayList<Int>()
        val urls = ArrayList<String>()
        Page.select(Page.id, Page.domain)
                .where { flag eq 0 }
                .limit(0, limit)
                .forEach {
                    urlID.add(it[Page.id]!!)
                    urls.add(it[Page.domain]!!)
                }
        Page.batchUpdate {
            for (i in urlID) {
                item {
                    flag to 1
                    where { it.id eq i }
                }
            }
        }
        return urls
    }

    fun getUrlByUrlFlag(limit: Int): List<String> {
        return getUrl(Page.urlFlag, limit)
    }

    fun getUrlByPageFlag(limit: Int): List<String> {
        return getUrl(Page.pageFlag, limit)
    }

    fun getUrlByDnssecureFlag(limit: Int): List<String> {
        return getUrl(Page.dnssecureFlag, limit)
    }
}