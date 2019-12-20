package com.ndp.server.bean

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.text
import me.liuwj.ktorm.schema.varchar

object Location : Table<Nothing>("GeoLite2-City-Locations-zh-CN") {
    val geoNameID by int("geoname_id").primaryKey()
    val localeCode by text("locale_code")
    val countryISOCode by text("country_iso_code")
    val countryName by text("country_name")
    val subdivision1ISOCode by text("subdivision_1_iso_code")
    val subdivision1Name by text("subdivision_1_name")
}
