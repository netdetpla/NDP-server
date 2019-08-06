package com.netdetpla.ndp.bean;

class Chart {
    protected String[] labels;
    protected int[] data;

    public String[] getLabels() {
        return labels;
    }

    public int[] getData() {
        return data;
    }

    public Chart(String[] labels, int[] data) {
        this.labels = labels;
        this.data = data;
    }
}
public class Charts {
    Chart port, service, hardware, os;

    public Chart getPort() {
        return port;
    }

    public Chart getService() {
        return service;
    }

    public Chart getHardware() {
        return hardware;
    }

    public Chart getOs() {
        return os;
    }

    public Charts(
            String[] portLabels, int[] portData,
            String[] serviceLabels, int[] serviceData,
            String[] hardwareLabels, int[] hardwareData,
            String[] osLabels, int[] osData
    ) {
        this.port = new Chart(portLabels, portData);
        this.service = new Chart(serviceLabels, serviceData);
        this.hardware = new Chart(hardwareLabels, hardwareData);
        this.os = new Chart(osLabels, osData);
    }
}
