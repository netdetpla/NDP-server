package com.ndp.server.controller

import com.ndp.server.bean.ResponseEnvelope
import com.ndp.server.utils.DatabaseHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

@RestController
class ImageController {

    // 保存镜像文件
    @Throws(IOException::class)
    private fun writeToLocal(destination: String, input: InputStream) {
        var index: Int
        val bytes = ByteArray(1024)
        val downloadFile = FileOutputStream(destination)
        while (input.read(bytes).also { index = it } != -1) {
            downloadFile.write(bytes, 0, index)
            downloadFile.flush()
        }
        downloadFile.close()
        input.close()
    }

    class Upload(val imageName: String, val tag: String, val testParam: String)

    // 上传镜像
    @PostMapping("/image/upload")

    fun uploadFile(
            @RequestParam("file") file: MultipartFile,
            @RequestParam("image-name") imageName: String,
            @RequestParam("tag") tag: String,
            @RequestParam("test-param") testParam: String
    ): ResponseEntity<*>? {
        println(file.originalFilename)
        val size = try {
            writeToLocal("/root/image/" + file.originalFilename, file.inputStream)
            String.format("%.2fMB", file.size.toDouble() / 1024 / 1024)
        } catch (e: IOException) {
            println(e.toString())
            return ResponseEntity(ResponseEnvelope<Any?>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request"
            ), HttpStatus.BAD_REQUEST)
        }
        // TODO 镜像导入、测试任务
        println("image name: $imageName")
        println("tag: $tag")
        println("param: $testParam")
        val nowTime = Date()
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        println(time.format(nowTime))
        DatabaseHandler.insertImage(
                imageName,
                tag,
                size,
                time.format(nowTime),
                file.originalFilename ?: ""
        )
        return ResponseEntity(ResponseEnvelope<Any?>(
                HttpStatus.OK.value(),
                "Image has been uploaded."
        ), HttpStatus.OK)
    }



    @GetMapping("/image")
    fun getImages(): ResponseEntity<*>? {
        val data = DatabaseHandler.selectImage()
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/image/{image_name}")
    fun getTags(
            @PathVariable(value = "image_name") imageName: String
    ): ResponseEntity<*>? {
        val data = DatabaseHandler.selectTags(imageName)
        return ResponseEntity(ResponseEnvelope(
                HttpStatus.OK.value(),
                "OK",
                data
        ), HttpStatus.OK)
    }

    @GetMapping("/image/{image_name}/{tag}")
    fun getImageInfo(
            @PathVariable(value = "image_name") imageName: String,
            @PathVariable tag: String
    ): ResponseEntity<*>? {
        // TODO 暂留接口
        throw UnsupportedOperationException()
    }
}