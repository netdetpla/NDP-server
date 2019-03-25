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
    var $mask = $("#mask");
    $mask.css("z-index", "2");
    $mask.css("background-color", "rgba(0, 0, 0, 0.5)");
}
// 卡片关闭及动画
function closeCard($card) {
    $card.css("display", "none");
    var $mask = $("#mask");
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
$("#imageTableLink").on("click", function () {
    getImages();
});
$("#okImageDetail").on("click", function () {
    closeCard($("#imageDetailCard"));
});

getImages();
