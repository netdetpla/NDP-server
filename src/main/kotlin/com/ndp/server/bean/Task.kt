package com.ndp.server.bean

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.text
import me.liuwj.ktorm.schema.varchar

object Task : Table<Nothing>("task") {
    val id by int("id").primaryKey()
    val tid by int("tid")
    val taskName by varchar("task_name")
    val imageID by int("image_id")
    val param by text("param")
    val priority by int("priority")
    val taskStatus by int("task_status")
}
