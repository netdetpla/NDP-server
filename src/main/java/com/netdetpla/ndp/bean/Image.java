package com.netdetpla.ndp.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ImageTest {
    private float cpu;
    private int memory;
    private int bandwidth;

    ImageTest(float cpu, int memory, int bandwidth) {
        this.cpu = cpu;
        this.memory = memory;
        this.bandwidth = bandwidth;
    }
    public float getCpu() {
        return cpu;
    }

    public int getMemory() {
        return memory;
    }

    public int getBandwidth() {
        return bandwidth;
    }
}
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Image {
    @JsonProperty(value = "image-name")
    private String imageName;
    private String tag;
    @JsonProperty(value = "current-use")
    private boolean currentUse;
    @JsonProperty(value = "upload-time")
    private String uploadTime;
    @JsonProperty(value = "size")
    private String size;
    private ImageTest test;

    public String getImageName() {
        return imageName;
    }

    public String getTag() {
        return tag;
    }

    public boolean isCurrentUse() {
        return currentUse;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getSize() {
        return size;
    }

    public ImageTest getTest() {
        return test;
    }

    public void setTest(float cpu, int memory, int bandwidth) {
        this.test = new ImageTest(cpu, memory, bandwidth);
    }

    public Image(String tag, String uploadTime, String size) {
        this.tag = tag;
        this.uploadTime = uploadTime;
        this.size = size;
    }

    public Image(
            String imageName,
            String tag,
            boolean currentUse,
            String uploadTime,
            String size,
            float cpu,
            int memory,
            int bandwidth
    ) {
        this.imageName = imageName;
        this.tag = tag;
        this.currentUse = currentUse;
        this.uploadTime = uploadTime;
        this.size = size;
        setTest(cpu, memory, bandwidth);
    }
}
