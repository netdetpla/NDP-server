package com.ndp.server.bean

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.varchar

object DomainIP : Table<Nothing>("domain_ip") {
    val id by int("id").primaryKey()
    val domain by varchar("domain")
    val domainHash by long("domain_hash")
    val ipID by long("ip_id")
    val ipTestFlag by int("ip_test_flag")
}