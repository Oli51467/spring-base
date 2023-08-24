package com.sdu.base.transaction.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdu.base.transaction.domain.entity.TransMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TransMessageMapper extends BaseMapper<TransMessage> {
}
