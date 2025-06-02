package com.jin.pixhive_backend.model.dto.picture;

import com.jin.pixhive_backend.api.aliyunai.model.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * Create Picture OutPainting Task Request
 */
@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    private Long pictureId;

    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}


