package com.nebula.api.controller;

import com.nebula.config.result.Result;
import com.nebula.config.util.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件上传下载相关接口")
public class FileController {

    private final MinioUtil minioUtil;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件是否为空
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 验证文件大小（10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过 10MB");
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID() + extension;

            log.info("开始上传文件: originalName={}, size={}, fileName={}",
                    originalFilename, file.getSize(), fileName);

            // 上传文件
            minioUtil.uploadFile(bucketName, fileName, file);

            // 获取访问URL
            String fileUrl = minioUtil.getPresignedUrl(bucketName, fileName);

            Map<String, String> data = new HashMap<>();
            data.put("fileName", fileName);
            data.put("fileUrl", fileUrl);
            data.put("originalFilename", originalFilename);

            log.info("文件上传成功: fileName={}", fileName);
            return Result.success("上传成功", data);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);

            // 判断是否是文件大小超限异常
            if (e.getMessage() != null && e.getMessage().contains("FileSizeLimitExceededException")) {
                return Result.error("文件大小超过限制，最大支持 10MB");
            }

            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    public Result<Boolean> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            minioUtil.deleteFile(bucketName, fileName);
            return Result.success("删除成功", true);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/url")
    @Operation(summary = "获取文件访问URL")
    public Result<String> getFileUrl(@RequestParam("fileName") String fileName,
                                     @RequestParam(value = "expires", defaultValue = "604800") Integer expires) {
        try {
            String url = minioUtil.getPresignedUrl(bucketName, fileName, expires);
            return Result.success(url);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return Result.error("获取文件URL失败: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取文件信息")
    public Result<Map<String, Object>> getFileInfo(@RequestParam("fileName") String fileName) {
        try {
            var fileInfo = minioUtil.getFileInfo(bucketName, fileName);

            Map<String, Object> data = new HashMap<>();
            data.put("fileName", fileName);
            data.put("size", fileInfo.size());
            data.put("contentType", fileInfo.contentType());
            data.put("lastModified", fileInfo.lastModified());

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取文件信息失败", e);
            return Result.error("获取文件信息失败: " + e.getMessage());
        }
    }
}
