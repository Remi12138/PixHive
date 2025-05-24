package com.jin.pixhive.interfaces.assembler;

import cn.hutool.json.JSONUtil;
import com.jin.pixhive.domain.picture.entity.Picture;
import com.jin.pixhive.interfaces.dto.picture.PictureEditRequest;
import com.jin.pixhive.interfaces.dto.picture.PictureUpdateRequest;
import org.springframework.beans.BeanUtils;

public class PictureAssembler {

    public static Picture toPictureEntity(PictureEditRequest request) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // list -> string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }

    public static Picture toPictureEntity(PictureUpdateRequest request) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // list -> string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }
}


