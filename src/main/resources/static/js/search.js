function searchPortChart(keyword) {
    $.post("/statistic/search", {
        "type": "port",
        "keyword": keyword
    }, function (json) {
        let data = json['data'];
        let $serviceChartBox = $("#serviceChartBox");
        $serviceChartBox.empty();
        $serviceChartBox.append("<canvas id=\"serviceChart\"></canvas>");
        drawChart($("#serviceChart")[0], data["labels"], data['data']);
        $("#serviceCard").removeClass("statistic-hide");
    });
}

function searchServiceChart(keyword) {
    $.post("/statistic/search", {
        "type": "service",
        "keyword": keyword
    }, function (json) {
        let data = json['data'];
        let $portChartBox = $("#portChartBox");
        $portChartBox.empty();
        $portChartBox.append("<canvas id=\"portChart\"></canvas>");
        drawChart($("#portChart")[0], data["labels"], data['data']);
        $("#portCard").removeClass("statistic-hide");
    });
}

function searchIPChart(keyword) {
    $.post("/statistic/search", {
        "type": "ip",
        "keyword": keyword
    }, function (json) {
        let data = json['data'];
        let portLabels = data["port"]["labels"];
        let portData = data["port"]["data"];
        let serviceLabels = data["service"]["labels"];
        let serviceData = data["service"]["data"];
        let $portChartBox = $("#portChartBox");
        $portChartBox.empty();
        $portChartBox.append("<canvas id=\"portChart\"></canvas>");
        drawChart($("#portChart")[0], portLabels, portData);
        $("#portCard").removeClass("statistic-hide");
        let $serviceChartBox = $("#serviceChartBox");
        $serviceChartBox.empty();
        $serviceChartBox.append("<canvas id=\"serviceChart\"></canvas>");
        drawChart($("#serviceChart")[0], serviceLabels, serviceData);
        $("#serviceCard").removeClass("statistic-hide");
    });
}

// init
$("#submitSearch").on("click", function () {
    $("#portCard").addClass("statistic-hide");
    $("#serviceCard").addClass("statistic-hide");
    let type = $("#searchType label.active").attr("id");
    let keyword = $("#taskName").val();
    switch (type) {
        case "0":
            searchPortChart(keyword);
            break;
        case "1":
            searchServiceChart(keyword);
            break;
        case "2":
            searchIPChart(keyword);
            break;
        default:
    }
});