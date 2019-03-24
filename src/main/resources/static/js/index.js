// 侧边栏切换监听
function handleClickSideItem() {
    $(this).addClass("blue darken-1 active");
    let href = $(this).attr("href");
    let sideItems = $(".sidebar").find("a");
    for (let i = 0; i < sideItems.length; i++) {
        let siteHref = $(sideItems[i]).attr("href");
        if (siteHref !== href) {
            $(sideItems[i]).removeClass("blue darken-1 active");
            $(siteHref).addClass("hide");
        }
    }
    $(href).removeClass("hide");
}
$(function () {
    let sidebars = $(".sidebar");
    for (let i = 0; i < sidebars.length; i++) {
        let sideItems = sidebars.find("a");
        for (let j = 0; j < sideItems.length; j++) {
            $(sideItems[j]).on("click", handleClickSideItem);
        }
    }
});