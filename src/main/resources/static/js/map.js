function mapInit() {
    var map = new AMap.Map('mapContainer', {
        mapStyle: 'amap://styles/grey',
        zoom: 5,
        center: [107.4976, 36.1697],
        viewMode: '3D'
    });

    var layer = new Loca.RoundPointLayer({
        fitView: true,
        map: map,
        eventSupport: true,
    });

    $.get("/statistic/map", {}, function (json) {
        let data = json.data;
        layer.setData(data, {
            lnglat: function (data) {
                return data.value.lnglat;
            }
        });
        layer.setOptions({
            style: {
                radius: 10,
                color: function (data) {
                    switch (data.value.level) {
                        case 2:
                            return "#08519c";
                        case 1:
                            return "#6baed6";
                        default:
                            return "#c6dbef";
                    }
                },
                opacity: 0.6,
                borderWidth: 0,
                borderColor: '#eee'
            }
        });

        layer.render();
    });
}
// init
mapInit();