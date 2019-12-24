package com.ndp.server.bean

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class TaskJson (
        @JsonProperty(value = "task-id") val taskID: Int,
        @JsonProperty(value = "task-name") val taskName: String,
        @JsonProperty(value = "image-name") val imageName: String,
        val tag: String,
        val total: Int,
        val handled: Int
)