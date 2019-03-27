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

    public Task(String taskName, String tag, int id, String startTime, String endTime) {
        this.taskName = taskName;
        this.id = id;
        this.tag = tag;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Task(String param) {
        this.param = param;
    }
}
