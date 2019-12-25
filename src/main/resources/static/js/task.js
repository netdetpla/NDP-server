String.format = function () {
    let param = [];
    for (let i = 0, l = arguments.length; i < l; i++) {
        param.push(arguments[i]);
    }
    let statement = param[0]; // get the first element(the original statement)
    param.shift(); // remove the first element from array
    return statement.replace(/{(\d+)}/g, function (m, n) {
        return param[n];
    });
};

function getRunningTask() {
    let $taskTableBody = $("#taskTableBody");
    $.get("/task/running", {}, function (json) {
        let data = json['data'];
        for (let i = 0; i < data.length; i++) {
            $taskTableBody.append("<tr></tr>");
            let $row = $taskTableBody.find("tr:last");
            $row.append(String.format("<td>{0}</td>", data[i]['task-id']));
            $row.append(String.format("<td>{0}</td>", data[i]['task-name']));
            $row.append(String.format("<td>{0}</td>", data[i]['image-name']));
            $row.append(String.format("<td>{0}</td>", data[i]['tag']));
            let process = (data[i]['handled'] / data[i]['total'] * 100).toFixed(1);
            $row.append(String.format(
                "<td><span>{0} / {1}</span>" +
                "<div class=\"progress process-back\">" +
                "<div class=\"progress-bar process-primary\" role=\"progressbar\" style=\"width: {2}%\" " +
                "aria-valuenow=\"25\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>" +
                "</div></td>",
                data[i]['handled'], data[i]['total'], process));
        }
    });
}

function drawTaskCharts() {
    $.get("/task/finished", {}, function (json) {
        let data = json['data'];
        drawChart($("#finishedTaskChart")[0], data['labels'], data['data']);
    });
    $.get("/task/unfinished", {}, function (json) {
        let data = json['data'];
        drawChart($("#waitingTaskChart")[0], data['labels'], data['data']);
    });
}

// init
getRunningTask();
drawTaskCharts();
