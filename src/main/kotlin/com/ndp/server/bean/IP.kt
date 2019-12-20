package com.ndp.server.bean

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.text
import me.liuwj.ktorm.schema.varchar

object IP : Table<Nothing>("ip") {
    val id by int("id").primaryKey()
    val ip by varchar("ip")
    val osVersion by text("os_version")
    val hardware by text("hardware")
    val lnglatID by int("lnglat_id")
    val ipTestFlag by int("ip_test_flag")
    val ipResolveFlag by int("ip_resolve_flag")
    val portScanFlag by int("port_scan_flag")
    val udpScanFlag by int("udp_scan_flag")
}
