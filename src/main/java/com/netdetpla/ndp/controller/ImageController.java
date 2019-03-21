package com.netdetpla.ndp.controller;

import com.netdetpla.ndp.bean.Image;
import com.netdetpla.ndp.bean.ResponseEnvelope;
import com.netdetpla.ndp.bean.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class ImageController {
    // 保存镜像文件
    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            if (file.isEmpty())
                continue;
            byte[] bytes = file.getBytes();
            Path path = Paths.get("./" + file.getOriginalFilename());
            Files.write(path, bytes);
        }
    }

    // 上传镜像
    @PostMapping("/image/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadFile,
            @RequestParam("image-name") String imageName,
            @RequestParam("tag") String tag,
            @RequestParam("test-param") String testParam
    ) {
        try {
            saveUploadedFiles(Collections.singletonList(uploadFile));
        } catch (IOException e) {
            return new ResponseEntity<>(new ResponseEnvelope<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request"
            ), HttpStatus.BAD_REQUEST);
        }
        // TODO 镜像导入、测试任务
        System.out.println("image name: " + imageName);
        System.out.println("tag: " + tag);
        System.out.println("param: " + testParam);
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "Image has been uploaded."
        ), HttpStatus.OK);
    }

    @GetMapping("/image")
    public ResponseEntity<?> getImages() {

        // TODO 查询任务
        List<String> data = new ArrayList<>();
        data.add("scanservice");
        data.add("dnssecure");
        data.add("dnsau");
        return new ResponseEntity<>(new ResponseEnvelope<>(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK);
    }

    @GetMapping("/image/{image_name}")
    public ResponseEntity<?> getImageInfo(@PathVariable String imageName) {
        //TODO 查询任务
        Image data = new Image(
                imageName,
                "1.0.0",
                true,
                "2019-03-01 09:00:00",
                "800MB",
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
