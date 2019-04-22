package com.netdetpla.ndp.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SubTask {

    @JsonProperty(value = "image-name")
    private String imageName;
    @JsonProperty(value = "task-name")
    private String taskName;
    @JsonProperty(value = "id")
    private int id;
    private int tid;
    @JsonProperty(value = "start-time")
    private String startTime;
    @JsonProperty(value = "end-time")
    private String endTime;
    @JsonProperty(value = "param")
    private String param;
    @JsonProperty(value = "task-status")
    private String taskStatus;
    @JsonProperty(value = "priority")
    private int priority;


    public int getTid() {
        return tid;
    }

    public String getImageName() {
        return imageName;
    }

    public String getTaskName() {
        return taskName;
    }

    public SubTask(int id, String startTime, String endTime, String param, String taskStatus, int priority) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.param = param;
        this.taskStatus = taskStatus;
        this.priority = priority;
    }
}
