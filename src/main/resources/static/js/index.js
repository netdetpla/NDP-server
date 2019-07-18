// 侧边栏切换监听
function handleClickSideItem() {
    $(this).removeClass("blue darken-1 active");
    let href = $(this).attr("href");
    let sideItems = $(".sidebar").find("a");
    for (let i = 0; i < sideItems.length; i++) {
        let siteHref = $(sideItems[i]).attr("href");
        if (siteHref !== href) {
            $(sideItems[i]).addClass("blue darken-1 active");
            $(siteHref).addClass("hide");
        }
    }
    $(href).removeClass("hide");
}

// 卡片显示及动画
function openCard($card) {
    $card.css("display", "block");
    let $mask = $("#mask");
    $mask.css("z-index", "2");
    $mask.css("background-color", "rgba(0, 0, 0, 0.5)");
}

// 卡片关闭及动画
function closeCard($card) {
    $card.css("display", "none");
    let $mask = $("#mask");
    $mask.css("background-color", "rgba(0, 0, 0, 0)");
    $mask.one("transitionend", function () {
        $mask.css("z-index", "-1");
    })
}

function getImageDetail(imageName, tag) {
    $.get(["image", imageName, tag].join("/"), {}, function (json) {
        $("#IDCImageName").html(json.data["image-name"]);
        $("#IDCTag").html(json.data.tag);
        $("#IDCCurrentUse").html(json.data["current-use"]);
        $("#IDCUploadTime").html(json.data["upload-time"]);
        $("#IDCSize").html(json.data.size);
        $("#IDCCPU").html(json.data.test.cpu);
        $("#IDCMemory").html(json.data.test.memory);
        $("#IDCBandwidth").html(json.data.test.bandwidth);
        $("#imageDetailTitle").html([imageName, tag].join(":"));
    });
}

//2019.4.15添加子任务弹窗功能
function getSubTask(imageName, tid, task_name) {
    $("#subTaskTitle").html(imageName + ":" + task_name + "  子任务列表");
    let $subTaskBody = $("#subTaskTableBody");
    $subTaskBody.empty("tr");
    $.get("task/" + imageName + "/" + task_name, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $subTaskBody.append("<tr></tr>");
            let $row = $subTaskBody.find("tr:last");
            $row.append("<td>" + (data[i].id || "") + "</td>");
            $row.append("<td>" + (data[i]['start-time'] || "") + "</td>");
            $row.append("<td>" + (data[i]['end-time'] || "") + "</td>");
            $row.append("<td>" + (data[i]['param'] || "") + "</td>");
            $row.append("<td>" + (data[i]['task-status'] || "") + "</td>");
            $row.append("<td>" + (data[i]['priority'] || "") + "</td>");
        }
        firstPage2();
    })
}

function getSubResult(imageName, rid) {
    let $subResultBody = $("#subResultTable");
    $subResultBody.empty("tr");
    $.get("result/" + imageName + "/" + rid, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $subResultBody.append("<tr></tr>");
            let $row = $subResultBody.find("tr:last");
            $row.append("<th>" + data[i][0] + "</th>");
            $row.append("<td>" + data[i][1] + "</td>");
        }
    });
}

function getTags(imageName) {
    $("#tagTitle").find("span:first").html(imageName);
    let $tagBody = $("#tagTableBody");
    $tagBody.empty("tr");
    $.get("image/" + imageName, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $tagBody.append("<tr></tr>");
            let $row = $tagBody.find("tr:last");
            $row.append("<td>" + data[i].tag + "</td>");
            $row.append("<td>" + data[i]['upload-time'] + "</td>");
            // TODO 添加操作按钮
            $row.append("<td></td>");
            $row.on("click", function () {
                getImageDetail(imageName, $(this).find("td:first").html());
                openCard($("#imageDetailCard"));
            });
        }
    })
}

function getImages() {
    let $imageBody = $("#imageTableBody");
    $imageBody.empty("tr");
    $.get("image", {}, function (json) {
        // json = JSON.parse(jsonStr);
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $imageBody.append("<tr></tr>");
            let $row = $imageBody.find("tr:last");
            $row.append("<td>" + data[i] + "</td>");
            $row.on("click", function () {
                getTags($(this).find("td:first").html());
            });
        }
    });
}

function submitImage() {
    // Get form
    let data = new FormData();
    data.append("file", $("#imageFile").prop("files")[0]);
    data.append("image-name", $("#imageName").val());
    data.append("tag", $("#imageTag").val());
    data.append("test-param", $("#testParam").val());
    $("#uploadSubmit").prop("disabled", true);
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/image/upload",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            //TODO
            alert("上传镜像成功！");
            $("#uploadSubmit").prop("disabled", false);
        },
        error: function (e) {
            //TODO
            alert("上传镜像失败！");
            $("#uploadSubmit").prop("disabled", false);
        }
    });
}

function getTasks(imageName) {
    $("#taskTitle").find("span:first").html(imageName);
    let $taskBody = $("#taskTableBody");
    $taskBody.empty("tr");
    $.get("task/" + imageName, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $taskBody.append("<tr></tr>");
            let $row = $taskBody.find("tr:last");
            $row.append("<td>" + (data[i]['tid'] || "") + "</td>");
            $row.append("<td>" + (data[i]['task-name'] || "") + "</td>");
            $row.append("<td>" + (data[i]['start-time'] || "") + "</td>");
            $row.append("<td>" + (data[i]['end-time'] || "") + "</td>");
            $row.on("click", function () {
                getSubTask(imageName, $(this).find("td:first").html(), $(this).children('td').eq(1).html());
                openCard($("#subTaskCard"));
            });
        }
        firstPage();
    })
}

//增加分页功能
var pagesize = 10;   /*每页条数*/
var currentPage;
var startRow;
var lastRow;
function pagination(i){
    departmentInfo = document.getElementById("taskTableBody");  /*获取table中的内容*/
    totalRow = departmentInfo.rows.length;   /*计算出总条数(这种方法用来获取table行数，获取列数用var cells = departmentsInfo.rows[0].cells.length;*/
    totalPage = Math.ceil(totalRow/pagesize);
    currentPage = i;/*当前页*/
    document.getElementById("numPage").value="第"+currentPage+"页";   /*显示页码*/
    startRow = (currentPage-1)*pagesize;/*每页显示第一条数据的行数*/
    lastRow = currentPage*pagesize;/*每页显示的最后一条数据的行数,因为表头也算一行，所以这里不需要减1，这种情况以自己的实际情况为准*/
    if(lastRow>totalRow){
        lastRow=totalRow;/*如果最后一页的最后一条数据显示的行数大于总条数，那么就让最后一条的行数等于总条数*/
    }
    for(var i = 0;i<totalRow;i++){   /*将数据遍历出来*/
        var hrow = departmentInfo.rows[i];
        if(i>=startRow&&i<lastRow){
            hrow.style.display="table-row";
        }else{
            hrow.style.display="none";
        }
    }
}
function firstPage(){
    var i = 1;
    pagination(i);
}
function prevPage(){
    var i= currentPage-1;
    if(i<1){
        i=currentPage;
    }
    pagination(i);
}
function pnextPage(){
    var i= currentPage+1;
    if(i>totalPage){
        i= currentPage;
    }
    pagination(i);
}
function lastPage() {
    var i = totalPage;
    pagination(i);
}

var pagesize2 = 10;   /*每页条数*/
var currentPage2;
var startRow2;
var lastRow2;
function pagination2(i){
    departmentInfo2 = document.getElementById("subTaskTableBody");  /*获取table中的内容*/
    totalRow2 = departmentInfo2.rows.length;   /*计算出总条数(这种方法用来获取table行数，获取列数用var cells = departmentsInfo.rows[0].cells.length;*/
    totalPage2 = Math.ceil(totalRow2/pagesize2);
    currentPage2 = i;/*当前页*/
    document.getElementById("numPage2").value="第"+currentPage2+"页";   /*显示页码*/
    startRow2 = (currentPage2-1)*pagesize2;/*每页显示第一条数据的行数*/
    lastRow2 = currentPage2*pagesize2;/*每页显示的最后一条数据的行数,因为表头也算一行，所以这里不需要减1，这种情况以自己的实际情况为准*/
    if(lastRow2>totalRow2){
        lastRow2=totalRow2;/*如果最后一页的最后一条数据显示的行数大于总条数，那么就让最后一条的行数等于总条数*/
    }
    for(var i = 0;i<totalRow2;i++){   /*将数据遍历出来*/
        var hrow = departmentInfo2.rows[i];
        if(i>=startRow2&&i<lastRow2){
            hrow.style.display="table-row";
        }else{
            hrow.style.display="none";
        }
    }
}
function firstPage2(){
    var i = 1;
    pagination2(i);
}
function prevPage2(){
    var i= currentPage2-1;
    if(i<1){
        i=currentPage2;
    }
    pagination2(i);
}
function pnextPage2(){
    var i= currentPage2+1;
    if(i>totalPage2){
        i= currentPage2;
    }
    pagination2(i);
}
function lastPage2() {
    var i = totalPage2;
    pagination2(i);
}
//分页结束
function getImages4Task() {
    let $imageBody = $("#image4TaskTableBody");
    $imageBody.empty("tr");
    $.get("image", {}, function (json) {
        // json = JSON.parse(jsonStr);
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $imageBody.append("<tr></tr>");
            let $row = $imageBody.find("tr:last");
            $row.append("<td>" + data[i] + "</td>");
            $row.on("click", function () {
                getTasks($(this).find("td:first").html());
            });
        }
    });
}

function getImages4Result() {
    let $imageBody = $("#image4ResultTableBody");
    $imageBody.empty("tr");
    $.get("image", {}, function (json) {
        // json = JSON.parse(jsonStr);
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $imageBody.append("<tr></tr>");
            let $row = $imageBody.find("tr:last");
            $row.append("<td>" + data[i] + "</td>");
            $row.on("click", function () {
                getResult($(this).find("td:first").html());
            });
        }
    });
}

function getImages4Select() {
    let $imageBody = $("#selectImageTableBody");
    let imageParam = {
        "scanweb": ["ip"],
        "ecdsystem": ["url", "level", "keyword"],
        "scanservice": ["ip", "port"],
        "info_shell": ["ip", "script"],
        "scanvul": ["url"],
        "scandns": ["ip"],
        "dnssecure": ["domains", "re_server"]
    };
    $imageBody.empty("tr");
    $.get("image", {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $imageBody.append("<tr></tr>");
            let $row = $imageBody.find("tr:last");
            $row.append("<td>" + data[i] + "</td>");
            $row.on("click", function () {
                $("#selectImage").html($(this).find("td:first").html());
                //修改了参数显示的UI
                imageName = $("#selectImage").html();
                paramChange(imageParam[imageName]);
                $("#selectTag").removeClass("disabled").html("选择标签");
                closeCard($("#selectImageCard"));
            });
        }
    });
}

//根据不同镜像，显示不同参数输入框
function paramChange(params) {
    let $paramDiv = $('#paramDiv');
    $paramDiv.empty();
    for (let i = 0; i < params.length; i++) {
        $paramDiv.append("<div class=\"input-field\">" +
            "<input id=\"" + params[i] + "\" type=\"text\" name=\"" + params[i] + "\" class=\"validate\">" +
            "<label for=\"" + params[i] + "\">" + params[i] + "</label></div>")
    }
}

function getTags4Select(imageName) {
    let $tagBody = $("#selectTagTableBody");
    $tagBody.empty("tr");
    $.get("image/" + imageName, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $tagBody.append("<tr></tr>");
            let $row = $tagBody.find("tr:last");
            $row.append("<td>" + data[i].tag + "</td>");
            $row.on("click", function () {
                $("#selectTag").html($(this).find("td:first").html());
                closeCard($("#selectTagCard"));
            });
        }
    })
}

function resetCreateTask() {
    $("#selectImage").html("选择镜像");
    $("#selectTag").addClass("disabled").html("选择标签");
    $("#taskParam").val("");
    $("#taskName").val("");
    $("#taskip").val("");
    $("#url").val("");
    $("#level").val("");
    $("#keyword").val("");
    $("#paramshow").css('display', 'block');
    $("#ipshow").css('display', 'none');
    for (let j = 1; j < 4; j++)
        $("#ecdsystem" + j).css('display', 'none')
}

function submitTask() {
    let data = new FormData();
    data.append("image-name", $("#selectImage").html());
    data.append("tag", $("#selectTag").html());
    data.append("task-name", $("#taskName").val());
    data.append("priority", $("#taskPriority").val());
    let params = [];
    $("#paramDiv input").each(function () {
        params.push(this.value);
    });
    for (let i = 0; i < params.length; i++) {
        data.append("params[]", params[i]);
    }
    $.ajax({
        type: "POST",
        url: "/task",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        success: function (data) {
            //TODO
            alert("新建任务成功！");
            resetCreateTask();
        },
        error: function (e) {
            //TODO
            alert("新建任务失败！");
            resetCreateTask();
        }
    });
}

//预加载
$(function () {
    let sidebars = $(".sidebar");
    for (let i = 0; i < sidebars.length; i++) {
        let sideItems = sidebars.find("a");
        for (let j = 0; j < sideItems.length; j++) {
            $(sideItems[j]).on("click", handleClickSideItem);
        }
    }
});
// click 绑定
// 侧边 - 镜像列表
$("#imageTableLink").on("click", function () {
    getImages();
});
// 侧边 - 任务列表
$("#taskTableLink").on("click", function () {
    getImages4Task();
});
// 侧边 - 结果列表
$("#resultTableLink").on("click", function () {
    getImages4Result();
});
//镜像详细信息 - ok
$("#okImageDetail").on("click", function () {
    closeCard($("#imageDetailCard"));
});
// 镜像上传 - 上传
$("#uploadSubmit").on("click", function (event) {
    //stop submit the form, we will post it manually.
    event.preventDefault();
    submitImage();
});
// 任务创建 - 选择镜像
$("#selectImage").on("click", function () {
    getImages4Select();
    openCard($("#selectImageCard"));
});
// 任务创建 - 选择标签
$("#selectTag").on("click", function () {
    getTags4Select($("#selectImage").html());
    openCard($("#selectTagCard"));
});
// 选择镜像 - 取消
$("#cancelSelectImage").on("click", function () {
    closeCard($("#selectImageCard"));
});
// 选择标签 - 取消
$("#cancelSelectTag").on("click", function () {
    closeCard($("#selectTagCard"));
});
//子任务展示 - 取消
$("#cancelSubTask").on("click", function () {
    closeCard($("#subTaskCard"));
});
// 结果详情 - 取消
$("#cancelSubResult").on("click", function () {
    closeCard($("#subResultCard"));
});
$("#submitTask").on("click", function () {
    submitTask();
});
//首次加载
getImages();
