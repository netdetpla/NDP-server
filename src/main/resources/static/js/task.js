$(document).ready(function () {
	$("select").material_select();
});
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
function setTaskDetailCard(json, pid) {
	var $taskDetailCard = $("#taskDetailCard");
	var $taskDetailBody = $taskDetailCard.find("tbody");
	var $taskDetailCardTitle = $("#taskDetailTitle");
	$taskDetailCardTitle.html(pid + "详情");
	$taskDetailBody.empty("tr");
	for (var i = 0; i < json.length; i++) {
		$taskDetailBody.append("<tr></tr>");
		var $row = $taskDetailBody.find("tr:last");
		$row.append("<td>" + json[i].sid + "</td>");
		$row.append("<td>" + json[i].parameter + "</td>");
		$row.append("<td>" + json[i].start + "</td>");
		$row.append("<td>" + json[i].end + "</td>");
		$row.append("<td>" + json[i].state + "</td>");
	}
	openCard($taskDetailCard);
}
function getTask() {
	var $taskBody = $("#taskTableBody");
	$taskBody.empty("tr");
	$.get("task", {}, function (jsonStr) {
		json = JSON.parse(jsonStr);
		for (var i = 0; i < json.length; i++) {
			$taskBody.append("<tr></tr>");
			var $row = $taskBody.find("tr:last");
			$row.append("<td>" + json[i].pid + "</td>");
			$row.append("<td>" + json[i].sTaskNum + "</td>");
			$row.append("<td>" + json[i].completeTaskNum + "</td>");
			$row.on("click", function () {
				var pid = $(this).find("td:first").html();
				$.get("taskDetail", {
					pid: pid
				}, function (jsonStr) {
					json = JSON.parse(jsonStr);
					setTaskDetailCard(json, pid);
				});
			});
		}
	});
}
function getImage() {
	$imageList = $("#imageList");
	$imageList.empty();
	$imageList.append("<select title=\"selectImage\"></select>");
	$select = $imageList.find("select");
	$select.append("<option value=\"\" disabled selected>选择镜像</option>");
	$.get("getImages", {}, function (jsonStr) {
		json = JSON.parse(jsonStr);
		for (var i = 0; i < json.length; i++) {
			$select.append("<option value=\"" + i.toString() + "\">" +
					json[i].image + ":" + json[i].tag + "</option>");
		}
		$("select").material_select();
	})
}
getTask();
$("#okTaskDetail").on("click", function () {
	closeCard($("#taskDetailCard"));
});
$("#addBtn").on("click", function () {
	getImage();
	openCard($("#addTaskCard"));
});
$("#cancel").on("click", function () {
	closeCard($("#addTaskCard"));
});
$("#submit").on("click", function () {
	console.log("click");
	var image = $("#imageList").find("li.active > span").html();
	var parameter = $("#parameter").val();
	console.log(parameter);
	$.post("assignTask", {
		image: image,
		parameter: parameter
	}, function () {
		getTask();
	});
	console.log("ajax");
	closeCard($("#addTaskCard"));
});
$("#addNS4ReDNS").on("click", function () {
	var $nsList = $("#nsList4ReDNS");
	var num = $nsList.children("li").length;
	$nsList.append(
			"<li>" +
			"<div class=\"row\">" +
			"<div class=\"input-field\">" +
			"<input id=\"nsItem4ReDNS" + num + "\" type=\"text\" class=\"validate\">" +
			"<label for=\"nsItem4ReDNS" + num + "\"></label>" +
			"</div>" +
			"</div>" +
			"</li>"
	);
});
$("#addNS4AuDNS").on("click", function () {
	var $nsList = $("#nsList4AuDNS");
	var num = $nsList.children("li").length;
	$nsList.append(
			"<li>" +
			"<div class=\"row\">" +
			"<div class=\"input-field\">" +
			"<input id=\"nsItem4AuDNS" + num + "\" type=\"text\" class=\"validate\">" +
			"<label for=\"nsItem4AuDNS" + num + "\"></label>" +
			"</div>" +
			"</div>" +
			"</li>"
	);
});
$(".cancelAdd").on("click", function () {
	closeCard($("#addTaskCard"));
});
$("#protocol4Hole").find("input").on("change", function () {
	if ($("#protocol4Hole").find("input[name=protocol4Hole]:checked").val() === "HTTP") {
		$("#get4Hole").removeAttr("disabled");
		$("#post4Hole").removeAttr("disabled");
		$("#userAgent4Hole").removeAttr("disabled");
	} else {
		$("#get4Hole").attr("disabled", "disabled");
		$("#post4Hole").attr("disabled", "disabled");
		$("#userAgent4Hole").attr("disabled", "disabled");
	}
});
function getPortParameter() {
	return {
		image: "port",
		ip: $("#ip4Port").val(),
		service_type: $("#type4Port").find("input[name=type4Port]:checked").val(),
		flag: $("#flag4Port").val(),
		port: $("#port4Port").val()
	}
}
function getOSParameter() {
	return {
		image: "os",
		ip: $("#ip4OS").val()
	}
}
function getReDNSParameter() {
	var $nsList = $("#nsList4ReDNS");
	var nsList = $nsList.children("li");
	var nsReturn = "";
	for (var i = 0; i < nsList.length; i++) {
		var $li = $nsList.find("li:nth-child(" + (i + 1) + ")");
		nsReturn = nsReturn + $li.find("input").val() + "+";
	}
	console.log(nsReturn);
	return {
		image: "redns",
		ip: $("#ip4ReDNS").val(),
		ns_list: nsReturn
	}
}
function getAuDNSParameter() {
	var $nsList = $("#nsList4AuDNS");
	var nsList = $nsList.children("li");
	var nsReturn = "";
	for (var i = 0; i < nsList.length; i++) {
		var $li = $nsList.find("li:nth-child(" + (i + 1) + ")");
		nsReturn = nsReturn + $li.find("input").val() + ";";
	}
	return {
		image: "audns",
		ip: $("#ip4AuDNS").val(),
		ns_list: nsReturn
	}
}
function getHoleParameter() {
	var protocol = $("#protocol4Hole").find("input[name=protocol4Hole]:checked").val();
	var method = "";
	var userAgent = "";
	if (protocol === "HTTP") {
		method = $("#method4Hole").find("input[name=method4Hole]:checked").val();
		userAgent = $("#userAgent4Hole").val();
	}
	return {
		image: "hole",
		ip: $("#ip4Hole").val(),
		port: $("#port4Hole").val(),
		protocol: protocol,
		method: method,
		user_agent: userAgent,
		data: $("#data4Hole").val(),
		has_hole: $("#hasHole4Hole").find("input[name=hasHole4Hole]:checked").val(),
		rule: $("#rule4Hole").find("input[name=rule4Hole]:checked").val(),
		content: $("#content4Hole").val()
	}
}
function getShellParameter() {
	return {
		image: "shell",
		ip: $("#ip4Shell").val(),
		script: $("#script4Shell").val()
	}
}
var parameterFunctions = {
		"port": getPortParameter,
		"os": getOSParameter,
		"redns": getReDNSParameter,
		"audns": getAuDNSParameter,
		"hole": getHoleParameter,
		"shell": getShellParameter
};
$(".submitAdd").on("click", function () {
	var postJson = parameterFunctions[$(this).attr("image")]();
	console.log(postJson);
	$.post("assignTask", postJson, function () {
	//location.reload();
});
});
