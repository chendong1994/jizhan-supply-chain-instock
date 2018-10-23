package com.jizhangyl.application.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.OSSObject;
import com.jizhangyl.application.config.OssConfig;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import static com.jizhangyl.application.enums.ResultEnum.FILE_UPLOAD_ERROR;

/**
 * @author 杨贤达
 * @date 2018/8/19 18:52
 * @description
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Autowired
    private OssConfig ossConfig;

    /**
     * oss 文件上传
     *
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {

        if (file.isEmpty()) {
            log.error("【文件上传】选中的文件为空");
            throw new GlobalException(FILE_UPLOAD_ERROR.getCode(), String.format(FILE_UPLOAD_ERROR.getMessage(), "选中的文件为空"));
        }
        OSSClient ossClient = null;
        String outsideUrl = null;
        try {
            InputStream is = new ByteArrayInputStream(file.getBytes());
            ossClient = new OSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            String bucketName = ossConfig.getBucketName();

            // 判断 bucket 是否存在
            if (ossClient.doesBucketExist(bucketName)) {
                log.info("【文件上传】已创建Bucket: {}", bucketName);
            } else {
                log.info("【文件上传】Bucket不存在, 已创建Bucket: {}", bucketName);
                ossClient.createBucket(bucketName);
            }

            // 打印 bucket 信息
            BucketInfo info = ossClient.getBucketInfo(bucketName);
            log.info("Bucket " + bucketName + "的信息如下：");
            log.info("\t数据中心：" + info.getBucket().getLocation());
            log.info("\t创建时间：" + info.getBucket().getCreationDate());
            log.info("\t用户标志：" + info.getBucket().getOwner());

            String fileName = file.getOriginalFilename();
            String fileNameSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            String lastFileName = UUID.randomUUID().toString().replace("-", "") + fileNameSuffix;

            // 上传文件开始
            ossClient.putObject(bucketName, lastFileName, is);
            log.info("【文件上传】文件: {}, 存入OSS成功, 新名称为: {}", fileName, lastFileName);

            // 组装该文件外网访问地址
            outsideUrl = ossConfig.getBucketUrl() + lastFileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(FILE_UPLOAD_ERROR.getCode(), String.format(FILE_UPLOAD_ERROR.getMessage(), e.getMessage()));
        } finally {
            ossClient.shutdown();
        }
        log.info("【文件上传】上传成功");
        return outsideUrl;
    }

    /**
     * OSS 文件删除
     *
     * @param key
     */
    public void delete(String key) {
        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            if (!ossClient.doesObjectExist(ossConfig.getBucketName(), key)) {
                log.info("【文件删除】{}文件不存在, 无需删除！", key);
            } else {
                ossClient.deleteObject(ossConfig.getBucketName(), key);
                log.info("【文件删除】{}删除成功！", key);
            }
        } catch (Exception e) {
            log.error("【文件删除】异常: {}", e.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResultEnum.OSS_FILE_DELETE_ERROR);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 文件下载
     * @param key
     */
    public void download(String key) {
        OSSClient ossClient = null;
        InputStream inputStream = null;
        try {
            ossClient = new OSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

            if (!ossClient.doesObjectExist(ossConfig.getBucketName(), key)) {
                log.info("【文件下载】{}文件不存在, 无法下载！", key);
            } else {
                // 获取文件内容
                OSSObject ossObject = ossClient.getObject(ossConfig.getBucketName(), key);
                inputStream = ossObject.getObjectContent();
                StringBuilder objectContent = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    objectContent.append(line);
                }

                log.info("Object：{}的内容是：{}", key, objectContent);
            }
        } catch (Exception e) {
            log.error("【文件下载】异常: {}", e.getMessage());
            throw new GlobalException(ResultEnum.OSS_FILE_DOWNLOAD_ERROR);
        } finally {
            try {
                inputStream.close();
                ossClient.shutdown();
            } catch (IOException e) {
                log.error("【文件下载】异常: {}", e.getMessage());
                throw new GlobalException(ResultEnum.IO_ERROR);
            }
        }
    }
}
