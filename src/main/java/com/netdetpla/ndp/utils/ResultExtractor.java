package com.netdetpla.ndp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ResultExtractor {
    public static List<List<String>> extractScanServiceResult(String resultLine) {
        List<List<String>> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> origin = mapper.readValue(resultLine, Map.class);
            List<Map> resultList = (List<Map>) origin.get("result");
            if (resultList.size() == 0) return result;
            Map resultMap = resultList.get(0);
            List<String> ip = new ArrayList<>();
            ip.add("IP地址");
            ip.add((String) resultMap.get("ip"));
            result.add(ip);
            List<String> hardware = new ArrayList<>();
            hardware.add("硬件类型");
            hardware.add((String) resultMap.get("hardware"));
            result.add(hardware);
            List<String> osVersion = new ArrayList<>();
            osVersion.add("操作系统");
            osVersion.add((String) resultMap.get("os_version"));
            result.add(osVersion);
            StringBuilder portBuilder = new StringBuilder();
            for (Map p : (List<Map>) resultMap.get("ports")) {
                portBuilder.append(',');
                portBuilder.append(p.get("port"));
            }
            portBuilder.deleteCharAt(0);
            List<String> port = new ArrayList<>();
            port.add("开放端口");
            port.add(portBuilder.toString());
            result.add(port);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }

    public static List<List<String>> extractScanWebResult(String resultLine) {
        List<List<String>> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> origin = mapper.readValue(resultLine, Map.class);
            Map resultMap = (Map) origin.get("result");
            List<String> ip = new ArrayList<>();
            ip.add("IP地址");
            ip.add((String) resultMap.get("ip"));
            result.add(ip);
            List<String> hardware = new ArrayList<>();
            hardware.add("硬件类型");
            hardware.add((String) resultMap.get("hardware"));
            result.add(hardware);
            List<String> osVersion = new ArrayList<>();
            osVersion.add("操作系统");
            osVersion.add((String) resultMap.get("os_version"));
            result.add(osVersion);
            List<Map> ports = (List<Map>) resultMap.get("ports");
            if (ports.size() == 0) return result;
            StringBuilder portBuilder = new StringBuilder();
            for (Map p : ports) {
                portBuilder.append(',');
                portBuilder.append(p.get("port"));
            }
            portBuilder.deleteCharAt(0);
            List<String> port = new ArrayList<>();
            port.add("开放的Web端口");
            port.add(portBuilder.toString());
            result.add(port);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }

    public static List<List<String>> extractScanDNSResult(String resultLine) {
        String[] resultList = resultLine.split(",");
        List<List<String>> result = new ArrayList<>();
        List<String> ip = new ArrayList<>();
        ip.add("IP地址");
        ip.add(resultList[0]);
        result.add(ip);
        List<String> tcpStatus = new ArrayList<>();
        tcpStatus.add("TCP 53");
        if (resultList[1].equals("open")) {
            tcpStatus.add("开放");
        } else {
            tcpStatus.add("未开放");
        }
        result.add(tcpStatus);
        List<String> udpStatus = new ArrayList<>();
        udpStatus.add("UDP 53");
        if (resultList[2].equals("open")) {
            udpStatus.add("开放");
        } else {
            udpStatus.add("未开放");
        }
        result.add(udpStatus);
        List<String> recursion = new ArrayList<>();
        recursion.add("是否支持递归");
        if (resultList[3].equals("1")) {
            recursion.add("是");
        } else {
            recursion.add("否");
        }
        result.add(recursion);
        List<String> version = new ArrayList<>();
        version.add("DNS软件版本");
        version.add(resultList[4]);
        result.add(version);
        return result;
    }

    public static List<List<String>> extractDNSSecureResult(String resultLine) {
        String[] resultList = resultLine.split(";");
        List<List<String>> result = new ArrayList<>();
        List<String> url = new ArrayList<>();
        url.add("域名");
        url.add(resultList[2]);
        result.add(url);
        List<String> reDNS = new ArrayList<>();
        reDNS.add("递归服务器");
        reDNS.add(resultList[3]);
        result.add(reDNS);
        String[] v = resultList[4].split("/");
        List<String> aRecord = new ArrayList<>();
        aRecord.add("A记录");
        if (resultList[4].equals("/")) {
            aRecord.add("");
        } else {
            aRecord.add(v[0].replace('+', ','));
        }
        result.add(aRecord);
        List<String> cName = new ArrayList<>();
        cName.add("CNAME");
        if (resultList[4].equals("/") || v.length < 2) {
            cName.add("");
        } else {
            cName.add(v[1].replace('+', ','));
        }
        result.add(cName);
        return result;
    }

    public static List<List<String>> extractDNSNSResult(String resultLine) {
        String[] resultList = resultLine.split(";");
        List<List<String>> result = new ArrayList<>();
        List<String> url = new ArrayList<>();
        url.add("域名");
        url.add(resultList[2]);
        result.add(url);
        List<String> reDNS = new ArrayList<>();
        reDNS.add("递归服务器");
        reDNS.add(resultList[3]);
        result.add(reDNS);
        String[] v = resultList[4].split("/");
        List<String> aRecord = new ArrayList<>();
        aRecord.add("NS记录");
        if (resultList[4].equals("/")) {
            aRecord.add("");
        } else {
            aRecord.add(v[0].replace('+', ','));
        }
        result.add(aRecord);
        List<String> cName = new ArrayList<>();
        cName.add("NS记录指向");
        if (resultList[4].equals("/") || v.length < 2) {
            cName.add("");
        } else {
            cName.add(v[1].replace('+', ','));
        }
        result.add(cName);
        return result;
    }
}
