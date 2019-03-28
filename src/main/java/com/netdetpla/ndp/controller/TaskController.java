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
            @RequestParam("task-name") String taskName,
            @RequestParam("param") String param
    ) throws SQLException {
        System.out.println("image-name: " + imageName);
        System.out.println("tag: " + tag);
        System.out.println("task-name: " + taskName);
        System.out.println("param: " + param);
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select id from image where image_name = ? and tag = ?",
                imageName,
                tag
        );
        resultSet.next();
        int image_id = resultSet.getInt(1);
        // TODO 处理任务添加失败
        DatabaseHandler.execute(
                "insert into task(task_name, image_id, param) values (?, ?, ?)",
                taskName,
                Integer.toString(image_id),
                param
        );
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK"
        ), HttpStatus.OK);
    }

    @GetMapping("/task/{image_name}")
    public ResponseEntity<?> getTask(@PathVariable("image_name") String imageName) throws SQLException {
        // TODO 查询任务
        List<Task> data = new ArrayList<>();
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select id, task_name, start_time, end_time from task where image_id in " +
                        "(select id from image where image_name = ?)",
                imageName
        );
        while (resultSet.next()) {
            data.add(new Task(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            ));
        }
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }
}
