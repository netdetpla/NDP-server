package com.netdetpla.ndp.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Task {

    @JsonProperty(value = "image-name")
    private String imageName;
    @JsonProperty(value = "task-name")
    private String taskName;
    private String tag;
    private int id;
    @JsonProperty(value = "start-time")
    private String startTime;
    @JsonProperty(value = "end-time")
    private String endTime;
    private String param;
    private String ip;

    public String getImage() {
        return imageName;
    }

    public String getTag() {
        return tag;
    }

    public int getId() {
        return id;
    }

    public String getParam() {
        return param;
    }

    public String getIp() {
        return ip;
    }

    public Task(int id, String taskName, String startTime, String endTime) {
        this.taskName = taskName;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Task(String ip) {
        this.ip = ip;
    } // 4.9 把param替换为ip了
}
