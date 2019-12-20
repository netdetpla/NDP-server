package com.ndp.server.bean

import me.liuwj.ktorm.schema.*

object Block : Table<Nothing>("GeoLite2-City-Blocks-IPv4") {
    val id by int("id").primaryKey()
    val network by text("network")
    val geoNameID by int("geoname_id")
    val latitude by double("latitude")
    val longitude by double("longitude")
    val longIPStart by long("long_ip_start")
    val longIPEnd by long("long_ip_end")
}
