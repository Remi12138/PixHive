package com.jin.pixhive.infrastructure.manage.delete;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin.pixhive.infrastructure.api.CosManager;
import com.jin.pixhive.infrastructure.mapper.PictureMapper;
import com.jin.pixhive.domain.picture.entity.Picture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * delete file in COS
 */
@Slf4j
@Component
public class PictureFileCleaner {

    @Resource
    protected CosManager cosManager;

    @Resource
    protected PictureMapper pictureMapper;

    /**
     * delete picture in COS
     * @param oldPicture
     */
    @Async
    public void clearPictureFile(Picture oldPicture) {
        // check if picture is used by multiple records
        String pictureUrl = oldPicture.getUrl();
        long count = pictureMapper.selectCount(
                new LambdaQueryWrapper<Picture>().eq(Picture::getUrl, pictureUrl)
        );
        // more than one, no delete
        if (count > 1) {
            return;
        }
        log.info("Deleting picture file: {}", extractObjectKey(pictureUrl));
        cosManager.deleteObject(extractObjectKey(pictureUrl));
        // delete thumbnail
        String thumbnailUrl = oldPicture.getThumbnailUrl();
        if (StrUtil.isNotBlank(thumbnailUrl)) {
            log.info("Deleting thumbnail: {}", extractObjectKey(thumbnailUrl));
            cosManager.deleteObject(extractObjectKey(thumbnailUrl));
        }
    }

    private String extractObjectKey(String fullUrl) {
        if (fullUrl == null) return null;
        try {
            URL url = new URL(fullUrl);
            return url.getPath().substring(1); // remove leading slash
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid picture URL: " + fullUrl, e);
        }
    }
}

