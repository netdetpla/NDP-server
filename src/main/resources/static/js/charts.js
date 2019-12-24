function setCharts() {
    let portLabels, portData;
    let serviceLabels, serviceData;
    $.get("/statistic/charts", {}, function (json) {
        let data = json['data'];
        portLabels = data["port"]["labels"];
        portData = data["port"]["data"];
        serviceLabels = data["service"]["labels"];
        serviceData = data["service"]["data"];

        drawChart($("#portChart")[0], portLabels, portData);
        drawChart($("#serviceChart")[0], serviceLabels, serviceData);
    });
}

// init
setCharts();