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
            @RequestParam("ip") String ip,
            @RequestParam("url") String url,
            @RequestParam("level") String level,
            @RequestParam("keyword") String keyword
    ) throws SQLException {
        System.out.println("image-name: " + imageName);
        System.out.println("tag: " + tag);
        System.out.println("task-name: " + taskName);
        System.out.println("priority: " + priority);
        System.out.println("param：" + param);

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

        if(imageName.equals("scanweb")){
            System.out.println("ip: " + ip);
            final Base64.Encoder encoder = Base64.getEncoder();

            //ip切分，把任务切分为小任务
            String[] ipInputSplit = ip.split(",");
            String[] ips =new String[256*256];
            int j = 0;
            for(int i=0;i<ipInputSplit.length;i++){
                if(ipInputSplit[i].contains("/")){
                    String[] ipAndmask = ipInputSplit[i].split("/");
                    ip = ipAndmask[0];
                    String mask = ipAndmask[1];
                    String[] ipsplit = ip.split("\\.");
                    int[] ipsplitInt = new int[4];
                    for (int k = 0; k < ipsplit.length; k++)
                        ipsplitInt[k] = Integer.parseInt(ipsplit[k]);
                    Double endIpDou=ipToDouble(getEndIpStr(ip, mask));
                    if(Integer.parseInt(mask)<24){
                        ips[j++] = ip + "/" + 24;
                        while(ipToDouble(getEndIpStr(ip,String.valueOf(24)))<endIpDou){
                            if(ipsplitInt[2]<255){
                                ipsplitInt[2]++;
                                ipsplitInt[3] = 0;
                                ip =  ipsplitInt[0] + "." + ipsplitInt[1] + "." + ipsplitInt[2] + "." + ipsplitInt[3];
                                ips[j++] = ip + "/24";
                            }else if(ipsplitInt[3]<255){
                                ipsplitInt[1]++;
                                ipsplitInt[2] = 0;
                                ip =  ipsplitInt[0] + "." + ipsplitInt[1] + "." + ipsplitInt[2] + "." + ipsplitInt[3];
                                ips[j++] = ip + "/24";
                            }else{
                                ipsplitInt[0]++;
                                ipsplitInt[1] = 0;
                                ip =  ipsplitInt[0] + "." + ipsplitInt[1] + "." + ipsplitInt[2] + "." + ipsplitInt[3];
                                ips[j++] = ip + "/24";
                            }
                        }
                    }else{
                        ips[j++]=ip+"/"+mask;
                    }
                }else{
                    ips[j++] = ipInputSplit[i];
                }
            }


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
                // TODO 处理任务添加失败
                DatabaseHandler.execute(
                        "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                        tidString,
                        taskName,
                        Integer.toString(image_id),
                        paramBase64,
                        priority
                );
            }
        }else if(imageName.equals("ecdsystem")){
            System.out.println("url：" + url);
            System.out.println("level：" + level);
            System.out.println("keyword：" + keyword);
            String[] urls = url.split(",");
            String[] urlsjson = new String[urls.length];
            if(keyword.equals("")){
                for(int i = 0;i<urls.length;i++)
                    urlsjson[i] = "{\"url\":\"" + urls[i] + "\",\"is_search\":0,\"search_level\":" + level + ",\"priority\":" + priority +"}";
                String urlMerge = urlsjson[0];
                for(int i = 1;i<urls.length;i++)
                    urlMerge = urlMerge + "," +urlsjson[i];
                param = "{\"urls\":[" + urlMerge + "],\"taskName\":\"" + taskName + "\"}";
                System.out.println("无keyword的parm："+param);
                // TODO 处理任务添加失败
                DatabaseHandler.execute(
                        "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                        tidString,
                        taskName,
                        Integer.toString(image_id),
                        param,
                        priority
                );
            }else{
//                params = {"para": {"url": "www.youku.com", "deep_level":2,"date":2,"keyword": "nba","type": "search"}}
                for(int i=0;i<urls.length;i++){
                    urlsjson[i] = "{\"url\":\"" + urls[i] + "\",\"deep_level\":" + level + ",\"date\":2,\"keyword\":\"" + keyword + "\",\"type\":\"search\"}";
                    System.out.println("有keyword的parm："+urlsjson[i]);
                    // TODO 处理任务添加失败
                    DatabaseHandler.execute(
                            "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                            tidString,
                            taskName,
                            Integer.toString(image_id),
                            urlsjson[i],
                            priority
                    );
                }
            }

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
                "select s.tid, s.task_name, s.start_time, s.end_time from " +
                        "(select *, row_number() over (partition by tid order by id) as group_idx from task) s " +
                        "where s.image_id in " +
                        "(select id from image where image_name = ?) " +
                        "and s.group_idx = 1",
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
