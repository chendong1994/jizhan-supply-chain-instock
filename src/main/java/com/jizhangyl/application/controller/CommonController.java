package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.utils.FileUploadUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author 杨贤达
 * @date 2018/8/25 21:01
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @PostMapping("/upload")
    public ResultVO upload(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileUploadUtil.upload(file);
        return ResultVOUtil.success(fileUrl);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFromUrl(@RequestParam("imageUrl") String imageUrl) throws IOException {
        if (StringUtils.isEmpty(imageUrl)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        ByteArrayOutputStream bos = null;
        InputStream is = null;

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            byte[] bytes = new byte[1024];
            bos = new ByteArrayOutputStream();
            int len = 0;

            is = conn.getInputStream();
            while ((len = is.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }

            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("utf-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            log.info("【文件下载】下载成功, imageUrl = {}", imageUrl);

            return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new GlobalException(ResultEnum.OSS_FILE_DOWNLOAD_ERROR.getCode(), e.getMessage());
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }
}
