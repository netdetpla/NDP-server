package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.ResponseEnvelope;
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
import com.netdetpla.ndp.IpUtil;

import static com.netdetpla.ndp.IpUtil.*;

@RestController
public class TaskController {

    @PostMapping("/task")
    public ResponseEntity<?> addTask(
            @RequestParam("image-name") String imageName,
            @RequestParam("tag") String tag,
            @RequestParam("task-name") String taskName,
            @RequestParam("priority") String priority,
            @RequestParam("param") String param,
            @RequestParam("ip") String ip
    ) throws SQLException {
        System.out.println("image-name: " + imageName);
        System.out.println("tag: " + tag);
        System.out.println("task-name: " + taskName);
        System.out.println("priority: " + priority);

        System.out.println("param：" + param);
        System.out.println("ip: " + ip);
        final Base64.Encoder encoder = Base64.getEncoder();
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
        //ip切分，把任务切分为小任务
        String[] ipAndmask = ip.split("/");
        ip = ipAndmask[0];
        String mask = ipAndmask[1];
        String[] ipsplit = ip.split("\\.");
        int[] ipsplitInt = new int[4];
        for (int i = 0; i < ipsplit.length; i++)
            ipsplitInt[i] = Integer.parseInt(ipsplit[i]);
        String[] ips =new String[256*256];
        Double endIpDou=ipToDouble(getEndIpStr(ip, mask));
        if(Integer.parseInt(mask)<24){
            ips[0] = ip + "/" + 24;
            int i = 1;

            while(ipToDouble(getEndIpStr(ip,String.valueOf(24)))<endIpDou){
                if(ipsplitInt[2]<255){
                    ipsplitInt[2]++;
                    ipsplitInt[3] = 0;
                    ip =  ipsplitInt[0] + "." + ipsplitInt[1] + "." + ipsplitInt[2] + "." + ipsplitInt[3];
                    ips[i] = ip + "/24";
                    i++;
                }else if(ipsplitInt[3]<255){
                    ipsplitInt[1]++;
                    ipsplitInt[2] = 0;
                    ip =  ipsplitInt[0] + "." + ipsplitInt[1] + "." + ipsplitInt[2] + "." + ipsplitInt[3];
                    ips[i] = ip + "/24";
                    i++;
                }else{
                    ipsplitInt[0]++;
                    ipsplitInt[1] = 0;
                    ip =  ipsplitInt[0] + "." + ipsplitInt[1] + "." + ipsplitInt[2] + "." + ipsplitInt[3];
                    ips[i] = ip + "/24";
                    i++;
                }
            }
        }else{
            ips[0]=ip+"/"+mask;
        }
        System.out.println(ips.length);
        // TODO 处理任务添加失败
        for(int i=0;ips[i]!=null;i++){
            System.out.println(ips[i]);
            String mixParam = param+ips[i];
            int idnew = id + i;
            String paramString = idnew + ";" + taskName + ";0;1;" + mixParam + ";" + idnew;
            String paramBase64 = null;
            try {
                paramBase64 = encoder.encodeToString(paramString.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            DatabaseHandler.execute(
                    "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                    tidString,
                    taskName,
                    Integer.toString(image_id),
                    paramBase64,
                    priority
            );
        }

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
