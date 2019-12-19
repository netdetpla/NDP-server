package com.netdetpla.ndp.bean;

public class LngLat {
    double[] lnglat = new double[2];
    int level;

    public double[] getLnglat() {
        return lnglat;
    }

    public int getLevel() {
        return level;
    }

    public LngLat(double[] lnglat, int level) {
        this.lnglat = lnglat;
        this.level = level;
    }
}
