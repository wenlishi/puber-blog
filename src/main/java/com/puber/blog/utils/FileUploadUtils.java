package com.puber.blog.utils;

import com.puber.blog.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传工具类
 * 处理图片上传、文件命名、类型验证等
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
public class FileUploadUtils {

    /**
     * 允许的图片类型
     */
    private static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif"};

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传图片文件
     *
     * @param file 上传的文件
     * @param uploadPath 上传根路径（如：E:/Desktop/puber-blog/uploads）
     * @return String 文件URL路径（如：/uploads/2026/04/13/uuid.jpg）
     * @throws BusinessException 文件上传异常
     */
    public static String uploadImage(MultipartFile file, String uploadPath) throws BusinessException {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(400, "文件大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(400, "文件名不能为空");
        }

        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);
        if (extension == null || !isAllowedImageType(extension)) {
            throw new BusinessException(400, "只允许上传jpg、png、gif格式的图片");
        }

        // 生成唯一文件名（UUID + 扩展名）
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        // 构建日期目录路径（yyyy/MM/dd）
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 构建完整文件路径
        Path fullPath = Paths.get(uploadPath, datePath);

        try {
            // 创建目录（如果不存在）
            Files.createDirectories(fullPath);

            // 保存文件
            Path filePath = fullPath.resolve(newFilename);
            file.transferTo(filePath.toFile());

            log.info("文件上传成功：{}", filePath.toString());

            // 返回URL路径（去掉上传根路径，保留相对路径）
            return "/uploads/" + datePath + "/" + newFilename;
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new BusinessException(500, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return String 扩展名（不含点号）
     */
    private static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null;
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 检查是否允许的图片类型
     *
     * @param extension 文件扩展名
     * @return true: 允许, false: 不允许
     */
    private static boolean isAllowedImageType(String extension) {
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param uploadPath 上传根路径
     * @param fileUrl 文件URL路径（如：/uploads/2026/04/13/uuid.jpg）
     * @return boolean 是否删除成功
     */
    public static boolean deleteFile(String uploadPath, String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        // 构建完整文件路径
        Path filePath = Paths.get(uploadPath, fileUrl.replace("/uploads/", ""));

        try {
            File file = filePath.toFile();
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("文件删除{}：{}", deleted ? "成功" : "失败", filePath.toString());
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage(), e);
            return false;
        }
    }
}