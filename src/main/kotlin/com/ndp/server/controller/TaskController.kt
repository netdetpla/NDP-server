package com.ndp.server.controller

import com.ndp.server.bean.ResponseEnvelope
import com.ndp.server.utils.DatabaseHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController {

    @PostMapping("/task")
    fun addTask(
            @RequestParam("image-name") imageName: String,
            @RequestParam("tag") tag: String,
            @RequestParam("task-name") taskName: String,
            @RequestParam("priority") priority: String,
            @RequestParam("params[]") params: Array<String>
    ): ResponseEntity<*>? {
        println("image-name: $imageName")
        println("tag: $tag")
        println("task-name: $taskName")
        println("priority: $priority")
        for (k in params.indices) {
            println("param" + k + "：" + params[k])
        }

        val imageID = DatabaseHandler.selectImageID(imageName, tag)
        if (imageID == -1)
            return ResponseEntity(ResponseEnvelope<Any?>(
                    HttpStatus.BAD_REQUEST.value(),
                    "No such image & tag"
            ), HttpStatus.BAD_REQUEST)
        val tid = DatabaseHandler.selectMaxTid()

        when (imageName) {

        }
    }
}