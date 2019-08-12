package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.Charts;
import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.bean.SearchChart;
import com.netdetpla.ndp.handler.DatabaseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RestController
public class StatisticController {
    // 统计图表数据接口
    @GetMapping("/statistic/charts")
    public ResponseEntity<?> getCharts() throws SQLException {
        // 端口统计
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select `port`, count(*) as c from `port` group by `port` order by c desc limit 8"
        );
        String[] portLabels = new String[8];
        int[] portData = new int[8];
        for (int i = 0; i < 8; i++) {
            resultSet.next();
            portLabels[i] = String.valueOf(resultSet.getInt(1));
            portData[i] = resultSet.getInt(2);
        }
        // 服务统计
        resultSet = DatabaseHandler.executeQuery(
                "select `service`, count(*) as c from `port` where `service` != 'unknow' group by `service` order by c desc limit 8"
        );
        String[] serviceLabels = new String[8];
        int[] serviceData = new int[8];
        for (int i = 0; i < 8; i++) {
            resultSet.next();
            serviceLabels[i] = resultSet.getString(1);
            serviceData[i] = resultSet.getInt(2);
        }
        // 操作系统类型
        resultSet = DatabaseHandler.executeQuery(
                "select count(*) from `ip`"
        );
        resultSet.next();
        int total = resultSet.getInt(1);
        int osOthers = total;
        resultSet = DatabaseHandler.executeQuery(
                "select `os_version`, count(*) as c from `ip` where `os_version` != 'unknown' group by `os_version` order by c desc limit 5"
        );
        String[] osLabels = new String[6];
        int[] osData = new int[6];
        for (int i = 0; i < 5; i++) {
            resultSet.next();
            osLabels[i] = resultSet.getString(1);
            osData[i] = resultSet.getInt(2);
            osOthers -= osData[i];
        }
        osLabels[5] = "other";
        osData[5] = osOthers;
        // 硬件类型
        int hardwareOthers = total;
        resultSet = DatabaseHandler.executeQuery(
                "select `hardware`, count(*) as c from `ip` where `hardware` != 'unknown' group by `hardware` order by c desc limit 5"
        );
        String[] hardwareLabels = new String[6];
        int[] hardwareData = new int[6];
        for (int i = 0; i < 5; i++) {
            resultSet.next();
            hardwareLabels[i] = resultSet.getString(1);
            hardwareData[i] = resultSet.getInt(2);
            hardwareOthers -= hardwareData[i];
        }
        resultSet = DatabaseHandler.executeQuery(
                "select count(*) from ip"
        );
        resultSet.next();
        hardwareLabels[5] = "other";
        hardwareData[5] = hardwareOthers;
        Charts data = new Charts(
                portLabels, portData,
                serviceLabels, serviceData,
                hardwareLabels, hardwareData,
                osLabels, osData
        );
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }

    // 搜索统计数据
    @PostMapping("/statistic/search")
    public ResponseEntity<?> getSearchCharts(
            @RequestParam("type") String type,
            @RequestParam("keyword") String keyword
    ) throws SQLException {
        switch (type) {
            case "port":
                return searchPortChart(keyword);
            case "service":
                return searchServiceChart(keyword);
            default:
                return new ResponseEntity<>(new ResponseEnvelope<>(
                        HttpStatus.OK.value(),
                        "OK",
                        new int[0]
                ), HttpStatus.OK);
        }
    }

    private SearchChart search(
            String sql,
            String title,
            String type,
            String keyword,
            int total
    ) throws SQLException {
        ResultSet resultSet = DatabaseHandler.executeQuery(sql, keyword);
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        int other = total;
        while (resultSet.next()) {
            labels.add(resultSet.getString(1));
            data.add(resultSet.getInt(2));
            other -= data.get(data.size() - 1);
        }
        if (other > 0) {
            labels.add("other");
            data.add(other);
        }
        return new SearchChart(
                title,
                type,
                labels.toArray(new String[0]),
                data.stream().mapToInt(i -> i).toArray()
        );
    }

    private ResponseEntity<?> searchPortChart(String keyword) throws SQLException {
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select count(*) from `port` where `port` = ?",
                keyword
        );
        resultSet.next();
        int total = resultSet.getInt(1);
        List<SearchChart> data = new ArrayList<>();
        // protocol
        data.add(search(
                "select `protocol`, count(*) as c from `port` where `port` = ? group by `protocol` order by c desc limit 7",
                "运输层协议",
                "doughnut",
                keyword,
                total
        ));
        // service
        data.add(search(
                "select `service`, count(*) as c from `port` where `port` = ? group by `service` order by c desc limit 7",
                "服务",
                "doughnut",
                keyword,
                total
        ));
        // product
        data.add(search(
                "select `product`, count(*) as c from `port` where `port` = ? group by `product` order by c desc limit 7",
                "软件",
                "doughnut",
                keyword,
                total
        ));
        // os
        data.add(search(
                "select `os_version`, count(*) as c from `ip` where `id` in (select `ip_id` from `port` where `port` = ?) group by `os_version` order by c desc limit 7",
                "操作系统",
                "doughnut",
                keyword,
                total
        ));
        // hardware
        data.add(search(
                "select `hardware`, count(*) as c from `ip` where `id` in (select `ip_id` from `port` where `port` = ?) group by `hardware` order by c desc limit 7",
                "设备类型",
                "doughnut",
                keyword,
                total
        ));
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }
    private ResponseEntity<?> searchServiceChart(String keyword) throws SQLException {
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select count(*) from `port` where `port` = ?",
                keyword
        );
        resultSet.next();
        int total = resultSet.getInt(1);
        List<SearchChart> data = new ArrayList<>();
        // protocol
        data.add(search(
                "select `protocol`, count(*) as c from `port` where `service` = ? group by `protocol` order by c desc limit 7",
                "运输层协议",
                "doughnut",
                keyword,
                total
        ));
        // port
        data.add(search(
                "select `port`, count(*) as c from `port` where `service` = ? group by `port` order by c desc limit 7",
                "端口",
                "doughnut",
                keyword,
                total
        ));
        // product
        data.add(search(
                "select `product`, count(*) as c from `port` where `service` = ? group by `product` order by c desc limit 7",
                "软件",
                "doughnut",
                keyword,
                total
        ));
        // os
        data.add(search(
                "select `os_version`, count(*) as c from `ip` where `id` in (select `ip_id` from `port` where `service` = ?) group by `os_version` order by c desc limit 7",
                "操作系统",
                "doughnut",
                keyword,
                total
        ));
        // hardware
        data.add(search(
                "select `hardware`, count(*) as c from `ip` where `id` in (select `ip_id` from `port` where `service` = ?) group by `hardware` order by c desc limit 7",
                "设备类型",
                "doughnut",
                keyword,
                total
        ));
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }
}
