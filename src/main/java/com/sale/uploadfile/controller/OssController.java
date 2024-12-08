package com.sale.uploadfile.controller;


import com.sale.uploadfile.config.MinioConfig;
import com.sale.uploadfile.entity.Result;
import io.minio.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/oss")
public class OssController {
    private Map<String, String> myMap;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient minioClient;
    @PostMapping("/upload")
    public Result upload(@RequestParam("file")MultipartFile file) throws Exception{
        if(file == null || file.getSize() == 0){
            log.error("===>上传文件异常：文件大小为空....");
            return  Result.error("文件大小为空");
        }
        boolean b = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
        if(!b){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
        }
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        String key = UUID.randomUUID().toString().replace("-", "");
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = String.format("%s%s", key, suffix);
        log.info("===>开始上传文件至minio,ObjectName: {}",objectName);
        InputStream inputStream = file.getInputStream();
        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(contentType)
                        .build());
        String url = String.format("%s/%s/%s", minioConfig.getEndpoint(),minioConfig.getBucketName(),objectName);
        log.info("===>文件上传至minio成功，访问路径：{}",url);
        inputStream.close();
        myMap = new HashMap<>();
        myMap.put("url", url);
        return Result.success(myMap);
    }

    @GetMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) {
        try(InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(filename)
                .build())) {
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.close();
        } catch (Exception e) {
            log.error("file download from minio exception, file name: {}", filename,  e);
        }
    }



}
