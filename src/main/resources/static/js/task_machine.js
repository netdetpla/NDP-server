function openCard($card) {
    $card.css("display", "block");
    var $mask = $("#mask");
    $mask.css("z-index", "2");
    $mask.css("background-color", "rgba(0, 0, 0, 0.5)");
}
function closeCard($card) {
    $card.css("display", "none");
    var $mask = $("#mask");
    $mask.css("background-color", "rgba(0, 0, 0, 0)");
    $mask.one("transitionend", function () {
        $mask.css("z-index", "-1");
    })
}
function getMachine() {
    var $machineBody = $("#taskMachineTableBody");
    $machineBody.empty("tr");
    $.get("machine", {}, function (jsonStr) {
    	json = JSON.parse(jsonStr);
        for (var i = 0; i < json.length; i++) {
            $machineBody.append("<tr></tr>");
            var $row = $machineBody.find("tr:last");
            $row.append("<td>" + json[i].ip + "</td>");
            $row.append("<td>" + json[i].net_use + "</td>");
            $row.append("<td>" + json[i].task_num + "</td>");
            $row.append("<td>" + json[i].score + "</td>");
            $row.append("<td>" + json[i].connect + "</td>");
        }
    });
}
getMachine();
$("#addBtn").on("click", function () {
    openCard($("#addTaskMachineCard"));
});
$("#cancel").on("click", function () {
    closeCard($("#addTaskMachineCard"));
});
$("#submit").on("click", function () {
    $.get("addMachine", {
        machineIp: $("#IP").val()
    }, function () {
        getMachine();
    });
    closeCard($("#addTaskMachineCard"));
});
