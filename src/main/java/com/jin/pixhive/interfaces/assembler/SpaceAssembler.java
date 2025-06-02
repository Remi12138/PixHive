package com.jin.pixhive.interfaces.assembler;

import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.interfaces.dto.space.SpaceAddRequest;
import com.jin.pixhive.interfaces.dto.space.SpaceEditRequest;
import com.jin.pixhive.interfaces.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

public class SpaceAssembler {

    public static Space toSpaceEntity(SpaceAddRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceUpdateRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceEditRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }
}
