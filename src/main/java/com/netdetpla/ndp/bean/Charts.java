package com.netdetpla.ndp.bean;

import java.util.List;

class Chart {
    protected List<String> labels;
    protected List<Integer> data;

    public List<String> getLabels() {
        return labels;
    }

    public List<Integer> getData() {
        return data;
    }

    public Chart(List<String> labels, List<Integer> data) {
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
            List<String> portLabels, List<Integer> portData,
            List<String> serviceLabels, List<Integer> serviceData,
            List<String> hardwareLabels, List<Integer> hardwareData,
            List<String> osLabels, List<Integer> osData
    ) {
        this.port = new Chart(portLabels, portData);
        this.service = new Chart(serviceLabels, serviceData);
        this.hardware = new Chart(hardwareLabels, hardwareData);
        this.os = new Chart(osLabels, osData);
    }
}
