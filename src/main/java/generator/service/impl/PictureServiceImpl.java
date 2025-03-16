package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin.pixhive_backend.model.entity.Picture;
import generator.service.PictureService;
import com.jin.pixhive_backend.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author xianjinghuang
* @description 针对表【picture(picture)】的数据库操作Service实现
* @createDate 2025-03-16 17:37:00
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




