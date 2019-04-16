package com.netdetpla.ndp.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SubTask {

    @JsonProperty(value = "image-name")
    private String imageName;
    @JsonProperty(value = "task-name")
    private String taskName;
    private int id;
    private int tid;
    @JsonProperty(value = "start-time")
    private String startTime;
    @JsonProperty(value = "end-time")
    private String endTime;
    private String param;
    private String taskStatus;
    private int priority;


    public int getTid() {
        return tid;
    }



    public SubTask(int id,String imageName, String taskName, String startTime, String endTime, String param, String taskStatus, int priority) {
        this.taskName = taskName;
        this.imageName = imageName;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.param = param;
        this.taskStatus = taskStatus;
        this.priority = priority;
    }
}
