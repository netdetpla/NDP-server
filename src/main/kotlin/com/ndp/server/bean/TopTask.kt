package com.ndp.server.bean

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int

object TopTask : Table<Nothing>("top_task") {
    val tid by int("tid").primaryKey()
    val imageID by int("image_id")
    val startID by int("start_id")
    val endID by int("end_id")
}