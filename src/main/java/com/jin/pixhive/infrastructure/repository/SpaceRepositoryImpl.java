package com.jin.pixhive.infrastructure.repository;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.space.repository.SpaceRepository;
import com.jin.pixhive.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Service;

@Service
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}

