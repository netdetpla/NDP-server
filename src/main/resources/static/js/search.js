function searchPortChart(keyword) {
    $.post("/statistic/search", {
        "type": "port",
        "keyword": keyword
    }, function (json) {
        drawChart($("#serviceChart")[0], json["labels"], json['data']);
        let card = $("#serviceCard");
        card.removeClass("statistic-hide");
    });
}

function searchServiceChart(keyword) {
    $.post("/statistic/search", {
        "type": "service",
        "keyword": keyword
    }, function (json) {
        drawChart($("#portChart")[0], json["labels"], json['data']);
        let card = $("#portCard");
        card.removeClass("statistic-hide");
    });
}

function searchIPChart(keyword) {
    $.post("/statistic/search", {
        "type": "ip",
        "keyword": keyword
    }, function (json) {
        let portLabels = json["port"]["labels"];
        let portData = json["port"]["data"];
        let serviceLabels = json["service"]["labels"];
        let serviceData = json["service"]["data"];

        drawChart($("#portChart")[0], portLabels, portData);
        $("#portCard").removeClass("statistic-hide");

        drawChart($("#serviceChart")[0], serviceLabels, serviceData);
        $("#serviceCard").removeClass("statistic-hide");
    });
}

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