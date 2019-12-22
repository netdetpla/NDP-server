package com.ndp.server.utils

import java.util.*
import java.util.stream.Collectors.toList
import java.util.stream.Stream
import kotlin.collections.ArrayList

object TaskGenerator {

    fun iNetString2Number(ipStr: String): Long {
        return Arrays.stream(ipStr.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .map { java.lang.Long.parseLong(it) }
                .reduce(0L) { x, y -> (x!! shl 8) + y!! }
    }

    fun parseIPEnd(ipStart: Long, mask: Int): Long {
        val endMask = ((1 shl 32 - mask) - 1).toLong()
        return ipStart or endMask
    }

    fun iNetNumber2String(ipLong: Long): String {
        var origin = ipLong
        val segments = java.util.ArrayList<String>()
        for (i in 0..3) {
            segments.add((origin % 256L).toString())
            origin /= 256
        }
        segments.reverse()
        return segments.joinToString(".")
    }

    fun ipMask2ipArray(ipMask: String): List<String> {
        val set = ipMask.split("/")
        val ipStart = iNetString2Number(set[0])
        val ipEnd = parseIPEnd(ipStart, Integer.parseInt(set[1]))
        val result = ArrayList<Long>()
        for (i in ipStart..ipEnd) {
            result.add(i)
        }
        return (ipStart..ipEnd)
                .map { iNetNumber2String(it) }
                .toList()
    }

    fun ports2portArray(ports: String): List<String> {
        val set = ports.split("-")
        val portStart = Integer.parseInt(set[0])
        val portEnd = Integer.parseInt(set[1])
        return (portStart..portEnd)
                .map { it.toString() }
                .toList()
    }

    // 对List<String>分组
    fun normalGroup(
            stringArray: List<String>,
            groupSize: Int,
            separator: String
    ): List<String> {
        val groups = ArrayList<String>()
        val g = ArrayList<String>()
        var i = 0
        for (s in stringArray) {
            g.add(s)
            i++
            if (i >= groupSize) {
                groups.add(g.joinToString(separator))
                i = 0
                g.clear()
            }
        }
        return groups
    }

    fun ipGroup(
            ips: List<String>,
            groupSize: Int,
            separator: String
    ): List<String> {
        val splitIPs = ArrayList<String>()
        for (o in ips) {
            if (o.contains("/")) {
                splitIPs.addAll(ipMask2ipArray(o))
            } else {
                splitIPs.add(o)
            }
        }
        return normalGroup(splitIPs, groupSize, separator)
    }

    fun portGroup(
            ports: List<String>,
            groupSize: Int,
            separator: String
    ): List<String> {
        val splitPorts = ArrayList<String>()
        for (p in ports) {
            if (p.contains("-")) {
                splitPorts.addAll(ports2portArray(p))
            } else {
                splitPorts.add(p)
            }
        }
        return normalGroup(splitPorts, groupSize, separator)
    }

    fun dnssecure(
            tid: Int,
            imageID: Int,
            taskName: String,
            priority: String,
            params: Array<String>
    ) {
        val domains = normalGroup(
                params[0].split("+"),
                Integer.parseInt(Settings.setting["dnssecureDomainGroup"] as String),
                "+"
        )
        val reServers = normalGroup(
                params[1].split("+"),
                Integer.parseInt(Settings.setting["dnssecureServerGroup"] as String),
                "+"
        )

        DatabaseHandler.batchInsertDnssecureTasks(
                tid,
                taskName,
                imageID,
                Integer.parseInt(priority),
                domains,
                reServers
        )
    }

    fun ipTest(
            tid: Int,
            imageID: Int,
            taskName: String,
            priority: String,
            params: Array<String>
    ) {
        val ips = ipGroup(
                params[0].split(","),
                Integer.parseInt(Settings.setting["ipTestGroup"] as String),
                ",")
        DatabaseHandler.batchInsertIPTestTasks(
                tid,
                taskName,
                imageID,
                Integer.parseInt(priority),
                ips
        )
    }

    fun portScan(
            tid: Int,
            imageID: Int,
            taskName: String,
            priority: String,
            params: Array<String>
    ) {
        val ips = ipGroup(
                params[0].split(","),
                Integer.parseInt(Settings.setting["portScanGroup"] as String),
                ","
        )
        val ports = portGroup(
                params[1].split(","),
                Integer.parseInt(Settings.setting["portScanPortGroup"] as String),
                ","
        )
        DatabaseHandler.batchInsertPortScanTasks(
                tid,
                taskName,
                imageID,
                Integer.parseInt(priority),
                ips,
                ports
        )
    }

    fun pageCrawl(
            tid: Int,
            imageID: Int,
            taskName: String,
            priority: String,
            params: Array<String>
    ) {
        val urls = normalGroup(
                params[0].split(","),
                Integer.parseInt(Settings.setting["pageCrawlURLGroup"] as String),
                ","
        )
        DatabaseHandler.batchInsertPageCrawlTasks(
                tid,
                taskName,
                imageID,
                Integer.parseInt(priority),
                urls
        )
    }

    fun urlCrawl(
            tid: Int,
            imageID: Int,
            taskName: String,
            priority: String,
            params: Array<String>
    ) {
        val urls = normalGroup(
                params[0].split(","),
                Integer.parseInt(Settings.setting["urlCrawlURLGroup"] as String),
                ","
        )
        DatabaseHandler.batchInsertURLCrawlTasks(
                tid,
                taskName,
                imageID,
                Integer.parseInt(priority),
                urls
        )
    }
}