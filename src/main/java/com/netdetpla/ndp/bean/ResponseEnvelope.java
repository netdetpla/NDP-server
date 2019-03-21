package com.netdetpla.ndp.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseEnvelope <T> {
    private Integer code;
    private String message;
    private T data;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public ResponseEnvelope(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public ResponseEnvelope(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}