package com.jin.pixhive.infrastructure.repository;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin.pixhive.domain.picture.entity.Picture;
import com.jin.pixhive.domain.picture.repository.PictureRepository;
import com.jin.pixhive.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Service;

@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}

