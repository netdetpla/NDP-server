package com.ndp.server.bean

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.varchar

object Page : Table<Nothing>("page") {
    val id by int("id").primaryKey()
    val domain by varchar("domain")
    val domainHash by long("domain_hash")
    val htmlPath by varchar("html_path")
    val pageFlag by int("page_flag")
    val dnssecureFlag by int("dnssecure_flag")
    val urlFlag by int("url_flag")
}