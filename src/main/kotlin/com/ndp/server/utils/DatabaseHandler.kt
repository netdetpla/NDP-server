package com.ndp.server.utils

import com.ndp.server.bean.*
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.schema.Column

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
        return Image.select(Image.imageName)
                .distinct()
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
        val tid = TopTask.select(max(TopTask.tid))
                .map { it[TopTask.tid] ?: 0 }
        return if (tid.isNotEmpty()) tid[0] else 1
    }

    fun selectMaxID(): Int {
        val id = Task.select(max(Task.id))
                .map { it[Task.id] ?: 0 }
        return if (id.isNotEmpty()) id[0] else 1
    }

    fun insertTopTask(
            imageID: Int,
            startID: Int,
            endID: Int
    ) {
        TopTask.insert {
            it.imageID to imageID
            it.startID to startID
            it.endID to endID
        }
    }

    fun batchInsertTasks(
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