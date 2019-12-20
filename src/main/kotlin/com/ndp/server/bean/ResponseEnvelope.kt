package com.ndp.server.bean

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ResponseEnvelope<T> {
    var code: Int
        private set
    var message: String
        private set
    var data: T? = null
        private set

    constructor(code: Int, message: String, data: T) {
        this.code = code
        this.message = message
        this.data = data
    }

    constructor(code: Int, message: String) {
        this.code = code
        this.message = message
    }
}