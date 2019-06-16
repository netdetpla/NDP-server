package com.netdetpla.ndp.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result {
    int rid;
    int tid;

    public Result(int rid, int tid) {
        this.rid = rid;
        this.tid = tid;
    }

    public int getRid() {
        return rid;
    }

    public int getTid() {
        return tid;
    }
}
