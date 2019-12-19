package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.Image;
import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.handler.DatabaseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@RestController
public class ImageController {

    // 保存镜像文件
    private static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        input.close();
    }

    // 上传镜像
    @PostMapping("/image/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadFile,
            @RequestParam("image-name") String imageName,
            @RequestParam("tag") String tag,
            @RequestParam("test-param") String testParam
    ) {
        String size = "";
        try {
//            saveUploadedFiles(Collections.singletonList(uploadFile));
            writeToLocal("/root/image/" + uploadFile.getOriginalFilename(), uploadFile.getInputStream());
            size = String.format("%.2fMB", (double) uploadFile.getSize() / 1024 / 1024);
        } catch (IOException e) {
            System.out.println(e.toString());
            return new ResponseEntity<>(new ResponseEnvelope<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request"
            ), HttpStatus.BAD_REQUEST);
        }
        // TODO 镜像导入、测试任务
        System.out.println("image name: " + imageName);
        System.out.println("tag: " + tag);
        System.out.println("param: " + testParam);
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(time.format(nowTime));
        DatabaseHandler.execute(
                "insert into image(image_name, tag, `size`, upload_time, file_name) values (?, ?, ?, ?, ?)",
                imageName,
                tag,
                size,
                time.format(nowTime),
                uploadFile.getOriginalFilename()
        );
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "Image has been uploaded."
        ), HttpStatus.OK);
    }

    @GetMapping("/image")
    public ResponseEntity<?> getImages() throws SQLException {
        List<String> data = new ArrayList<>();
        ResultSet resultSet = DatabaseHandler.executeQuery("select distinct image_name from image");
        while (resultSet.next()) {
            data.add(resultSet.getString(1));
        }
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }

    @GetMapping("/image/{image_name}")
    public ResponseEntity<?> getTags(@PathVariable(value = "image_name") String imageName) throws SQLException {
        List<Image> data = new ArrayList<>();
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select tag, upload_time, `size` from image where image_name = ?",
                imageName
        );
        while (resultSet.next()) {
            data.add(new Image(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3)
            ));
        }
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }

    @GetMapping("/image/{image_name}/{tag}")
    public ResponseEntity<?> getImageInfo(
            @PathVariable(value = "image_name") String imageName,
            @PathVariable String tag
    ) throws SQLException {
        //TODO 查询任务
        ResultSet resultSet = DatabaseHandler.executeQuery(
                "select upload_time, size from image where image_name = ? and tag = ?",
                imageName,
                tag
        );
        resultSet.next();
        // TODO 镜像大小
        // TODO 测试数据
        Image data = new Image(
                imageName,
                tag,
                true,
                resultSet.getString(1),
                resultSet.getString(2),
                0.5f,
                1024,
                20
        );
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }
}
