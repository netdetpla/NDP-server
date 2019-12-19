function setCharts() {
    // var ctx = $("#portChart")[0];
    var colorSet = [
        'rgba(161, 197, 253, 1)',
        'rgba(254, 152, 221, 1)',
        'rgba(226, 255, 153, 1)',
        'rgba(255, 218, 153, 1)',
        'rgba(152, 253, 199, 1)',
        'rgba(196, 161, 253, 1)',
        'rgba(255, 243, 153, 1)',
        'rgba(255, 176, 153, 1)',
    ];
    var colorSet2 = [
        'rgba(120, 170, 249, 1)',
        'rgba(251, 108, 204, 1)',
        'rgba(214, 254, 109, 1)',
        'rgba(255, 202, 109, 1)',
        'rgba(107, 250, 173, 1)',
    ];
    let portLabels, portData;
    let serviceLabels, serviceData;
    let hardwareLabels, hardwareData;
    let osLabels, osData;
    $.get("/statistic/charts", {}, function (json) {
        let data = json.data;
        portLabels = data["port"]["labels"];
        portData = data["port"]["data"];
        serviceLabels = data["service"]["labels"];
        serviceData = data["service"]["data"];
        hardwareLabels = data["hardware"]["labels"];
        hardwareData = data["hardware"]["data"];
        osLabels = data["os"]["labels"];
        osData = data["os"]["data"];

        let gradientChartOptionsConfiguration = {
            maintainAspectRatio: false,
            legend: {
                display: false
            },

            tooltips: {
                backgroundColor: '#fff',
                titleFontColor: '#333',
                bodyFontColor: '#666',
                bodySpacing: 4,
                xPadding: 12,
                mode: "nearest",
                intersect: 0,
                position: "nearest"
            },
            responsive: true,
            scales: {
                yAxes: [{
                    barPercentage: 1,
                    gridLines: {
                        drawBorder: false,
                        color: 'rgba(29, 140, 248, 0.0)',
                        zeroLineColor: "transparent",
                    },
                    ticks: {
                        fontColor: "#9a9a9a"
                    }
                }],

                xAxes: [{
                    barPercentage: 1,
                    gridLines: {
                        drawBorder: false,
                        color: 'rgba(220, 53, 69, 0.1)',
                        zeroLineColor: "transparent",
                    },
                    ticks: {
                        padding: 20,
                        fontColor: "#9a9a9a"
                    }
                }]
            }
        };
        let ctx = $("#portChart")[0].getContext("2d");
        let gradientStroke = ctx.createLinearGradient(0, 160, 0, 90);
        gradientStroke.addColorStop(1, 'rgba(82, 143, 239,0.2)');
        gradientStroke.addColorStop(0.2, 'rgba(72, 72, 176, 0.0)');
        gradientStroke.addColorStop(0, 'rgba(119, 52, 169, 0)');
        new Chart(ctx, {
            type: "bar",
            data: {
                labels: portLabels,
                datasets: [{
                    data: portData,
                    fill: true,
                    borderColor: colorSet[0],
                    backgroundColor: gradientStroke,
                    borderWidth: 2,
                    borderDash: [],
                    borderDashOffset: 0.0,
                }]
            },
            options: gradientChartOptionsConfiguration
        });
        ctx = $("#serviceChart")[0].getContext("2d");
        gradientStroke = ctx.createLinearGradient(0, 160, 0, 90);
        gradientStroke.addColorStop(1, 'rgba(254, 152, 221, 0.2)');
        gradientStroke.addColorStop(0.2, 'rgba(72, 72, 176, 0.0)');
        gradientStroke.addColorStop(0, 'rgba(119, 52, 169, 0)');
        new Chart(ctx, {
            type: "bar",
            data: {
                labels: serviceLabels,
                datasets: [{
                    data: serviceData,
                    fill: true,
                    borderColor: colorSet[1],
                    backgroundColor: gradientStroke,
                    borderWidth: 2,
                    borderDash: [],
                    borderDashOffset: 0.0,
                }]
            },
            options: gradientChartOptionsConfiguration
        });
        ctx = $("#hardwareChart")[0].getContext("2d");
        gradientStroke = ctx.createLinearGradient(0, 160, 0, 90);
        gradientStroke.addColorStop(1, 'rgba(226, 255, 153, 0.2)');
        gradientStroke.addColorStop(0.2, 'rgba(72, 72, 176, 0.0)');
        gradientStroke.addColorStop(0, 'rgba(119, 52, 169, 0)');
        new Chart(ctx, {
            type: "bar",
            data: {
                labels: hardwareLabels,
                datasets: [{
                    data: hardwareData,
                    fill: true,
                    borderColor: colorSet[2],
                    backgroundColor: gradientStroke,
                    borderWidth: 2,
                    borderDash: [],
                    borderDashOffset: 0.0,
                }]
            },
            options: gradientChartOptionsConfiguration
        });
        ctx = $("#osChart")[0].getContext("2d");
        gradientStroke = ctx.createLinearGradient(0, 160, 0, 90);
        gradientStroke.addColorStop(1, 'rgba(255, 218, 153, 0.2)');
        gradientStroke.addColorStop(0.2, 'rgba(72, 72, 176, 0.0)');
        gradientStroke.addColorStop(0, 'rgba(119, 52, 169, 0)');
        new Chart(ctx, {
            type: "bar",
            data: {
                labels: osLabels,
                datasets: [{
                    data: osData,
                    fill: true,
                    borderColor: colorSet[3],
                    backgroundColor: gradientStroke,
                    borderWidth: 2,
                    borderDash: [],
                    borderDashOffset: 0.0,
                }]
            },
            options: gradientChartOptionsConfiguration
        });
    });
}

// init
setCharts();