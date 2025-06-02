package com.jin.pixhive.infrastructure.manage.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.jin.pixhive.infrastructure.api.CosManager;
import com.jin.pixhive.infrastructure.config.CosClientConfig;
import com.jin.pixhive.infrastructure.exception.BusinessException;
import com.jin.pixhive.infrastructure.exception.ErrorCode;
import com.jin.pixhive.infrastructure.exception.ThrowUtils;
import com.jin.pixhive.infrastructure.manage.upload.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * file manager
 * @deprecated use PictureUploadTemplate instead
 */
@Deprecated
@Slf4j
@Service
public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * Upload Picture
     *
     * @param multipartFile    file
     * @param uploadPathPrefix
     * @return
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // valid picture
        validPicture(multipartFile);
        // upload path
        String uuid = RandomUtil.randomString(16);
        String originFilename = multipartFile.getOriginalFilename();
        // Concatenate the file name, instead of using the original name
        // -> Enhanced security
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String projName = "PixHive";
        String uploadPath = String.format(projName + "/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            // create Temp File
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            // upload picture
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // get picture info
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // return encapsulated obj -> UploadPictureResult
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("Failed to upload the picture to the COS", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload the picture to the COS");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * valid picture (not null / size / format)
     *
     * @param multipartFile multipart file
     */
    public void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "File can not be null!");
        // 1. check size
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "File size can not be larger than 2 MB!");
        // 2. check file format
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // allowed file suffix
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "Not support file format!(jpeg, jpg, png, webp)");
    }

    /**
     * delete temporary file
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

    public UploadPictureResult uploadPictureByUrl(String fileUrl, String uploadPathPrefix) {
        // valid picture
        // validPicture(multipartFile);
        validPicture(fileUrl);
        // upload path
        String uuid = RandomUtil.randomString(16);
        // String originFilename = multipartFile.getOriginalFilename();
        String originFilename = FileUtil.mainName(fileUrl);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            // create tmp file
            file = File.createTempFile(uploadPath, null);
            // multipartFile.transferTo(file);
            HttpUtil.downloadFile(fileUrl, file);
            // upload
            // ... remain same
            return uploadPictureByUrl(fileUrl, uploadPathPrefix);
        } catch (Exception e) {
            log.error("error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "upload file error");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * valid picture by url
     */
    private void validPicture(String fileUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "file url cannot be blank!");

        try {
            // 1. check url format
            new URL(fileUrl); // url should be valid
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid url!");
        }

        // 2. check URL protocol
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR, "Only support HTTP or HTTPS protocol");

        // 3. send HEAD request
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            // if not ok, just return (no need to throw exception since some do not have head response)
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 4. check Content-Type
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                // ALLOW_CONTENT_TYPES
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "file format is not supported! (only support jpeg, jpg, png, webp)");
            }
            // 5. check file size
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long TWO_MB = 2 * 1024 * 1024L; // 2MB
                    ThrowUtils.throwIf(contentLength > TWO_MB, ErrorCode.PARAMS_ERROR, "file size can not be larger than 2 MB!");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "Get file size(Content-Length format) error!");
                }
            }
        } finally {
            // release response
            if (response != null) {
                response.close();
            }
        }
    }
}
