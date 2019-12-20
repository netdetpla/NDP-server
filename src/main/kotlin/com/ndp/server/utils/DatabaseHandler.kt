package com.ndp.server.utils

import com.ndp.server.bean.Image
import com.ndp.server.bean.ImageJson
import com.ndp.server.bean.Task
import me.liuwj.ktorm.dsl.*

object DatabaseHandler {

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
                .where { Image.imageName eq imageName}
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
        val id =  Image.select(Image.id)
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
                .map { it[Task.tid] ?: 0}
        return if (tid.isNotEmpty()) tid[0] else 1
    }
}