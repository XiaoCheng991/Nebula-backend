package com.nebula.config.util;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;

    /**
     * 检查存储桶是否存在
     */
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
    }

    /**
     * 创建存储桶
     */
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("创建存储桶成功: {}", bucketName);
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @param file       文件
     * @return 文件路径
     */
    @SneakyThrows
    public String uploadFile(String bucketName, String fileName, MultipartFile file) {
        InputStream inputStream = file.getInputStream();

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(inputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        log.info("文件上传成功: {}/{}", bucketName, fileName);
        return fileName;
    }

    /**
     * 上传文件流
     *
     * @param bucketName  存储桶名称
     * @param fileName    文件名
     * @param inputStream 文件流
     * @param contentType 文件类型
     * @return 文件路径
     */
    @SneakyThrows
    public String uploadFile(String bucketName, String fileName, InputStream inputStream, String contentType) {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(inputStream, inputStream.available(), -1)
                .contentType(contentType)
                .build());

        log.info("文件上传成功: {}/{}", bucketName, fileName);
        return fileName;
    }

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     */
    @SneakyThrows
    public void deleteFile(String bucketName, String fileName) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());

        log.info("文件删除成功: {}/{}", bucketName, fileName);
    }

    /**
     * 获取文件访问URL（临时）
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @param expires    过期时间（秒）
     * @return 访问URL
     */
    @SneakyThrows
    public String getPresignedUrl(String bucketName, String fileName, Integer expires) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                .expiry(expires, TimeUnit.SECONDS)
                .build());
    }

    /**
     * 获取文件访问URL（默认7天）
     */
    public String getPresignedUrl(String bucketName, String fileName) {
        return getPresignedUrl(bucketName, fileName, 7 * 24 * 60 * 60);
    }

    /**
     * 下载文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @return 文件流
     */
    @SneakyThrows
    public InputStream downloadFile(String bucketName, String fileName) {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());
    }

    /**
     * 获取文件信息
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @return 文件信息
     */
    @SneakyThrows
    public StatObjectResponse getFileInfo(String bucketName, String fileName) {
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());
    }

    /**
     * 复制文件
     *
     * @param sourceBucket 源存储桶
     * @param sourceFile   源文件
     * @param targetBucket 目标存储桶
     * @param targetFile   目标文件
     */
    @SneakyThrows
    public void copyFile(String sourceBucket, String sourceFile, String targetBucket, String targetFile) {
        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(targetBucket)
                .object(targetFile)
                .source(CopySource.builder()
                        .bucket(sourceBucket)
                        .object(sourceFile)
                        .build())
                .build());

        log.info("文件复制成功: {}/{} -> {}/{}", sourceBucket, sourceFile, targetBucket, targetFile);
    }

    /**
     * 从URL下载图片并上传到MinIO
     *
     * @param bucketName 存储桶名称
     * @param imageUrl   图片URL
     * @param fileName   目标文件名（可选，为空则自动生成）
     * @return 包含fileName、fileSize、fileUrl的Map
     */
    @SneakyThrows
    public Map<String, Object> uploadImageFromUrl(String bucketName, String imageUrl, String fileName) {
        log.info("开始从URL下载图片: {}", imageUrl);

        // 从URL下载图片
        URL url = new URI(imageUrl).toURL();
        try (InputStream inputStream = url.openStream()) {
            // 读取到字节数组，以便获取大小
            byte[] imageBytes = inputStream.readAllBytes();
            long fileSize = imageBytes.length;

            // 生成文件名
            String finalFileName = fileName;
            if (finalFileName == null || finalFileName.isEmpty()) {
                String extension = getExtensionFromUrl(imageUrl);
                finalFileName = "avatars/" + UUID.randomUUID() + extension;
            }

            // 上传到MinIO
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(finalFileName)
                        .stream(byteArrayInputStream, fileSize, -1)
                        .contentType(getContentTypeFromUrl(imageUrl))
                        .build());
            }

            log.info("图片上传成功: {}/{}, 大小: {} bytes", bucketName, finalFileName, fileSize);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", finalFileName);
            result.put("fileSize", fileSize);
            result.put("fileUrl", finalFileName); // 这里返回的是MinIO中的object name

            return result;
        }
    }

    /**
     * 从URL获取文件扩展名
     */
    private String getExtensionFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains(".png")) {
            return ".png";
        } else if (lowerUrl.contains(".jpg") || lowerUrl.contains(".jpeg")) {
            return ".jpg";
        } else if (lowerUrl.contains(".gif")) {
            return ".gif";
        } else if (lowerUrl.contains(".webp")) {
            return ".webp";
        }
        return ".jpg"; // 默认
    }

    /**
     * 从URL获取Content-Type
     */
    private String getContentTypeFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains(".png")) {
            return "image/png";
        } else if (lowerUrl.contains(".jpg") || lowerUrl.contains(".jpeg")) {
            return "image/jpeg";
        } else if (lowerUrl.contains(".gif")) {
            return "image/gif";
        } else if (lowerUrl.contains(".webp")) {
            return "image/webp";
        }
        return "image/jpeg"; // 默认
    }
}
