package com.ndp.server.bean

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ImageJson (
        @JsonProperty(value = "image-name") val imageName: String,
        val tag: String,
        @JsonProperty(value = "upload-time") val uploadTime: String,
        val size: String
)