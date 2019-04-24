package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.bean.SubTask;
import com.netdetpla.ndp.bean.Task;
import com.netdetpla.ndp.handler.DatabaseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.netdetpla.ndp.utils.ImageUtil.*;
import static com.netdetpla.ndp.utils.IpUtil.*;

@RestController
public class TaskController {

    @PostMapping("/task")
    public ResponseEntity<?> addTask(
            @RequestParam("image-name") String imageName,
            @RequestParam("tag") String tag,
            @RequestParam("task-name") String taskName,
            @RequestParam("priority") String priority,
            @RequestParam("params[]") String params[]
    ) throws SQLException {
        System.out.println("image-name: " + imageName);
        System.out.println("tag: " + tag);
        System.out.println("task-name: " + taskName);
        System.out.println("priority: " + priority);
        for(int k=0;k<params.length;k++){
            System.out.println("param"+k+"：" + params[k]);
        }

        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select id from image where image_name = ? and tag = ?",
                imageName,
                tag
        );
        resultSet.next();
        int image_id = resultSet.getInt(1);
        //获取id,tid
        ResultSet resultSet2 = DatabaseHandler.executeQuery(
                "select max(id),max(tid) from task");
        resultSet2.next();
        int id = resultSet2.getInt(1)+1;
        int tid = resultSet2.getInt(2)+1;
        String tidString = String.valueOf(tid);

        switch (imageName){
            case "scanweb":
                scanweb(id,tidString,image_id,taskName,priority,params,24);
                break;
            case "ecdsystem":
                ecdsystem(id,image_id,tidString,taskName,priority,params);
                break;
            case "scanservice":
                scanservice(id,tidString,image_id,taskName,priority,params,24);
                break;
            default:
                break;
        }


        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK"
        ), HttpStatus.OK);
    }

    @GetMapping("/task/{image_name}/{task_name}")
    public ResponseEntity<?> getSubTask(@PathVariable(value = "task_name") String task_name) throws SQLException {
        List<SubTask> data = new ArrayList<>();
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select id,start_time,end_time,param,task_status,priority from task where task_name = ?",
                task_name
        );
        while (resultSet.next()) {
            data.add(new SubTask(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getInt(6)
            ));
        }
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }

    @GetMapping("/task/{image_name}")
    public ResponseEntity<?> getTask(@PathVariable("image_name") String imageName) throws SQLException {
        // TODO 查询任务
        List<Task> data = new ArrayList<>();
        ResultSet resultSet = DatabaseHandler.executeQuery(
//                "select s.tid, s.task_name, s.start_time, s.end_time from " +
//                        "(select *, row_number() over (partition by tid order by id) as group_idx from task) s " +
//                        "where s.image_id in " +
//                        "(select id from image where image_name = ?) " +
//                        "and s.group_idx = 1",
                "select  tid, task_name, start_time, end_time from task where image_id in (select id from image where image_name = ?) GROUP BY tid ORDER BY tid desc",
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
