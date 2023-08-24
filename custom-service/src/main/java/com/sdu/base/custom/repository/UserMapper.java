package com.sdu.base.custom.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdu.base.custom.entity.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
