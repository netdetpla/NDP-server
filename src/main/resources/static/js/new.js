function submitTask() {
    let data = new FormData();
    data.append("image-name", $selectImageBtn.html());
    data.append("tag", $selectTag.html());
    data.append("task-name", $("#taskName").val());
    data.append("priority", $("#taskPriority").val());
    let params = $("#taskParam").val().split(",");
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
function getImages() {
    let $imageBody = $("#imageList");
    $imageBody.empty("button");
    $.get("/image", {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $imageBody.append(
                "<button type=\"button\"  data-dismiss=\"modal\" class=\"btn btn-primary animation-on-hover\">"
                + data[i] + "</button>"
            );
            let $row = $imageBody.find("button:last");
            $row.on("click", function () {
                let image = $(this).html();
                $selectImageBtn.html(image);
                $selectTag.on("click", function () {
                    getTags($selectImageBtn.html());
                });
                $selectTag.attr("disabled", false);
            });
        }
    });
}
function getTags(imageName) {
    let $tagBody = $("#tagList");
    $tagBody.empty("button");
    $.get("/image/" + imageName, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $tagBody.append(
                "<button type=\"button\"  data-dismiss=\"modal\" class=\"btn btn-primary animation-on-hover\">"
                + data[i].tag + "</button>"
            );
            let $row = $tagBody.find("button:last");
            $row.on("click", function () {
                let tag = $(this).html();
                $selectTag.html(tag);
            });
        }
    });
}
// init
let $selectImageBtn = $("#selectImage");
let $selectTag = $("#selectTag");
$selectImageBtn.on("click", function () {
    getImages();
});
$("#submitTask").on("click", function () {
    submitTask()
});
