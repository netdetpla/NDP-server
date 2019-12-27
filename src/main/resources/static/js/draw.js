function drawChart(canvas, labels, data) {
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
                    fontColor: "#9a9a9a",
                    beginAtZero : true
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
    const colorSet = [
        'rgba(161, 197, 253, 1)',
        'rgba(254, 152, 221, 1)',
        'rgba(226, 255, 153, 1)',
        'rgba(255, 218, 153, 1)',
        'rgba(152, 253, 199, 1)',
        'rgba(196, 161, 253, 1)',
        'rgba(255, 243, 153, 1)',
        'rgba(255, 176, 153, 1)',
    ];
    const midColorSet = [
        'rgba(161, 197, 253, 0.2)',
        'rgba(254, 152, 221, 0.2)',
        'rgba(226, 255, 153, 0.2)',
        'rgba(255, 218, 153, 0.2)',
        'rgba(152, 253, 199, 0.2)',
        'rgba(196, 161, 253, 0.2)',
        'rgba(255, 243, 153, 0.2)',
        'rgba(255, 176, 153, 0.2)',
    ];
    let colorID = Math.ceil(Math.random() * 8);

    let ctx = canvas.getContext("2d");
    let gradientStroke = ctx.createLinearGradient(0, 160, 0, 90);
    gradientStroke.addColorStop(1, midColorSet[colorID]);
    gradientStroke.addColorStop(0.2, 'rgba(72, 72, 176, 0.0)');
    gradientStroke.addColorStop(0, 'rgba(119, 52, 169, 0)');
    return new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                data: data,
                fill: true,
                borderColor: colorSet[colorID],
                backgroundColor: gradientStroke,
                borderWidth: 2,
                borderDash: [],
                borderDashOffset: 0.0,
            }]
        },
        options: gradientChartOptionsConfiguration
    });
}