function setProcessBar(url, $processBar, $process) {
    $.get(url, {}, function (json) {
        let available = json['data']['available'];
        let total = json['data']['total'];
        let process = (available / total * 100).toFixed(1);
        $processBar.css("width", process.toString() + "%");
        $process.html(available.toString() + " / " + total.toString());
    });
}

function onBatchAddButton(url, $button, $input) {
    $button.on("click", function () {
        let data = new FormData();
        data.append("num", $input.val());
        $.ajax({
            type: "POST",
            url: url,
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            success: function (data) {
                alert("新建任务成功！");
            },
            error: function (e) {
                alert("新建任务失败！");
            }
        });
    });
}

//init
setProcessBar("/task/available/ipTestA", $("#ipTestAProcessBar"), $("#ipTestAProcess"));
setProcessBar("/task/available/portScan", $("#portScanProcessBar"), $("#portScanProcess"));
setProcessBar("/task/available/dnssecure", $("#dnssecureProcessBar"), $("#dnssecureProcess"));
setProcessBar("/task/available/urlCrawl", $("#urlCrawlProcessBar"), $("#urlCrawlProcess"));
setProcessBar("/task/available/pageCrawl", $("#pageCrawlProcessBar"), $("#pageCrawlProcess"));

onBatchAddButton("/task/batchAdd/ipTestGeo", $("#ipTestGeoSubmit"), $("#ipTestGeoNum"));
onBatchAddButton("/task/batchAdd/ipTestA", $("#ipTestASubmit"), $("#ipTestANum"));
onBatchAddButton("/task/batchAdd/portScan", $("#portScanSubmit"), $("#portScanNum"));
onBatchAddButton("/task/batchAdd/dnssecure", $("#dnssecureSubmit"), $("#dnssecureNum"));
onBatchAddButton("/task/batchAdd/urlCrawl", $("#urlCrawlSubmit"), $("#urlCrawlNum"));
onBatchAddButton("/task/batchAdd/pageCrawl", $("#pageCrawlSubmit"), $("#pageCrawlNum"));