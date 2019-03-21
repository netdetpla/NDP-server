package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.bean.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TaskController {

    @PostMapping("/task")
    public ResponseEntity<?> addTask(
            @RequestParam("image-name") String imageName,
            @RequestParam("tag") String tag,
            @RequestParam("param") String param
    ) {
        // TODO 处理新建任务
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
                "scanservice",
                "1.0.0",
                10000,
                "2019-03-19 09:00:00",
                "2019-03-19 10:00:00"
        ));
        data.add(new Task(
                "scanservice",
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
