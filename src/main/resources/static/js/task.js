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
                getTasks(image);
            });
        }
    });
}
function getTasks(imageName) {
    let $tagBody = $("#taskTableBody");
    $tagBody.empty("tr");
    $.get("/task/" + imageName, {}, function (json) {
        let data = json.data;
        for (let i = 0; i < data.length; i++) {
            $tagBody.append("<tr></tr>");
            let $row = $tagBody.find("tr:last");
            $row.append("<td>" + (data[i]['tid'] || "") + "</td>");
            $row.append("<td>" + (data[i]['task-name'] || "") + "</td>");
            $row.append("<td>" + (data[i]['start-time'] || "") + "</td>");
            $row.append("<td>" + (data[i]['end-time'] || "") + "</td>");
        }
    });
}
// init
getImages();