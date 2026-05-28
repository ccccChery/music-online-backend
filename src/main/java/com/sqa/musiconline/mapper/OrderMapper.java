package com.sqa.musiconline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqa.musiconline.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
