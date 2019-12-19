package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.bean.Result;
import com.netdetpla.ndp.handler.DatabaseHandler;
import com.netdetpla.ndp.utils.ResultExtractor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ResultController {
    @GetMapping("result/{image_name}")
    public ResponseEntity<?> getResult(@PathVariable("image_name") String imageName) throws SQLException {
        List<Result> data = new ArrayList<>();
        String sql = "select id, task_id from " + imageName + " order by id desc limit 20";
        ResultSet resultSet = DatabaseHandler.executeQuery(sql);
        while (resultSet.next()) {
            data.add(new Result(
                    resultSet.getInt(1),
                    resultSet.getInt(2)
            ));
        }
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }

    @GetMapping("result/{image_name}/{rid}")
    public ResponseEntity<?> getResultDetail(
            @PathVariable("image_name") String imageName,
            @PathVariable("rid") String rid
    ) throws SQLException {
        List<List<String>> data = new ArrayList<>();
        String sql = "select result_line from " + imageName + " where id=?";
        ResultSet resultSet = DatabaseHandler.executeQuery(sql, rid);
        if (resultSet.next()) {
            switch (imageName) {
                case "scanservice":
                    data = ResultExtractor.extractScanServiceResult(resultSet.getString(1));
                    break;
                case "scanweb":
                    data = ResultExtractor.extractScanWebResult(resultSet.getString(1));
                    break;
                case "scandns":
                    data = ResultExtractor.extractScanDNSResult(resultSet.getString(1));
                    break;
                case "dnssecure":
                    data = ResultExtractor.extractDNSSecureResult(resultSet.getString(1));
                    break;
                case "dnsns":
                    data = ResultExtractor.extractDNSNSResult(resultSet.getString(1));
                    break;
            }
        }
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }

}
