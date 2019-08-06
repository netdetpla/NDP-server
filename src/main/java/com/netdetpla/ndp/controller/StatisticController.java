package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.Charts;
import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.handler.DatabaseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;

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
}
