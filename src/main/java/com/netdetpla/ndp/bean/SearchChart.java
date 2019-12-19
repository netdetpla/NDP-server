package com.netdetpla.ndp.bean;

public class SearchChart {
    String title;
    String type;
    String[] labels;
    int[] data;

    public SearchChart(String title, String type, String[] labels, int[] data) {
        this.title = title;
        this.type = type;
        this.labels = labels;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String[] getLabels() {
        return labels;
    }

    public int[] getData() {
        return data;
    }
}
