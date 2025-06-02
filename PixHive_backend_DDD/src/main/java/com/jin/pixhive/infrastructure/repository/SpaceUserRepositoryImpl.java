package com.jin.pixhive.infrastructure.repository;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin.pixhive.domain.space.entity.SpaceUser;
import com.jin.pixhive.domain.space.repository.SpaceUserRepository;
import com.jin.pixhive.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}

