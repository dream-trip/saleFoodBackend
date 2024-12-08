package com.sale.uploadfile.controller;

import com.sale.uploadfile.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController { //提交
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient minioClient;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("/yyy/MM/dd/");
    @PostMapping("/image")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception{
        // 文件大小
        long size = file.getSize();
        if (size == 0) {
            return ResponseEntity.badRequest().body("禁止上传空文件");
        }
        // 文件名称
        String fileName = file.getOriginalFilename();
        // 文件后缀
        String ext = "";
        int index = fileName.lastIndexOf(".");
        if (index ==-1) {
            return ResponseEntity.badRequest().body("禁止上传无后缀的文件");
        }
        ext = fileName.substring(index);
        // 文件类型
        String contentType = file.getContentType();
        if (contentType.isEmpty()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        // 根据日期打散目录，使用 UUID 重命名文件
        String filePath = formatter.format(LocalDate.now()) +
                UUID.randomUUID().toString().replace("-", "") +
                ext;

        log.info("文件名称：{}", fileName);
        log.info("文件大小：{}", size);
        log.info("文件类型：{}", contentType);
        log.info("文件路径：{}", filePath);


        try(InputStream inputStream = file.getInputStream()){
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())		// 指定 Bucket
                    .contentType(contentType)	// 指定 Content Type
                    .object(filePath)			// 指定文件的路径
                    .stream(inputStream, size, -1) // 文件的 Inputstream 流
                    .build());
        }
        return ResponseEntity.ok(minioConfig.getEndpoint()+minioConfig.getBucketName()+filePath);
    }
}














