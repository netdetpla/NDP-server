package com.netdetpla.ndp.utils;

import com.netdetpla.ndp.handler.DatabaseHandler;
import org.apache.logging.log4j.util.Strings;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.netdetpla.ndp.utils.IpUtil.*;

public class ImageUtil {


    public static void scanweb(int id, String tidString, int image_id, String taskName, String priority, String[] params, int num) {
        final Base64.Encoder encoder = Base64.getEncoder();

        //ip切分，把任务切分为小任务
        String[] ips = ipSplit(params, 24);

        for (int i = 0; i < ips.length; i++) {
            System.out.println(ips[i]);
            int idnew = id + i;
            String paramString = idnew + ";" + taskName + ";0;1;" + ips[i] + ";" + idnew;
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
    }

    public static void ecdsystem(int id, int image_id, String tidString, String taskName, String priority, String[] params) {
        String url = params[0];
        String level = params[1];
        String keyword = params[2];
        String param;
        System.out.println("url：" + url);
        System.out.println("level：" + level);
        System.out.println("keyword：" + keyword);
        String[] urls = url.split(",");
        String[] urlsjson = new String[urls.length];
        if (keyword.equals("")) {
            for (int i = 0; i < urls.length; i++)
                urlsjson[i] = "{\"url\":\"" + urls[i] + "\",\"is_search\":0,\"search_level\":" + level + ",\"priority\":" + priority + "}";
            String urlMerge = urlsjson[0];
            for (int i = 1; i < urls.length; i++)
                urlMerge = urlMerge + "," + urlsjson[i];
            param = "{\"taskID\":\"" + id + "\",\"urls\":[" + urlMerge + "],\"taskName\":\"" + taskName + "\",\"type\": \"excel\",\"filename\":\"filename\"}";
            System.out.println("无keyword的parm：" + param);
            // TODO 处理任务添加失败
            DatabaseHandler.execute(
                    "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                    tidString,
                    taskName,
                    Integer.toString(image_id),
                    param,
                    priority
            );
        } else {
//                params = {"para": {"url": "www.youku.com", "deep_level":2,"date":2,"keyword": "nba","type": "search"}}
            for (int i = 0; i < urls.length; i++) {
                urlsjson[i] = "{\"taskID\":\"" + id + "\",\"url\":\"" + urls[i] + "\",\"deep_level\":" + level + ",\"date\":2,\"keyword\":\"" + keyword + "\",\"type\":\"search\"}";
                System.out.println("有keyword的parm：" + urlsjson[i]);
                // TODO 处理任务添加失败
                DatabaseHandler.execute(
                        "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                        tidString,
                        taskName,
                        Integer.toString(image_id),
                        urlsjson[i],
                        priority
                );
                id++;
            }
        }
    }

    public static void scanservice(int id, String tidString, int image_id, String taskName, String priority, String[] params, int num) {
        final Base64.Encoder encoder = Base64.getEncoder();

        String[] ip = params[0].split(",");
        String[] port = params[1].split(",");

        //ip切分，把任务切分为小任务
        String[] ips = ipSplit(ip, 24);

        //port切分，把任务切分为小任务
        String[] ports = urlSplit(portSplit(port), 100, ",");

        for (int i = 0; i < ips.length; i++) {
            for (int j = 0; j < ports.length; j++) {
                String paramString = id + ";" + taskName + ";0;" + ports[j] + ";1;0;;" + ips[i] + ";" + id;
                id++;
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

        }
    }

    public static void info_shell(int id, String tidString, int image_id, String taskName, String priority, String[] params) {
        final Base64.Encoder encoder = Base64.getEncoder();

        String[] ips = params[0].split(",");
        String script = params[1];

        for (int i = 0; i < ips.length; i++) {
            String paramString = "{\"task_id\":\"" + id + "\", \"task_name\":\"" + taskName + "\", \"vul_id\":\"1\", \"dst_ip\":\"" + ips[i] + "\"}" + "\n" + script;
            id++;
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
    }

    public static void scanvul(int id, String tidString, int image_id, String taskName, String priority, String[] params) {
        final Base64.Encoder encoder = Base64.getEncoder();

        String[] url = params[0].split(",");
        // 100个一组进行分割
        String[] urls = urlSplit(url, 100, ",");

        for (int i = 0; i < urls.length; i++) {
            String paramString = id + ";" + taskName + ";" + "0;" + "struts2;" + urls[i];
            id++;
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
    }

    public static void scandns(int id, String tidString, int image_id, String taskName, String priority, String[] params) {
        final Base64.Encoder encoder = Base64.getEncoder();

        String[] ip = params[0].split(",");
        String[] ips = ipSplit(ip, 24);

        for (int i = 0; i < ips.length; i++) {
            String paramString = id + ";" + taskName + ";" + "0;" + ips[i] + ";" + id;
            id++;
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
    }

    public static void dnssecure(int id, String tidString, int image_id, String taskName, String priority, String[] params) {
        final Base64.Encoder encoder = Base64.getEncoder();

        String[] domain = params[0].split("\\+");
        String[] domains = urlSplit(domain, 100, "+");
        String[] re_server = params[1].split("\\+");
        String[] re_servers = urlSplit(re_server, 100, "+");

        for (int i = 0; i < domains.length; i++) {
            for (int j = 0; j < re_servers.length; j++) {
                String paramString = id + "," + domains[i] + "," + re_servers[j] + "," + taskName + "," + id;
                id++;
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
        }
    }

    public static void ipTest(
            int id,
            String tidString,
            int image_id,
            String taskName,
            String priority,
            String[] params
    ) {
        String[] ips = ipSplit(params[0].split(","), 24);
        for (String ip : ips) {
            DatabaseHandler.execute(
                    "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                    tidString,
                    taskName,
                    Integer.toString(image_id),
                    ip,
                    priority
            );
        }
    }

    public static void portScan(
            int id,
            String tidString,
            int image_id,
            String taskName,
            String priority,
            String[] params
    ) {
        String[] ips = ipSplit(params[0].split(","), 24);
        String[] ports = urlSplit(portSplit(params[1].split(",")), 20, ",");

        List<String> ipSet = new ArrayList<>();
        for (String port : ports) {
            for (int i = 0, step = 0; i < ips.length; i++, step++) {
                ipSet.add(ips[i]);
                if (step >= 99 || i >= ips.length - 1) {
                    String paramString = Strings.join(ipSet, ',') + ";" + port;
                    // TODO 处理任务添加失败
                    DatabaseHandler.execute(
                            "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                            tidString,
                            taskName,
                            Integer.toString(image_id),
                            paramString,
                            priority
                    );
                    step = 0;
                    ipSet.clear();
                }
            }
        }
    }

    public static void pageCrawl(
            int id,
            String tidString,
            int image_id,
            String taskName,
            String priority,
            String[] params
    ) {
        String[] urls = urlSplit(params[0].split(","), 50, ",");

        for (String url : urls) {
            DatabaseHandler.execute(
                    "insert into task(tid, task_name, image_id, param, priority) values (?, ?, ?, ?, ?)",
                    tidString,
                    taskName,
                    Integer.toString(image_id),
                    url,
                    priority
            );
        }
    }
}
