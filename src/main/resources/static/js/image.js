function getImages() {
    let $imageBody = $("#imageList");
    $imageBody.empty("a");
    let $selectImageBtn = $("#selectImageBtn");
    $.get("/image", {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $imageBody.append("<tr></tr>");
            $imageBody.append("<a class=\"dropdown-item\" href=\"#\">" + data[i] + "</a>");
            let $row = $imageBody.find("a:last");
            $row.on("click", function () {
                let image = $(this).html();
                $selectImageBtn.html(image);
                getTags(image);
            });
        }
    });
}
function getTags(imageName) {
    let $tagBody = $("#imageTableBody");
    $tagBody.empty("tr");
    $.get("/image/" + imageName, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $tagBody.append("<tr></tr>");
            let $row = $tagBody.find("tr:last");
            $row.append("<td>" + imageName + "</td>");
            $row.append("<td>" + data[i].tag + "</td>");
            $row.append("<td>" + data[i]['upload-time'] + "</td>");
            $row.append("<td>" + data[i]['size'] + "</td>");
        }
    })
}
// init
getImages();
