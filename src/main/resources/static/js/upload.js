function submitImage() {
    // Get form
    let data = new FormData();
    data.append("file", $("#imageFile").prop("files")[0]);
    data.append("image-name", $("#imageName").val());
    data.append("tag", $("#imageTag").val());
    data.append("test-param", "1");
    $uploadSubmit.prop("disabled", true);
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
            $uploadSubmit.prop("disabled", false);
        },
        error: function (e) {
            //TODO
            alert("上传镜像失败！");
            $uploadSubmit.prop("disabled", false);
        }
    });
}
// init
let $uploadSubmit = $("#uploadSubmit");
$uploadSubmit.on("click", function () {
    submitImage();
});
