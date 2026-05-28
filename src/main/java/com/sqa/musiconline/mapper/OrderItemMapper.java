package com.sqa.musiconline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqa.musiconline.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
