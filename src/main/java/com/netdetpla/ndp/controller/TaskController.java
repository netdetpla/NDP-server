package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.bean.Task;
import com.netdetpla.ndp.handler.DatabaseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TaskController {

    @PostMapping("/task")
    public ResponseEntity<?> addTask(
            @RequestParam("image-name") String imageName,
            @RequestParam("tag") String tag,
            @RequestParam("param") String param
    ) throws SQLException {
        System.out.println("image-name: " + imageName);
        System.out.println("tag: " + tag);
        System.out.println("param: " + param);
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select id from image where image_name = ? and tag = ?",
                imageName,
                tag
        );
        int image_id = resultSet.getInt(0);
        // TODO 处理任务添加失败
        DatabaseHandler.execute("insert into task(image_id, param) values (?, ?)", Integer.toString(image_id), param);
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK"
        ), HttpStatus.OK);
    }

    @GetMapping("/task/{image_name}")
    public ResponseEntity<?> getTask(@PathVariable("image_name") String image) {
        // TODO 查询任务
        List<Task> data = new ArrayList<>();
        data.add(new Task(
                "scanA",
                "1.0.0",
                10000,
                "2019-03-19 09:00:00",
                "2019-03-19 10:00:00"
        ));
        data.add(new Task(
                "scanB",
                "1.0.0",
                10001,
                "2019-03-19 09:00:00",
                "2019-03-19 10:00:00"
        ));
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }
}
